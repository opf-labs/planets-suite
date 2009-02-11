package eu.planets_project.ifr.core.wee.impl;

import static org.junit.Assert.assertEquals;

import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;
 
/* 
 * This class is used to bootstrap the ejb3 container. It find things by ClassLoader.getResource 
 * and will load by default the embedded-jbosss-beans.xml file and the ejb3-interceptors-aop.xml file.
 * This is usually only used in standalone Java programs or Junit tests. 
*/
//import org.jboss.ejb3.embedded.EJB3StandaloneBootstrap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
 
public class MessagingTest {
 
    @BeforeClass
    public static void initContainer() {
        //we assume JBoss already started and instantiated
    	//JBossUtil.startDeployer();
    }
 
    /*@Before
    public void createAccount() {
    }
 
    private AccountInventory getAccountInventory() {
        return JBossUtil.lookup(AccountInventory.class,
                "AccountInventoryBean/local");
    }
 
    @After
    public void removeAccount() {
        getAccountInventory().removeAccountById(account.getId());
    }
 
    @AfterClass
    public static void cleanupContainer() {
        EJB3StandaloneBootstrap.shutdown();
    }*/
 
    public static InitialContext getInitialContext() throws NamingException {
        return new InitialContext();
    }
 
    @Test
    public void addCharge() throws Exception {
        /*final Queue queue = (Queue) getInitialContext().lookup("queue/tolltag");
        final QueueConnectionFactory factory = 
                  (QueueConnectionFactory) getInitialContext()
                  .lookup("java:/ConnectionFactory");
        final QueueConnection connection = factory.createQueueConnection();
        final QueueSession session = connection.createQueueSession(false,
                QueueSession.AUTO_ACKNOWLEDGE);
 
        MapMessage message = session.createMapMessage();
        message.setString("tollTagNumber", TAG_NUMBER);
        message.setDouble("amount", .5d);
 
        final QueueSender sender = session.createSender(queue);
        sender.send(message);
 
        Thread.sleep(1000);
        session.close();
        connection.close();
 
        assertEquals(.5d, getAccountInventory().getTotalChargesOnAccountById(
                account.getId()));
                */
    }
}