package models;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;

import com.amazonaws.Request;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.SqlUpdate;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.validation.Email;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;
import com.feth.play.module.pa.user.NameIdentity;
import com.feth.play.module.pa.user.FirstLastNameIdentity;

import controllers.DInitial;
import controllers.GHelp;
import models.Notifications.UserSubscriptions;
import models.TokenAction.Type;
import play.Logger;
import play.api.mvc.Controller;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import plugins.S3Plugin;

import javax.persistence.*;

import org.hibernate.validator.constraints.URL;

import java.io.File;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.*;

/**
 * Initial version based on work by Steve Chaloner (steve@objectify.be) for
 * Deadbolt2
 */
@Entity
@Table(name = "userrate")
public class UserRate extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;

	public Long user_id;
	
	public Long product_id;
	
	public float rate;
	
	public static final Finder<Long, UserRate> find = new Finder<Long, UserRate>(
			Long.class, UserRate.class);

	
	public static void save(User user, Product p, float rate)
	{
		UserRate ur = find.where().eq("user_id", user.id).eq("product_id", p.id).findUnique();
		if (ur == null) {
			ur = new UserRate();
			ur.user_id = user.id;
			ur.product_id = p.id;
			ur.rate = rate;
			ur.save();
		} else {
			ur.rate = rate;
			ur.update();
		}
		
	}
	















	
}
