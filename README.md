# Beagle - the sequence search engine 

Beagle is project that aims to provide biologists with a quick interactive search within there sequence data sets.

## How to run it

For now, everything has to be started manually. I hope to automate this process soon using Helm.

### Start the HTTP server

For development:

```shell
sbt runDev
```

In production:

```shell
sbt run
```

### Start an Elastic Search in Docker

```shell
docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.0.1
```

## Contribute

Feel free to email me, if you like to contribute or create a ticket if you have feature requests.
