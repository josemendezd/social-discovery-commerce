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
@Table(name = "users")
public class User extends Model implements Subject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;

	@Email
	// if you make this unique, keep in mind that users *must* merge/link their
	// accounts then on signup with additional providers
	// @Column(unique = true)
	public String email;
	
	@Column(unique = true)
	@Required
	public String name;
	
	public String firstName;
	
	public String lastName;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date lastLogin;
	
	public Date lastattempt;
	
	public int failedattempt=0; 

	public boolean active;

	public boolean emailValidated;
	
	public long gender;
	
	public String profileimage;
	
	@URL
	public String website;
	
	public String location;
	
	public String biography;
	
	@CreatedTimestamp
	public Timestamp registeredon;
	
	
	public String registerip;
	
	public String registerlocation;
	
	@Constraints.Required
	public Integer registerstatus;
	
	@OneToOne(mappedBy="user", cascade=CascadeType.ALL)
	public Contributor contrib;

	@ManyToMany
	public List<SecurityRole> roles;

	@OneToMany(cascade = CascadeType.ALL)
	public List<LinkedAccount> linkedAccounts;

	@ManyToMany
	public List<UserPermission> permissions;
	
	@ManyToMany(targetEntity=Category.class,cascade= CascadeType.ALL)
	public Set<Category> categorypreferred;
	
	@OneToMany(cascade=CascadeType.ALL)
	public List<Cart> carts;

	public static final Finder<Long, User> find = new Finder<Long, User>(
			Long.class, User.class);

	@Override
	public String getIdentifier()
	{
		return Long.toString(id);
	}

	@Override
	public List<? extends Role> getRoles() {
		return roles;
	}

	@Override
	public List<? extends Permission> getPermissions() {
		return permissions;
	}

	public static boolean existsByAuthUserIdentity( final AuthUserIdentity identity) {
		final ExpressionList<User> exp;
		if (identity instanceof UsernamePasswordAuthUser) {
			exp = getUsernamePasswordAuthUserFind((UsernamePasswordAuthUser) identity);
		} else {
			exp = getAuthUserFind(identity);
		}
		return exp.findRowCount() > 0;
	}

	private static ExpressionList<User> getAuthUserFind(
			final AuthUserIdentity identity) {
		return find.where().eq("active", true)
				.eq("linkedAccounts.providerUserId", identity.getId())
				.eq("linkedAccounts.providerKey", identity.getProvider());
	}

	public static User findByAuthUserIdentity(final AuthUserIdentity identity) {
		if (identity == null) {
			return null;
		}
		if (identity instanceof UsernamePasswordAuthUser) {
			return findByUsernamePasswordIdentity((UsernamePasswordAuthUser) identity);
		} else {
			return getAuthUserFind(identity).findUnique();
		}
	}

	public static User findByUsernamePasswordIdentity(
			final UsernamePasswordAuthUser identity) {
		return getUsernamePasswordAuthUserFind(identity).findUnique();
	}

	private static ExpressionList<User> getUsernamePasswordAuthUserFind(
			final UsernamePasswordAuthUser identity) {
		return getEmailUserFind(identity.getEmail()).eq("linkedAccounts.providerKey", identity.getProvider());
	}

	public void merge(final User otherUser) {
		for (final LinkedAccount acc : otherUser.linkedAccounts) {
			this.linkedAccounts.add(LinkedAccount.create(acc));
		}
		// do all other merging stuff here - like resources, etc.

		// deactivate the merged user that got added to this one
		otherUser.active = false;
		Ebean.save(Arrays.asList(new User[] { otherUser, this }));
	}
	//----------------------------------------------------------
	public static User create(final AuthUser authUser) {
	//	Logger.info("user info create 1");
		final User user = new User();
		user.roles = Collections.singletonList(SecurityRole
				.findByRoleName(controllers.Application.USER_ROLE));
		// user.permissions = new ArrayList<UserPermission>();
		// user.permissions.add(UserPermission.findByValue("printers.edit"));
		user.active = true;
		user.lastLogin = new Date();
		user.linkedAccounts = Collections.singletonList(LinkedAccount
				.create(authUser));

		if (authUser instanceof EmailIdentity) {
			final EmailIdentity identity = (EmailIdentity) authUser;
			// Remember, even when getting them from FB & Co., emails should be
			// verified within the application as a security breach there might
			// break your security as well!
			user.email = identity.getEmail();
			user.emailValidated = false;
		}

		if (authUser instanceof NameIdentity) {
			final NameIdentity identity = (NameIdentity) authUser;
			final String name = identity.getName();
			if (name != null) {
				user.name = name;
			}
		}
		
		if (authUser instanceof FirstLastNameIdentity) {
		  final FirstLastNameIdentity identity = (FirstLastNameIdentity) authUser;
		  final String firstName = identity.getFirstName();
		  final String lastName = identity.getLastName();
		  if (firstName != null) {
		    user.firstName = firstName;
		  }
		  if (lastName != null) {
		    user.lastName = lastName;
		  }
		}
		/*
		if(Logger.isDebugEnabled())
			Logger.debug(GHelp.StringDump.dump(authUser));
			*/
		user.contrib=new Contributor(user);
		user.profileimage=DInitial.DefaultUserImage[DInitial.GENDER.UNKNOWN];
		user.gender=DInitial.GENDER.UNKNOWN;
		user.registerstatus=DInitial.SIGNUP_STAGE.JUST_REGISTERED;
		try{
			String IPAddress=play.mvc.Controller.request().remoteAddress();
			String initiallocation=DInitial.geoipreader.country(InetAddress.getByName(IPAddress)).getCountry().getName();
			user.registerip=IPAddress;
			user.registerlocation=initiallocation;
			user.location=initiallocation;
		}catch(Exception e){user.registerlocation="Unknown";}
		user.save();
		UserSubscriptions.InitSubscription(user.contrib);
		user.saveManyToManyAssociations("roles");
		// user.saveManyToManyAssociations("permissions");
		return user;
	}
	//----------------------------------------------------------
	public static User createDifferentUserName(final AuthUser authUser) {
		//Logger.info("user info create 2");
		final User user = new User();
		user.roles = Collections.singletonList(SecurityRole
				.findByRoleName(controllers.Application.USER_ROLE));
		// user.permissions = new ArrayList<UserPermission>();
		// user.permissions.add(UserPermission.findByValue("printers.edit"));
		user.active = true;
		user.lastLogin = new Date();
		user.linkedAccounts = Collections.singletonList(LinkedAccount
				.create(authUser));

		if (authUser instanceof EmailIdentity) {
			final EmailIdentity identity = (EmailIdentity) authUser;
			// Remember, even when getting them from FB & Co., emails should be
			// verified within the application as a security breach there might
			// break your security as well!
			user.email = identity.getEmail();
			user.emailValidated = false;
		}

		if (authUser instanceof NameIdentity) {
			final NameIdentity identity = (NameIdentity) authUser;
			final String name = identity.getName();
			//Logger.info(" name :"+name);
			if (name != null) {
				
				 ExpressionList<User> users= getUsersLikeName(name);
				 //Logger.info(" different than  null:"+users.toString());
				    Integer numberOfUsers = users.findRowCount();
				   boolean nameOk = false;
					while(!nameOk){	   
				    if(findByUserName(name+numberOfUsers)==null ){
				    	user.name = name+numberOfUsers;
				    	nameOk=true;
				    }
				    else{
				    	numberOfUsers++;
				    }
					}
			}
			else{
				Logger.info(" name is null ");
			
			}
		}
		
		if (authUser instanceof FirstLastNameIdentity) {
		  final FirstLastNameIdentity identity = (FirstLastNameIdentity) authUser;
		  final String firstName = identity.getFirstName();
		  final String lastName = identity.getLastName();
		  if (firstName != null) {
		    user.firstName = firstName;
		  }
		  if (lastName != null) {
		    user.lastName = lastName;
		  }
		}
		/*
		if(Logger.isDebugEnabled())
			Logger.debug(GHelp.StringDump.dump(authUser));
			*/
		user.contrib=new Contributor(user);
		user.profileimage=DInitial.DefaultUserImage[DInitial.GENDER.UNKNOWN];
		user.gender=DInitial.GENDER.UNKNOWN;
		user.registerstatus=DInitial.SIGNUP_STAGE.JUST_REGISTERED;
		try{
			String IPAddress=play.mvc.Controller.request().remoteAddress();
			String initiallocation=DInitial.geoipreader.country(InetAddress.getByName(IPAddress)).getCountry().getName();
			user.registerip=IPAddress;
			user.registerlocation=initiallocation;
			user.location=initiallocation;
		}catch(Exception e){user.registerlocation="Unknown";}
		user.save();
		UserSubscriptions.InitSubscription(user.contrib);
		user.saveManyToManyAssociations("roles");
		// user.saveManyToManyAssociations("permissions");
		return user;
	}

	public static void merge(final AuthUser oldUser, final AuthUser newUser) {
		User.findByAuthUserIdentity(oldUser).merge(
				User.findByAuthUserIdentity(newUser));
	}

	public static void merge(final User oldUser, final AuthUser newUser) {
		oldUser.merge(
				User.findByAuthUserIdentity(newUser));
	}
	
	
	public Set<String> getProviders() {
		final Set<String> providerKeys = new HashSet<String>(
				linkedAccounts.size());
		for (final LinkedAccount acc : linkedAccounts) {
			providerKeys.add(acc.providerKey);
		}
		return providerKeys;
	}
	public static void addLinkedAccount(final User oldUser,
			final AuthUser newUser) {
	Logger.info("Merging usrs");
		oldUser.linkedAccounts.add(LinkedAccount.create(newUser));
		oldUser.save();
	}

	public static void addLinkedAccount(final AuthUser oldUser,
			final AuthUser newUser) {
		final User u = User.findByAuthUserIdentity(oldUser);
		u.linkedAccounts.add(LinkedAccount.create(newUser));
		u.save();
	}

	public static void setLastLoginDate(final AuthUser knownUser) {
		final User u = User.findByAuthUserIdentity(knownUser);
		if(u!=null)
		{
			UserInfo.addlogininfo(u, play.mvc.Controller.request().remoteAddress());
			u.lastLogin = new Date();
			u.save();
		}
	}

	public static User findByEmail(final String email) {
		return getEmailUserFind(email).findUnique();
	}
	
	public static User findByUserName(final String uname) {
		return getUserFind(uname).findUnique();
	}
	
	private static ExpressionList<User> getUserFind(final String uname) {
		return find.where().eq("active", true).eq("name", uname);
	}
	
	private static ExpressionList<User> getUsersLikeName(final String uname) {
		return find.where().eq("active", true).like("name", uname+'%');
				
	}
	public static ExpressionList<User> getEmailUserFind(final String email) {
		return find.where().eq("active", true).eq("email", email);
	}

	public LinkedAccount getAccountByProvider(final String providerKey) {
		return LinkedAccount.findByProviderKey(this, providerKey);
	}

	public static void verify(final User unverified) {
		// You might want to wrap this into a transaction
		unverified.emailValidated = true;
		unverified.save();
		TokenAction.deleteByUser(unverified, Type.EMAIL_VERIFICATION);
	}

	public void changePassword(final UsernamePasswordAuthUser authUser,
			final boolean create) {
		LinkedAccount a = this.getAccountByProvider(authUser.getProvider());
		if (a == null) {
			if (create) {
				a = LinkedAccount.create(authUser);
				a.user = this;
			} else {
				throw new RuntimeException(
						"Account not enabled for password usage");
			}
		}
		a.providerUserId = authUser.getHashedPassword();
		a.save();
	}

	public void resetPassword(final UsernamePasswordAuthUser authUser,
			final boolean create) {
		// You might want to wrap this into a transaction
		this.changePassword(authUser, create);
		TokenAction.deleteByUser(this, Type.PASSWORD_RESET);
	}
	
	public void setimage(final String imageurl)
	{
		this.profileimage=imageurl;
		this.save();
	}
	
	public void setgender(final int gencode)
	{
		this.gender=gencode;
		this.save();
	}
	
	public void setusersemail(String email)
	{
		this.email=email;
		this.save();
	}
	
	public void updatestatus(Integer registerstatus){
		this.registerstatus=registerstatus;
		this.save();
	}
	
	public void savecategories(Set<Category> categories){
		SqlUpdate sqlu=Ebean.createSqlUpdate("delete from users_category where users_id = :uid ;").setParameter("uid", this.id) ;
		Ebean.execute(sqlu);
		this.categorypreferred.addAll(categories);
		this.saveManyToManyAssociations("categorypreferred");
	}
	
	public void setdefaultinfluencers()
	{
		List<Contributor> marktofollow = Contributor.GetTopInfluencers(0, DInitial.GENERIC_PAGE_SIZE, this.contrib).getList();
		Contributor presentuser=this.contrib;
		for(Contributor c : marktofollow)
			c.AddFollower(presentuser);
	}
	
	public String getusersimagename(boolean ext)
	{
		return "profilepic"+id+(ext?".png":"");
	}
	public String getNameNotEmpty()
	{	
	  String userNameNotEmpty ;
      if (this.firstName == null){
    	  userNameNotEmpty=this.name;
      }
      else{
    	  userNameNotEmpty= this.firstName + " "+ this.lastName;
      }
      
      return userNameNotEmpty;
	}	
	public String getusersimage()
	{
		return S3File.getUrl(User.class.getSimpleName(), profileimage, DInitial.IMAGESTORESIZE.AS_IT_IS.filestate);
	}
	
	public String getusersimagesmall()
	{
		return S3File.getUrl(User.class.getSimpleName(), profileimage, DInitial.IMAGESTORESIZE.THUMBNAIL_VERYSMALL.filestate);
	}
	
	public void savetocdn(String allotedname,File tempstorage)
	{
		File ActualSize=S3File.getimageresized(tempstorage, allotedname,DInitial.IMAGESTORESIZE.AS_IT_IS.filestate, DInitial.IMAGESTORESIZE.AS_IT_IS.Identifier, DInitial.IMAGESTORESIZE.AS_IT_IS.width, DInitial.IMAGESTORESIZE.AS_IT_IS.height),
		VSmallSize=S3File.getimageresized(tempstorage, allotedname, DInitial.IMAGESTORESIZE.THUMBNAIL_VERYSMALL.filestate, DInitial.IMAGESTORESIZE.THUMBNAIL_VERYSMALL.Identifier, DInitial.IMAGESTORESIZE.THUMBNAIL_VERYSMALL.width, DInitial.IMAGESTORESIZE.THUMBNAIL_VERYSMALL.height);
		
		S3File.createfile(S3Plugin.s3Bucket, User.class.getSimpleName(), allotedname, DInitial.IMAGESTORESIZE.AS_IT_IS.filestate, id, ActualSize);
		S3File.createfile(S3Plugin.s3Bucket, User.class.getSimpleName(), allotedname, DInitial.IMAGESTORESIZE.THUMBNAIL_VERYSMALL.filestate, id,VSmallSize );
		
		ActualSize.delete();
		VSmallSize.delete();
	}
	
	@Override
	public void delete(){
		/*
		List<String> deletequery=new ArrayList<String>();		
		//delete users_category
		deletequery.add("delete from users_category where users_id =" + id +" ");
		//delete users_user_permission
		deletequery.add("delete from users_user_permission where users_id =" + id +" ");
		//delete users_security_role
		deletequery.add("delete from users_security_role where users_id =" + id +" ");
		//delete collectionadmins
		deletequery.add("delete from collectionadmins where users_id =" + contrib.id +" ");
		//delete user_address
		deletequery.add("delete from user_address where user_id =" + id +" ");
		//delete token_action
		deletequery.add("delete from token_action where target_user_id =" + id +" ");
		//delete linked_account
		deletequery.add("delete from linked_account where user_id =" + id +" ");
		//delete cart
		deletequery.add("delete from cart where user_id =" + id +" ");
		//delete eventlog
		deletequery.add("delete from users_category where users_id =" + id +" ");
		//delete fsearch
		deletequery.add("delete from fsearch where userid_id =" + id +" ");
		//delete s3file
		deletequery.add("delete from s3file where modelref =" + id +" and filequalifier = "+User.class.getSimpleName()+"");
		//delete subscription
		deletequery.add("delete from usersubscriptions where subscriber_id =" + id +" ");
		*/
		//delete contributor
		Contributor cnt = Contributor.find.where().eq("user", id).findUnique();
		if(cnt!=null)
			cnt.delete();
		
		super.delete();
	}
	
}
