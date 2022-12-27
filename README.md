# Prime Numbers calculator

## Summary
The app `primenumbers-grpc` is composed essentially by 2 modules:
- An Akka-gRPC service (listing on port `9000`) in charge of doing all the calculations to retrieve the list of prime numbers up to the given 
number.
- An Akka-HTTP server (listing on port `8000`) in charge of exposing the previous service via http, on the `GET /prime` endpoint.

As we are using a communication via gRPC, we need to define the schema that both services will use to communicate.
So both modules need a protobuf schema which contain the definition for our goal.

_NOTE: For some reason (TODO), the HTTP module was not able to connect
with the gRPC server. Anyway, the dockerfiles and the docker-compose files are included._

## Instructions

Once you have cloned the repo by `git clone -b master ./primenumbers-grpc.gitbundle prime-numbers-test`, you need to follow the next steps from your terminal to run the application:
1. Build the application from the app folder you cloned into:
```shell
$ cd primenumbers-grpc
$ sbt assembly
```
2. Run the grpc-server
```shell
$ java -Xms512m -Xmx1024m -cp ./grpc/target/scala-2.13/grpc-assembly-0.1.0-SNAPSHOT.jar primenumbers.GrpcServer
```
3. Run the http-server
```shell
$ java -Xms512m -Xmx1024m -cp ./http/target/scala-2.13/http-assembly-0.1.0-SNAPSHOT.jar primenumbers.HttpServer```
```
4. Once both processes are running, you can perform the proper request pointing to `http://localhost:8000/prime/`, such as:
```shell
$ curl --location --request GET 'http://localhost:8000/prime/100'

reponse: [2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97]
```