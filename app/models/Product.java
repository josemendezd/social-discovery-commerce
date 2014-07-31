package models;

import play.Logger;
import play.data.format.Formats;
import play.data.validation.*;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import plugins.S3Plugin;

import javax.persistence.*;
import javax.validation.Constraint;

import models.Admin.Addfailed;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.annotation.CreatedTimestamp;

import controllers.Application;
import controllers.DInitial;
import controllers.GHelp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Entity
public class Product extends  Model {
	@Id
	@GeneratedValue
	public Long id;
	
    @Constraints.Required
	public String productname;
	
    @Constraints.Required
    public String Currency;
    
    @Constraints.Required
    public double Pricetag;
    
	@OneToOne
	@Constraints.Required
	public User Founder;
	
    @Constraints.Required
	public String siteurl; 
    
    @Constraints.Required
    public String ImageLocation;
    
    public long gender;
    
    public long views;
    
    public String description;
    
    public float rate;
	
	@ManyToOne
	public Category category;
	
	public Boolean alive;
	
	@Constraints.Required
	@CreatedTimestamp
	public Timestamp timeofadd;	
	
	@ManyToMany(targetEntity=Contributor.class,mappedBy="Owned")
	public List<Contributor> Owners;
	
	@ManyToMany(targetEntity=Contributor.class,mappedBy="Liked")
	public List<Contributor> Likers;
	
	@ManyToMany(targetEntity=Contributor.class,mappedBy="Wanted")
	public List<Contributor> Wanters;
	
	@ManyToMany(targetEntity=UserCollection.class,mappedBy="productlist")//(cascade = CascadeType.ALL)//fetch = FetchType.EAGER,
	public List<UserCollection> collectlist;
	
	@OneToMany(targetEntity=Comment.class,mappedBy="post")
	public List<Comment> commentlist;
	
	@ManyToOne
	public Store pstore;

	public static Model.Finder<Long,Product> find = new Finder<Long, Product>(Long.class, Product.class);
	
	public Product(String ProductName,String currency,double pricetag,User usr,String SitUrl,String ImgUrl,Category categ,boolean status,String description)
	{
		this.productname=ProductName;
		this.Currency=currency;
		this.Pricetag=pricetag;
		this.Founder=usr;
		this.siteurl=SitUrl;
		this.pstore=Store.FindStore(SitUrl);
		this.ImageLocation=ImgUrl;
		this.category=categ;
		this.alive=status;
		this.gender=usr.gender;
		this.views=0L;
		this.description=description;
		this.save();
	}
	
	public void savetocdn(String allotedname,File tempstorage)
	{
		File ActualSize=S3File.getimageresized(tempstorage, allotedname,DInitial.IMAGESTORESIZE.AS_IT_IS.filestate, DInitial.IMAGESTORESIZE.AS_IT_IS.Identifier, DInitial.IMAGESTORESIZE.AS_IT_IS.width, DInitial.IMAGESTORESIZE.AS_IT_IS.height),
				SmallSize=S3File.getimageresized(tempstorage, allotedname, DInitial.IMAGESTORESIZE.THUMBNAIL_BRICK.filestate, DInitial.IMAGESTORESIZE.THUMBNAIL_BRICK.Identifier, DInitial.IMAGESTORESIZE.THUMBNAIL_BRICK.width, DInitial.IMAGESTORESIZE.THUMBNAIL_BRICK.height);
		S3File.createfile(S3Plugin.s3Bucket, Product.class.getSimpleName(), allotedname, DInitial.IMAGESTORESIZE.AS_IT_IS.filestate, id, ActualSize);
		S3File.createfile(S3Plugin.s3Bucket, Product.class.getSimpleName(), allotedname, DInitial.IMAGESTORESIZE.THUMBNAIL_BRICK.filestate, id,SmallSize );
		ActualSize.delete();
		SmallSize.delete();
		Ebean.createSqlUpdate("UPDATE PRODUCT SET IMAGE_LOCATION =:LOC  WHERE ID = :PID").setParameter("LOC", this.getproductimagename(true)).setParameter("PID", this.id).execute();
	}
	
	public String getproductimagename(boolean ext)
	{
		return "productpic"+id+(ext?".png":"");
	}
	
	public String getproductimage()
	{
		return S3File.getUrl(Product.class.getSimpleName(), ImageLocation, DInitial.IMAGESTORESIZE.AS_IT_IS.filestate);
	}
	
	public String getproductimagethumb()
	{
		return S3File.getUrl(Product.class.getSimpleName(), ImageLocation, DInitial.IMAGESTORESIZE.THUMBNAIL_BRICK.filestate);
	}
	
	//Static Modifiers
	public static Product CreateProduct(String ProductName,String currency,double pricetag,User usr,String SitUrl,String ImgUrl,Category categ,boolean status,long pgender,String description)
	{
		Product p=new Product(ProductName, currency, pricetag, usr, SitUrl, ImgUrl, categ, status,description);

		try {
			String allotedname=p.getproductimagename(true);
			File tempstorage =GHelp.downloadandsaveimage(ImgUrl, allotedname) ;
			if(tempstorage==null)
				throw new FileNotFoundException();
			p.savetocdn(allotedname, tempstorage);
			tempstorage.delete();
		} catch (IOException e) {
			e.printStackTrace();
			p.delete();
			Addfailed.Reportfailure(usr, ImgUrl, SitUrl, e.getMessage());
			return null;
		}
		if(pgender!=0L)
			SetGender(p, pgender);
		return p;
	}
	
