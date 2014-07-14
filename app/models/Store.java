package models;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.annotation.CreatedTimestamp;

import controllers.DInitial;
import controllers.GHelp;

import java.sql.Timestamp;
import java.util.*;

@Entity
public class Store extends  Model {
	
	@Id
	@GeneratedValue
	public long id;
	
	@Constraints.Required
	@CreatedTimestamp
	public Timestamp firstappearance;
	
	public String name;
	public String storeurl;
	
	public String location;
	public String servicearea;
	
	public Contributor owner;	
	
	
	@OneToMany(targetEntity=Product.class,mappedBy="pstore")	
	public List<Product> allproducts;
	
	@ManyToMany
	public List<UserCollection> collectlist;
	
	@ManyToMany
	public List<Contributor> customers;
	
	public Store(String name, String storeurl)
	{
		this.name=name;
		this.storeurl=storeurl;
		this.save();
	}
	
	public static Model.Finder<Long,Store> find = new Model.Finder<Long, Store>(Long.class, Store.class);
	
	public static Store FindStore(String SiteURL)
	{
		String storename=(SiteURL!=null?GHelp.getDomainName(SiteURL):null);
		if(storename==null)
		{
			Store refstore=find.where().eq("name","UPLOAD CHANNEL").findUnique();
			if(refstore!=null)
				return refstore;
			refstore=new Store("UPLOAD CHANNEL", "javascript:void(0);");
			return refstore;
		}
		Store refstore=find.where().eq("name",storename).findUnique();
		if(refstore!=null)
			return refstore;
		refstore=new Store(storename, "http://"+storename);
		return refstore;
	}
	
	//.........................................................
	
	public boolean AddCustomer(Contributor c)
	{		
		if(!this.customers.add(c))
			return false;
		this.save();
		return true;		
	}
	
	public boolean IsCustomer(Contributor c)
	{		
		return this.customers.contains(c);
	}
	
	public boolean RemoveCustomer(Contributor c)
	{
		if(!this.customers.remove(c))
			return false;
		this.save();
		return true;
	}
	
	
	
	public static void AddFollower(Store store,Contributor follower)
	{
		if(!Follow.IsStoreFollower(store.id, follower.id))
			Follow.AddFollower(store.id, follower.id,1);			
	}
	
	public static Boolean RemoveFollower(Store store,Contributor follower)
	{
		return Follow.RemoveStoreFollower(store.id, follower.id);				
	}
	
	
	//Query	    
	
	
	public static List<Follow> Followers(Store PresentStore)
	{
		return  Follow.GetStoreFollowers(PresentStore.id);   
	}

	public static int FollowersCount(Store PresentStore)
	{
		return Follow.GetStoreFollowersCount(PresentStore.id);
	}
	
	public static Page<Contributor> FollowerFollowing(int subcat,int page, int pageSize, String un,Store st) {
		String getfollowers="SELECT contributor.id FROM follow, contributor, users WHERE  users.active = true AND  follow.follower = contributor.id AND contributor.user_id = users.id AND follow.type = :type AND follow.leader = :leader AND users.name like :uname";
        RawSql rawSql =	RawSqlBuilder.parse(getfollowers).columnMapping("contributor.id", "id").create();
        return Contributor.find.setRawSql(rawSql).setParameter("type", DInitial.FOLLOWERTYPE.STORE).setParameter("leader", st.id).setParameter("uname", "%"+un+"%").findPagingList(pageSize).getPage(page);        
    }
	
	public static Page<UserCollection> StoreCollections(int page, int pageSize, String filter,Store st) {
		return  UserCollection.find.where().eq("storelist.id", st.id)
				.ilike("colname", "%" + filter + "%").orderBy("timeofadd desc").findPagingList(pageSize).getPage(page);
    }
	public static Page<Contributor> StoreCustomers(int page, int pageSize, String filter,Store st) {
		String getcustomers="SELECT    store_contributor.contributor_id FROM    store_contributor,    contributor,    users WHERE    store_contributor.contributor_id = contributor.id AND   contributor.user_id = users.id AND   users.active = true AND    users.name like :uname  AND  store_contributor.store_id=:id ";
		RawSql rawSql =	RawSqlBuilder.parse(getcustomers).columnMapping("store_contributor.contributor_id", "id").create();
	    return Contributor.find.setRawSql(rawSql).setParameter("id", st.id).setParameter("uname", "%"+filter+"%").findPagingList(pageSize).getPage(page);        
	}
	
	//Search Result Display
	public static Page<Store> MatchedStores(int page, int pageSize, String filter) {
        return 
            find.where()
                .ilike("name", "%" + filter + "%")
                .orderBy("firstappearance desc")
                .findPagingList(pageSize)
                .getPage(page);
    }
	
	public static Page<Product> FilteredProduct(int page, int pageSize, String pn,long gd,int prindex,long storeid) {
        ExpressionList<Product> fp=Product.find.where().ilike("productname", "%" + pn + "%");
        if(gd!=0)
        	fp=fp.eq("gender", gd);
        fp=fp.ge("Pricetag", DInitial.PR[prindex][0]).le("Pricetag", DInitial.PR[prindex][1]);
        if(storeid!=0)
        	fp=fp.eq("pstore.id", storeid);
		return	fp.orderBy("timeofadd desc").findPagingList(pageSize).getPage(page);
    }
	
	public static int MatchedStoresCount(String filter)
	{	return find.where().ilike("name", "%" + filter + "%").findRowCount();	}
	
	public static List<Product> RecentProducts(Store store,int contentcount)
	{
		return Product.find.where().eq("pstore.id", store.id).eq("alive", true).orderBy("timeofadd desc").findPagingList(contentcount).getPage(0).getList();
	}

}
