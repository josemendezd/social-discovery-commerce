package models;
 
import java.util.*;
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

import play.data.validation.*;
 
@Entity
public class CollectionComment extends Model {
 
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
    public UserCollection collection;
    
    public CollectionComment(UserCollection collection, Contributor author, String content) {
        this.collection = collection;
        this.author = author;
        this.content = content;
        this.postedAt = new Date();
        this.save();
    }
    
    public String toString() {
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }
    
    public static Model.Finder<Long,CollectionComment> find = new Finder<Long, CollectionComment>(Long.class, CollectionComment.class);
    
    public static CollectionComment AddComment(UserCollection collection, Contributor author, String content)
    {
    	return new CollectionComment(collection, author, content);
    }
    
    public static boolean RemoveComment(CollectionComment cmnt,Contributor contrib)
    {
    	
    	if(cmnt==null || !cmnt.author.equals(contrib) )
    		return false;
    	models.Notifications.SOCommentsCo.Deleteinstanceofcomment(cmnt);
    	cmnt.delete();
    	return true;
    }
    
    public static List<CollectionComment> CollectionCommentPage(UserCollection collection) {        
        return	find.where().eq("collection.id", collection.id).orderBy("postedAt ASC").findList();
    }
    
    public static String CollectionCommentPageJSON(UserCollection collection) {        
    	String query="SELECT string_agg(row_to_json(data):: text,',') AS LIST  FROM ( SELECT    users.name un,    users.profileimage ui,    collection_comment.id cid,    collection_comment.author_id uid,    collection_comment.posted_at  AT TIME ZONE current_setting('TIMEZONE') cd,    collection_comment.content ct FROM    public.contributor,    public.users,    public.collection_comment WHERE  users.active = true AND  contributor.user_id = users.id AND   collection_comment.author_id = contributor.id AND   collection_comment.collection_id = "+collection.id+" ORDER BY   collection_comment.posted_at ASC   )  data(un,ui,cid,uid,cd,ct)";
		return Ebean.createSqlQuery(query).findUnique().getString("LIST");
    }
 
}