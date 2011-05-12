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
package eu.planets_project.pp.plato.converters;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import eu.planets_project.pp.plato.bean.FastTrackTemplate;
import eu.planets_project.pp.plato.bean.FastTrackTemplates;

@Name("fastTrackTemplateConverter")  
@Scope(ScopeType.STATELESS)  
@Install(precedence = FRAMEWORK)  
@org.jboss.seam.annotations.faces.Converter  
@BypassInterceptors  
public class FastTrackTemplateConverter  implements Converter, Serializable {
    
    private static final long serialVersionUID = 5134289952432499559L;
    
    @In(create = true)
    private FastTrackTemplates fastTrackTemplates;
    
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        
        List<FastTrackTemplate> list = null;
        for (int i =0 ; i < component.getChildCount()-1; i++) {
            if (component.getChildren().get(i) instanceof UIParameter) {
                UIParameter param = (UIParameter)component.getChildren().get(i);
                if ("templateList".equals(param.getName())) {
                    list = (List<FastTrackTemplate>)param.getValue();
                }
            }
        }
        
        for (FastTrackTemplate ftt : list) {
            if (value.equals(ftt.getDisplayString())) {
                return ftt;
            }
        }
        
        return null; 
    }
    
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        
        if (value instanceof FastTrackTemplate) {
            return ((FastTrackTemplate)value).getDisplayString();
        }
        
        return "";
    }

}
