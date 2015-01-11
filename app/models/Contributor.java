package models;
import play.Logger;
import play.data.format.Formats;
import play.db.ebean.Model;

import javax.persistence.*;

import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Junction;
import com.avaje.ebean.Page;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;

import controllers.DInitial;

import java.util.*;

@Entity
public class Contributor extends  Model {
	
	@Id
	@GeneratedValue
	public long id;
	
	@OneToOne
	public User user;
	
	@ManyToMany//(targetEntity=Product.class,mappedBy="Owners", cascade=CascadeType.ALL)
	@JoinTable(name = "ownedproduct")
	public List<Product> Owned;
	
	@ManyToMany//(targetEntity=Product.class,mappedBy="Likers", cascade=CascadeType.ALL)
	@JoinTable(name = "likedproduct")
	public List<Product> Liked;
	
	@ManyToMany//(targetEntity=Product.class,mappedBy="Wanters", cascade=CascadeType.ALL)
	@JoinTable(name = "wantedproduct")
	public List<Product> Wanted ;
	
	@OneToMany//(mappedBy="contributor", cascade=CascadeType.ALL)//(cascade=CascadeType.ALL)
	public List<UserCollection> UCollections = new ArrayList<UserCollection>();		
	
	@ManyToMany//(targetEntity=Product.class,mappedBy="Wanters", cascade=CascadeType.ALL)
	@JoinTable(name = "collectionadmins")
	public List<UserCollection> collectadmin ;	
	
	public static Model.Finder<Long,Contributor> find = new Model.Finder<Long, Contributor>(Long.class, Contributor.class);
	
	
	//	Operations
	public Contributor(User PresentUser)
	{
		this.user=PresentUser;
	}
	//Insert Owner
	public boolean AddOwner(Product p)
	{
		if(this.Owned.contains(p)|| !this.Owned.add(p))
			return false;
		this.save();
		return true;
	}
	
	public boolean RemoveOwner(Product p)
	{
		if(!this.Owned.contains(p) || !this.Owned.remove(p))
			return false;
		this.save();
		return true;
	}
	
	//Insert Lover
	public boolean AddLover(Product p)
	{
		if(this.Liked.contains(p)|| !this.Liked.add(p))
			return false;
		this.save();
		return true;
	}
	
	public boolean RemoveLover(Product p)
	{
		if(!this.Liked.contains(p) || !this.Liked.remove(p))
			return false;
		this.save();
		return true;
	}
	
	//Insert Wanter
	public boolean AddWanter(Product p)
	{
		if(this.Wanted.contains(p)|| !this.Wanted.add(p))
			return false;
		this.save();
		return true;
	}
	
	public boolean RemoveWanter(Product p)
	{
		if(!this.Wanted.contains(p) || !this.Wanted.remove(p))
			return false;
		this.save();
		return true;
	}
	
	//Insert a new collection
	public boolean AddUCollection(String ucname)
	{
		if(UserCollection.find.where().contains("colname", ucname).findRowCount()>0)
			return false;
		UserCollection uc = new UserCollection(this, ucname);
		if(uc==null || !this.UCollections.add(uc))
			return false;
		this.save();
		return true;		
	}
	public boolean RemoveUCollection(String ucname)
	{
		UserCollection uc = UserCollection.find.where().eq("colname", ucname).findUnique();
		if(uc==null || !this.UCollections.remove(uc))
			return false;
		this.save();
		return true;		
	}
	
	//.........................................................
	
	public void AddFollower(Contributor follower)
	{
		if(!Follow.IsFollower(this.id, follower.id))
		{
			Follow.AddFollower(this.id, follower.id,0);
			models.Notifications.SOFollows.ReportEvent(follower, this);
		}
	}
	
	public Boolean RemoveFollower(Contributor follower)
	{
		return Follow.RemoveFollower(this.id, follower.id);				
	}
	
	//Query
    
	//find following
	public static List<Follow> Leaders(Contributor PresentUser)
	{
		return  Follow.GetLeaders(PresentUser.id);   
	}
	
	public static List<Follow> Followers(Contributor PresentUser)
	{
		return  Follow.GetFollowers(PresentUser.id);   
	}
	
	public static int LeadersCount(Contributor c)
	{
		return Follow.GetLeadersCount(c.id);
	}

	public static int FollowersCount(Contributor c)
	{
		return Follow.GetFollowersCount(c.id);
	}
	
