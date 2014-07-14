package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class BlogImage extends Model {
	
	@Id
	@GeneratedValue
	public Long id;
	
	public Long blogid;
	
	@ManyToOne
	public S3File file;
	
	public Boolean isVideo = false;
	public String fileType;
	public String ok;
	
	public String getURL(){
		if(file!=null)
			return file.geturlstring();
		else
			return "";
		
	}
	
	public static List<BlogImage> getImagesForBlog(Blog blog){
		return find.where().eq("blogid", blog.id).findList();
		
	}
	
	public static Model.Finder<Long,BlogImage> find = new Finder<Long, BlogImage>(Long.class, BlogImage.class);
	  
	
}
