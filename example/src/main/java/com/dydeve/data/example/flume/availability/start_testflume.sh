#!/bin/sh

home=$(cd `dirname $0`; cd ..; pwd)

. ${home}/bin/common.sh

${flume_home}/bin/flume-ng agent \
--conf ${conf_home} \
-f ${conf_home}/avro-memory-logger.properties -n agent1 \
>> ${logs_home}/testflume.log 2>&1 &

echo $! > ${logs_home}/testflume.pid