	public static void SetGender(Product p,long newgender){p.gender = newgender;p.update();}
	
	public static void DeleteProduct(Product p){p.delete();}
	
	public static boolean ProductHibernate(Product p,boolean newstatus)
	{
		if(p.alive==newstatus)
			return false;
		else
		{
			p.alive=newstatus;
			p.save();
			return true;
		}
	}
	
	//Queries...	
	public static int Likes(Product p){return p.Likers.size();}
	public static int Wants(Product p){return p.Wanters.size();}
	public static int Owns(Product p){return p.Owners.size();}
	public static int Collections(Product p){return p.collectlist.size();}
	public static void Incviewcount(Product p){p.views++;p.save();}
	
	public static Page<Product> RecentAdds(int page, int pageSize, String filter) {
        /*
		ExpressionList<Product> fp=find.where().ilike("productname", "%" + pn + "%");
        if(gd!=0)
        	fp=fp.eq("gender", gd);
        fp=fp.ge("Pricetag", DInitial.PR[prindex][0]).le("Pricetag", DInitial.PR[prindex][1]);
        if(catgid!=0)
        {
        	List<String> items = Arrays.asList(Category.ChildList(catgid).split(","));
        	fp=fp.in("category.id",items);//
        }
		return	fp.orderBy("timeofadd desc").findPagingList(pageSize).getPage(page);
		*/
		String sqlquery="SELECT ID , PRODUCTNAME , CURRENCY , PRICETAG , FOUNDER_ID , SITEURL , IMAGE_LOCATION , GENDER , VIEWS , CATEGORY_ID , ALIVE , PSTORE_ID , TIMEOFADD FROM PRODUCT WHERE ALIVE = true AND "+filter+" ORDER BY TIMEOFADD DESC";
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
	
	public static List<Product> EarlyLoaded(int loadsize) {
		String sqlquery="SELECT ID , PRODUCTNAME , CURRENCY , PRICETAG , FOUNDER_ID , SITEURL , IMAGE_LOCATION , GENDER , VIEWS , CATEGORY_ID , ALIVE , PSTORE_ID , TIMEOFADD FROM ( SELECT ROW_NUMBER() OVER (PARTITION BY category_id order by timeofadd DESC) AS r, t.* from product t where t.alive = true AND  category_id in (select id from category where pcategory_id=:parentcategory) ) x where x.r <= :loadsize";
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
		
		return Ebean.find(Product.class).setRawSql(rawSql).setParameter("parentcategory", Category.root().id).setParameter("loadsize", loadsize).findList();
	}
	
	public static Page<Product> Usersfeed(int page, int pageSize, User user) {
		String sqlquery = "" +
				" SELECT pid FROM  \n" +
				" ( \n" +
				" --COLLECTION \n" +
				" 	SELECT DISTINCT(ucp.product_id) pid FROM user_collection_product AS ucp,follow AS f WHERE ucp.user_collection_id=f.leader AND f.type=2 AND f.follower=:userid \n" +
				" 	UNION \n" +
				" --LEADER \n" +
				" 	SELECT DISTINCT(lp.product_id) pid FROM likedproduct AS lp WHERE lp.contributor_id IN (SELECT id FROM contributor WHERE contributor.user_id IN (SELECT leader FROM follow WHERE follower=:userid AND type=0)) \n" +
				" 	UNION \n" +
				" --STORE \n" +
				" 	SELECT DISTINCT(p.id) pid FROM product AS p WHERE  p.alive = true AND p.pstore_id IN  (SELECT leader FROM follow WHERE follower=:userid AND type=1) \n" +
				" 	UNION \n" +
				" --SEARCH \n" +
				" 	SELECT DISTINCT(p.id) pid FROM (SELECT * FROM fsearch WHERE userid_id =:userid) AS fs,product AS p WHERE p.alive = true AND  p.productname LIKE CONCAT('%',fs.skey,'%') \n" +
				" 	UNION \n" +
				" --FAVOURITE \n" +
				" 	SELECT DISTINCT(p.id) pid FROM product p,(SELECT category_id FROM users_category WHERE users_category.users_id = :userid) uc,category c	WHERE p.alive = true AND  p.category_id = c.id AND c.toplevel=uc.category_id \n" +
				" ) AS ALLPRODUCT \n";
		RawSql rawSql =	RawSqlBuilder.parse(sqlquery).columnMapping("pid", "id").create();
		return find.setRawSql(rawSql).setParameter("userid", user.id).findPagingList(pageSize).getPage(page);
		
	}
	
	public static int MatchedProductsCount(String filter)
	{	
		String sqlquery="SELECT COUNT(*) TC FROM PRODUCT WHERE ALIVE = true AND "+filter+" ";
		return Ebean.createSqlQuery(sqlquery).findUnique().getInteger("TC");
	}
	
	
	public static Page<Product> RelatedProducts(int page, int pageSize,Product prod) {
        ExpressionList<Product> fp=find.where().eq("gender", prod.gender).eq("alive", true);
        //fp=fp.eq("category.id", prod.category.id);
        fp=fp.or(Expr.eq("Founder.id", prod.Founder.id),Expr.eq("pstore.id", prod.pstore.id));
		return	fp.orderBy("timeofadd desc").findPagingList(pageSize).getPage(page);
    }
	
}
