//jxmlmq by Hellflamer/mgm
//todo причесать, разбить на процедуры..
//links
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

public class dbg{
	public static void main(String[] args){
		//открывашки
		String dest;
		TransformerFactory tFactory;
		Transformer transformer;
		DOMSource source;
		StreamResult result;
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		Document document;
		Node node;
		Element element;
		ConnectionFactory cFactory;
		Connection connection;
		Session session;
		Destination destination;
		MessageProducer mProducer;
		MessageConsumer mConsumer;
		StreamMessage sMessage;
		
		try{
			//адрес сервера тут должен быть
			dest = "nowhere";
			//инициализация
			cFactory = new com.sun.messaging.ConnectionFactory();
			connection = cFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			sMessage = session.createStreamMessage();
			destination = session.createTopic(dest);
			mProducer = session.createProducer(destination);
			factory=DocumentBuilderFactory.newInstance();
			builder=factory.newDocumentBuilder();
			tFactory=TransformerFactory.newInstance();
			transformer=tFactory.newTransformer();
			//берём шаблон
			document=builder.parse(new File("data/dbg.xml"));
			//заполняем
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
			//соединяемся
			connection.start();
			//пихаем х№емэль
			sMessage.writeObject(document);
			mProducer.send(sMessage);
			//немаловероятно что ждём ответ
			sMessage = null;
			mConsumer = session.createConsumer(destination);
			while (sMessage==null) {
				sMessage = (StreamMessage) mConsumer.receive();
			}
			//выдираем хмл и пишем
			document = (Document) sMessage.readObject();
			source=new DOMSource(document);
			result=new StreamResult(new File("data/dbg2.xml"));
			transformer.transform(source, result);
		}catch(Exception e){
			System.out.println("Something goes wrong.(O_o )");
			e.printStackTrace();
		}finally{
			//todo закрывашки
		}
		return;
	}
}
