#!/bin/bash
#chmod u+x remote.sh
#./remote.sh "ls" all
#./remote.sh "mkdir /home/hadoop/app" slave
#./remote.sh "mkdir /home/hadoop/data" all

if [ $# -lt 2 ]
then
    echo "usage: ./remote.sh command machineTag"
    echo "usage: ./remote.sh command machineTag confFile"
    exit
fi

cmd=$1
tag=$2

if [ 'b'$3'b' == 'bb' ]
then
    confFile=./deploy.sh
else
    confFile=$3
fi

if [ -f $confFile ]
then
    # 去掉注释  逗号分隔
    for server in `cat $confFile | grep -v '^#' | grep ','$tag',' |awk -F',' '{print $1}'`
    do
        echo "*********server*********"
        ssh $server "source ~/.bashrc; $cmd"
    done
else
    echo "Error: please assign confFile"
fi

