#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

java -jar ${DIR}/target/openapi-diff-2.0.0-SNAPSHOT-jar-with-dependencies.jar $*
