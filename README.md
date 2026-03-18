# Pekko HTTP microservice example

[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/theiterators/pekko-http-microservice/master/COPYING)
![Build Status](https://github.com/theiterators/pekko-http-microservice/actions/workflows/ci.yml/badge.svg)

**This repository is a fork of [akka-http-microservice](https://github.com/theiterators/akka-http-microservice), migrated to [Apache Pekko](https://pekko.apache.org/) and Scala 3.**

This project demonstrates the [Pekko HTTP](https://pekko.apache.org/docs/pekko-http/current/) library and Scala 3 to write a simple REST (micro)service. The project shows the following tasks that are typical for most Pekko HTTP-based projects:

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

The service provides two REST endpoints - one which gives GeoIP info for a given IP and another for calculating the geographical distance between a pair of IPs. It uses the [ip-api](http://ip-api.com/) service which offers a free JSON GeoIP REST API for non-commercial use.

For a more thorough explanation, check out the [tutorial](https://github.com/theiterators/pekko-http-microservice/blob/master/TUTORIAL.md).

## Usage

Start the service with sbt:

```
$ sbt
> ~reStart
```

With the service up, you can start sending HTTP requests:

```
$ curl http://localhost:9000/ip/8.8.8.8
{
  "city": "Mountain View",
  "query": "8.8.8.8",
  "country": "United States",
  "lon": -122.0881,
  "lat": 37.3845
}
```

```
$ curl -X POST -H 'Content-Type: application/json' http://localhost:9000/ip -d '{"ip1": "8.8.8.8", "ip2": "93.184.216.34"}'
{
  "distance": 4347.624347494718,
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

### Testing

Execute tests using `test` command:

```
$ sbt
> test
```

## Author & license

If you have any questions regarding this project, contact:

Lukasz Sowa <lukasz@iteratorshq.com> from [Iterators](https://www.iteratorshq.com).

For licensing info see LICENSE file in project's root directory.
