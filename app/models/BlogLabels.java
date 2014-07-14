package models;

import java.util.*;

import javax.persistence.*;
 


import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import javax.persistence.*;
import javax.validation.Constraint;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;

import play.data.validation.*;

@Entity
public class BlogLabels extends Model {
	
	@Id
	@GeneratedValue
	public Long id;

	@Required
	@Column(unique=true)	
    public String tag;
    
    @ManyToMany(targetEntity=Blog.class,mappedBy="labels")
    public List<Blog> blogs;
    
    public BlogLabels(String tagstring) {
        this.tag = tagstring;
        this.save();
    }
    
    public static Model.Finder<Long,BlogLabels> find = new Finder<Long, BlogLabels>(Long.class, BlogLabels.class);
    
    public static Page<BlogLabels> RecentBlogLabels(int page, int pageSize) {        
        return	find.findPagingList(pageSize).getPage(page);
    }
    
    public static BlogLabels AddTag(String tagstring)
    {
    	String cleanuptag=tagstring.trim();
    	BlogLabels label=find.where().eq("tag", cleanuptag).findUnique();
    	if(label!=null )
    		return label;
    	return new BlogLabels(cleanuptag);
    }
    
    public static boolean RemoveTag(String tagstring)
    {
    	BlogLabels label=find.where().eq("tag", tagstring).findUnique();
    	if(label==null )
    		return false;
    	label.delete();
    	return true;
    }  
    

}
