package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;

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

	public static ImageBeer getRandomImage() {
		SqlRow row =  Ebean
	             .createSqlQuery("select * from image_beer Order by RANDOM() LIMIT 1")
	             .findUnique();
		return ImageBeer.find.byId(Long.parseLong(row.getString("id")));
	}
	
	
	
	
}
