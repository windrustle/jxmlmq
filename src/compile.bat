cls
del xml.class
javac -classpath ../lib/imq.jar;../lib/jms.jar xml.java
java -cp ../lib/imq.jar;../lib/jms.jar;./ xml
pause 0
