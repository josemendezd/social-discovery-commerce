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

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.annotation.CreatedTimestamp;

import play.data.validation.*;
 
@Entity
@Table(name="advertisements")
public class Advertisements extends Model {
 
	@Id
	@GeneratedValue
	public Long id;
	
    @Required
    public Long associatedid;
    
    @Required
    public Long associatedtype;
    
    @Required
    @Lob
    public String content;
    
    @CreatedTimestamp
    public Date moment;    
    
    public Advertisements(Long associatedid, Long associatedtype,String content) {
        this.associatedid = associatedid;
        this.associatedtype = associatedtype;
        this.content = content;
        this.save();
    }
    
    public static Model.Finder<Long,Advertisements> find = new Finder<Long, Advertisements>(Long.class, Advertisements.class);   
    
    
    public static Advertisements ReportEvent(Long associatedid, Long associatedtype,String content) 
    {
    	Advertisements adv=new Advertisements(associatedid, associatedtype, content);
    	return adv;
    }
    
    public static boolean DeleteEvent(Advertisements adv) 
    {
    	adv.delete();
    	return true;
    }
    
    public static boolean ComputeTask() 
    {
    	return true;
    }
    
 
}