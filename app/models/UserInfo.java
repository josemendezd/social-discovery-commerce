package models;
 
import java.net.InetAddress;
import java.util.*;

import javax.persistence.*;
 


import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import javax.persistence.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import controllers.DInitial;

import play.data.validation.*;
 
@Entity
@Table(name="userinfo")
public class UserInfo extends Model {
 
	@Id
	@GeneratedValue
	public Long id;
	
    @Required
    @OneToOne
    public User loginuser;
    
	public String lastip;
	
	public String lastlocation;
	
	@Required
	public Date lastlogin;
    
    public UserInfo(User u, String ipaddress,String lastlocation) {
        this.loginuser = u;
        this.lastip = ipaddress;
        this.lastlocation = lastlocation;
        this.lastlogin = new Date();
        this.save();
    }  
    
    
    
    public static Model.Finder<Long,UserInfo> find = new Finder<Long, UserInfo>(Long.class, UserInfo.class);
    
    public static UserInfo addlogininfo(User u, String ipaddress)
    {
    	String country="Unknown";
    	try{
    		country=DInitial.geoipreader.country(InetAddress.getByName(ipaddress)).getCountry().getName();
		}catch(Exception e){e.printStackTrace();}    	
    	return new UserInfo(u, ipaddress, country);
    }      
    
    public static boolean removeinfo(UserInfo uinfo)
    {    	
    	if(uinfo==null)
    		return false;
    	uinfo.delete();
    	return true;
    }
    
    public static Page<UserInfo> recentlogins(int page, int pageSize) {        
        return	find.orderBy("lastlogin DESC").findPagingList(pageSize).getPage(page);
    }
    
    public static Page<UserInfo> searchbyuser(int page, int pageSize,User u) {        
        return	find.where().eq("loginuser",u ).orderBy("lastlogin DESC").findPagingList(pageSize).getPage(page);
    }
    
    public static Page<UserInfo> searchuserwithintimeframe(int page, int pageSize,User u,Date timelower,Date timeupper) {        
        return	find.where().eq("loginuser",u ).ge("lastlogin", timelower).le("lastlogin", timeupper).orderBy("lastlogin DESC").findPagingList(pageSize).getPage(page);
    }
}