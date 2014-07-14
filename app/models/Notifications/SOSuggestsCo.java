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
@Table(name="sosuggestsco")
public class SOSuggestsCo extends Model {
 
	@Id
	@GeneratedValue
	public Long id;
	
    @Required
    @ManyToOne
    public Contributor suggester;
    
    @Required
    @ManyToOne
    public Product product;
    
    @Required
    @ManyToOne
    public UserCollection collection;
    
    @CreatedTimestamp
    public Date moment;    
    
    public SOSuggestsCo(Contributor suggester, Product product, UserCollection collection) {
        this.suggester = suggester;
        this.product = product;
        this.collection = collection;
        this.save();
    }

    public static void DispatchMessage(SOSuggestsCo sc){
    	GHelp.mailsender.sendMail(Messages.get("boozology.notifications.sosuggestsco",sc.suggester.user.name,sc.product.productname,sc.collection.colname),
    			new com.feth.play.module.mail.Mailer.Mail.Body(views.html.Templates.MailTemplates.collectionsuggestproduct
    					.render(sc.collection.contributor.user.name,sc.suggester.user.name,sc.collection.colname,Application.WebAddress+routes.Application.ProductPage(sc.product.id, false).url())
    					.toString(),views.txt.Templates.MailTemplates.collectionsuggestproduct
    					.render(sc.collection.contributor.user.name,sc.suggester.user.name,sc.collection.colname,Application.WebAddress+routes.Application.ProductPage(sc.product.id, false).url())
    					.toString()),
    			GHelp.getEmailName(sc.collection.contributor.user.email, sc.collection.contributor.user.name));
    }
    
    public static Model.Finder<Long,SOSuggestsCo> find = new Finder<Long, SOSuggestsCo>(Long.class, SOSuggestsCo.class);   
    
    
    public static SOSuggestsCo ReportEvent(Contributor suggester, Product product, UserCollection collection) 
    {
    	SOSuggestsCo ssc=new SOSuggestsCo(suggester, product, collection);
    	ComputeTask(ssc);
    	return ssc;
    }
    
    public static boolean DeleteEvent(SOSuggestsCo ssc) 
    {
    	ssc.delete();
    	return true;
    }

    public static void ComputeTask(SOSuggestsCo ssc) 
    {
    	SOSuggestCoDispatcher.ComputeTask(ssc);
    }
    
 
}