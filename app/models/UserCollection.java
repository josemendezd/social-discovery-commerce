package models;

import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import javax.persistence.*;

import org.hibernate.validator.constraints.URL;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.Sql;

import controllers.Application;
import controllers.DInitial;
import controllers.GHelp;
import controllers.routes;

import java.util.*;

@Entity
public class UserCollection extends  Model {
	@Id
	@GeneratedValue
	public Long id;
	
	@Constraints.Required
	public String colname; 	
	
	@Constraints.Required
	public boolean isprivate;
	
	@URL
	public String coverimage;
	
	@ManyToOne
	public Contributor contributor;
	
	@ManyToMany(targetEntity=Contributor.class,mappedBy="collectadmin",cascade=CascadeType.ALL)
	public List<Contributor> collectionadmin;
	
	@Constraints.Required
	@CreatedTimestamp
	public Date timeofadd;
	
	@ManyToMany//(targetEntity=Product.class)
	/*
    @JoinTable(name="USER_COLLECTION_PRODUCT",
        joinColumns=
            @JoinColumn(name="USER_COLLECTION_ID", referencedColumnName="ID"),
        inverseJoinColumns=
            @JoinColumn(name="PRODUCT_ID", referencedColumnName="ID")
        )*/
	public List<Product> productlist;
	
	@ManyToMany(targetEntity=Store.class,mappedBy="collectlist")
	public List<Store> storelist;
	
    public static Model.Finder<Long,UserCollection> find = new Finder<Long, UserCollection>(Long.class, UserCollection.class);
	
	public UserCollection(Contributor c,String cname) {
		this.colname=cname;
		this.contributor=c;
		this.isprivate = false;
		this.productlist=new ArrayList<Product>();
		this.save();
	}
	
	public UserCollection(Contributor c,String cname,boolean isprivate) {
		this.colname=cname;
		this.contributor=c;
		this.isprivate = isprivate;
		this.productlist=new ArrayList<Product>();
		this.save();
	}
	
	public boolean AddProduct(Product px,Contributor c)
	{
		if(!this.contributor.equals(c) && !this.collectionadmin.contains(c))
			return false;
		if(!this.productlist.add(px))
			return false;
		if(!this.storelist.contains(px.pstore))
			this.storelist.add(px.pstore);
		this.save();
		/*
		List<Product> pcol=Product.find.all();
		for (Product prod : pcol) {
		    Logger.info("Products's name is " + prod.productname);
		    for (UserCollection ucl : prod.collectList) {
		        Logger.info(prod.productname + " in collection " + ucl.colname);
		    }
		}
		*/
		//this.saveManyToManyAssociations("productlist");
		//px.refresh();
		return true;		
	}
	
	public boolean RemoveProduct(Product px,Contributor c)
	{
		if(!this.contributor.equals(c) && !this.collectionadmin.contains(c))
			return false;
		if(!this.productlist.remove(px))
			return false;		
		if(this.storelist.contains(px.pstore) && !(find.where().eq("storelist", px.pstore).findRowCount()>1))
			this.storelist.remove(px.pstore);
		this.save();
		//this.saveManyToManyAssociations("productlist");
		//px.refresh();
		return true;
	}
	
	//Static Modifiers
	public static int ProductCount(UserCollection uc){return uc.productlist.size();}
	
	public static void DeleteCollection(UserCollection uc){
		String[] commentlistsql={
				"DELETE FROM socommentsco WHERE collection_id = "+uc.id,
				"DELETE FROM sosuggestsco WHERE collection_id = "+uc.id,
				"DELETE FROM collectionadmins WHERE user_collection_id = "+uc.id,
				"DELETE FROM store_user_collection WHERE user_collection_id = "+uc.id,
				"DELETE FROM user_collection_product WHERE user_collection_id = "+uc.id,
				"DELETE FROM collection_comment WHERE collection_id = "+uc.id				
		};
		for(String updatecommands:commentlistsql)
			Ebean.createSqlUpdate(updatecommands).execute();				
		uc.delete();
	}
	
	public static boolean CollectionExists(String cn,Contributor c)
	{
		return find.where().eq("contributor.id",c.id).eq("colname",cn).findRowCount()>0;
	}
	
	public static boolean DoesExist(UserCollection uc,Product px)
	{
		return find.where().eq("productlist.id",px.id).findRowCount()>0;
	}
	
	//.........................................................
	
	public static void AddFollower(UserCollection uc,Contributor follower)
	{
		Follow.AddFollower(uc.id, follower.id,2);			
	}
	
	public static Boolean RemoveFollower(UserCollection uc,Contributor follower)
	{
		return Follow.RemoveCollectionFollower(uc.id, follower.id);				
	}
	
	public static Boolean IsCollectionAdmin(UserCollection uc,Contributor c)
	{
		if(c==null)
			return false;
		return uc.contributor.equals(c) || uc.collectionadmin.contains(c);
	}
	