	public static int  UsersCollections(Contributor c)
	{ 
		return UserCollection.find.where().eq("contributor.id", c.id).findRowCount();		
	}
	
	public static Page<Contributor> MatchedUsers(int page, int pageSize, String filter) {
        return 
            find.where()
                .ilike("user.name", "%" + filter + "%")
                .eq("user.active", true)
                .orderBy("user.lastLogin desc")
                .findPagingList(pageSize)
                .getPage(page);
    }
	
	public static int MatchedUsersCount(String filter) {
        return   find.where().ilike("user.name", "%" + filter + "%").eq("user.active", true).findRowCount();
    }
	
	//For the user Page
	public static Page<Product> FilteredProduct(int subcat,int page, int pageSize, String pn,long gd,int prindex,Contributor c) {
        ExpressionList<Product> fp=Product.find.where().eq("alive", true);
        switch(subcat)
        {
        case 1:
        	fp=fp.eq("Likers.id", c.id);
        	break;
        case 2:
        	fp=fp.eq("Wanters.id", c.id);
        	break;
        case 3:
        	fp=fp.eq("Owners.id", c.id);
        	break;
        	default:
        		break;
        }
        fp=fp.ilike("productname", "%" + pn + "%");
        if(gd!=0)
        	fp=fp.eq("gender", gd);
        fp=fp.ge("Pricetag", DInitial.PR[prindex][0]).le("Pricetag", DInitial.PR[prindex][1]);
        
		return	fp.orderBy("timeofadd desc").findPagingList(pageSize).getPage(page);
    }
	
	//For the Product Page
	//For the user Page
	public static Page<Contributor> FilteredLWT(int subcat,int page, int pageSize, String un,long gd,Product p) {
        ExpressionList<Contributor> fp=Contributor.find.where();
        switch(subcat)
        {
        case DInitial.PRODUCT_RELATION_QUERY.LOVERS:
        	fp=fp.eq("Liked.id", p.id);
        	break;
        case DInitial.PRODUCT_RELATION_QUERY.WANTERS:
        	fp=fp.eq("Wanted.id", p.id);
        	break;
        case DInitial.PRODUCT_RELATION_QUERY.CONSUMERS:
        	fp=fp.eq("Owned.id", p.id);
        	break;
        	default:
        		break;
        }
        fp=fp.eq("user.active", true).ilike("user.name", "%" + un + "%");
        if(gd!=0)
        	fp=fp.eq("user.gender", gd);
        
		return	fp.findPagingList(pageSize).getPage(page);
    }
	
	public static Page<Product> UsersLWT(int subcat,int page, int pageSize, String pn,long gd,int prindex,Contributor c) {
        ExpressionList<Product> fp=Product.find.where().eq("alive", true);
        switch(subcat)
        {
        case DInitial.PRODUCT_RELATION_QUERY.LOVERS:
        	fp=fp.eq("Likers.id",c.id );
        	break;
        case DInitial.PRODUCT_RELATION_QUERY.WANTERS:
        	fp=fp.eq("Wanters.id", c.id);
        	break;
        case DInitial.PRODUCT_RELATION_QUERY.CONSUMERS:
        	fp=fp.eq("Owners.id", c.id);
        	break;
        	default:
        		break;
        }
        fp=fp.ilike("productname", "%" + pn + "%");
        if(gd!=0)
        	fp=fp.eq("gender", gd);
        fp=fp.ge("Pricetag", DInitial.PR[prindex][0]).le("Pricetag", DInitial.PR[prindex][1]);
        
		return	fp.orderBy("timeofadd desc").findPagingList(pageSize).getPage(page);
    }
	
	public static Page<UserCollection> UsersPersonalCollections(int page, int pageSize, String cn,Contributor c) {
		RawSql rawSql =	RawSqlBuilder.parse("select uc.id from (user_collection uc left join collectionadmins ca  on ca.user_collection_id=uc.id)   where (uc.contributor_id = :id OR ca.contributor_id = :id) AND uc.colname ilike :colname").columnMapping("uc.id", "id").create();
		return Ebean.find(UserCollection.class).setRawSql(rawSql).setParameter("id", c.id).setParameter("colname", "%"+cn+"%").findPagingList(pageSize).getPage(page);       
    }
	
	public static int UsersPersonalCollectionsCount(Contributor c) {
		RawSql rawSql =	RawSqlBuilder.parse("select uc.id from (user_collection uc left join collectionadmins ca  on ca.user_collection_id=uc.id)   where (uc.contributor_id = :id OR ca.contributor_id = :id)").columnMapping("uc.id", "id").create();
		return Ebean.find(UserCollection.class).setRawSql(rawSql).setParameter("id", c.id).findList().size();       
    }
	
