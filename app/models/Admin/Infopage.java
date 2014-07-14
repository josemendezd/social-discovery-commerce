package models.Admin;
 
import java.util.*;

import javax.persistence.*;
 


import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import javax.persistence.*;

import models.*;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.annotation.CreatedTimestamp;

import play.data.validation.*;
 
@Entity
@Table(name="infopage")
public class Infopage extends Model {
 
	@Id
	@GeneratedValue
	public Long id;
	
    @Required
    @ManyToOne
    public Contributor author;
    
    @Required
    public Date lastedited;
    
    @CreatedTimestamp
    public Date datecreated;
     
    @Lob
    public String content;    
    
    @Required
    public String title;
    
    public Infopage(String title, Contributor author, String content) {
        this.title = title;
        this.author = author;
        this.content = content;
        this.lastedited = new Date();
        this.save();
    }   
    
    public static Model.Finder<Long,Infopage> find = new Finder<Long, Infopage>(Long.class, Infopage.class);   
    
    
    public static Infopage Createpage(String titlekey,String PageContent,Contributor c) 
    {
    	Infopage ip=findbytitle(titlekey);
    	if(ip==null)
    		ip=new Infopage(titlekey, c, PageContent);
    	return ip;
    }
    
    public static Page<Infopage> RecentInfoPage(int page, int pageSize, String sortBy, String order) {        
        return	find.orderBy(sortBy + " " + order).findPagingList(pageSize).getPage(page);
    }
    
    public static Infopage findbytitle(String titlekey) {        
        return	find.where().eq("title", titlekey).findUnique();
    }
    
    public static boolean Updatepage(String titlekey,String PageContent) 
    {
    	Infopage ip= findbytitle(titlekey);
    	if(ip==null)
    		return false;
    	ip.content=PageContent;
    	ip.lastedited=new Date();
    	ip.save();
    	return true;
    }
    
    public static boolean Deletepage(String titlekey,String PageContent) 
    {
    	Infopage ip= findbytitle(titlekey);
    	if(ip==null)
    		return false;
    	ip.delete();
    	return true;
    }
    
 
}