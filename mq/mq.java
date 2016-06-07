import com.sun.messaging.ConnectionFactory;
import com.sun.messaging.ConnectionConfiguration;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;
import javax.jms.JMSException;

public class mq{

	public static void startMQ()throws JMSException{
		ConnectionFactory cFactory = new ConnectionFactory();           
		cFactory.setProperty(ConnectionConfiguration.imqAddressList,"mq://127.0.0.1:7676");
		Connection connection=cFactory.createConnection();
		Session session=connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
		Destination destination=session.createTopic("something");
		MessageProducer producer=session.createProducer(destination);
		MessageConsumer consumer=session.createConsumer(destination);
		TextMessage tMessage=session.createTextMessage();
		connection.start();
		tMessage.setText("Text message");
		producer.send(tMessage);
		tMessage.setText("Old message");
		tMessage=(TextMessage)consumer.receive(1000*10);
		System.out.println(tMessage.getText());
	}

	public static void main(String[] args)throws JMSException{
		startMQ();
	}
}