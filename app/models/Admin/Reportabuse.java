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
@Table(name="reportabuse")
public class Reportabuse extends Model {
 
	@Id
	@GeneratedValue
	public Long id;
	
    @Required
    @ManyToOne
    public Contributor author;
    
    @CreatedTimestamp
    public Date datecreated;
     
    @Required
    public Long contentid;
    
    @Required
    public int ratype;
    
    @URL
    public String frompage;
    
    public String complaintext;
    
    public Reportabuse(Long contentid,int ratype, String frompage, Contributor author,String complaintext) {
        this.contentid = contentid;
        this.author = author;
        this.ratype = ratype;
        this.frompage = frompage;
        this.complaintext = complaintext;
        this.save();
    }
    
    public static Model.Finder<Long,Reportabuse> find = new Finder<Long, Reportabuse>(Long.class, Reportabuse.class);   
    
    
    public static Reportabuse CreateAbuse(Long contentid,int ratype, String frompage, Contributor author, String complaintext) 
    {
    	Reportabuse ra=new Reportabuse(contentid, ratype, frompage, author, complaintext);
    	return ra;
    }
    
    public static Page<Reportabuse> RecentAbusePage(int page, int pageSize, String sortBy, String order,int ratype) {  
    	if(ratype>0)
    		return find.where().eq("ratype", ratype).orderBy(sortBy + " " + order).findPagingList(pageSize).getPage(page);
        return	find.orderBy(sortBy + " " + order).findPagingList(pageSize).getPage(page);
    }
    
    public static Reportabuse findbytype(int ratype) {        
        return	find.where().eq("ratype", ratype).findUnique();
    }
    
    public static Page<Reportabuse> MyAbuseReports(int page, int pageSize, String sortBy, String order,int author) {  
    	return find.where().eq("author", author).orderBy(sortBy + " " + order).findPagingList(pageSize).getPage(page);
    }
    
    public static boolean DeleteAbuse(Reportabuse ra) 
    {
    	ra.delete();
    	return true;
    }
    
 
}