	public static Page<Contributor> FollowerFollowing(int subcat,int page, int pageSize, String un,Contributor c) {
        String getfollowers="SELECT contributor.id FROM follow, contributor, users WHERE follow.follower = contributor.id AND users.active = true AND  contributor.user_id = users.id AND follow.type = :type AND follow.leader = :leader AND users.name ilike :uname";
        String getleaders="SELECT contributor.id FROM follow, contributor, users WHERE users.active = true AND  contributor.id = follow.leader AND contributor.user_id = users.id AND follow.type = :type AND follow.follower = :follower AND users.name ilike :uname";
		
        
        switch(subcat)
        {
        case DInitial.USER_RELATION_QUERY.FOLLOWING:
        	RawSql rawSqlFollowing =	RawSqlBuilder.parse(getleaders).columnMapping("contributor.id", "id").create();
        	return find.setRawSql(rawSqlFollowing).setParameter("type", DInitial.FOLLOWERTYPE.INDIVIDUAL).setParameter("follower", c.id).setParameter("uname", "%"+un+"%").findPagingList(pageSize).getPage(page);
        	
        case DInitial.USER_RELATION_QUERY.FOLLOWERS:
        	RawSql rawSql =	RawSqlBuilder.parse(getfollowers).columnMapping("contributor.id", "id").create();
        	return find.setRawSql(rawSql).setParameter("type", DInitial.FOLLOWERTYPE.INDIVIDUAL).setParameter("leader", c.id).setParameter("uname", "%"+un+"%").findPagingList(pageSize).getPage(page);
        default:
        	break;
        }
        
		return null;
    }
	public static Page<Store> FollowingStores(int page, int pageSize, String sn,Contributor c) {
        //return Store.find.where().eq("follower", c.id).eq("type", DInitial.FOLLOWERTYPE.STORE).ilike("name", "%" + sn + "%").findPagingList(pageSize).getPage(page);
		RawSql rawSql =	RawSqlBuilder.parse("SELECT  store.id,store.name FROM follow,contributor,users,store WHERE users.active = true AND  follow.follower = contributor.id AND  contributor.user_id = users.id AND  store.id = follow.leader AND  follow.type = :type AND   contributor.id = :follower AND store.name ilike :sname ").columnMapping("store.id", "id").create();
		return Store.find.setRawSql(rawSql).setParameter("type", DInitial.FOLLOWERTYPE.STORE).setParameter("follower", c.id).setParameter("sname", "%"+sn+"%").findPagingList(pageSize).getPage(page);
    }
	
	
	public static List<Product> RecentLikedProducts(Contributor con,int contentcount)
	{
		return Product.find.where().eq("Likers.id", con.id).orderBy("timeofadd desc").findPagingList(contentcount).getPage(0).getList();
	}
	
	public static Page<Contributor> GetTopInfluencers(int page, int pageSize,Contributor c)
	{
		String querystring="select distinct contributor.id from contributor , likedproduct where contributor.id = likedproduct.contributor_id";// contributors that liked at least one product.
		RawSql rawSql =	RawSqlBuilder.unparsed(querystring).columnMapping("contributor.id", "id").create();
    	
	//	Logger.info(String.format("sql - - - -" + rawSql.getSql().toString() ));
		
		return find.setRawSql(rawSql).findPagingList(pageSize).getPage(page);
	}
	
	public static List<Contributor> GetSocialFriends(String provider,String listedids)
	{
		String querystring="SELECT contributor.id FROM users, contributor, linked_account WHERE  users.active = true AND contributor.user_id = users.id AND   linked_account.user_id = users.id AND   linked_account.provider_user_id in ("+listedids+") AND   linked_account.provider_key = '"+provider+"'   ; ";//TODO: Add right query
	//	String querystring="SELECT contributor.id FROM users, contributor, linked_account WHERE  users.active = true AND contributor.user_id = users.id AND   linked_account.user_id = users.id 
	
	//	Logger.info(String.format(" - - querystring:"+ querystring + " - "));
		RawSql rawSql =	RawSqlBuilder.parse(querystring).columnMapping("contributor.id", "id").create();
		
	//	Logger.info(String.format(" - - rawSql:"+ rawSql + " - - "));
    	return find.setRawSql(rawSql).findList();
	}
	
}
