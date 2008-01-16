/**
 * 
 */
package eu.planets_project.tb.gui.backing;

import org.apache.myfaces.custom.tree2.TreeNodeBase;

/**
 * @author AnJackson
 *
 */
public class CommentTreeNode extends TreeNodeBase {
    static final long serialVersionUID = 981263621092194234l;
    private String title;
    private String body;
    private String author;
    private String time;
    
    private void initComment() {
        this.setType("comment");
        this.setDescription(this.getTitle());
    }
        
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
        this.initComment();
    }
    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }
    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
        this.initComment();
    }
    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }
    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
        this.initComment();
    }
    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }
    /**
     * @param time the time to set
     */
    public void setTime(String time) {
        this.time = time;
        this.initComment();
    }
    
    

}
