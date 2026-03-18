# Pekko HTTP microservice tutorial

This tutorial walks through a small REST microservice built with [Pekko HTTP](https://pekko.apache.org/docs/pekko-http/current/) and Scala 3. You'll learn about:

* starting a standalone HTTP server,
* handling file-based configuration,
* logging,
* routing,
* deconstructing requests,
* unmarshalling JSON entities to Scala's case classes,
* marshalling Scala's case classes to JSON responses,
* error handling,
* issuing requests to external services,
* testing with mocking of external services.

The tutorial focuses on the HTTP layer and doesn't cover database access or other persistence concerns.

Check out the code and feel free to open issues on [GitHub](https://github.com/theiterators/pekko-http-microservice).

## What is a microservice?

A microservice is a small, focused program that handles a single bounded domain. Microservices are typically:

* short and concise,
* responsible for one type of data or operation.

Because the code stays small, it's easier to understand, rewrite, and reuse across projects. In this example, the bounded domain is IP geolocation data.

## What does this microservice do?

The service has two features:

* locate an IP address geographically,
* compute the distance between two IP addresses.

It exposes two HTTP JSON endpoints:

* `GET /ip/X.X.X.X` — returns geolocation data for the given IP,
* `POST /ip` — accepts `{"ip1": "X.X.X.X", "ip2": "Y.Y.Y.Y"}` and returns the distance between the two IPs along with their geolocation data.

## Running the service

Start the service with:

```
$ sbt "~reStart"
```

Check where Google's DNS servers are located by opening [`http://localhost:9000/ip/8.8.8.8`](http://localhost:9000/ip/8.8.8.8) in your browser, or use `curl`:

```
$ curl http://localhost:9000/ip/8.8.8.8
```

```
$ curl -X POST -H 'Content-Type: application/json' http://localhost:9000/ip -d '{"ip1": "8.8.8.8", "ip2": "8.8.4.4"}'
```

## Example responses

Single IP lookup:

```json
{
  "city": "Mountain View",
  "query": "8.8.8.8",
  "country": "United States",
  "lon": -122.0881,
  "lat": 37.3845
}
```

IP pair distance:

```json
{
  "distance": 4347.6243474947,
  "ip1Info": {
    "city": "Mountain View",
    "query": "8.8.8.8",
    "country": "United States",
    "lon": -122.0881,
    "lat": 37.3845
  },
  "ip2Info": {
    "city": "Norwell",
    "query": "93.184.216.34",
    "country": "United States",
    "lon": -70.8228,
    "lat": 42.1508
  }
}
```

## Code overview

The project has four main parts:

* [build.sbt](https://github.com/theiterators/pekko-http-microservice/blob/master/build.sbt) and [plugins.sbt](https://github.com/theiterators/pekko-http-microservice/blob/master/project/plugins.sbt) — build configuration and dependencies,
* [application.conf](https://github.com/theiterators/pekko-http-microservice/blob/master/src/main/resources/application.conf) — runtime configuration,
* [PekkoHttpMicroservice.scala](https://github.com/theiterators/pekko-http-microservice/blob/master/src/main/scala/PekkoHttpMicroservice.scala) — the service implementation,
* [ServiceSpec.scala](https://github.com/theiterators/pekko-http-microservice/blob/master/src/test/scala/ServiceSpec.scala) — tests.

## Build scripts

### build.sbt

`build.sbt` declares the project metadata, Scala compiler flags, and dependencies:

* `pekko-actor` — the Actor system that `pekko-http` and `pekko-stream` are built on.
* `pekko-stream` — Reactive Streams implementation using Pekko actors.
* `pekko-http` — the core library for building reactive HTTP services.
* `circe-core` — JSON handling.
* `circe-generic` — automatic derivation of JSON encoders and decoders for case classes.
* `pekko-http-circe` — integration between Pekko HTTP and circe for request/response marshalling.
* `pekko-testkit` — testing utilities for Pekko actors.
* `pekko-http-testkit` — testing utilities for Pekko HTTP routes.
* `scalatest` — Scala testing library.

### plugins.sbt

The project uses four sbt plugins:

* `sbt-revolver` — recompiles and restarts the service on file changes (`~reStart` command). Initialized in `build.sbt`.
* `sbt-assembly` — packages the service as a single fat JAR for deployment.
* `sbt-native-packager` — produces native packages (Docker images, .deb, etc.).
* `sbt-updates` — provides the `dependencyUpdates` command to check for newer dependency versions.

## Configuration

The configuration lives in [application.conf](https://github.com/theiterators/pekko-http-microservice/blob/master/src/main/resources/application.conf) and has three sections:

* `pekko` — Pekko actor system settings (log level, etc.),
* `http` — HTTP server interface and port,
* `services` — external service endpoints (ip-api host and port).

Configuration values can be overridden at runtime:

```
java -jar microservice.jar -Dservices.ip-api.port=8080
```

## The service code

All of the code lives in [PekkoHttpMicroservice.scala](https://github.com/theiterators/pekko-http-microservice/blob/master/src/main/scala/PekkoHttpMicroservice.scala). It breaks down into these parts:

* **Domain types** — case classes and enums modeling the data,
* **Protocols** — JSON encoder/decoder instances,
* **Service trait** — external HTTP communication and route definitions,
* **Main object** — wiring and server startup.

### Domain types

The service defines a few types:

* `IpApiResponse` / `IpApiResponseStatus` — models the external ip-api.com response, including a status enum to distinguish success from failure.
* `IpPairSummaryRequest` — models the incoming POST request body.
* `IpInfo` and `IpPairSummary` — intermediate types that get serialized to JSON responses. `IpPairSummary` also contains the Haversine distance calculation between two coordinates.

### Protocols (JSON marshalling)

The `Protocols` trait (which extends `ErrorAccumulatingCirceSupport` from pekko-http-circe) defines implicit circe encoders and decoders for all domain types. These are derived automatically using `circe-generic`'s `deriveEncoder` and `deriveDecoder`. Having these implicits in scope lets Pekko HTTP automatically marshal and unmarshal JSON request/response bodies.

### External HTTP requests

The `Service` trait defines how the service communicates with ip-api.com:

1. `ipApiConnectionFlow` — an Pekko HTTP client connection flow to the external service, configured from `application.conf`.
2. `ipApiRequest` — sends a single HTTP request through the connection flow and collects the response.
3. `fetchIpInfo` — builds a GET request to `/json/{ip}`, sends it, checks the response status, and unmarshals the JSON body into either an `IpInfo` (on success) or an error message string (on failure). Uses Scala 3 union types (`String | IpInfo`) for the result.

### Routes

The route tree is defined in the `Service` trait's `routes` value:

```
pathPrefix("ip") {
  (get & path(Segment)) { ip => ... }    // GET /ip/X.X.X.X
  ~
  (post & entity(as[IpPairSummaryRequest])) { req => ... }  // POST /ip
}
```

Pekko HTTP's routing DSL works by nesting directives. Each directive either filters the request (e.g. `get`, `post`, `pathPrefix`) or extracts data from it (e.g. `path(Segment)`, `entity(as[T])`). A request passes through only if it satisfies all directives, at which point the innermost `complete` block produces the response.

Key directives used here:

* `pathPrefix("ip")` — matches requests whose path starts with `/ip`.
* `path(Segment)` — extracts the next path segment as a string.
* `get` / `post` — filters by HTTP method.
* `entity(as[IpPairSummaryRequest])` — unmarshals the request body to a case class.
* `logRequestResult(...)` — logs each request/response pair.

Other useful directives (not used in this example):

* `formFields("field1", "field2")` — extracts form fields from POST requests.
* `headerValueByName("X-Auth-Token")` — extracts a header value.
* `path("member" / Segment / "books")` — matches a path pattern with an extracted segment.

See the full list of [Pekko HTTP directives](https://pekko.apache.org/docs/pekko-http/current/routing-dsl/directives/index.html).

### Building responses

With JSON marshalling in scope, returning a response is straightforward — just return a marshallable type inside `complete`. Pekko HTTP handles serialization and sets appropriate status codes. `Future[T]`, `Option[T]`, tuples of `(StatusCode, T)`, and plain values all work. Returning `None` automatically produces a 404.

### Server startup

The `PekkoHttpMicroservice` object wires everything together: it creates the actor system, loads config, and binds the routes to the configured interface and port.

## Tests

The [test suite](https://github.com/theiterators/pekko-http-microservice/blob/master/src/test/scala/ServiceSpec.scala) demonstrates two things:

1. **Route testing syntax** — `pekko-http-testkit` lets you send requests directly to routes and assert on status codes, content types, and response bodies without starting a real server.

2. **Mocking external services** — the test overrides `ipApiConnectionFlow` with a `Flow` that returns canned responses instead of making real HTTP calls to ip-api.com. This keeps tests fast and deterministic.

Run them with:

```
$ sbt test
```

## Summary

That's it. This project shows how to build a small HTTP microservice with Pekko HTTP that handles JSON GET and POST requests and communicates with an external service. The patterns here — routing DSL, circe-based marshalling, connection flows for outbound HTTP, and testkit-based testing — apply to larger Pekko HTTP services as well.

Questions or feedback? Open an issue on [GitHub](https://github.com/theiterators/pekko-http-microservice).
