package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class ImageMixology extends Model{
	
	@Id
	public Long id;
	
	public String url;
	
	public static ImageMixology createInitial(String value){
		ImageMixology img = new ImageMixology();
		img.id = 1L;
		img.url = value;
		img.save();
		return img;
	}
	public static Model.Finder<Long,ImageMixology> find = new Model.Finder<Long,ImageMixology>(Long.class, ImageMixology.class);

}
