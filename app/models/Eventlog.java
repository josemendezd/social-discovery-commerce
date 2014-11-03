package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;


import com.avaje.ebean.*;
import com.avaje.ebean.Query;
import com.avaje.ebean.annotation.CreatedTimestamp;

import controllers.DInitial;

import java.sql.Timestamp;
import java.util.*;

@Entity
public class Eventlog extends Model {
	
	@Id
	@GeneratedValue
	public long id;
	
	@Constraints.Required
	public long eventtype;
	
	@Constraints.Required
	@CreatedTimestamp
	public Timestamp eventtime;
	
	@ManyToOne
	@Constraints.Required
	public Product productinv;
	
	@ManyToOne
	@Constraints.Required
	public Contributor userinv;
	
	@ManyToOne
	public UserCollection collectinv;
	
	@ManyToOne
	public Store storeinv;
	
	public Eventlog(long Eventtype,Product ProductInvolved,Contributor UserInvolved,UserCollection CollectionInvolved,Store StoreInvolved)
	{
		
		this.eventtype=Eventtype;
		this.productinv=ProductInvolved;
		this.userinv=UserInvolved;
		this.collectinv=CollectionInvolved;
		this.storeinv=StoreInvolved;
		this.save();
	}
	
	public static Model.Finder<Long,Eventlog> find = new Model.Finder<Long, Eventlog>(Long.class, Eventlog.class);
	
	public static Eventlog ReportEvent(int Eventtype,Product ProductInvolved,Contributor UserInvolved,UserCollection CollectionInvolved,Store StoreInvolved)
	{
		switch(Eventtype)
		{
		case DInitial.PRODUCT_ADDED:
			if(find.where().eq("eventtype", Eventtype).eq("productinv", ProductInvolved).eq("userinv", UserInvolved).findRowCount()>0)
				return null;
			break;
		case DInitial.PRODUCT_LOVED:
			if(find.where().eq("eventtype", Eventtype).eq("productinv", ProductInvolved).eq("userinv", UserInvolved).findRowCount()>0)
				return null;
			break;
		case DInitial.PRODUCT_COLLECTED:
			if(find.where().eq("eventtype", Eventtype).eq("productinv", ProductInvolved).eq("userinv", UserInvolved).eq("collectinv", CollectionInvolved).findRowCount()>0)
				return null;
			break;
		default:
			break;
		}
		return new Eventlog(Eventtype, ProductInvolved, UserInvolved, CollectionInvolved, StoreInvolved);
	}	

	
	public static boolean RemoveEvent(long Eventtype,Product ProductInvolved,Contributor UserInvolved)
	{
		Eventlog delevent=find.where().eq("eventtype", Eventtype).eq("productinv", ProductInvolved).eq("userinv", UserInvolved).findUnique();
		if(delevent==null)
			return false;
		delevent.delete();
		return true;
	}
	
	public static boolean RemoveEventCollection(long Eventtype,Product ProductInvolved,Contributor UserInvolved, UserCollection uc) {
		Eventlog delevent=find.where().eq("eventtype", Eventtype).eq("productinv", ProductInvolved).eq("userinv", UserInvolved).eq("collectinv", uc).findUnique();
		if(delevent==null)
			return false;
		delevent.delete();
		return true;
	}
	
	public static void RemoveAllProductEvent(Product ProductInvolved)
	{
		SqlUpdate down = Ebean.createSqlUpdate("DELETE FROM EVENTLOG WHERE PRODUCTINV_ID = :id").setParameter("id", ProductInvolved.id);
		down.execute();
	}
	public static void RemoveAllCollectionEvent(UserCollection ucoll)
	{
		SqlUpdate down = Ebean.createSqlUpdate("DELETE FROM EVENTLOG WHERE COLLECTINV_ID = :id").setParameter("id", ucoll.id);
		down.execute();
	}
	public static void RemoveAllContributorEvent(Contributor UserInvolved)
	{
		SqlUpdate down = Ebean.createSqlUpdate("DELETE FROM EVENTLOG WHERE USERINV_ID = :id").setParameter("id", UserInvolved.id);
		down.execute();
	}
	
	public static Page<Product> RelevanceFeed(int page, int pageSize) {
		return Ebean.find(Product.class).where().in("id", DInitial.productIds).findPagingList(pageSize).getPage(page);		
    }
	
