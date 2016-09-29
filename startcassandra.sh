date
service cassandra status
ps -ef | grep cassandra | grep -v grep | awk '{print $2}' | xargs kill
mv /var/run/cassandra/cassandra.pid /var/run/cassandra/cassandra.pid.old
service cassandra stop
sleep 4
ps -efl | grep cassandra
mv /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.91-3.b14.el6_8.x86_64/jre/lib/ext/guava-16.0.1.jar /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.91-3.b14.el6_8.x86_64/jre/lib/
service cassandra start
sleep 4
cp /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.91-3.b14.el6_8.x86_64/jre/lib/guava-16.0.1.jar  /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.91-3.b14.el6_8.x86_64/jre/lib/ext/
ps -efl | grep cassandra
