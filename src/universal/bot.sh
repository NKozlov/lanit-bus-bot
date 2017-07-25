#!/bin/bash
DIR=$(cd $(dirname "${BASH_SOURCE[0]}");pwd)
JAR="lanit-bus-bot-2.0-2.12.2.jar"
#JAVA_TZ="-Duser.timezone=Europe/Moscow"
ARGS="${JAVA_TZ} -Dconfig.file=${DIR}/conf/application.conf -Dlogback.configurationFile=${DIR}/conf/logback.xml"
START_CMD="java -Xms64m -Xmx128m ${ARGS} -jar ${DIR}/lib/${JAR}"

proc_alive () {
ps -ef | grep -q "ja[v]a.*${DIR}/lib/${JAR}"
}

get_pid () {
ps -ef | grep "ja[v]a.*${DIR}/lib/${JAR}" | cut -d" " -f2
} 

start_func () {
   if proc_alive
      then
         echo "Service is already running with PID $(get_pid)"
         exit 1
      else
         echo -ne "Starting service"
         cd ${DIR}
#         nohup ${START_CMD} &>> ${DIR}/logs/systemout.log &
         nohup ${START_CMD} &
         for i in {1..3}; do sleep 1; echo -ne '.'; done
         for i in {1..7}; do sleep 1; if ! proc_alive; then echo -ne '.'; else break; fi; done
         if proc_alive; then echo -e "\nService is started with PID $(get_pid)"; else echo -e "\nService start failed. Check log ${DIR}/logs/systemout.log ."; fi
   fi
}

stop_func () {
  if ! proc_alive; then echo "Service is already stopped."; return 1; fi

  echo -en "sending SIGTERM (15) "
  kill -15 $(get_pid)
  for i in {1..30}; do sleep 1; if proc_alive; then echo -ne '.'; else break; fi; done
  if ! proc_alive; then echo -e "\nService stop completed."; return 0; fi

  echo -en "\nsending SIGINT (2) "
  kill -2 $(get_pid)
  for i in {1..15}; do sleep 1; if proc_alive; then echo -ne '.'; else break; fi; done
  if ! proc_alive; then echo -e "\nService stop completed."; return 0; fi

  echo -en "\nsending SIGKILL (9) "
  kill -9 $(get_pid)
  for i in {1..5}; do sleep 1; if proc_alive; then echo -ne '.'; else break; fi; done
  echo -e "\nService stop completed."
}

status_func () {
   if proc_alive
      then
         echo -e "Service is running:\n$(ps -e -o pid,user,etime,args | grep "ja[v]a.*${DIR}/lib/${JAR}" | awk '{print "\tPID: "$1"\n\tuser: "$2"\n\telapsed: "$3;$1=$2=$3="";print "\tcommand: "$0}')"
         exit 0
      else
         echo "Service is stopped."
         exit 1
   fi
}

case "$1" in
start)
   start_func
   ;;
stop)
   stop_func
   ;;
restart)
   stop_func
   sleep 3
   start_func
   ;;
status)
   status_func
   ;;
*)
   echo "Usage: $0 [start|stop|restart|status]"
   exit 1
esac
