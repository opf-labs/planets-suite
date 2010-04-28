/**
 * Copyright (c) 2007, 2008 The Planets Project Partners.
 * 
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * GNU Lesser General Public License v3 which 
 * accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/lgpl.html
 * 
 */
package eu.planets_project.tb.impl.model.eval;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.el.ELContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author anj
 *
 */
public class PropertyEvaluation {
    static private Log log = LogFactory.getLog(PropertyEvaluation.class);
    
    /** The property that has been evaluated */
    URI propertyUri = null;
    
    
    /* ---- User comparison evaluation ---- */
    
    /** This is the list of pre-supplied answers for the userEquivalenceDetail. String mappings
     * to 'Quite Similar' etc are held in UIResources.properties
     * TODO Consider simplifying or augmenting? For example There are two kinds of EQUAL:
     * there is precisely equal (same information in the same encoding), and there is equivalent
     * (same information in different encodings). All others are different/lossy.
     * When lossy, does Completely Different really mean Completely Lost/Missing?
     * There is also 'Supposed to be different', which applies e.g. to format identification under migration. 
     */
    public enum EquivalenceStatement {
        /* The users judges the property is equal across this comparison */
        EQUAL,
        /* The users judges the property is similar across this comparison */
        SIMILAR,
        /* The users judges the property is different across this comparison */
        DIFFERENT,
        /* The users judges the property is completely different across this comparison */
        NOT_EQUAL,
        /* The users find the property is missing on one side or the other. FIXME Same as NOT_EQUAL, i.e. complete loss. */
        MISSING,
        /* The users judges the property cannot be evaluated. FIXME Is this meaningful? */
        /*
        INCOMPARABLE,
        */
        /* The users judges that no such judgement should be made. */
        NOT_APPLICABLE,
    }
    
    /** For comparative measurements, records the Experimenter's opinion of the equivalence of the DOBs w.r.t this property. */
    private EquivalenceStatement userEquivalence = null;
    
    /** For comparative measurements, the Experimenter can record a more detailed statement about the equivalence. */
    private String userEquivalenceComment = "";


    /**
     * @param uri
     */
    public PropertyEvaluation(URI uri) {
        this.propertyUri = uri;
    }

    /**
     * @return the userEquivalence
     */
    public EquivalenceStatement getUserEquivalence() {
        return userEquivalence;
    }

    /**
     * @param userEquivalence the userEquivalence to set
     */
    public void setUserEquivalence(EquivalenceStatement userEquivalence) {
        this.userEquivalence = userEquivalence;
    }

    /**
     * @return the userEquivalenceComment
     */
    public String getUserEquivalenceComment() {
        return userEquivalenceComment;
    }

    /**
     * @param userEquivalenceComment the userEquivalenceComment to set
     */
    public void setUserEquivalenceComment(String userEquivalenceComment) {
        this.userEquivalenceComment = userEquivalenceComment;
    }

    /**
     * This is a bit horrible. Loading the resource bundle should work, but I can't get it to 
     * stick to the right locale, so hacking into the resource bundle via EL works better.
     * 
     * TODO Forget this, and just hardcode the types into the comparison page?
     * 
     * @return A list of select items corresponding to the different evaluation types.
     */
    public static List<SelectItem> getEquivalenceOptions() {
        // Build up select items:
        List<SelectItem> selects = new ArrayList<SelectItem>();
        for( EquivalenceStatement state : EquivalenceStatement.values() ) {
            selects.add(new SelectItem( state, lookupName(state) ));
        }
        return selects;
    }
    
    private static String lookupName( EquivalenceStatement state ) {
        try {
            ELContext elContext = FacesContext.getCurrentInstance().getELContext();
            // Load the resource bundle:
            ResourceBundle bundle = null;
            /*
            try {
                Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
                bundle = ResourceBundle.getBundle("eu.planets_project.tb.gui.UIResources", locale );
            } catch ( MissingResourceException e ) {
                log.error("Could not load resource bundle: "+e);
            }
            */
            Map map = (Map) elContext.getELResolver().getValue(elContext, null, "res");
            // Look up
            String label = state.toString();
            String key = "exp_stage5.evaluation."+label;
            String lookup = "res['"+key+"']";
            String name = (String) map.get(key);
            if( bundle != null ) label = bundle.getString(key);
            //log.info("For "+state+" got "+label+" and "+name);
            if( name != null ) label = name;
            return label;
        } catch( Exception e ) {
            log.error("Failure when looking up "+state+" :: "+e);
            return state.toString();
        }
    }
    
}
