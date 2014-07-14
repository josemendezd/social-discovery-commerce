package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import play.db.ebean.Model;

@Entity
public class Cart extends Model{
	
	@Id
	public Long id;

	public String uuid;
	public String status;
	
	@OneToMany(cascade=CascadeType.ALL)
	public List<Item> items;
	
	@ManyToOne(cascade=CascadeType.ALL)
	public User user;	
	
	public static Model.Finder<Long,Cart> find = new Model.Finder<Long,Cart>(Long.class, Cart.class);
	

}
