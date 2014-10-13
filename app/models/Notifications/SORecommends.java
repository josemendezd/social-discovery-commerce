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
import providers.MyMadMimiMailer;
import scala.concurrent.ExecutionContext;
 
@Entity
public class SORecommends extends Model {
 
	@Id
	@GeneratedValue
	public Long id;
	
    @Required
    @ManyToOne
    public Contributor recommender;
    
    @Required
    @ManyToOne
    public Contributor leader;
    
    @Required
    @ManyToOne
    public Product product;
    
    @CreatedTimestamp
    public Date moment;    
    
    public SORecommends(Contributor recommender, Product product, Contributor leader) {
        this.recommender = recommender;
        this.leader = leader;
        this.product = product;
        this.save();
    }

    public static void DispatchMessage(SORecommends sr){
     	
    	/**
    	GHelp.mailsender.sendMail(Messages.get("boozology.notifications.sorecommends",sr.recommender.user.name,sr.product.productname),
    			new com.feth.play.module.mail.Mailer.Mail.Body(views.html.Templates.MailTemplates.recommendproduct
    					.render(sr.leader.user.name,sr.recommender.user.name,Application.WebAddress+routes.Application.ProductPage(sr.product.id, false).url())
    					.toString(),views.txt.Templates.MailTemplates.recommendproduct
    					.render(sr.leader.user.name,sr.recommender.user.name,Application.WebAddress+routes.Application.ProductPage(sr.product.id, false).url())
    					.toString()),
    			GHelp.getEmailName(sr.leader.user.email, sr.leader.user.name));
    			**/
    	String myTempRecommenderName;
		if(sr.recommender.user.firstName==null ){
			myTempRecommenderName=sr.recommender.user.name;
		}
		else{
			myTempRecommenderName=sr.recommender.user.firstName+ " "+sr.recommender.user.lastName;
		}    	

    	String myTempUserName;
		if(sr.leader.user.firstName==null ){
			myTempUserName=sr.leader.user.name;
		}
		else{
			myTempUserName=sr.leader.user.firstName+ " "+sr.leader.user.lastName;
		} 
		
    	//------------------------------------------------------------------------------------
   
      	Map<String, String> map = new HashMap<String, String>();
    	map.put("recommender", myTempRecommenderName);
    	map.put("productname",sr.product.productname);
    	map.put("url",Application.WebAddress+routes.Application.ProductPage(sr.product.id, false).url());
    	//map.put("comment", sp.comment.content);
    	map.put("username", myTempUserName);
    	boolean res =false;
    	try {
		 res=	MyMadMimiMailer.sendMyNotification(map, sr.leader.user,"SORecommends");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
	}
    
    public static Model.Finder<Long,SORecommends> find = new Finder<Long, SORecommends>(Long.class, SORecommends.class);   
    
    
    public static SORecommends ReportEvent(Contributor recommender,Product product,Contributor leader) 
    {
    	SORecommends sr=new SORecommends(recommender, product, leader);
    	ComputeTask(sr);
    	return sr;
    }
    
    public static boolean DeleteEvent(SORecommends sr) 
    {
    	sr.delete();
    	return true;
    }

    public static void ComputeTask(SORecommends sr) 
    {
    	SORecommentsDispatcher.ComputeTask(sr);
    }
    
 
}