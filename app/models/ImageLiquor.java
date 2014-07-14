package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class ImageLiquor extends Model{
	
	@Id
	public Long id;
	
	public String url;
	
	public static ImageLiquor createInitial(String value){
		ImageLiquor img = new ImageLiquor();
		img.id = 1L;
		img.url = value;
		img.save();
		return img;
	}
	public static Model.Finder<Long,ImageLiquor> find = new Model.Finder<Long,ImageLiquor>(Long.class, ImageLiquor.class);

}
