RUN_AS_USER=athena
PID_FILE=/opt/athena/run/athena.pid

exec start-stop-daemon --start --user $RUN_AS_USER  --pidfile $PID_FILE --make-pidfile --background --chdir /opt/athena/bin --exec /usr/bin/java -- -Dport=8080 -XX:MaxPermSize=200m -cp /opt/athena/config -jar /opt/athena/bin/runner.jar