#!/bin/bash

JAR_FILE="$JAVA_HOME/jre/lib/$1.jar"
zipinfo -1 $JAR_FILE \*.class | sed 's/\.class//' | xargs javap -classpath "$JAR_FILE" -public | grep -v "^Compiled" > $1.javap
