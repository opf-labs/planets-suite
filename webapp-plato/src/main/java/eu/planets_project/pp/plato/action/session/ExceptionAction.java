/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package eu.planets_project.pp.plato.action.session;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;

import eu.planets_project.pp.plato.action.interfaces.IException;
import eu.planets_project.pp.plato.action.interfaces.IMessages;
import eu.planets_project.pp.plato.application.ErrorClass;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.User;
import eu.planets_project.pp.plato.util.PlatoLogger;

@Stateful
@Scope(ScopeType.SESSION)
@Name("exceptionHandler")
public class ExceptionAction implements IException, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5343905607776164599L;

    @In(required = false, create = true)
    @Out(scope = ScopeType.APPLICATION)
    private IMessages allmessages;

    private static Log log = PlatoLogger.getLogger(ExceptionAction.class);

    @In(required = false)
    private String body;

    @In(required = false)
    private String userEmail;

    @In(scope = ScopeType.SESSION, value = "org.jboss.seam.handledException", required = false)
    private Exception handledException;

    private Exception lastHandledException = null;

    //not sure if required should be set to false
    @In(required=false)
    private User user;

    @In(required = false)
    private Plan selectedPlan;

    @In
    private FacesContext facesContext;

    private DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private final static String separatorLine = "\n-------------------------------------------\n";

    @Remove
    @Destroy
    public void destroy() {
    }

    //RaiseEvent("exceptionHandled")
    public String handleError() {
        this.lastHandledException = handledException;
        String errorType = null;
        String errorMessage = null;
        if (handledException != null) {
            errorType = handledException.getClass().getCanonicalName();
            errorMessage = handledException.getMessage();
        }
        String id = "";
        try {
            id = ((HttpServletRequest) facesContext.getExternalContext()
                    .getRequest()).getSession().getId();
        } catch (RuntimeException e) {
            log.debug("Couldn't get SessionID");
        }

        allmessages.addErrorMessage(new ErrorClass(errorType, errorMessage, id,
                ((user==null)? "Unknown" : user.getUsername()), facesContext.getViewRoot().getViewId(),
                selectedPlan));
        return "bugreport";
    }

    @RaiseEvent("exceptionHandled")
    public String sendMail() {
        try {
            log.debug(body);
            Properties props = System.getProperties();
            Properties mailProps = new Properties();
            mailProps.load(ExceptionAction.class
                    .getResourceAsStream("/mail.properties"));
            props.put("mail.smtp.host", mailProps.getProperty("SMTPSERVER"));
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailProps.getProperty("FROM")));
            message.setRecipient(RecipientType.TO, new InternetAddress(
                    mailProps.getProperty("TO")));
            String exceptionType = "Unknown";
            String exceptionMessage = "";
            String stackTrace = "";

            String host = ((HttpServletRequest) facesContext
                    .getExternalContext().getRequest()).getLocalName();
            

            if (lastHandledException != null) {
                exceptionType = lastHandledException.getClass()
                        .getCanonicalName();
                exceptionMessage = lastHandledException.getMessage();
                StringWriter writer = new StringWriter();
                lastHandledException.printStackTrace(new PrintWriter(writer));
                stackTrace = writer.toString();
            }

            message.setSubject("[PlatoError] " + exceptionType + " at " + host);
            StringBuilder builder = new StringBuilder();
            builder.append("Date: " + format.format(new Date()) + "\n");
            builder.append("User: " + ((user==null)? "Unknown" : user.getUsername()) + "\n");
            builder.append("ExceptionType: " + exceptionType + "\n");
            builder.append("ExceptionMessage: " + exceptionMessage + "\n\n");

            builder.append("UserMail:" + separatorLine + this.userEmail
                    + separatorLine + "\n");
            builder.append("User Description:" + separatorLine + this.body
                    + separatorLine + "\n");
            builder.append(stackTrace);
            message.setText(builder.toString());
            message.saveChanges();
            Transport.send(message);
            this.lastHandledException = null;
            facesContext.addMessage( null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Bugreport sent.",
                            "Thank you for your feedback. We will try to analyse and resolve the issue as soon as possible."));
        } catch (Exception e) {
            log.debug(e.getMessage(),e);
            facesContext
                    .addMessage(
                            null,
                            new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    "Bugreport couldn't be sent",
                                    "Because of an enternal error your bug report couldn't be sent. We apologise for this and hope you are willing to inform us about this so we can fix the problem. "
                                            + "Please send an email to plato@ifs.tuwien.ac.at with a "
                                            + "description of what you have been doing at the time of the error." +
                                            		"Thank you very much!"));
            return null;
        }
        return "home";
    }
}
