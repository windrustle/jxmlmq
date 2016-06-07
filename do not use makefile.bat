javac -cp lib/jms.jar;lib/imq.jar src/xml.java
jar cvfe xml.jar xml -C src xml.class
java -jar -cp lib;. xml.jar
pause 0