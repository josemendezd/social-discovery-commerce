package models.Notifications;
 
import java.util.*;
import java.util.concurrent.Callable;

import javax.persistence.*;
 


import play.Logger;
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
import scala.concurrent.ExecutionContext;
 
@Entity
@Table(name="socommentspr")
public class SOCommentsPr extends Model {
 
	@Id
	@GeneratedValue
	public Long id;
	
	@Required
    @ManyToOne
    public Contributor commenter;
    
    @Required
    @ManyToOne
    public Product product;
    
    @Required
    @ManyToOne
    public Comment comment;
    
    @CreatedTimestamp
    public Date moment;    
    
    public SOCommentsPr(Contributor commenter, Product product, Comment comment) {
        this.commenter = commenter;
        this.product = product;
        this.comment = comment;
        this.save();
    }

    public static void DispatchMessage(SOCommentsPr sp){
    	GHelp.mailsender.sendMail(Messages.get("boozology.notifications.socommentspr",sp.commenter.user.name,sp.product.productname),
    			new com.feth.play.module.mail.Mailer.Mail.Body(views.html.Templates.MailTemplates.productcomment
    					.render(sp.product.Founder.name,sp.commenter.user.name,sp.product.productname,Application.WebAddress+routes.Application.ProductPage(sp.product.id, false).url(),sp.comment.content)
    					.toString(),views.txt.Templates.MailTemplates.productcomment
    					.render(sp.product.Founder.name,sp.commenter.user.name,sp.product.productname,Application.WebAddress+routes.Application.ProductPage(sp.product.id, false).url(),sp.comment.content)
    					.toString()),
    			GHelp.getEmailName(sp.product.Founder.email, sp.product.Founder.name));
    }
    
    public static Model.Finder<Long,SOCommentsPr> find = new Finder<Long, SOCommentsPr>(Long.class, SOCommentsPr.class);   
    
    
    public static SOCommentsPr ReportEvent(Contributor commenter, Product product, Comment comment) 
    {
    	SOCommentsPr scp=new SOCommentsPr(commenter, product, comment);
    	ComputeTask(scp);
    	return scp;
    }
    
    public static int Deleteinstanceofcomment(Comment comment)
    {
    	return Ebean.createSqlUpdate("delete from SOCommentsPr where comment_id=:cmntid").setParameter("cmntid", comment.id).execute();
    }
    
    public static boolean DeleteEvent(SOCommentsPr scp) 
    {
    	scp.delete();
    	return true;
    }

    public static void ComputeTask(SOCommentsPr scp) 
    {
    	SOCommentsDispatcher.ComputeTask(scp);
    }
    
 
}