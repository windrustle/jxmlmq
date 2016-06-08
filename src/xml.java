import com.sun.messaging.ConnectionFactory;
import com.sun.messaging.ConnectionConfiguration;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.MessageConsumer;
import javax.jms.StreamMessage;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.*;
import java.util.Scanner;

public class xml{

	public static Document readXML(File iFile){
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		Document document=null;
		try{
			factory=DocumentBuilderFactory.newInstance();
			builder=factory.newDocumentBuilder();
			document=builder.parse(iFile);
		}catch(Exception e){
			System.out.println("Reading XML failed.(o_O )");
			e.printStackTrace();
		}finally{}
		return document;
	}

	public static Document fillXML(Document document){
		//todo: fill from serialized object
		Node node;
		Element element;
		try{
			node=document.getElementsByTagName("Title").item(0);
			node.setTextContent("Hi there!");
			element=(Element)document.getElementsByTagName("Body").item(0);
			element.setAttribute("length","14");
			element=(Element)document.getElementsByTagName("SubParam").item(0);
			element.setAttribute("value","font:arial,14,italic");
			element=(Element)document.getElementsByTagName("SubParam").item(1);
			element.setAttribute("value","margin:0");
			node=document.getElementsByTagName("Text").item(0);
			node.setTextContent("From nowhere..");
			node=document.getElementsByTagName("Sign").item(0);
			element=(Element)node;
			element.setAttribute("on","true");
			node.setTextContent("Cya, pals!");
		}catch(Exception e){
			System.out.println("Filling XML failed.(o_O )");
			e.printStackTrace();
		}finally{}
		return document;
	}

	public static byte[] byteXML(Document document){
		TransformerFactory tFactory;
		Transformer transformer;
		DOMSource source;
		StreamResult result;
		ByteArrayOutputStream baos;
		byte[] array=null;
		try{
			tFactory=TransformerFactory.newInstance();
			transformer=tFactory.newTransformer();
			source=new DOMSource(document);
			baos=new ByteArrayOutputStream();
			result=new StreamResult(baos);
			transformer.transform(source,result);
			array=baos.toByteArray();
		}catch(Exception e){
			System.out.println("Converting XML failed.(o_O )");
			e.printStackTrace();
		}finally{}
		return array;
	}

	public static void writeXML(Document document,File oFile){
		TransformerFactory tFactory;
		Transformer transformer;
		DOMSource source;
		StreamResult result;
		try{
			tFactory=TransformerFactory.newInstance();
			transformer=tFactory.newTransformer();
			source=new DOMSource(document);
			result=new StreamResult(oFile);
			transformer.transform(source, result);
		}catch(Exception e){
			System.out.println("Writing XML failed.(o_O )");
			e.printStackTrace();
		}finally{}
		return;
	}

	public static Destination sendXML(byte[] array)throws Exception{
		ConnectionFactory cFactory;
		Connection connection=null;
		Session session=null;
		Destination destination=null;
		MessageProducer mProducer=null;
		StreamMessage sMessage;
		try{
			cFactory=new ConnectionFactory();
			cFactory.setProperty(ConnectionConfiguration.imqAddressList,"mq://127.0.0.1:7676");
			connection=cFactory.createConnection();
			session=connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			sMessage=session.createStreamMessage();
			destination=session.createTopic("local");
			mProducer=session.createProducer(destination);
			sMessage.writeObject(array);
			sMessage.reset();
			connection.start();
			mProducer.send(sMessage);
		}catch(Exception e){
			System.out.println("Sending XML failed.(o_O )");
			e.printStackTrace();
		}finally{
			if(connection!=null)connection.close();
			if(session!=null)session.close();
			if(mProducer!=null)mProducer.close();
		}
		return destination;
	}

