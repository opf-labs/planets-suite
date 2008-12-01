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
    private InternetAddress sender = new InternetAddress();
    private String subject = new String();
    private String body = new String();
    private List recipients = new ArrayList();
    private List ccRecipients = new ArrayList();
    private List bccRecipients = new ArrayList();

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

    public void setSender(String sender) 
    {
        this.sender = makeInternetAddress(sender);
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public void setBody(String body) 
    {
        this.body = body;
    }

    public void addRecipient(String recipient)
    {
        recipients.add(makeInternetAddress(recipient));
    }

    public void setRecipients(List recipients) 
    {
        this.recipients.clear();
        this.recipients.addAll(recipients);
    }

    public void addCcRecipient(String recipient) 
    {
        ccRecipients.add(makeInternetAddress(recipient));
    }

    public void setCcRecipients(List ccRecipients)
    {
        this.ccRecipients.clear();
        this.ccRecipients.addAll(ccRecipients);
    }

    public void addBccRecipient(String recipient) 
    {
        bccRecipients.add(makeInternetAddress(recipient));
    }

    public void setBccRecipients(List bccRecipients)
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

    private static List internetAddressesToStrings(List internetAddressRecipients) 
    {
        List stringRecipients = new ArrayList();
        Iterator internetAddressRecipientsIter = internetAddressRecipients.iterator();

        while (internetAddressRecipientsIter.hasNext()) 
        {
            InternetAddress internetAddress = (InternetAddress)internetAddressRecipientsIter.next();
            stringRecipients.add(internetAddress.toString());
        }

        return stringRecipients;
    }

    private static List stringsToInternetAddresses(List stringRecipients) 
    {
        List internetAddressRecipients = new ArrayList();
        Iterator stringRecipientsIter = stringRecipients.iterator();

        while (stringRecipientsIter.hasNext()) 
        {
            String address = (String) stringRecipientsIter.next();
            internetAddressRecipients.add(makeInternetAddress(address));
        }
        return internetAddressRecipients;
    }

    public void send() 
    {
        try {
            InternetAddress[] recipientsArr;
            recipientsArr = (InternetAddress[]) recipients.toArray(new InternetAddress[0]);
            MimeMessage msg = new MimeMessage(session);
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
