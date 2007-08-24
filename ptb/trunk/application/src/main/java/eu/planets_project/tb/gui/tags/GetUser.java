
package eu.planets_project.tb.gui.tags;

import javax.servlet.jsp.JspException;
import com.sun.facelets.tag.*;
import javax.servlet.http.*;
import com.sun.facelets.FaceletContext;
import javax.faces.component.UIComponent;
import javax.faces.FacesException;
import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.gui.util.JSFUtil;

import org.apache.commons.logging.Log;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import java.io.IOException;
import javax.el.ELException;
import javax.faces.context.FacesContext;

public final class GetUser extends TagHandler {

	private Log log = PlanetsLogger.getLogger(this.getClass(),"testbed-log4j.xml");
  

   /**
   * @param config
   */
  public GetUser(TagConfig config) {
      super(config);
  }

  /**
   */
  public void apply(FaceletContext ctx, UIComponent parent)
          throws IOException, FacesException, ELException {
     		log.debug("in Tag: GetUser!");		
     		FacesContext context = ctx.getFacesContext().getCurrentInstance();
  			//Object session = context.getExternalContext().getSession(false);
  			//if (session != null) {
  				UserBean user = (UserBean)JSFUtil.getManagedObject("UserBean");
  				//UserBean user = (UserBean)(((HttpSession)session).getAttribute("userBean"));
	     		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
	     		if (user.getUserid() == null) {
	     			String userId = request.getRemoteUser();
	     			// JAAS Login before?
	     			if (userId != null) {        		
	     				//user = new UserBean();
	     				user.setUserid(userId);       
	     				//((HttpSession)session).setAttribute("userBean",user);
	     			}
	     		}  			
	     		log.debug("user is: " + user.getUserid());
  			//}
  	}
}
