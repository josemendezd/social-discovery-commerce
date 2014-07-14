package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class ImageToys extends Model{
	
	@Id
	public Long id;
	
	public String url;
	
	public static ImageToys createInitial(String value){
		ImageToys img = new ImageToys();
		img.id = 1L;
		img.url = value;
		img.save();
		return img;
	}
	public static Model.Finder<Long,ImageToys> find = new Model.Finder<Long,ImageToys>(Long.class, ImageToys.class);

}
