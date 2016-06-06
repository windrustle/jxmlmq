package mgm.hellflamer.xml;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.jms.ConnectionFactory;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.MessageConsumer;
import javax.jms.StreamMessage;
import java.io.File;
import java.util.Scanner;

public class xml{

	private static int inCon (int min, int max){
		int i=0;
		String s="";
		boolean b=true;
		Scanner inStr=new Scanner(System.in);
		while(b){
			try{
				s=inStr.nextLine();
				i=Integer.parseInt(s);
			}catch(Exception e){i=-1;}
			if(i>=min&&i<=max){
				b=false;
			}
			else System.out.println("Incorrect input, try again");
		}
		return i;
	}

	public static Document readXML(File inFile){
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		Document document=null;
		try{
			factory=DocumentBuilderFactory.newInstance();
			builder=factory.newDocumentBuilder();
			document=builder.parse(inFile);
		}catch(Exception e){
			System.out.println("Reading XML failed.(O_o )");
			e.printStackTrace();
		}finally{}
		return document;
	}

	public static void writeXML(Document document,File outFile){
		TransformerFactory tFactory;
		Transformer transformer;
		DOMSource source;
		StreamResult result;
		try{
			tFactory=TransformerFactory.newInstance();
			transformer=tFactory.newTransformer();
			source=new DOMSource(document);
			result=new StreamResult(outFile);
			transformer.transform(source, result);
		}catch(Exception e){
			System.out.println("Writing XML failed.(O_o )");
			e.printStackTrace();
		}finally{}
		return;
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
			System.out.println("Filling XML failed.(O_o )");
			e.printStackTrace();
		}finally{}
		return document;
	}

	public static void sendXML(Document document,String sAddr)throws Exception{
		ConnectionFactory cFactory;
		Connection connection=null;
		Session session=null;
		Destination destination;
		MessageProducer mProducer=null;
		StreamMessage sMessage;
		try{
			cFactory=new com.sun.messaging.ConnectionFactory();
			connection=cFactory.createConnection();
			session=connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			sMessage=session.createStreamMessage();
			destination=session.createTopic(sAddr);
			mProducer=session.createProducer(destination);
			connection.start();
			sMessage.writeObject(document);
			mProducer.send(sMessage);
		}catch(Exception e){
			System.out.println("Sending XML failed.(O_o )");
			e.printStackTrace();
		}finally{
			if(connection!=null)connection.close();
			if(session!=null)session.close();
			if(mProducer!=null)mProducer.close();
		}
		return;
	}

	public static Document recieveXML(String sAddr) throws Exception{
		ConnectionFactory cFactory;
		Connection connection=null;
		Session session=null;
		Destination destination;
		MessageConsumer mConsumer=null;
		StreamMessage sMessage;
		Document document=null;
		try{
			cFactory=new com.sun.messaging.ConnectionFactory();
			connection=cFactory.createConnection();
			session=connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			sMessage=session.createStreamMessage();
			destination=session.createTopic(sAddr);
			mConsumer=session.createConsumer(destination);
			while(sMessage==null){
				sMessage=(StreamMessage)mConsumer.receive();
			}
			document=(Document)sMessage.readObject();
		}catch(Exception e){
			System.out.println("Recieving XML failed.(O_o )");
			e.printStackTrace();
		}finally{
			if(connection!=null)connection.close();
			if(session!=null)session.close();
			if(mConsumer!=null)mConsumer.close();
		}
		return document;
	}

	public static void main(String[] args) throws Exception{
		Document document;
		String inFileName=".";
		String outFileName=".";
		String sAddr=null;
		try{
			int minParts=2;
			int maxParts=9;
			int parts=0;
			int i;
			for(String arg:args){
				try{
					switch(arg.substring(0,2)){
						case "-h":
							System.out.println("Usage:\n\tSplitter [-h] [-i:input file or dir] [-o:output file or dir] [-n:number of parts]");
							return;
						case "-i":
							inFileName=arg.substring(3);
							break;
						case "-o":
							outFileName=arg.substring(3);
							break;
						case "-a":
							sAddr=arg.substring(3);
							break;
/*
больше свитчей при необходимости
*/
					}
				}catch(Exception e){e.printStackTrace();}
			}
			System.out.println("\nUse -h for more info");
			String[] fileListName;
			File inFile=new File(inFileName);
			if(!inFile.exists())inFile=new File(".");
			while(!inFile.isFile()){
				inFileName=inFile.getCanonicalPath()+'\\';
				fileListName=inFile.list();
				i=1;
				System.out.println("\n  0\t..");
				try{
					for(String s:fileListName){
						System.out.println("  "+(i++)+"\t"+s);
					}
				}catch(Exception e){e.printStackTrace();}
				System.out.println("\nSelect (0"+((i==1)?"":"-"+(i-1))+"):");
				i=inCon(0,i-1)-1;
				inFileName+=(i==-1)?"..":fileListName[i];
				inFile=new File(inFileName);
				System.out.println("\nSelected: '"+inFile.getName()+"'");
			}
			i=0;
			document=readXML(inFile);
			document=fillXML(document);
			if(sAddr==null)sAddr="nowhere";
			sendXML(document,sAddr);
			File outFile=new File(outFileName);
			if(!outFile.exists()){
				outFileName=inFile.getCanonicalPath();
				outFile=new File(outFileName);
			}
			if(!outFile.isFile())outFileName=outFile.getCanonicalPath()+'\\'+inFile.getName();
			parts=0;
			try{
				outFile=new File(outFileName+"."+parts++);
				outFile.createNewFile();
			}catch(Exception e){
				System.out.println("Bad destination: '"+outFileName+"', set default.");
				outFileName=inFile.getCanonicalPath();
				outFile=new File(outFileName+"."+parts++);
				outFile.createNewFile();
			}
			document=recieveXML(sAddr);
			writeXML(document,outFile);
		}catch(Exception e){
			System.out.println("Something goes wrong.(O_o )");
			e.printStackTrace();
		}finally{}
		return;
	}
} 