	//Query	    
	
	
	public static List<Follow> Followers(UserCollection uc)
	{
		return  Follow.GetCollectionFollowers(uc.id);   
	}

	public static int FollowersCount(UserCollection uc)
	{
		return Follow.GetCollectionFollowersCount(uc.id);
	}
	
	public static Page<Contributor> FollowerFollowing(int subcat,int page, int pageSize, String un,UserCollection uc) {
		String getfollowers="SELECT contributor.id FROM follow, contributor, users WHERE  users.active = true AND  follow.follower = contributor.id AND contributor.user_id = users.id AND follow.type = :type AND follow.leader = :leader AND users.name like :uname";
        RawSql rawSql =	RawSqlBuilder.parse(getfollowers).columnMapping("contributor.id", "id").create();
        return Contributor.find.setRawSql(rawSql).setParameter("type", DInitial.FOLLOWERTYPE.COLLECTION).setParameter("leader", uc.id).setParameter("uname", "%"+un+"%").findPagingList(pageSize).getPage(page);        
    }
	
	
	
	public static Page<UserCollection> MatchedCollections(int page, int pageSize, String filter) {
		ExpressionList<UserCollection> el= find.where();
		if(!GHelp.VerifyACL(DInitial.CURRENT_ACL.ADMINISTRATOR_ONLY, null)){ el= el.eq("isprivate", false); }
		
		return el.ilike("colname", "%" + filter + "%")
                .orderBy("timeofadd desc")
                .findPagingList(pageSize)
                .getPage(page);
    }
	
	public static Page<Product> FilteredProduct(int page, int pageSize, String pn,long gd,int prindex,long colid) {
        ExpressionList<Product> fp=Product.find.where().ilike("productname", "%" + pn + "%").eq("alive", true);
        if(gd!=0)
        	fp=fp.eq("gender", gd);
        fp=fp.ge("Pricetag", DInitial.PR[prindex][0]).le("Pricetag", DInitial.PR[prindex][1]);
        if(colid!=0)
        	fp=fp.eq("collectlist.id", colid);
		return	fp.orderBy("timeofadd desc").findPagingList(pageSize).getPage(page);
    }
	
	public static int MatchedCollectionsCount(String filter) {
		ExpressionList<UserCollection> el= find.where();
		if(!GHelp.VerifyACL(DInitial.CURRENT_ACL.ADMINISTRATOR_ONLY, null)){ el= el.eq("isprivate", false); }
		return el.ilike("colname", "%" + filter + "%").findRowCount();
    }
	
	public static Page<UserCollection> UsersCollectionPage(Contributor c,int page,int pageSize) {
		ExpressionList<UserCollection> el= find.where();
		if(!GHelp.VerifyACL(DInitial.CURRENT_ACL.ADMIN_SELF, c.user)){ el= el.eq("isprivate", false); }
        return el.eq("contributor.id", c.id).findPagingList(pageSize).getPage(page);
    }
	
	public static Page<UserCollection> ProductCollectionPage(int subcat,int page, int pageSize, String cn,Product p) {
        
        
        ExpressionList<UserCollection> fp=find.where();
        if(!GHelp.VerifyACL(DInitial.CURRENT_ACL.ADMINISTRATOR_ONLY, null)){ fp= fp.eq("isprivate", false); }
        switch(subcat)
        {
        case DInitial.PRODUCT_RELATION_QUERY.COLLECTIONS:
        	fp=fp.eq("productlist.id", p.id);
        	break;
        	default:
        		break;
        }
        fp=fp.ilike("colname", "%" + cn + "%");        
        
		return	fp.findPagingList(pageSize).getPage(page);
    }
	
	public static List<Product> RecentProducts(UserCollection colect,int contentcount)
	{
		return Product.find.where().eq("alive", true).eq("collectlist.id", colect.id).orderBy("timeofadd desc").findPagingList(contentcount).getPage(0).getList();
	}
	
	public static String UserCollectionStatus(Contributor c,Product p)
	{
		String sqlstring="SELECT string_agg(row_to_json( ROW( UC.ID,UC.COLNAME,(UC.ID IN (SELECT UCP.USER_COLLECTION_ID FROM USER_COLLECTION_PRODUCT UCP WHERE UCP.PRODUCT_ID="+p.id+"))))::text,',') AS JO  FROM USER_COLLECTION UC WHERE UC.CONTRIBUTOR_ID="+c.id+
				  " OR UC.ID IN (SELECT CA.USER_COLLECTION_ID FROM COLLECTIONADMINS CA WHERE CA.CONTRIBUTOR_ID="+c.id+")";
		return Ebean.createSqlQuery(sqlstring).findUnique().getString("JO");
		
		//return Ebean.find(x.class).setRawSql(rawSql).findUnique().y;
	}
	
	public void SetCoverImage(String imageaddress)
	{
		this.coverimage=imageaddress;
		this.save();
	}
	
}