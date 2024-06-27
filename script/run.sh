PROFILE="${SERVICE_PROFILE:=server}"
java -Xmx500M -Xms312M -Dspring.profiles.active=$PROFILE -jar jar/* "$@" &
echo $! >pid
