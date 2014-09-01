package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class ProductLabel extends Model  {
	@Id
	@GeneratedValue
	public Long id;

	@Required
	@Column(unique=true)	
    public String tag;
    
    @ManyToMany(targetEntity=Product.class,mappedBy="labels")
    public List<Product> products;
    
    public ProductLabel(String tagstring) {
        this.tag = tagstring;
        this.save();
    }
    
    public static Model.Finder<Long,ProductLabel> find = new Finder<Long, ProductLabel>(Long.class, ProductLabel.class);
    
    public static ProductLabel AddTag(String tagstring)
    {
    	String cleanuptag=tagstring.trim();
    	ProductLabel label=find.where().eq("tag", cleanuptag).findUnique();
    	if(label!=null )
    		return label;
    	return new ProductLabel(cleanuptag);
    }
    
    public static boolean RemoveTag(String tagstring)
    {
    	ProductLabel label=find.where().eq("tag", tagstring).findUnique();
    	if(label==null )
    		return false;
    	label.delete();
    	return true;
    }
}
