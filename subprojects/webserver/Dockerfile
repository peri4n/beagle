# syntax=docker/dockerfile:experimental

############################
# Start of the build stage #
############################
FROM hseeberger/scala-sbt:11.0.2_2.12.8_1.2.8 as build-stage

WORKDIR /usr/src/build

# Copy build plugins
COPY project ./project/

# Copy build configuration
COPY build.sbt ./

# Copy source code
COPY src ./src/

# Build the application
RUN --mount=type=cache,target=/root/.ivy2/ sbt stage

############################
# Start of the run stage   #
############################
FROM openjdk:12-oraclelinux7

WORKDIR /usr/src/dist/

# Copy application
COPY --from=build-stage /usr/src/build/target/universal/stage/ .

CMD bin/beagle
