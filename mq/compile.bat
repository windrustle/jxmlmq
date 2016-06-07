cls
del mq.class
javac -classpath ../lib/imq.jar;../lib/jms.jar mq.java
java -cp ../lib/imq.jar;../lib/jms.jar;./ mq
