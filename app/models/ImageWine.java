package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class ImageWine extends Model{
	
	@Id
	public Long id;
	
	public String url;
	
	public static ImageWine createInitial(String value){
		ImageWine img = new ImageWine();
		img.id = 1L;
		img.url = value;
		img.save();
		return img;
	}
	
	public static Model.Finder<Long,ImageWine> find = new Model.Finder<Long,ImageWine>(Long.class, ImageWine.class);

}
