#!/bin/bash
#chmod u+x deploy.sh
#./deploy.sh deploy.conf /home/hadoop/ slave

if [ $# lt 3 ]
then
    echo "usage: ./deploy.sh srcFile(or dir) destFile(or dir) machineTag"
    echo "usage: ./deploy.sh srcFile(or dir) destFile(or dir) machineTag confFile"
    exit
fi

src=$1
dest=$2
tag=$3

if [ 'b'$4'b' == 'bb' ]
then
    confFile=./deploy.conf
else
    confFile=$4
fi

if [ -f $confFile ]
then
    if [ -f $src ]
    then
        # 去掉注释  逗号分隔
        for server in `cat $confFile | grep -v '^#' | grep ','$tag',' |awk -F',' '{print $1}'`
        do
            scp $src $server":"$dest
        done
    elif [ -d $src ]
    then
        for server in `cat $confFile | grep -v '^#' | grep ','$tag',' |awk -F',' '{print $1}'`
        do
            # 循环拷贝
            scp -r $src $server":"$dest
        done
    else
        echo "Error: no source file exist"
    fi
else
    echo "Error: please assign config file"
fi

