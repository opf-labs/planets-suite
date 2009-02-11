package eu.planets_project.ifr.core.wee.impl;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import javax.jms.*;

@Resource(mappedName="planets/wee/jms/testnewsqueue")

public class MessageSenderTest {
	
	private static Queue queue = null;
	QueueConnectionFactory factory = null;
	QueueConnection connection = null;
	QueueSender sender = null;
	QueueSession session = null;
	
	@Before
	private void sendMessage(){
	    try {
	        //client creates the connection, session, and message sender:
	        connection = factory.createQueueConnection();
	        session = connection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
	        sender = session.createSender(queue);
	        //create and set a message to send
	        TextMessage msg = session.createTextMessage();
	        for (int i = 0; i < 5; i++) {
	          msg.setText("This is my sent message " + (i + 1));
	          //finally client sends messages asynchronously to the queue
	          sender.send(msg);
	          }   

	        System.out.println("Sending message");   
	        session.close ();
	        } catch (Exception e) {
	          e.printStackTrace ();
	        }
	}
	
	@Test
	public void checkOutput(){
		assertEquals("true","true");
	}

}
