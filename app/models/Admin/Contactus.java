package models.Admin;
 
import java.util.*;
import javax.persistence.*;
 


import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import javax.persistence.*;

import models.*;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.EnumValue;
import com.avaje.ebean.validation.Email;

import play.data.validation.*;
 
@Entity
@Table(name="contactus")
public class Contactus extends Model {
	
	@Id
	@GeneratedValue
	public Long id;
	
	@Required
	public Integer querytype;
	
	@Email
    public String email;
    
	@Required
    public String firstname;
    
	@Required
    public String lastname;
    
	@Required
    public String subject;
    
    @CreatedTimestamp
    public Date datecreated;
     
    @Lob
    @MaxLength(10000)
    @MinLength(30)
    public String content;    
    
     
    
    public Contactus(int querytype,String email,String firstname,String lastname,String subject,String Content) {
        this.querytype = querytype;
    	this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.subject = subject;
        this.content = Content;
    } 
    
    public static Model.Finder<Long,Contactus> find = new Finder<Long, Contactus>(Long.class, Contactus.class);    
    
    
    public static Contactus CreateQuery(int querytype,String email,String firstname,String lastname,String subject,String Content) 
    {
    	Contactus cu=new Contactus(querytype, email, firstname, lastname, subject, Content);
    	cu.save();
    	return cu;
    }
    
    
    public static Page<Contactus> recentquerypage(int page, int pageSize, String sortBy, String order) {        
        return	find.orderBy(sortBy + " " + order).findPagingList(pageSize).getPage(page);
    }
    
    public static Page<Contactus> filteredquerypage(int page, int pageSize,String filter,Object filterby, String sortBy, String order) {        
        return	find.where().eq(filter, filterby).orderBy(sortBy + " " + order).findPagingList(pageSize).getPage(page);
    }
}