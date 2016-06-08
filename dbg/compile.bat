cls
del dbg.class
javac -classpath ../lib/imq.jar;../lib/jms.jar dbg.java
java -cp ../lib/imq.jar;../lib/jms.jar;./ dbg
pause 0