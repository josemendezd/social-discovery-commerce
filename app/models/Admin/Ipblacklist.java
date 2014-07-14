package models.Admin;
 
import java.net.InetAddress;
import java.net.UnknownHostException;
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

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.annotation.CreatedTimestamp;

import play.data.validation.*;
 
@Entity
@Table(name="ipblacklist")
public class Ipblacklist extends Model {
 
	@Id
	@GeneratedValue
	public Long id;
	
	@Required
    @ManyToOne
    public User author;
	 
    @Required
    public Long lowerip;
    
    public Long upperip;
    
    public Long range;
    
    @Lob
    public String description;
    
    @CreatedTimestamp
    public Date datecreated;
    
    
    public Ipblacklist(User author,Long lowerip,Long upperip, String description ) {
        this.author = author;
        this.lowerip=lowerip;
        this.upperip=upperip;
        this.range=upperip-lowerip+1;
        this.description = description;
        this.save();
    }
    
    public static Model.Finder<Long,Ipblacklist> find = new Finder<Long, Ipblacklist>(Long.class, Ipblacklist.class);   
    
    
    public static Ipblacklist AddnewBlacklist(User author,Long lowerip,Long upperip, String description) 
    {
    	Ipblacklist bl=new Ipblacklist(author, lowerip, upperip, description);
    	return bl;
    }
    
    public static Ipblacklist AddnewBlacklistSingle(User author,Long singleip,String description) 
    {
    	Ipblacklist bl=new Ipblacklist(author, singleip, singleip, description);
    	return bl;
    }
    
    public static Page<Ipblacklist> RecentBlacklists(int page, int pageSize, String sortBy, String order) {  
    	return	find.orderBy(sortBy + " " + order).findPagingList(pageSize).getPage(page);
    }

    public static void updateblacklist(Ipblacklist bl,User author,Long lowerip,Long upperip, String description) 
    {
    	bl.author = author;
        bl.lowerip=lowerip;
        bl.upperip=upperip;
        bl.range=upperip-lowerip+1;
        bl.description = description;
    	bl.save();
    }
    
    public static void updateblacklistdescription(Ipblacklist bl,String description) 
    {
    	bl.description=description;
    	bl.save();
    }
    
    public static boolean isblacklisted(String ip) 
    {
    	try{
    		Long ipaddress=ipToLong(ip);
    		return find.where().and(Expr.ge("lowerip", ipaddress), Expr.le("upperip", ipaddress)).findRowCount()>0;
    		
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		return false;
    	}
    }
    
    public static List<Ipblacklist> blacklistref(Long ip) 
    {
    	return find.where().and(Expr.ge("lowerip", ip), Expr.le("upperip", ip)).findList();
    }
    
    public static List<Ipblacklist> blacklistref(Long lowerip,Long upperip) 
    {
    	return find.where().and(Expr.ge("lowerip", lowerip), Expr.le("upperip", upperip)).findList();
    }
    
    public static void DeleteBlacklist(Ipblacklist bl) 
    {
    	bl.delete();
    }
    
    public static long ipToLong(String ip) throws UnknownHostException {
    	return ipToLong(InetAddress.getByName(ip));
    }
    
    public static long ipToLong(InetAddress ip) {
        byte[] octets = ip.getAddress();
        long result = 0;
        for (byte octet : octets) {
            result <<= 8;
            result |= octet & 0xff;
        }
        return result;
    }
    
    public static String Longtoip(Long ip) {
    	StringBuilder ipAddress = new StringBuilder();
		for (int i = 3; i >= 0; i--) {
			int shift = i * 8;
			ipAddress.append((ip & (0xff << shift)) >> shift);
			if (i > 0) {
				ipAddress.append(".");
			}
		}
		return ipAddress.toString();
    }
    
 
}