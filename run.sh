#!/bin/bash
# Compiles common/ and exercise/ (or solution/) against the jars of the Spark install.
# It packages the classes as build/steps.jar and submits a program, named by its class name.
#
#   ./run.sh departures                                     exercise/03_departures/Departures.java, data from /data
#   ./run.sh solution departures                            solution/03_departures/Departures.java
#   ./run.sh solution departures hdfs                       data from hdfs://namenode:8020/data
#   ./run.sh solution delayreport                           the Spark SQL report, for the DataFlint tab
#   ./run.sh solution all                                   every program, in folder order
set -euo pipefail
cd "$(dirname "$0")"

PROGRAMS=(Platforms Timetable Departures LateTrains DelayMinutes SumThreeWays StationNames WorstStations Punctuality
          DelayReport)

usage() {
    sed -n '5,9p' "$0" | sed 's/^# //' >&2
    echo >&2
    for class in "${PROGRAMS[@]}"; do echo "  $class" >&2; done
    exit 1
}

folder=exercise
classes=()
base=/data
for arg in "$@"; do
    # "departures", "03_departures" and "solution/03_departures/" (tab completion) all name Departures.
    word=$(echo "$arg" | tr '[:upper:]' '[:lower:]')
    case "$word" in
        exercise/?* | solution/?*) folder=${word%%/*} ;;
    esac
    word=${word%/}
    word=${word##*/}
    word=${word%.java}
    word=${word#[0-9][0-9]_}
    case "$word" in
        exercise | solution) folder=$word ;;
        local) base=/data ;;
        hdfs) base=hdfs://namenode:8020/data ;;
        all) classes=("${PROGRAMS[@]}") ;;
        *)
            found=
            for class in "${PROGRAMS[@]}"; do
                [ "$(echo "$class" | tr '[:upper:]' '[:lower:]')" = "$word" ] && found=$class
            done
            [ -n "$found" ] || usage
            classes=("$found")
            ;;
    esac
done
[ ${#classes[@]} -gt 0 ] || usage

# exercise/ and solution/ hold classes of the same names, so only the chosen folder is compiled.
rm -rf build/classes
mkdir -p build/classes
javac -proc:none -cp "/opt/spark/jars/*" -d build/classes common/*.java "$folder"/*/*.java
jar cf build/steps.jar -C build/classes .

for class in "${classes[@]}"; do
    spark-submit --class "$class" build/steps.jar "$base"
done
