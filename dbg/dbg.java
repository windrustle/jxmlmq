//jxmlmq by Hellflamer/mgm

import com.sun.messaging.ConnectionFactory;
import com.sun.messaging.ConnectionConfiguration;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.MessageConsumer;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class dbg{

	public static Document readXML(String iFile){
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		Document document=null;
		try{
			factory=DocumentBuilderFactory.newInstance();
			builder=factory.newDocumentBuilder();
			document=builder.parse(new File(iFile));
		}catch(Exception e){
			System.out.println("Reading XML failed.(o_O )");
			e.printStackTrace();
		}finally{}
		return document;
	}

	public static void writeXML(Document document,String oFile){
		TransformerFactory tFactory;
		Transformer transformer;
		DOMSource source;
		StreamResult result;
		try{
			tFactory=TransformerFactory.newInstance();
			transformer=tFactory.newTransformer();
			source=new DOMSource(document);
			result=new StreamResult(new File(oFile));
			transformer.transform(source, result);
		}catch(Exception e){
			System.out.println("Writing XML failed.(o_O )");
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
			System.out.println("Filling XML failed.(o_O )");
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
			cFactory=new ConnectionFactory();
			cFactory.setProperty(ConnectionConfiguration.imqAddressList,"mq://127.0.0.1:7676");
			connection=cFactory.createConnection();
			session=connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			sMessage=session.createStreamMessage();
			destination=session.createTopic(sAddr);
			mProducer=session.createProducer(destination);
			sMessage.writeObject("an object");
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
		return;
	}

	public static Document receiveXML(String sAddr) throws Exception{
		ConnectionFactory cFactory;
		Connection connection=null;
		Session session=null;
		Destination destination;
		MessageConsumer mConsumer=null;
		StreamMessage sMessage;
		Document document=null;
		try{
			cFactory=new ConnectionFactory();
			cFactory.setProperty(ConnectionConfiguration.imqAddressList,"mq://127.0.0.1:7676");
			connection=cFactory.createConnection();
			session=connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			sMessage=session.createStreamMessage();
			destination=session.createTopic(sAddr);
			mConsumer=session.createConsumer(destination);
			connection.start();
			sMessage=(StreamMessage)mConsumer.receive(1000*10);
			if(sMessage!=null){
				document=(Document)sMessage.readObject();
			}else{
				System.out.println("Nothing to receive.(o_O )");
			}
		}catch(Exception e){
			System.out.println("Receiving XML failed.(o_O )");
			e.printStackTrace();
		}finally{
			if(connection!=null)connection.close();
			if(session!=null)session.close();
			if(mConsumer!=null)mConsumer.close();
		}
		return document;
	}

	public static void main(String[] args){
		String sAddr="local", iFile="../data/dbg.xml", oFile="../data/dbg2.xml";
		Document document;
		try{
			document=readXML(iFile);
			document=fillXML(document);
			sendXML(document,sAddr);
			document=receiveXML(sAddr);
			writeXML(document,oFile);
		}catch(Exception e){
			System.out.println("Something goes wrong.(o_O )");
			e.printStackTrace();
		}finally{}
		return;
	}
}
