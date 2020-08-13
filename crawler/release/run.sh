# 以下仅供参考 有问题@李宏果
#!/bin/sh
# 发布机器python路径
export PYTHON_HOME=/usr/bin/python3
# 本地配置
#export PYTHON_HOME=/Users/ervin.li/.pyenv/shims/python
#runPyPath='./main.py'
#logPath='./log.txt'
#requirementsPath='./requirements.txt'

# 发布配置
requirementsPath='/usr/local/webserver/openapi-tmall-crawler/requirements.txt'
runPyPath='/usr/local/webserver/openapi-tmall-crawler/Crawler.py'
healthCheckPyPath='/usr/local/webserver/openapi-tmall-crawler/do_wsgi.py'
logPath='/var/log/openapi-tmall-crawler/log.txt'
pidFile='/tmp/openapi-tmall-crawler.pid'

runPy='Crawler.py'
wsgiPy='do_wsgi.py'
requirementsInstallCommand='pip3 install -r '${requirementsPath}
execStartCommand="${PYTHON_HOME} ${runPyPath}"
execHealthCheckCommand="${PYTHON_HOME} ${healthCheckPyPath}"

#获得当前路径
#. /etc/rc.d/init.d/functions
#. /etc/sysconfig/network
#[ "${NETWORKING}" = "no" ] && exit 0

# 定义脚本对外提供api方法
usage() {
   echo "$0 start|stop|restart"
   exit 1
}

[[ $# -ne 1 ]] && usage

start() {
    if [[ -f pidFile ]]
      then
         echo ${pidFile}' has already started.'
         exit 1
    fi
    if [[ -f ${runPyPath} ]]
    then
        echo '==== start '${runPyPath}
        echo '---- install requirements.txt'
        ${requirementsInstallCommand}
        echo '---- install requirements.txt end'
        nohup ${execStartCommand} > ${logPath} 2>&1 &
        nohup ${execHealthCheckCommand} > ${logPath} 2>&1 &
        echo '==== '${runPyPath}' start end'
    else
      echo ${runPy}' is not exists!'
    fi
    RETVAL=$?
    echo
    return $RETVAL
}

stopCrawler() {
    pid=`ps -ef | grep ${runPy} | grep -v grep | awk '{print $2}'`
    echo "=== stopping..."
    kill -9 ${pid} || failure
    echo "=== stop done"
    RETVAL=$?
    echo
    return $RETVAL
}

stopHealthCheck() {
    pid=`ps -ef | grep ${wsgiPy} | grep -v grep | awk '{print $2}'`
    echo "=== stopping..."
    kill -9 ${pid} || failure
    echo "=== stop done"
    RETVAL=$?
    echo
    return $RETVAL
}

restart(){
    stopCrawler
    stopHealthCheck
    start
}

case $1 in
    start)
    start
    ;;
    stop)
    stopHealthCheck
    stopCrawler
    ;;
    restart)
    restart
    ;;
    *)
    usage
    ;;
esac