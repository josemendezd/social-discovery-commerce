package models.Admin;
 
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
@Table(name="addfailed")
public class Addfailed extends Model {
 
	@Id
	@GeneratedValue
	public Long id;
	
    @Required
    @ManyToOne
    public User victim;
    
    @CreatedTimestamp
    public Date datehappened;
     
    @Required
    @URL
    public String imagelocation;
    
    @URL
    public String weblocation;
    
    @Lob
    public String exception;
    
    public Addfailed(User victim,String imagelocation,String weblocation,String exception) {
        this.victim = victim;
        this.imagelocation = imagelocation;
        if(weblocation != null)
        this.weblocation = weblocation;
        if(exception != null)
        this.exception = exception;
        this.save();
    }   
    
    public static Model.Finder<Long,Addfailed> find = new Finder<Long, Addfailed>(Long.class, Addfailed.class);   
    
    
    public static Addfailed Reportfailure(User victim,String imagelocation,String weblocation,String exception) 
    {
    	Addfailed af=new Addfailed(victim, imagelocation, weblocation, exception);
    	return af;
    }
    
    public static Page<Addfailed> RecentInfoPage(int page, int pageSize, String sortBy, String order) {        
        return	find.orderBy(sortBy + " " + order).findPagingList(pageSize).getPage(page);
    }
    
    public static Addfailed findbyuser(User victim) {        
        return	find.where().eq("victim", victim).findUnique();
    }
    
    public static boolean Deletepage(Long id) 
    {
    	Addfailed af= find.byId(id);
    	if(af==null)
    		return false;
    	af.delete();
    	return true;
    }
    
 
}