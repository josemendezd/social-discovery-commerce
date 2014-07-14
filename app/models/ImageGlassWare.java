package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class ImageGlassWare extends Model{
	
	@Id
	public Long id;
	
	public String url;
	
	public static ImageGlassWare createInitial(String value){
		ImageGlassWare img = new ImageGlassWare();
		img.id = 1L;
		img.url = value;
		img.save();
		return img;
	}
	public static Model.Finder<Long,ImageGlassWare> find = new Model.Finder<Long,ImageGlassWare>(Long.class, ImageGlassWare.class);

}
