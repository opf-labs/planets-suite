package eu.planets_project.tb.gui.backing;


import java.io.IOException;
import javax.faces.component.html.HtmlInputSecret;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;


/**
 * Backing bean for login.jsp
 */


public class Login {

  private HtmlInputText userid;
  private HtmlInputSecret password;


  public Login() {
  }


  public String login() {
	  return null;
  }


    public String logout() throws IOException{    
      ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
      HttpSession session = (HttpSession)ectx.getSession(false);
      session.invalidate();        
      return "success";    
    }


  public void setUserid(HtmlInputText userid) {
    this.userid = userid;
  }


  public HtmlInputText getUserid() {
    return userid;
  }


  public void setPassword(HtmlInputSecret password) {
    this.password = password;
  }


  public HtmlInputSecret getPassword() {
    return password;
  }


}
