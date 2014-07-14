package models;


import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import javax.persistence.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.Formula;

import java.util.*;

@Entity
public class Category extends  Model {
	@Id
	@GeneratedValue
	public long id;
	
	@Constraints.Required
	public String cname;
	
	@ManyToOne
	Category pcategory;
	
	Long toplevel;
	
	@OneToMany(targetEntity=Product.class,mappedBy="category")
	public List<Product> products;
	
	@OneToMany(mappedBy="pcategory",orphanRemoval=true)
	public Set<Category> subcategory;

	public Category(String ctgname,Category parent)
	{
		this.cname=ctgname;
		if(parent!=null)
			this.pcategory=parent;
		this.save();
		updatetoplevel(this.id);
	}
	
	public static Model.Finder<Long,Category> find = new Finder<Long, Category>(Long.class, Category.class);
	
	public static void updatetoplevel(Long cid)
	{
		Ebean.createSqlUpdate("update category set toplevel= COALESCE( ( WITH RECURSIVE  subcategories(id, cname, pcategory_id) AS ( SELECT id,cname,pcategory_id FROM category  WHERE  id = :cid UNION ALL SELECT c.id, c.cname, c.pcategory_id  FROM subcategories sc, category c  WHERE sc.pcategory_id = c.id ) SELECT DISTINCT(id) AS LIST FROM subcategories WHERE id in (  SELECT id FROM category WHERE  pcategory_id in ( SELECT id FROM category WHERE COALESCE(pcategory_id,0) =0) ) ),0) WHERE id=:cid ").setParameter("cid", cid).execute();
	}
	
	public static Category root()
	{
		Category root=find.where().eq("cname","All").isNull("pcategory").findUnique();
		if(root!=null)
			return root;
		return	new Category("All",null);
	}
	
	public static Category CreateRootCategory(String ctgname){
		if(find.where().eq("cname", ctgname).findRowCount()>0)
			return null;
		return new Category(ctgname,root());
	}
	
	
	public static Long gettoplevel(Long cid)
	{
		List<Category> al =TopLevelCategories();
		Category cr=find.byId(cid);
		while(cr!=null)
		{
			if(al.contains(cr))
				return cr.id;
			cr=cr.pcategory;
		}
		return 0L;
	}
	
	public static Category CreateChildCategory(String ctgname,Category parent){
		if(find.where().eq("cname", ctgname).eq("pcategory",parent).findRowCount()>0)
			return null;
		return new Category(ctgname,parent);
	} 
	
	public static boolean AddSubCategory(Category parent, String ctgname)
	{
		Category newsubcategory=CreateChildCategory(ctgname, parent);
		if(newsubcategory!=null)
		{
			if(!parent.subcategory.add(newsubcategory))
				newsubcategory.delete();
			else{
				parent.save();
				return true;
			}
		}
		return false;		
	}
	
	public static void AddSubCategoryList(Category parent, List<String> ctgname)
	{
		for(String cctgname:ctgname )
		{
			Category newsubcategory=CreateChildCategory(cctgname, parent);
			if(newsubcategory!=null)
			{
				newsubcategory.save();
				if(parent.subcategory.add(newsubcategory))
					parent.save();
				else
					newsubcategory.delete();
			}
		}
				
	}
	
	public static List<Category> TopLevelCategories()
	{
		return find.where().eq("pcategory", root()).findList();
	}
	
	public static List<Category> Ancestors(Category c)
	{
		
		//play.Logger.info("Category:= "+c.cname+" Parent Category:= "+c.pcategory.cname);
		List<Category> al =new ArrayList<Category>();
		Category cr=find.byId(c.id);
		while(cr!=null)
		{
			al.add(cr);
			cr=cr.pcategory;
		}
		Collections.reverse(al);
		return al;
	}
	
	public static String ChildList(Long catgid)
	{
		String query="WITH RECURSIVE subcategories(id, cname, pcategory_id) AS (     SELECT id,cname,pcategory_id FROM category  WHERE COALESCE(pcategory_id,0) = "+catgid+" OR id = "+catgid+"   UNION ALL     SELECT c.id, c.cname, c.pcategory_id     FROM subcategories sc, category c     WHERE c.pcategory_id = sc.id   ) SELECT string_agg(id::text,',') AS LIST FROM subcategories";
		return Ebean.createSqlQuery(query).findUnique().getString("LIST");
	}
	
	public static String ChildJSON(long cid)
	{
		String query="SELECT string_agg(row_to_json(data):: text,',') AS LIST FROM (SELECT * from category WHERE COALESCE(pcategory_id,0) = "+cid+" )data(id,name,pid)";
		
		return Ebean.createSqlQuery(query).findUnique().getString("LIST");
	}
	
	public static boolean RemoveSubCategory(Category parentcategory, Category childcategory)
	{
		if(parentcategory.subcategory.contains(childcategory) && parentcategory.subcategory.remove(childcategory))
		{
			parentcategory.save();
			return true;
		}
		return false;		
	}
	
	public static Long gettoplevel(Category c)
	{
		return c.toplevel;
	}
	
}
