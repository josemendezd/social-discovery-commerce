package models;
 
import java.util.*;
import javax.persistence.*;
 


import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import javax.persistence.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;

import controllers.DInitial;

import play.data.validation.*;
 
@Entity
public class Blog extends Model {
 
	@Id
	@GeneratedValue
	public Long id;
	
    @Required
    @ManyToOne
    public Contributor author;
    
    @Required
    public Date postedAt;
     
    @Lob
    @Required
    @Constraints.MaxLength(50000)
    public String content;
    
    @Transient
    public String htmlLessContent;
    
    @Required
    public String title;

    @Required
    @Column(unique=true)
    public String permaLink;
    
    @ManyToMany
    public List<Tags> labels;
    
    public Blog(String title, Contributor author, String content) {
        this.title = title;
        this.author = author;
        this.content = content;
        this.postedAt = new Date();
        this.save();
    }  
    
    /*public void ApplyLabel(String labelarray)
    {
    	Ebean.createSqlUpdate("DELETE from blog_blog_labels where blog_blog_labels.blog_id = :bid ").setParameter("bid", this.id).execute();
    	Ebean.createSqlUpdate("DELETE from blog_labels where blog_labels.id NOT IN( SELECT DISTINCT blog_blog_labels.blog_labels_id from blog_blog_labels)").execute();
    	String tags[]=labelarray.split(",");
    	for(String tag:tags)
    	{
    		if(tag!=null)
    			AddLabel(BlogLabels.AddTag(tag)) ;
    	}
    	this.save();
    }*/
    
    public void ApplyTag(String labels)
    {
    	String tags[]=labels.split(",");
    	for(String tag:tags)
    	{
    		if(tag!=null)
    			this.labels.add(Tags.find.byId(Long.parseLong(tag)));
    	}
    	this.save();
    }
    
   /* public boolean AddLabel(BlogLabels bl)
    {
    	if(this.labels.size()>=DInitial.BLOG_LABELS_LIMIT||this.labels.contains(bl))
    		return false;
    	this.labels.add(bl);
    	this.save();
    	return true;
    }
    
    public boolean RemoveLabel(BlogLabels bl)
    {
    	if(!this.labels.contains(bl) || !this.labels.remove(bl) )
    		return false;
    	this.save();
    	return true;
    }*/
    
    public static Model.Finder<Long,Blog> find = new Finder<Long, Blog>(Long.class, Blog.class);
    
    public static Blog AddBlog(String title, Contributor author, String content)
    {
    	return new Blog(title, author, content);
    }  
    
    
    
    public static boolean EditBlog(Blog blogid,String title, Contributor author, String content)
    {
    	try{
    		blogid.author=author;
    		blogid.title=title;
    		blogid.content=content;
    		blogid.save();
    		return true;
    	}
    	catch(Exception E){ return false; }
    } 
    
    
    public static boolean RemoveBlog(Blog blg)
    {    	
    	if(blg==null)
    		return false;
    	Ebean.createSqlUpdate("DELETE from blog_blog_labels where blog_blog_labels.blog_id = :bid ").setParameter("bid", blg.id).execute();
    	Ebean.createSqlUpdate("DELETE from blog_labels where blog_labels.id NOT IN( SELECT DISTINCT blog_blog_labels.blog_labels_id from blog_blog_labels)").execute();
    	Ebean.createSqlUpdate("DELETE from blog_comment where blog_comment.post_id = :bid ").setParameter("bid", blg.id).execute();
    	BlogImage.removeImagesForBlog(blg);
    	BlogLikes.removeLikesForBlog(blg);
    	blg.delete();
    	return true;
    }
    
    public static Page<Blog> RecentBlogPage(int page, int pageSize) {        
        return	find.orderBy("postedAt DESC").findPagingList(pageSize).getPage(page);
    }
    
    public static Page<Blog> Searchbytitle(int page, int pageSize,String titlekey) {        
        return	find.where().ilike("title","%" + titlekey + "%" ).orderBy("postedAt DESC").findPagingList(pageSize).getPage(page);
    }
    
    public static int SearchbytitleCount(String titlekey) {        
        return	find.where().ilike("title","%" +  titlekey + "%").orderBy("postedAt DESC").findRowCount();
    }
    
    public static Page<Blog> Searchbycontent(int page,int pageSize,String allkey){
    	return	find.where().or(Expr.ilike("title", "%" + allkey + "%"),Expr.ilike("content", allkey)).orderBy("postedAt DESC").findPagingList(pageSize).getPage(page);
    }
    
    public static Page<Blog> Searchbytag(int page,int pageSize,String tagkey){
    	return	find.where().eq("labels.tag", tagkey).orderBy("postedAt DESC").findPagingList(pageSize).getPage(page);
    }
    
    public List<BlogImage> getImages(){
    	return BlogImage.getImagesForBlog(this);
    }

	public static Blog findByPermalink(String permalink) {
		return find.where().eq("permaLink", permalink).findUnique();
	}


	
	public static Page<Blog> RecentBlogPage(int page, int ps, Long id) {
		 return	find.where().ne("id", id).orderBy("postedAt DESC").findPagingList(ps).getPage(page);
	}
    
    
}