	public static Page<Product> RelevanceFeed(int page, int pageSize,String filter) {
		/*
		RawSql rawSql =	RawSqlBuilder.parse("SELECT  PRODUCTINV_ID FROM EVENTLOG WHERE EVENTTYPE =2 GROUP BY PRODUCTINV_ID ORDER BY" +
				"  SUM(EVENTTIME BETWEEN DATEADD('HOUR', -1,NOW()) AND  NOW()) DESC, MAX(EVENTTIME)")
		// map result columns to bean properties
		.columnMapping("PRODUCTINV_ID", "productinv.id")
		.create();
		*/
		//String sqlquery="SELECT ID , PRODUCTNAME , CURRENCY , PRICETAG , FOUNDER_ID , SITEURL , IMAGE_LOCATION , GENDER , VIEWS , CATEGORY_ID , ALIVE , PSTORE_ID , TIMEOFADD  FROM PRODUCT AS PR JOIN (SELECT  PRODUCTINV_ID,SUM(EVENTTIME BETWEEN DATEADD('HOUR', -1,NOW()) AND  NOW()) AS SE,MAX(EVENTTIME) AS ME FROM EVENTLOG WHERE EVENTTYPE =2 GROUP BY PRODUCTINV_ID) AS EF ON PR.ID=EF.PRODUCTINV_ID "+filter+"  ORDER BY EF.SE DESC,EF.ME ";
		String sqlquery="SELECT ID , PRODUCTNAME , CURRENCY , PRICETAG , FOUNDER_ID , SITEURL , IMAGE_LOCATION , GENDER , VIEWS , CATEGORY_ID , ALIVE , PSTORE_ID , TIMEOFADD  FROM ( PRODUCT AS PR LEFT JOIN (SELECT  PRODUCTINV_ID,SUM(cast(EVENTTIME BETWEEN (NOW()-interval '1 hour') AND  NOW() as int)) AS SE,MAX(EVENTTIME) AS ME FROM EVENTLOG WHERE EVENTTYPE =2 GROUP BY PRODUCTINV_ID) AS EF ON PR.ID=EF.PRODUCTINV_ID ) WHERE PR.ALIVE = true AND "+filter+"  ORDER BY  COALESCE(EF.SE,0) DESC,COALESCE(EF.ME,timestamp '2001-01-01 00:00:00')  DESC ";
		//System.out.println("Relevance = "+sqlquery);
		//  AT TIME ZONE current_setting('TIMEZONE')
		RawSql rawSql =	RawSqlBuilder.parse(sqlquery)
				.columnMapping("ID", "id")
				.columnMapping("PRODUCTNAME", "productname")
				.columnMapping("CURRENCY", "Currency")
				.columnMapping("PRICETAG", "Pricetag")
				.columnMapping("FOUNDER_ID", "Founder.id")
				.columnMapping("SITEURL", "siteurl")
				.columnMapping("IMAGE_LOCATION", "ImageLocation")
				.columnMapping("GENDER", "gender")
				.columnMapping("VIEWS", "views")
				.columnMapping("CATEGORY_ID", "category.id")
				.columnMapping("ALIVE", "alive")
				.columnMapping("PSTORE_ID", "pstore.id")
				.columnMapping("TIMEOFADD", "timeofadd")
		.create();
		
		return Ebean.find(Product.class).setRawSql(rawSql).findPagingList(pageSize).getPage(page);		
        
    }
	
	public static Page<Product> Alltime(int page, int pageSize,String filter)
	{
		/*
		RawSql rawSql =	RawSqlBuilder.parse("SELECT PRODUCTINV_ID   FROM EVENTLOG WHERE EVENTTYPE =2 " +
				"GROUP BY PRODUCTINV_ID,EVENTTYPE ORDER BY COUNT(EVENTTYPE) DESC")
		.columnMapping("PRODUCTINV_ID", "productinv.id")
		.create();
		*/
		// map result columns to bean properties
		//String sqlquery="SELECT ID , PRODUCTNAME , CURRENCY , PRICETAG , FOUNDER_ID , SITEURL , IMAGE_LOCATION , GENDER , VIEWS , CATEGORY_ID , ALIVE , PSTORE_ID , TIMEOFADD FROM PRODUCT AS PR JOIN (SELECT PRODUCTINV_ID,COUNT(EVENTTYPE) AS LE   FROM EVENTLOG  WHERE EVENTTYPE =2 GROUP BY PRODUCTINV_ID) AS EF ON PR.ID=EF.PRODUCTINV_ID "+filter+" ORDER BY EF.LE DESC";
		
		String sqlquery="SELECT ID , PRODUCTNAME , CURRENCY , PRICETAG , FOUNDER_ID , SITEURL , IMAGE_LOCATION , GENDER , VIEWS , CATEGORY_ID , ALIVE , PSTORE_ID , TIMEOFADD FROM ( PRODUCT AS PR LEFT JOIN (SELECT PRODUCT_ID,COUNT(CONTRIBUTOR_ID) AS LE   FROM LIKEDPRODUCT GROUP BY PRODUCT_ID) AS EF ON PR.ID=EF.PRODUCT_ID ) WHERE PR.ALIVE = true AND  "+filter+" ORDER BY COALESCE(EF.LE,0) DESC";
		//System.out.println("Alltime = "+sqlquery); 
		RawSql rawSql =	RawSqlBuilder.parse(sqlquery)
				.columnMapping("ID", "id")
				.columnMapping("PRODUCTNAME", "productname")
				.columnMapping("CURRENCY", "Currency")
				.columnMapping("PRICETAG", "Pricetag")
				.columnMapping("FOUNDER_ID", "Founder.id")
				.columnMapping("SITEURL", "siteurl")
				.columnMapping("IMAGE_LOCATION", "ImageLocation")
				.columnMapping("GENDER", "gender")
				.columnMapping("VIEWS", "views")
				.columnMapping("CATEGORY_ID", "category.id")
				.columnMapping("ALIVE", "alive")
				.columnMapping("PSTORE_ID", "pstore.id")
				.columnMapping("TIMEOFADD", "timeofadd")
		.create();
		
		return Ebean.find(Product.class).setRawSql(rawSql).findPagingList(pageSize).getPage(page);
		
	}



}
