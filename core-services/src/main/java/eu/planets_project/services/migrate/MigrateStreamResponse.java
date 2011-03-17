/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
/**
 * 
 */
package eu.planets_project.services.migrate;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlMimeType;

import eu.planets_project.services.datatypes.Event;

/**
 * A stream-based response from migration services.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class MigrateStreamResponse {
    
    private Event event;
    @XmlMimeType("application/octet-stream")
    private DataHandler stream;
    /**
     * Create a stream-based migrate response.
     * @param event The event
     * @param stream The strem
     */
    public MigrateStreamResponse(Event event, DataHandler stream){
        this.event=event;
        this.stream=stream;
    }
    /**
     * @return the event
     */
    public Event getEvent() {
        return event;
    }
    /**
     * @return the stream
     */
    public DataHandler getStream() {
        return stream;
    }
    

}
