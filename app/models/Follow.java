package models;

import java.util.*;

import javax.persistence.*;

import controllers.DInitial;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Follow extends  Model
{
	
	@Id
	@GeneratedValue
	public long id;
	
	@Required
	public long leader;	
	
	@Required
	public long follower;
	
	
	public int type;
	
	public Follow(long leader,long follower, int type)
	{
		
		this.leader=leader;
		this.follower=follower;
		this.type=type;
	}
	
	
	
	//Static Part
	public static Finder<Long, Follow> find = new Finder<Long, Follow>(Long.class, Follow.class);
	public static void AddFollower(long leader,long follower,int type)
	{
		Follow f=new Follow(leader, follower,type);
		f.save();
	}
	
	//Section-II Start-->
	//Retrieve list of users following an individual,store or collection
	
	public static List<Follow> GetFollowers(long c)
	{
		return find.where().eq("leader", c).eq("type", DInitial.FOLLOWERTYPE.INDIVIDUAL).findList();
	}
	
	public static int GetFollowersCount(long c)
	{
		return find.where().eq("leader", c).eq("type", DInitial.FOLLOWERTYPE.INDIVIDUAL).findRowCount();
	}

	public static List<Follow> GetStoreFollowers(long c)
	{
		return find.where().eq("leader", c).eq("type", DInitial.FOLLOWERTYPE.STORE).findList();
	}
	
	public static int GetStoreFollowersCount(long c)
	{
		return find.where().eq("leader", c).eq("type", DInitial.FOLLOWERTYPE.STORE).findRowCount();
	}

	public static List<Follow> GetCollectionFollowers(long c)
	{
		return find.where().eq("leader", c).eq("type", DInitial.FOLLOWERTYPE.COLLECTION).findList();
	}
	
	public static int GetCollectionFollowersCount(long c)
	{
		return find.where().eq("leader", c).eq("type", DInitial.FOLLOWERTYPE.COLLECTION).findRowCount();
	}
	
	//Section-III Start-->
	//Retrieve list of user,store,collections being followed by an individual
	
	public static List<Follow> GetLeaders(long c)
	{
		return find.where().eq("follower", c).eq("type", DInitial.FOLLOWERTYPE.INDIVIDUAL).findList();
	}
	
	public static int GetLeadersCount(long c)
	{
		return find.where().eq("follower", c).eq("type", DInitial.FOLLOWERTYPE.INDIVIDUAL).findRowCount();
	}
	
	public static List<Follow> Getstores(long c)
	{
		return find.where().eq("follower", c).eq("type", DInitial.FOLLOWERTYPE.STORE).findList();
	}
	
	public static int GetStoresCount(long c)
	{
		return find.where().eq("follower", c).eq("type", DInitial.FOLLOWERTYPE.STORE).findRowCount();
	}
	
	public static List<Follow> GetCollections(long c)
	{
		return find.where().eq("follower", c).eq("type", DInitial.FOLLOWERTYPE.COLLECTION).findList();
	}
	
	public static int GetCollectionsCount(long c)
	{
		return find.where().eq("follower", c).eq("type", DInitial.FOLLOWERTYPE.COLLECTION).findRowCount();
	}
	
	//Section-III Over....
	
	public static boolean IsFollower(long leader,long follower)
	{
		return find.where().eq("leader", leader).eq("follower",follower).eq("type", DInitial.FOLLOWERTYPE.INDIVIDUAL).findRowCount()>0;
	}
	
	public static boolean RemoveFollower(long leader,long follower)
	{
		if(!IsFollower(leader, follower))
			return false;
		find.where().eq("leader", leader).eq("follower",follower).eq("type", DInitial.FOLLOWERTYPE.INDIVIDUAL).findUnique().delete();		
		return true;
	}

	public static boolean IsStoreFollower(long leader,long follower)
	{
		return find.where().eq("leader", leader).eq("follower",follower).eq("type", DInitial.FOLLOWERTYPE.STORE).findRowCount()>0;
	}
	
	public static boolean RemoveStoreFollower(long leader,long follower)
	{
		if(!IsStoreFollower(leader, follower))
			return false;
		find.where().eq("leader", leader).eq("follower",follower).eq("type", DInitial.FOLLOWERTYPE.STORE).findUnique().delete();		
		return true;
	}

	public static boolean IsCollectionFollower(long leader,long follower)
	{
		return find.where().eq("leader", leader).eq("follower",follower).eq("type", DInitial.FOLLOWERTYPE.COLLECTION).findRowCount()>0;
	}
	
	public static boolean RemoveCollectionFollower(long leader,long follower)
	{
		if(!IsCollectionFollower(leader, follower))
			return false;
		find.where().eq("leader", leader).eq("follower",follower).eq("type", DInitial.FOLLOWERTYPE.COLLECTION).findUnique().delete();		
		return true;
	}
	
	
	
}
