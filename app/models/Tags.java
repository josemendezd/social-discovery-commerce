package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

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
        this.save();
    }
    
    public static Model.Finder<Long,Tags> find = new Finder<Long, Tags>(Long.class, Tags.class);
    
    public static void AddTag(String tagstring)
    {
    	String tags[]=tagstring.split(",");
    	for(String tag : tags){
	    	String cleanuptag = tag.trim();
	    	Tags label = find.where().eq("name", cleanuptag).findUnique();
	    	if(label != null )
	    		continue;
	    	new Tags(cleanuptag);
    	}
    }
    
    public static boolean RemoveTag(String tagstring)
    {
    	Tags label=find.where().eq("tag", tagstring).findUnique();
    	if(label==null )
    		return false;
    	label.delete();
    	return true;
    }

	public static List<Tags> getAllTags() {
		return Tags.find.all();
	}
}
