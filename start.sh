#!/bin/sh
pkill java

if [ "x$JAVA_OPTS" = "x" ]; then
   JAVA_OPTS=" -Dcom.sun.management.jmxremote.port=9988                             
-Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"
else
   JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.port=9988                   
-Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"
fi

export CLASSPATH=.:dist/*
java -Xmx4500m \
     -Dfile.encoding=UTF-8 \
     -server server.Start
#     -Dcom.sun.management.jmxremote=true \
#     -Dcom.sun.management.jmxremote.port=13999 \
#     -Dcom.sun.management.jmxremote.authenticate=false \
#     -Dcom.sun.management.jmxremote.ssl=false \
#     -server server.Start