	public static byte[] receiveXML(Destination destination)throws Exception{
		ConnectionFactory cFactory;
		Connection connection=null;
		Session session=null;
		MessageConsumer mConsumer=null;
		StreamMessage sMessage=null;
		Document document=null;
		byte[] array=null;
		try{
			cFactory=new ConnectionFactory();
			cFactory.setProperty(ConnectionConfiguration.imqAddressList,"mq://127.0.0.1:7676");
			connection=cFactory.createConnection();
			session=connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			mConsumer=session.createConsumer(destination);
			connection.start();
			while(!(sMessage instanceof StreamMessage)){
				sMessage=(StreamMessage)mConsumer.receive();
			}
			array=(byte[])sMessage.readObject();
		}catch(Exception e){
			System.out.println("Receiving XML failed.(o_O )");
			e.printStackTrace();
		}finally{
			if(connection!=null)connection.close();
			if(session!=null)session.close();
			if(mConsumer!=null)mConsumer.close();
		}
		return array;
	}

	public static Document unbyteXML(byte[] array){
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		InputStream iStream;
		Document document=null;
		ByteArrayInputStream bais;
		try{
			factory=DocumentBuilderFactory.newInstance();
			builder=factory.newDocumentBuilder();
			bais=new ByteArrayInputStream(array);
			iStream=(InputStream)bais;
			document=builder.parse(iStream);
			document.getDocumentElement().normalize();
		}catch(Exception e){
			System.out.println("Converting XML failed.(o_O )");
			e.printStackTrace();
		}finally{}
		return document;
	}

	public static void main(String[] args)throws Exception{
		byte[] array;
		Scanner inStr;
		String inFileName=".";
		String outFileName="";
		Document document=null;
		Destination destination;
		try{
			inStr=new Scanner(System.in);
			for(String arg:args){
				try{
					switch(arg.substring(0,2)){
						case "-h":
							System.out.println("Usage:\n-h\t this help;\n-i\tinput file or dir, if empty, used progdir by default;\n-o\toutput file or dir, if empty, used in file name\n");
							return;
						case "-i":
							inFileName=arg.substring(3);
							break;
						case "-o":
							outFileName=arg.substring(3);
							break;
					}
				}catch(Exception e){}
			}
			System.out.println("\nUse -h for more info");
			int i,j;
			String s;
			String[] fileListName;
			File inFile=new File(inFileName);
			if(!inFile.exists())inFile=new File(".");
			while(true){
				inFileName=inFile.getCanonicalPath()+'\\';
				fileListName=inFile.list();
				i=0;
				System.out.println("\n  0\t..");
				for(String st:fileListName){
					System.out.println("  "+(++i)+"\t"+st);
				}
				System.out.println("\nSelect (0"+((i==0)?"":"-"+(i))+"):");
				while(true){
					try{
						s=inStr.nextLine();
						j=Integer.parseInt(s);
						if(j>=0&&j<=i){break;}
					}catch(Exception e){}
						System.out.println("Incorrect input, try again");
				}
				inFileName+=(j==0)?"..":fileListName[j-1];
				inFile=new File(inFileName);
				System.out.println("\nSelected: '"+inFile.getName()+"'");
				if(inFile.isFile()){
					if(inFile.getName().endsWith("xml")){break;}
					inFile=new File(inFile.getParent());
					System.out.println(inFileName.substring(inFileName.length()-3)=="xml");
				}
			}
			File outFile=new File(outFileName);
			while(true){
				try{
					outFile.createNewFile();
					System.out.println("Writing file: '"+outFile.getName()+"'.");
					break;
				}catch(Exception e){
					System.out.println("Bad destination: '"+outFileName+"', set default.");
					outFileName=inFile.getCanonicalPath();
					outFile=new File(outFileName.substring(0,outFileName.length()-4)+".new.xml");
				}
			}
//writeXML(unbyteXML(recieveXML(sendXML(byteXML(fillXML(readXML(inFile)))))),outFile);
			document=readXML(inFile);
			document=fillXML(document);
			destination=sendXML(byteXML(document));
			array=receiveXML(destination);
			document=unbyteXML(array);
			writeXML(document,outFile);
		}catch(Exception e){
			System.out.println("Something goes wrong.(O_o )");
		}finally{}
		return;
	}
} 