package models.Notifications;
 
import java.util.*;

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

import play.data.validation.*;
 
@Entity
@Table(name="usersubscriptions")
public class UserSubscriptions extends Model {
 
	@Id
	@GeneratedValue
	public Long id;
	
    @Required
    @OneToOne
    public Contributor subscriber;
    

    public boolean sofollows = true;
    public boolean socommentpr = true;
    public boolean socommentco = true;
    public boolean sorecommends = true;
    public boolean sosuggestsco = true;
    public boolean advertisements = true;
    
    public UserSubscriptions(Contributor subscriber) {
        this.subscriber = subscriber;
        this.save();
    }
    
    public void Setsubscription(boolean sofollows,boolean socommentpr,boolean socommentco,boolean sorecommends,boolean sosuggestsco,boolean advertisements) 
    {
    	this.sofollows=sofollows;
    	this.socommentpr=socommentpr;
    	this.socommentco=socommentco;
    	this.sorecommends=sorecommends;
    	this.sosuggestsco=sosuggestsco;
    	this.advertisements=advertisements;
    	this.save();
    }
    
    public static Model.Finder<Long,UserSubscriptions> find = new Finder<Long, UserSubscriptions>(Long.class, UserSubscriptions.class);   
    
    public static UserSubscriptions InitSubscription(Contributor subscriber) 
    {
    	return new UserSubscriptions(subscriber);
    }
    
    public static UserSubscriptions getsubscriptions(Contributor subscriber) 
    {
    	return find.where().eq("subscriber",subscriber).findUnique();
    }
    
    public static boolean Issubscribed(Long subscriber,String column) 
    {
    	String sqlstring="SELECT  :selectcolumn STATE FROM usersubscriptions WHERE socommentpr=TRUE AND subscriber_id= :selectsubscriber";
    	return Ebean.createSqlQuery(sqlstring).setParameter("selectcolumn", column)
    			.setParameter("selectsubscriber", subscriber)
    			.findUnique().getBoolean("STATE");
    }
    
    
    
    
    
 
}