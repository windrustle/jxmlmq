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

public class dbg{

	public static void main(String[] args)throws Exception{
//open
		Scanner inStr;
		String inFileName=".";
		String outFileName="";
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		Document document=null;
		Node node;
		Element element;
		TransformerFactory tFactory;
		Transformer transformer;
		DOMSource source;
		StreamResult result;
		InputStream iStream;
		ByteArrayInputStream bais;
		ByteArrayOutputStream baos;
		byte[] array;
		ConnectionFactory cFactory;
		Connection connection=null;
		Session session=null;
		Destination destination;
		MessageProducer mProducer=null;
		MessageConsumer mConsumer=null;
		StreamMessage sMessage=null;
		try{
//init
			inStr=new Scanner(System.in);
			factory=DocumentBuilderFactory.newInstance();
			builder=factory.newDocumentBuilder();
			tFactory=TransformerFactory.newInstance();
			transformer=tFactory.newTransformer();
			cFactory=new ConnectionFactory();
			cFactory.setProperty(ConnectionConfiguration.imqAddressList,"mq://127.0.0.1:7676");
			connection=cFactory.createConnection();
			session=connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			destination=session.createTopic("local");
			mProducer=session.createProducer(destination);
			mConsumer=session.createConsumer(destination);
			sMessage=session.createStreamMessage();
			connection.start();
//parse args
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
//select input file
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
//select output file
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
//read
			document=builder.parse(inFile);
//fill
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
//convert
			source=new DOMSource(document);
			baos=new ByteArrayOutputStream();
			result=new StreamResult(baos);
			transformer.transform(source,result);
			array=baos.toByteArray();
//send
			sMessage.writeObject(array);
			mProducer.send(sMessage);
//receive
			while(!(sMessage instanceof StreamMessage)){
				sMessage=(StreamMessage)mConsumer.receive();
			}
			array=(byte[])sMessage.readObject();
			bais=new ByteArrayInputStream(array);
			iStream=(InputStream)bais;
			document=builder.parse(iStream);
			document.getDocumentElement().normalize();
//write
			source=new DOMSource(document);
			result=new StreamResult(outFile);
			transformer.transform(source, result);
		}catch(Exception e){
			System.out.println("Something goes wrong.(O_o )");
		}finally{
			if(connection!=null)connection.close();
			if(session!=null)session.close();
			if(mConsumer!=null)mConsumer.close();
			if(mProducer!=null)mProducer.close();
		}
		return;
	}
} 
