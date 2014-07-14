package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class BlogLikes extends Model {
	
	@Id
	@GeneratedValue
	public Long id;
	
	@ManyToOne
	public Blog blog;
	
	@Required
    @ManyToOne
    public Contributor author;
	
	/**
	 * LIke or dislike blog. -1 is for dislike and 1 is like
	 */
	public Number like = 0;
	
	public static void rateBlog(Blog bl, Contributor usr, int lk){
		BlogLikes unqB = BlogLikes.find.where().and(Expr.eq("blog",bl), Expr.eq("author", usr)).findUnique();
		if(unqB==null){
			unqB = new BlogLikes();
			unqB.blog = bl;
			unqB.author = usr;
			unqB.like = lk;
			unqB.save();
		}else{
			unqB.like = lk;
			unqB.update();
		}
		
	}
	
	public static int getLikes(){
		return BlogLikes.find.where().gt("like", 0).findRowCount();
	}
	
	public static int getDislikes(){
		return BlogLikes.find.where().lt("like", 0).findRowCount();
	}
	
	
	
	 public static Model.Finder<Long,BlogLikes> find = new Finder<Long, BlogLikes>(Long.class, BlogLikes.class);
	   

}
