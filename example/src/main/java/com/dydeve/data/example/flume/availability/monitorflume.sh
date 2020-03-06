#!/bin/sh
#echo "enter monitorflume.sh"
export JAVA_HOME=/home/hadoop/app/jdk
#echo "find flume agent"
ps -ef | grep flume| grep java | grep -v grep  
if [ $? -ne 0 ]  
then  
echo "start process......"
sh /home/hadoop/shell/bin/start_testflume.sh
else  
echo "runing......"  
fi 
