package models;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import java.util.*;

import javax.persistence.*;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;

import controllers.DInitial;
import controllers.GHelp;

import play.data.validation.Constraints.Required;

@Entity
public class FSearch extends Model {
	@Id
	@GeneratedValue
	public long id;
	
	@Required
	public String skey;	
	
	@Required
	public long genlow;	

	@Required
	public long genup;
	
	@Required
	public double prlow;
	
	@Required
	public double prhigh;
	
	@Required
	@ManyToOne
	public Contributor userid;
	
	@Required
	public boolean isprivate;
	
	public FSearch(String Skey,long gl,long gu,double pl,double pu,Contributor user)
	{
		this.skey=Skey;
		this.genlow=gl;
		this.genup=((gu==0)?Long.MAX_VALUE:gu);
		this.prlow=pl;
		this.prhigh=pu==0.0d?DInitial.PR[0][1]:pu;
		this.userid=user;
		this.save();
				
	}
	
	public static Finder<Long, FSearch> find = new Finder<Long, FSearch>(Long.class, FSearch.class);
	
	public static FSearch FollowSearch(String pn,long gd,int prindex,Contributor user)
	{
		return new FSearch(pn, gd, gd, DInitial.PR[prindex][0], DInitial.PR[prindex][1], user);
	}
	
	public FSearch CopySearch(Contributor user)
	{
		return new FSearch(skey, genlow, genup, prlow, prhigh, user);
	}
	
	public static void DeleteSearch(long id)
	{
		find.ref(id).delete();
	}
	
	public int GetPriceIndex(){
		int i=0;
		for(i=0;i<DInitial.PR.length;i++)
		{
			if((DInitial.PR[i][0]==prlow)&&(DInitial.PR[i][1]==prhigh))
					break;
		}
		return i;
	}
	
	public static FSearch IsFollowingSearch(String pn,long gd,int prindex,Contributor user)
	{
		return find.where().eq("userid", user).eq("skey",pn).eq("genlow", gd).eq("prlow",DInitial.PR[prindex][0]).eq("prhigh", DInitial.PR[prindex][1]).findUnique();
	}
	
	public FSearch IsFollowingSearch(Contributor c)
	{
		if(userid.equals(c))
			return this;
		return null;
	}
	
	public static Page<FSearch> FollowingSearches(int page, int pageSize, String fn,Contributor c) {
		ExpressionList<FSearch> el= find.where().eq("userid", c);
		if(!GHelp.VerifyACL(DInitial.CURRENT_ACL.ADMIN_SELF, c.user)){ el= el.eq("isprivate", false); }
		
		return el.ilike("skey", "%" + fn + "%").findPagingList(pageSize).getPage(page);
    }
	
	public static int UserFollowedSearchCount(Contributor c)
	{
		ExpressionList<FSearch> el= find.where().eq("userid", c);
		if(!GHelp.VerifyACL(DInitial.CURRENT_ACL.ADMIN_SELF, c.user)){ el= el.eq("isprivate", false); }
		return el.findRowCount();
	}
	
	public static List<Product> RecentProducts(FSearch fs,int contentcount)
	{
		ExpressionList<Product> fp=Product.find.where().ilike("productname", "%" + fs.skey + "%");
		fp=fp.ge("gender", fs.genlow).le("gender", fs.genup);
        fp=fp.ge("Pricetag", fs.prlow).le("Pricetag", fs.prhigh);
		return	fp.orderBy("timeofadd desc").findPagingList(contentcount).getPage(0).getList();
	}
	

}
