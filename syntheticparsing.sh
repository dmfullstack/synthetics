#!/bin/bash
cd /root/malay/java
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.91-3.b14.el6_8.x86_64
echo "Java Home is $JAVA_HOME"
export PATH=/usr/lib64/qt-3.3/bin:/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:/root/bin:/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.91-3.b14.el6_8.x86_64/bin:/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.91-3.b14.el6_8.x86_64
export CLASSPATH=.:..:$CLASSPATH:
echo "Path is is $PATH"
echo "CLASSPATH is is $CLASSPATH"
echo `date +"%Y%m%d %T"` >> /root/malay/cronjobs/syntheticsParsing.lst
$JAVA_HOME/bin/java syntheticsParsing >>  /root/malay/cronjobs/syntheticsParsing.lst
echo '*********************' >> /root/malay/cronjobs/syntheticsParsing.lst
echo "$JAVA_HOME/bin/java  /root/malay/cronjobs/syntheticsParsing"
ls -ltr
