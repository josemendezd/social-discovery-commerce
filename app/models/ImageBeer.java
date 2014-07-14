package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class ImageBeer extends Model{
	
	@Id
	public Long id;

	public String url;
	
	public static ImageBeer createInitial(String value){
		ImageBeer imgb = new ImageBeer();
		imgb.id = 1L;
		imgb.url = value;
		imgb.save();
		return imgb;
	}
	public static Model.Finder<Long,ImageBeer> find = new Model.Finder<Long,ImageBeer>(Long.class, ImageBeer.class);
}
