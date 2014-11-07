package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import com.avaje.ebean.Ebean;

@Entity
public class Tags extends Model  {
	@Id
	@GeneratedValue
	public Long id;

	@Required
	@Column(unique=true)	
    public String name;
    
    public Tags(String tagstring) {
        this.name = tagstring;
      //  Logger.info("add new tag");
       this.save();
    }
    
    public static Model.Finder<Long,Tags> find = new Finder<Long, Tags>(Long.class, Tags.class);
    
    public static List<Tags> AddTag(String tagstring)
    {
    	String tags[]=tagstring.split(",");
    	List<Tags> tagsList = new ArrayList<Tags>();
    	for(String tag : tags){
	    	String cleanuptag = tag.trim();
	    	Tags label = find.where().ieq("name", cleanuptag).findUnique();
	    	if(label != null )
	    		continue;
	    	tagsList.add(new Tags(cleanuptag));
    	}
    	return tagsList;
    }
    
    public static boolean RemoveTag(Long tagID)
    {
    	Ebean.createSqlUpdate("DELETE from product_tags where product_tags.tags_id = :id ").setParameter("id", tagID).execute();
    	Ebean.createSqlUpdate("DELETE from blog_tags where blog_tags.tags_id = :id ").setParameter("id", tagID).execute();
    	Tags label=Tags.find.byId(tagID);
    	if(label==null )
    		return false;
    	label.delete();
    	return true;
    }

	public static List<Tags> getAllTags(String query) {
		if(query != null){
			return Tags.find.where().ilike("name", query+"%").findList();
		}
		return Tags.find.all();
	}
}
