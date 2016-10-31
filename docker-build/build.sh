#!/bin/bash
docker run --rm -v /Users/oliver/devel:/usr/src/devel -w /usr/src/devel maven:3-jdk-7 bash -c "cd webservice-client &&  mvn install && cd ../toolkit && mvn package"
