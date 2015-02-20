#!/bin/bash

mvn clean package
mkdir -p build
cp target/client-remoting-1.0-SNAPSHOT.jar build/remoting.jar
cp Dockerfile build/Dockerfile
cp supervisord-remoting.conf build/supervisord-remoting.conf

pushd build
docker build -t giko/remoting:latest .
popd