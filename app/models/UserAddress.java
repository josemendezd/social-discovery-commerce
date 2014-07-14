package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.db.ebean.Model;


@Entity
public class UserAddress extends Model{
	
	@Id
	public Long id;

	public String firstName;
	public String lastName;
	public String address1;
	public String address2;
	public String city;
	public String province;
	public Integer zip;
	public String country;
	public String phone;
	
	public String uuid;
	@OneToOne(cascade=CascadeType.ALL)
	public User user;
	
	
	public static Model.Finder<Long,UserAddress> find = new Model.Finder<Long,UserAddress>(Long.class, UserAddress.class);


}
