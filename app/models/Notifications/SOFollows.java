package models.Notifications;
 
import java.util.*;
import java.util.concurrent.*;

import javax.persistence.*;
 


import play.Logger;
import play.api.libs.concurrent.Promise;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

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
import play.libs.Akka;
import scala.concurrent.ExecutionContext;
 
@Entity
@Table(name="sofollows")
public class SOFollows extends Model {
 
	@Id
	@GeneratedValue
	public Long id;
	
    @Required
    @ManyToOne
    public Contributor follower;
    
    @Required
    @ManyToOne
    public Contributor leader;
    
    @CreatedTimestamp
    public Date moment;    
    
    public SOFollows(Contributor follower, Contributor leader) {
        this.follower = follower;
        this.leader = leader;
        this.save();
    }
    
    public static void DispatchMessage(SOFollows sf){
    	GHelp.mailsender.sendMail(Messages.get("boozology.notifications.sofollows",sf.follower.user.name),
    			new com.feth.play.module.mail.Mailer.Mail.Body(views.html.Templates.MailTemplates.follow
    					.render(sf.leader.user.name,sf.follower.user.name,Application.WebAddress+routes.Application.ContributorPage(sf.leader.id, false, false).url())
    					.toString(),views.txt.Templates.MailTemplates.follow
    					.render(sf.leader.user.name,sf.follower.user.name,Application.WebAddress+routes.Application.ContributorPage(sf.leader.id, false, false).url())
    					.toString()),
    			GHelp.getEmailName(sf.leader.user.email, sf.leader.user.name));
    }
    
    public static Model.Finder<Long,SOFollows> find = new Finder<Long, SOFollows>(Long.class, SOFollows.class);   
    
    
    public static SOFollows ReportEvent(Contributor follower, Contributor leader) 
    {
    	SOFollows bl=new SOFollows(follower,leader);
    	ComputeTask(bl);
    	return bl;
    }
    
    public static SOFollows FindEvent(Contributor follower, Contributor leader) 
    {
    	return find.where().eq("leader",leader).eq("follower",follower).findUnique();
    }

    public static void FindandDeleteEvent(Contributor follower, Contributor leader) 
    {
    	SOFollows sf=FindEvent(follower, leader);
    	if(sf!=null)
    		DeleteEvent(sf);
    }
    
    public static boolean DeleteEvent(SOFollows bl) 
    {
    	bl.delete();
    	return true;
    }
    
    public static void ComputeTask(final SOFollows sf) 
    {
    	SOFollowsDispatcher.ComputeTask(sf);
    }
    
 
}