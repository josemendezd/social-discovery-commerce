package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;

import play.db.ebean.Model;

@Entity
public class ImageGadgets extends Model{
	
	@Id
	public Long id;
	
	public String url;
	
	public static ImageGadgets createInitial(String value){
		ImageGadgets img = new ImageGadgets();
		img.id = 1L;
		img.url = value;
		img.save();
		return img;
	}
	public static Model.Finder<Long,ImageGadgets> find = new Model.Finder<Long,ImageGadgets>(Long.class, ImageGadgets.class);

	public static ImageGadgets getRandomImage() {
		SqlRow row =  Ebean
	             .createSqlQuery("select * from image_gadgets Order by RANDOM() LIMIT 1")
	             .findUnique();
		return ImageGadgets.find.byId(Long.parseLong(row.getString("id")));
	}
	
}
