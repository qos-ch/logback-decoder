#!/bin/sh
#
# This script creates the uber jar (with debug symbols). The tests are skipped.
# The jar will be stored in the "./target" directory"
#
#   ./target/logback-decoder-<version>.jar
#

# Usage: ./makrjar.sh [-r]
#
#  Options
#
#  -r  make release build (default: false)
#
profile="debug"

if [ "x$1" == "x-r" ];
then
  profile="release"
fi

set -e

# set the uber jar's version to match the one in pom.xml
version=$(mvn help:evaluate -Dexpression=project.version | grep '^[^[]')
mvn -f pom-uber.xml versions:set -DnewVersion=${version}

mvn -P $profile clean install -DskipTests=true
mvn -f pom-uber.xml package

