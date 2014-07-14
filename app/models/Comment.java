package models;
 
import java.util.*;

import javax.management.Notification;
import javax.persistence.*;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import javax.persistence.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;

import controllers.DInitial;
import play.data.validation.*;
 
@Entity
public class Comment extends Model {
 
	@Id
	@GeneratedValue
	public Long id;
	
    @Required
    @ManyToOne
    public Contributor author;
    
    @Required
    public Date postedAt;
     
    
    @Required
    public String content;
    
    @ManyToOne
    @Required
    public Product post;
    
    public Comment(Product post, Contributor author, String content) {
        this.post = post;
        this.author = author;
        this.content = content;
        this.postedAt = new Date();
        this.save();
    }
    
    public String toString() {
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }
    
    public static Model.Finder<Long,Comment> find = new Finder<Long, Comment>(Long.class, Comment.class);
    
    public static Comment AddComment(Product post, Contributor author, String content)
    {
    	return new Comment(post, author, content);
    }
    
    public static boolean RemoveComment(Comment cmnt,Contributor contrib)
    {
    	
    	if(cmnt==null || !cmnt.author.equals(contrib) )
    		return false;
    	models.Notifications.SOCommentsPr.Deleteinstanceofcomment(cmnt);
    	cmnt.delete();
    	return true;
    }
    
    public static List<Comment> ProductCommentPage(Product p) {        
        return	find.where().eq("post.id", p.id).orderBy("postedAt ASC").findList();
    }
    
    public static String ProductCommentPageJSON(Product p) {        
    	String query="SELECT string_agg(row_to_json(data):: text,',') AS LIST  FROM ( SELECT    users.name un,     getimageurl("+String.format("'%s',users.profileimage,'%s','%s'", User.class.getSimpleName(),DInitial.IMAGESTORESIZE.THUMBNAIL_VERYSMALL.filestate,S3File.getbaseurl())+")   ui,    comment.id cid,    comment.author_id uid,    comment.posted_at  AT TIME ZONE current_setting('TIMEZONE') cd,    comment.content ct FROM    comment,    contributor,    users WHERE users.active = true AND  contributor.id = comment.author_id AND   users.id = contributor.user_id AND   comment.post_id = "+p.id+" ORDER BY   comment.posted_at ASC   )  data(un,ui,cid,uid,cd,ct)";
		return Ebean.createSqlQuery(query).findUnique().getString("LIST");
    }
 
}