#!/bin/bash

TIMESTAMP=`date -u +'%Y-%m-%dT%H:%M:%S.%3NZ'`
LOG_1="member1_${TIMESTAMP}.log"
LOG_2="member2_${TIMESTAMP}.log"
HEAP_DUMP_1="member1_${TIMESTAMP}.hprof"
HEAP_DUMP_2="member2_${TIMESTAMP}.hprof"

function kill_processes() {
  kill ${P1}
  kill ${P2}
  exit 0
}

trap 'kill_processes' 1 2 3 15

mvn clean install

(mvn exec:java > ${LOG_1}) &
P1=${!}

(mvn exec:java > ${LOG_2}) &
P2=${!}

while true
do
  [ -f ${HEAP_DUMP_1} ] && rm -f ${HEAP_DUMP_1}
  [ -f ${HEAP_DUMP_2} ] && rm -f ${HEAP_DUMP_2}
  jcmd ${P1} GC.heap_dump ${HEAP_DUMP_1} > /dev/null 2>&1
  jcmd ${P2} GC.heap_dump ${HEAP_DUMP_2} > /dev/null 2>&1
  sleep 5m
done
