# Server

This contains the entire backend infrastructure of the beagle project.

# Running the server

To run the server execute the following command in the sbt shell.

```sbt
sbt:beagle> run
```

# Running tests

There are two commands to trigger the tests:

If you want in memory persistence:

```sbt
sbt:beagle> InMem / test
```

If you want database persistence:

```sbt
sbt:beagle> InDb / test
```

