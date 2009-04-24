/**
 * 
 */
package eu.planets_project.ifr.core.common.mail;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * @author AnJackson
 *
 */
public class PlanetsMailMessage {
    private Session session = null;
    private InternetAddress sender = null;
    private String subject = "";
    private String body = "";
    private List<InternetAddress> recipients = new ArrayList<InternetAddress>();
    private List<InternetAddress> ccRecipients = new ArrayList<InternetAddress>();
    private List<InternetAddress> bccRecipients = new ArrayList<InternetAddress>();

    /**
     * No arg constructor.
     */
    public PlanetsMailMessage() 
    {
        try {
            Context ctx = new InitialContext();
            session = (Session)ctx.lookup("java:Mail");
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    /**
     * @param sender
     */
    public void setSender(String sender) 
    {
        this.sender = makeInternetAddress(sender);
    }

    /**
     * @param subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    /**
     * @param body
     */
    public void setBody(String body) 
    {
        this.body = body;
    }

    /**
     * @param recipient
     */
    public void addRecipient(String recipient)
    {
        recipients.add(makeInternetAddress(recipient));
    }

    /**
     * @param recipients
     */
    public void setRecipients(List<InternetAddress> recipients) 
    {
        this.recipients.clear();
        this.recipients.addAll(recipients);
    }

    /**
     * @param recipient
     */
    public void addCcRecipient(String recipient) 
    {
        ccRecipients.add(makeInternetAddress(recipient));
    }

    /**
     * @param ccRecipients
     */
    public void setCcRecipients(List<InternetAddress> ccRecipients)
    {
        this.ccRecipients.clear();
        this.ccRecipients.addAll(ccRecipients);
    }

    /**
     * @param recipient
     */
    public void addBccRecipient(String recipient) 
    {
        bccRecipients.add(makeInternetAddress(recipient));
    }

    /**
     * @param bccRecipients
     */
    public void setBccRecipients(List<InternetAddress> bccRecipients)
    {
        this.bccRecipients.clear();
        this.bccRecipients.addAll(bccRecipients);
    }

    private static InternetAddress makeInternetAddress(String emailAddress)
    {
        InternetAddress internetAddress = null;
        try {
            internetAddress = new InternetAddress(emailAddress);
            internetAddress.validate();
        } catch (AddressException ae) {
            ae.printStackTrace();
            // throw new MailException(Mail.INVALID_ADDRESS_MSG + " " + emailAddress + ":\n" + ae);
        }
        return internetAddress;
    }

    @SuppressWarnings("unused")
	private static List<String> internetAddressesToStrings(List<InternetAddress> internetAddressRecipients) 
    {
        List<String> stringRecipients = new ArrayList<String>();
        Iterator<InternetAddress> internetAddressRecipientsIter = internetAddressRecipients.iterator();

        while (internetAddressRecipientsIter.hasNext()) 
        {
            InternetAddress internetAddress = (InternetAddress)internetAddressRecipientsIter.next();
            stringRecipients.add(internetAddress.toString());
        }

        return stringRecipients;
    }

    @SuppressWarnings("unused")
	private static List<InternetAddress> stringsToInternetAddresses(List<String> stringRecipients) 
    {
        List<InternetAddress> internetAddressRecipients = new ArrayList<InternetAddress>();
        Iterator<String> stringRecipientsIter = stringRecipients.iterator();

        while (stringRecipientsIter.hasNext()) 
        {
            String address = (String) stringRecipientsIter.next();
            internetAddressRecipients.add(makeInternetAddress(address));
        }
        return internetAddressRecipients;
    }

    /**
     * 
     */
    public void send() 
    {
        try {
            InternetAddress[] recipientsArr;
            recipientsArr = (InternetAddress[]) recipients.toArray(new InternetAddress[0]);
            MimeMessage msg = new MimeMessage(session);
            // Optionally override the sender.
            if( sender != null )
                msg.setFrom(sender);
            msg.setRecipients(Message.RecipientType.TO, recipientsArr);

            if (!ccRecipients.isEmpty()) 
            {
                        InternetAddress[] ccRecipientsArr;
                ccRecipientsArr = (InternetAddress[]) ccRecipients.toArray(new InternetAddress[0]);
                msg.setRecipients(Message.RecipientType.CC, ccRecipientsArr);
            }
            if (!bccRecipients.isEmpty()) 
            {
                        InternetAddress[] bccRecipientsArr;
                bccRecipientsArr = (InternetAddress[]) bccRecipients.toArray(new InternetAddress[0]);
                msg.setRecipients(Message.RecipientType.BCC, bccRecipientsArr);
            }

            msg.setSubject(subject);
            msg.setText(body);
            msg.setSentDate(new Date());

            Transport.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
