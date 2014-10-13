package models.Notifications;
 
import java.util.*;
import java.util.concurrent.Callable;

import javax.persistence.*;
 


import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import javax.persistence.*;

import org.hibernate.validator.constraints.URL;

import models.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.annotation.CreatedTimestamp;

import controllers.Application;
import controllers.GHelp;
import controllers.routes;

import play.data.validation.*;
import play.i18n.Messages;
import providers.MyMadMimiMailer;
import scala.concurrent.ExecutionContext;
 
@Entity
@Table(name="socommentsco")
public class SOCommentsCo extends Model {
 
	@Id
	@GeneratedValue
	public Long id;
	
    @Required
    @ManyToOne
    public Contributor commenter;
    
    //@Required
    @ManyToOne
    public UserCollection collection;
    
    @Required
    @ManyToOne
    public CollectionComment comment;
    
    @CreatedTimestamp
    public Date moment;    
    
    public SOCommentsCo(Contributor commenter, UserCollection collection, CollectionComment comment) {
        this.commenter = commenter;
        this.collection = collection;
        this.comment = comment;
        this.save();
    }

    public static void DispatchMessage(SOCommentsCo sc){
       	/**
    	GHelp.mailsender.sendMail(Messages.get("boozology.notifications.socommentsco",sc.commenter.user.name,sc.collection.colname),
    			new com.feth.play.module.mail.Mailer.Mail.Body(
    					views.html.Templates.MailTemplates.collectioncomment.render(sc.collection.contributor.user.name,sc.commenter.user.name,sc.collection.colname,Application.WebAddress+routes.Application.CollectionPage(sc.collection.id, false).url(),sc.comment.content).toString(),
    					views.txt.Templates.MailTemplates.collectioncomment.render(sc.collection.contributor.user.name,sc.commenter.user.name,sc.collection.colname,Application.WebAddress+routes.Application.CollectionPage(sc.collection.id, false),sc.comment.content).toString()
    					),
    			GHelp.getEmailName(sc.collection.contributor.user.email, sc.collection.contributor.user.name));
    	
    	**/
    	
    	
    	Map<String, String> map = new HashMap<String, String>();
    	map.put("commenter", sc.commenter.user.getNameNotEmpty());
    	map.put("collection", sc.collection.colname);
    	map.put("url", Application.WebAddress+routes.Application.CollectionPage(sc.collection.id, false).url());
    	map.put("comment", sc.comment.content);
    	
    	try {
			MyMadMimiMailer.sendMyNotification(map, sc.collection.contributor.user, "CommentsCo");//Not working
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static Model.Finder<Long,SOCommentsCo> find = new Finder<Long, SOCommentsCo>(Long.class, SOCommentsCo.class);   
    
    
    public static SOCommentsCo ReportEvent(Contributor commenter, UserCollection collection, CollectionComment comment) 
    {
    	SOCommentsCo scc=new SOCommentsCo(commenter, collection, comment);
    	ComputeTask(scc);
    	return scc;
    }
    
    public static int Deleteinstanceofcomment(CollectionComment cmnt)
    {
    	return Ebean.createSqlUpdate("delete from SOCommentsCo where comment_id=:cmntid").setParameter("cmntid", cmnt.id).execute();
    }
    
    public static boolean DeleteEvent(SOCommentsCo scc) 
    {
    	scc.delete();
    	return true;
    }

    public static void ComputeTask(SOCommentsCo sc) 
    {
    	SOCommentsCoDispatcher.ComputeTask(sc);
    }
    
 
}