package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;


import com.avaje.ebean.*;
import com.avaje.ebean.Query;
import com.avaje.ebean.annotation.*;

import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Logger;

@Entity
@Sql
public class EventLogSql extends Model {
	
	@Id
	public long id;	
	public long signify;	
	public String info;
	public long eventtype;
	
	
	public Timestamp eventtime;
	
	@ManyToOne
	public Product productinv;
	
	@ManyToOne
	public Contributor userinv;
	
	@ManyToOne
	public UserCollection collectinv;
	
	@ManyToOne
	public Store storeinv;
	
	@ManyToOne
	public FSearch searchinv;
	
	public static Page<EventLogSql> Feed(int page, int pageSize, Contributor user,String filter) {
		
		String sqlstring="SELECT JX.SIGNIFY , JX.INFO ,JX.SEARCH_ID , JX.ID , JX.EVENTTYPE , JX.PRODUCTINV_ID , JX.USERINV_ID , JX.COLLECTINV_ID , JX.STOREINV_ID , JX.EVENTTIME   FROM  (" +
		//This is Search Feed
		"(SELECT 1 AS SIGNIFY,FS.SKEY AS INFO,FS.ID AS SEARCH_ID,EL.* FROM (SELECT * FROM FSEARCH WHERE USERID_ID =" + user.id +") AS FS,PRODUCT AS P,EVENTLOG AS EL WHERE P.GENDER BETWEEN FS.GENLOW AND FS.GENUP AND P.PRICETAG  BETWEEN FS.PRLOW  AND FS.PRHIGH AND P.PRODUCTNAME LIKE CONCAT('%',FS.SKEY,'%') AND P.ID=EL.PRODUCTINV_ID AND EL.EVENTTYPE=1 ) " +
		"UNION " +//This is Leaders Feed
		"(SELECT 2 AS SIGNIFY,USR.NAME AS INFO ,NULL  AS SEARCH_ID,EL.*  FROM EVENTLOG AS EL, CONTRIBUTOR AS CTR,USERS AS USR WHERE EL.USERINV_ID IN (SELECT LEADER FROM FOLLOW WHERE FOLLOWER=" + user.id +" AND TYPE=0)  AND  EL.USERINV_ID=CTR.ID AND USR.ID=CTR.USER_ID AND EVENTTYPE =2 ) " +
		"UNION " +//This is Store Feed
		"(SELECT 3 AS SIGNIFY, STR.NAME AS INFO ,NULL  AS SEARCH_ID,EL.* FROM EVENTLOG AS EL,STORE AS STR WHERE EL.STOREINV_ID IN  (SELECT LEADER FROM FOLLOW WHERE FOLLOWER=" + user.id +" AND TYPE=1) AND EL.STOREINV_ID=STR.ID AND EL.EVENTTYPE =1 ) " +
		"UNION " +//This is Collection Feed
		"(SELECT 4 AS SIGNIFY, UC.COLNAME AS INFO ,NULL  AS SEARCH_ID,EL.* FROM EVENTLOG AS EL,USER_COLLECTION AS UC WHERE EL.COLLECTINV_ID IN  (SELECT LEADER FROM FOLLOW WHERE FOLLOWER=" + user.id +" AND TYPE=2) AND EL.COLLECTINV_ID=UC.ID AND EL.EVENTTYPE =3 ) " +
		")  AS JX" +
		" JOIN PRODUCT P ON P.ID=JX.PRODUCTINV_ID AND " +
		"P.CATEGORY_ID IN (WITH RECURSIVE SUBCATEGORIES(ID, CNAME, PCATEGORY_ID) AS ( SELECT ID,CNAME,PCATEGORY_ID FROM CATEGORY  WHERE ID IN (SELECT CATEGORY_ID FROM USERS_CATEGORY WHERE USERS_ID =" + user.id +")  UNION ALL     SELECT C.ID, C.CNAME, C.PCATEGORY_ID     FROM SUBCATEGORIES SC, CATEGORY C     WHERE C.PCATEGORY_ID = SC.ID   ) SELECT DISTINCT(ID) FROM SUBCATEGORIES) AND " +filter+
		"  ORDER BY JX.EVENTTIME DESC";//JOIN (SELECT PRODUCTINV_ID,MAX(EVENTTIME) AS MET FROM EVENTLOG GROUP BY PRODUCTINV_ID ) AS EVL ON EVL.PRODUCTINV_ID=JX.PRODUCTINV_ID AND JX.EVENTTIME=EVL.MET ORDER BY JX.EVENTTIME DESC"
		/*
		if(play.Logger.isDebugEnabled())
		{
			play.Logger.info(sqlstring);
		}
		*/
		RawSql rawSql =	RawSqlBuilder.parse(sqlstring).columnMapping("JX.PRODUCTINV_ID", "productinv.id")
		.columnMapping("JX.USERINV_ID", "userinv.id")
		.columnMapping("JX.COLLECTINV_ID", "collectinv.id")
		.columnMapping("JX.STOREINV_ID", "storeinv.id")
		.columnMapping("JX.SEARCH_ID", "searchinv.id")
		.columnMapping("JX.ID", "id")
		.columnMapping("JX.SIGNIFY", "signify")
		.columnMapping("JX.INFO", "info")
		.columnMapping("JX.EVENTTYPE", "eventtype")
		.columnMapping("JX.EVENTTIME", "eventtime")
		.create();		
		
		return Ebean.find(EventLogSql.class).setRawSql(rawSql).findPagingList(pageSize)
                .getPage(page);
		
        
    }
	
public static Page<EventLogSql> FriendFeed(int page, int pageSize, Contributor user,String filter) {
		
		String sqlstring="SELECT JX.SIGNIFY , JX.INFO ,JX.SEARCH_ID , JX.ID , JX.EVENTTYPE , JX.PRODUCTINV_ID , JX.USERINV_ID , JX.COLLECTINV_ID , JX.STOREINV_ID , JX.EVENTTIME   FROM  (" +
		//This is Search Feed
		"(SELECT 1 AS SIGNIFY,FS.SKEY AS INFO,FS.ID AS SEARCH_ID,EL.* FROM (SELECT * FROM FSEARCH WHERE USERID_ID =" + user.id +") AS FS,PRODUCT AS P,EVENTLOG AS EL WHERE P.GENDER BETWEEN FS.GENLOW AND FS.GENUP AND P.PRICETAG  BETWEEN FS.PRLOW  AND FS.PRHIGH AND P.PRODUCTNAME LIKE CONCAT('%',FS.SKEY,'%') AND P.ID=EL.PRODUCTINV_ID AND EL.EVENTTYPE=1 ) " +
		"UNION " +//This is Leaders Feed
		"(SELECT 2 AS SIGNIFY,USR.NAME AS INFO ,NULL  AS SEARCH_ID,EL.*  FROM EVENTLOG AS EL, CONTRIBUTOR AS CTR,USERS AS USR WHERE EL.USERINV_ID IN (SELECT LEADER FROM FOLLOW WHERE FOLLOWER=" + user.id +" AND TYPE=0)  AND  EL.USERINV_ID=CTR.ID AND USR.ID=CTR.USER_ID AND EVENTTYPE =2 ) " +
		"UNION " +//This is Store Feed
		"(SELECT 3 AS SIGNIFY, STR.NAME AS INFO ,NULL  AS SEARCH_ID,EL.* FROM EVENTLOG AS EL,STORE AS STR WHERE EL.STOREINV_ID IN  (SELECT LEADER FROM FOLLOW WHERE FOLLOWER=" + user.id +" AND TYPE=1) AND EL.STOREINV_ID=STR.ID AND EL.EVENTTYPE =1 ) " +
		"UNION " +//This is Collection Feed
		"(SELECT 4 AS SIGNIFY, UC.COLNAME AS INFO ,NULL  AS SEARCH_ID,EL.* FROM EVENTLOG AS EL,USER_COLLECTION AS UC WHERE EL.COLLECTINV_ID IN  (SELECT LEADER FROM FOLLOW WHERE FOLLOWER=" + user.id +" AND TYPE=2) AND EL.COLLECTINV_ID=UC.ID AND EL.EVENTTYPE =3 ) " +
		")  AS JX" +
		" JOIN PRODUCT P ON P.ID=JX.PRODUCTINV_ID AND  P.ALIVE = true AND " +filter+
		"  ORDER BY JX.EVENTTIME DESC";//JOIN (SELECT PRODUCTINV_ID,MAX(EVENTTIME) AS MET FROM EVENTLOG GROUP BY PRODUCTINV_ID ) AS EVL ON EVL.PRODUCTINV_ID=JX.PRODUCTINV_ID AND JX.EVENTTIME=EVL.MET ORDER BY JX.EVENTTIME DESC"
		
		/*
		if(play.Logger.isDebugEnabled())
		{
			play.Logger.info(sqlstring);
		}
		*/
		RawSql rawSql =	RawSqlBuilder.parse(sqlstring).columnMapping("JX.PRODUCTINV_ID", "productinv.id")
		.columnMapping("JX.USERINV_ID", "userinv.id")
		.columnMapping("JX.COLLECTINV_ID", "collectinv.id")
		.columnMapping("JX.STOREINV_ID", "storeinv.id")
		.columnMapping("JX.SEARCH_ID", "searchinv.id")
		.columnMapping("JX.ID", "id")
		.columnMapping("JX.SIGNIFY", "signify")
		.columnMapping("JX.INFO", "info")
		.columnMapping("JX.EVENTTYPE", "eventtype")
		.columnMapping("JX.EVENTTIME", "eventtime")
		.create();		
		
		return Ebean.find(EventLogSql.class).setRawSql(rawSql).findPagingList(pageSize)
                .getPage(page);
		
        
    }
	
	public static String getCustomStackTrace(Throwable aThrowable) {
	    //add the class name and any message passed to constructor
	    final StringBuilder result = new StringBuilder( "Error Print: " );
	    result.append(aThrowable.toString());
	    final String NEW_LINE = System.getProperty("line.separator");
	    result.append(NEW_LINE);

	    //add each element of the stack trace
	    for (StackTraceElement element : aThrowable.getStackTrace() ){
	      result.append( element );
	      result.append( NEW_LINE );
	    }
	    return result.toString();
	  }
	

}
