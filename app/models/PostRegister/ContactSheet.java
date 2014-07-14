package models.PostRegister;
 
import java.util.*;

import javax.persistence.*;
 


import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import javax.persistence.*;

import org.hibernate.validator.constraints.URL;

import models.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.annotation.CreatedTimestamp;

import play.data.validation.*;
 
@Entity
@Table(name="contactsheet")
public class ContactSheet extends Model { 
	
	@OneToOne
	public User inviter;
	
	public String contact;
	
	public Boolean hasinvited;
	
	public ContactSheet(User Inviter,String Contact,Boolean Hasinvited)
	{
		this.inviter=Inviter;
		this.contact=Contact;
		this.hasinvited=Hasinvited;
		this.save();
	}
	
	public static Finder<Long, ContactSheet> find = new Model.Finder<Long, ContactSheet>(Long.class, ContactSheet.class);
	
	public static ContactSheet findcontactuser(User Inviter){
		return find.where().eq("inviter", Inviter).findUnique();
	}
	
	public static void addallcontacts(User Inviter,List<String> contactmails,Boolean Hasinvited)
	{
		List<ContactSheet> foundcontacts=find.where().eq("inviter", Inviter).in("contact", contactmails).findList();
		for(ContactSheet cs:foundcontacts)
			contactmails.remove(cs.contact);
		for(String cm:contactmails)
			new ContactSheet(Inviter, cm, Hasinvited);
	}
	
	public static void changeinvitation(ContactSheet cs,boolean invitation)
	{
		cs.hasinvited=invitation;
		cs.save();
	}
}