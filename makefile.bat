javac -cp lib/jms.jar;lib/imq.jar -d bin/ src/xml.java
jar cfm xml.jar MANIFEST.MF -C bin mgm/hellflamer/xml/xml.class
java -jar xml.jar