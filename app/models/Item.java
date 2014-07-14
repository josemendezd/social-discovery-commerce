package models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import play.data.format.Formats;
import play.db.ebean.Model;

@Entity
public class Item extends Model{
	
	@Id
	public Long id;
	
	public Integer quantity;
	
	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date date;
	
	@OneToOne(cascade=CascadeType.ALL)
	public Product product;
	
	@ManyToOne(cascade=CascadeType.ALL)
	public Cart cart;
	
	public static Model.Finder<Long,Item> find = new Model.Finder<Long,Item>(Long.class, Item.class);


}
