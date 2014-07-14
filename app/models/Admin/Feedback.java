package models.Admin;
 
import java.util.*;
import javax.persistence.*;
 


import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import javax.persistence.*;

import models.*;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.validation.Email;

import play.data.validation.*;
 
@Entity
@Table(name="feedback")
public class Feedback extends Model {
 
	@Id
	@GeneratedValue
	public Long id;
	
	@ManyToOne
    public Contributor author;
    
    @Email
    public String email;
    
    public String name;
    
    @CreatedTimestamp
    public Date datecreated;
     
    @Lob
    @MaxLength(10000)
    public String content;     
    
    
    public Feedback(Contributor author, String content) {
        this.author = author;
        this.content = content;
        this.save();
    } 
    
    public Feedback(String email,String name,String Content) {
        this.email = email;
        this.name = name;
        this.content = Content;
        this.save();
    } 
    
    public static Model.Finder<Long,Feedback> find = new Finder<Long, Feedback>(Long.class, Feedback.class); 
    
    public static boolean CreateFBAnonymous(String email,String name,String Content) 
    {
    	try{
    	Feedback fb=new Feedback(email, name, Content);
    	if(fb!=null)
    		return true;
    	}
    	catch(Exception E){} 
    	return false;
    }
    
    public static boolean CreateFB(Contributor c,String Content) 
    {
    	try{
        	Feedback fb=new Feedback(c, Content);
        	if(fb!=null)
        		return true;
        	}
        	catch(Exception E){} 
        	return false;
    }
    
    
    public static Page<Feedback> RecentFBPage(int page, int pageSize, String sortBy, String order) {        
        return	find.orderBy(sortBy + " " + order).findPagingList(pageSize).getPage(page);
    }
}