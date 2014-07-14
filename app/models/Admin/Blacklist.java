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
@Table(name="blacklist")
public class Blacklist extends Model {
 
	@Id
	@GeneratedValue
	public Long id;
	
    @Required
    @ManyToOne
    public Contributor author;
    
    @CreatedTimestamp
    public Date datecreated;
     
    @URL
    public String site;
    
    public String shortdescription;
    
    public Blacklist(String site, String shortdescription, Contributor author) {
        this.site = site;
        this.author = author;
        this.shortdescription = shortdescription;
        this.save();
    }
    
    public static Model.Finder<Long,Blacklist> find = new Finder<Long, Blacklist>(Long.class, Blacklist.class);   
    
    
    public static Blacklist AddnewBlacklist(String site, String shortdescription, Contributor author) 
    {
    	Blacklist bl=new Blacklist(site, shortdescription, author);
    	return bl;
    }
    
    public static Page<Blacklist> RecentBlacklists(int page, int pageSize, String sortBy, String order,int ratype) {  
    	return	find.orderBy(sortBy + " " + order).findPagingList(pageSize).getPage(page);
    }
    
    public static void updateblacklistsd(Blacklist bl,String shortdescription) 
    {
    	bl.shortdescription=shortdescription;
    	bl.save();
    }
    
    public static boolean DeleteBlacklist(Blacklist bl) 
    {
    	bl.delete();
    	return true;
    }
    
 
}