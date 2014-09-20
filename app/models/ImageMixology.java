package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;

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

	public static ImageMixology getRandomImage() {
		SqlRow row =  Ebean
	             .createSqlQuery("select * from image_mixology Order by RANDOM() LIMIT 1")
	             .findUnique();
		return ImageMixology.find.byId(Long.parseLong(row.getString("id")));
	}
	
}
