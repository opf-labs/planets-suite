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
package eu.planets_project.pp.plato.util.jsf;
  
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Collection;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
  
/** 
 * Supports conversion of an object to/from an object
 * Two params have to specified in the parent component (e.g. h:selectOneMenu)
 * f:param name="collection" value="collectionBeanName"
 * f:param name="identifier" value="identifierName" 
 * 
 * @author kraxner
 */  
@Name("eu.planets_project.pp.plato.util.jsf.SelectableItemConverter")  
@Scope(ScopeType.STATELESS)  
@Install(precedence = FRAMEWORK)  
@Converter  
@BypassInterceptors  
public class SelectableItemConverter implements Serializable, javax.faces.convert.Converter {  
    /**
     * 
     */
    private static final long serialVersionUID = -9198051270216125645L;
    private Log log = LogFactory.getLog(SelectableItemConverter.class);  

    /**
     * retrieves the collection with all possible values from the session context, 
     * using the value of UIParameter with name "collection" from <param>component</param> 
     *   
     * @param ctx
     * @param component
     * @return
     */
    private Collection retrieveCollection(FacesContext ctx, UIComponent component)
    {
        String collectionBean = null;
        for (int i =0 ; i < component.getChildCount()-1; i++) {
            if (component.getChildren().get(i) instanceof UIParameter) {
                UIParameter param = (UIParameter)component.getChildren().get(i);
                if ("collection".equals(param.getName())) {
                    collectionBean = (String)param.getValue();
                }
            }
        }
        if (collectionBean != null) {
           Object c = ctx.getExternalContext().getSessionMap().get(collectionBean);
            if (c instanceof Collection) {
                return (Collection)c;
            }
        }
        return null;
    }

    /**
     * retrieves the name of identifier attribute, 
     * using the value of UIParameter with name "identifier" from <param>component</param>
     * @param ctx
     * @param component
     * @return
     */
    public String retrieveIdentifier(FacesContext ctx, UIComponent component) {
        for (int i = 0 ; i < component.getChildCount(); i++) {
            if (component.getChildren().get(i) instanceof UIParameter) {
                UIParameter param = (UIParameter)component.getChildren().get(i);
                if ("identifier".equals(param.getName())) {
                    return (String)param.getValue();
                }
            }
        }

        return null;
    }
    public Object getAsObject(FacesContext ctx, UIComponent component, String s) {  
        if (s == null)  
            return null;

        String ident = retrieveIdentifier(ctx, component);
        
        Collection col = retrieveCollection(ctx, component);
        if (col != null) {  
            for (Object item : col) {  
                String id = getItemIdentifier(item, ident);  
  
                if (id != null && id.equals(s)) {  
                    return item;  
                }  
            }  
        }  
  
        return null;  
    }  
  
    public String getAsString(FacesContext ctx, UIComponent component, Object o) {
        String ident = retrieveIdentifier(ctx, component);
        return getItemIdentifier(o, ident);  
    }  
  
    protected String getItemIdentifier(Object o, String property) {  
        PropertyDescriptor desc;  
        Object result;  
  
        try {  
            desc = new PropertyDescriptor(property, o.getClass());  
            result = desc.getReadMethod().invoke(o);  
  
            return result.toString();  
        } catch (Throwable e) {  
            log.error("Unable to get object identifier!", e);  
        }  
  
        return null;  
    }    
} 