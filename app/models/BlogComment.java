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
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;

import controllers.DInitial;
import play.data.validation.*;
 
@Entity
public class BlogComment extends Model {
 
	@Id
	@GeneratedValue
	public Long id;
	
    @Required
    @ManyToOne
    public Contributor author;
    
    @Required
    public Date postedAt;
     
    @Required    
    public String content;
    
    @ManyToOne
    @Required
    public Blog post;
    
    public boolean spam_flag;
    
    public BlogComment(Blog post, Contributor author, String content) {
        this.post = post;
        this.author = author;
        this.content = content;
        this.postedAt = new Date();
        this.save();
    }
    
    public BlogComment(Blog post, Contributor author, String content, boolean spam_flag) {
        this.post = post;
        this.author = author;
        this.content = content;
        this.postedAt = new Date();
        this.spam_flag = spam_flag;
        this.save();
    }
    
    public String toString() {
        return content;
    }
    
    public static Model.Finder<Long,BlogComment> find = new Finder<Long, BlogComment>(Long.class, BlogComment.class);
    
    public static BlogComment AddComment(Blog post, Contributor author, String content)
    {
    	return new BlogComment(post, author, content);
    }
    
    public static BlogComment AddComment(Blog post, Contributor author, String content, boolean spam_flag)
    {
    	return new BlogComment(post, author, content, spam_flag);
    }
    
    public static boolean RemoveComment(BlogComment cmnt,Contributor contrib)
    {
    	
    	if(cmnt==null || !cmnt.author.equals(contrib) )
    		return false;
    	cmnt.delete();
    	return true;
    }
    
    public static int CommentCount(Blog blog)
    {
    	return find.where().eq("post.id", blog.id).findRowCount();
    }
    
    public static Page<BlogComment> BlogCommentPage(int page, int pageSize, Blog p) {        
        return	find.where().eq("post.id", p.id).findPagingList(pageSize).getPage(page);
    }
    
    public static String BlogCommentPageJSON(Blog p, Integer limit) {
    	
    	String query="SELECT string_agg(row_to_json(data):: text,',') AS LIST  FROM ( SELECT    users.name un,  getimageurl("+String.format("'%s',users.profileimage,'%s','%s'", User.class.getSimpleName(),DInitial.IMAGESTORESIZE.THUMBNAIL_VERYSMALL.filestate,S3File.getbaseurl())+")   ui,    blog_comment.id cid,    blog_comment.author_id uid,    blog_comment.posted_at  AT TIME ZONE current_setting('TIMEZONE') cd,    blog_comment.content ct ,blog_comment.spam_flag sf   FROM    public.blog_comment,    public.contributor,    public.users WHERE    contributor.user_id = users.id AND   contributor.id = blog_comment.author_id AND   blog_comment.post_id = "+p.id+" ORDER BY   blog_comment.posted_at ASC   )  data(un,ui,cid,uid,cd,ct,sf)";
    	if(limit>0){
    		query = "SELECT string_agg(row_to_json(data):: text,',') AS LIST  FROM ( SELECT    users.name un,  getimageurl("+String.format("'%s',users.profileimage,'%s','%s'", User.class.getSimpleName(),DInitial.IMAGESTORESIZE.THUMBNAIL_VERYSMALL.filestate,S3File.getbaseurl())+")   ui,    blog_comment.id cid,    blog_comment.author_id uid,    blog_comment.posted_at  AT TIME ZONE current_setting('TIMEZONE') cd,    blog_comment.content ct,  blog_comment.spam_flag sf  FROM    public.blog_comment,    public.contributor,    public.users WHERE    contributor.user_id = users.id AND   contributor.id = blog_comment.author_id AND   blog_comment.post_id = "+p.id+" ORDER BY   blog_comment.posted_at ASC LIMIT " + limit.toString() + " )  data(un,ui,cid,uid,cd,ct,sf)";
    	}
    	return Ebean.createSqlQuery(query).findUnique().getString("LIST");
    }

	public static List<BlogComment> findAllSpams(int start, int rowsPerPage) {
		return find.where().eq("spam_flag", true).setFirstRow(start).setMaxRows(rowsPerPage).findList();
	}
 
}