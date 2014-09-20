package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;

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

	public static ImageToys getRandomImage() {
		SqlRow row =  Ebean
	             .createSqlQuery("select * from image_toys Order by RANDOM() LIMIT 1")
	             .findUnique();
		return ImageToys.find.byId(Long.parseLong(row.getString("id")));
	}
	
}
