package models;
 
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
import com.avaje.ebean.SqlRow;

import play.data.validation.*;
 
@Entity
@Table(name="kvarrayproperty")
public class Kvarrayproperty extends Model {
 
	@Id
	@GeneratedValue
	public Long id;
	
    @Required
    @Column(name="arraypropertyname")
    public String arraypropertyname;
    
    //Single Value
    @Column(name="datavalue")
    public String datavalue;
    
    @Column(name="arrayindex")
    public Integer arrayindex;
    
    //Create Property values
    public Kvarrayproperty(String arraypropertyname, String datavalue,int index) {
    	this.arraypropertyname=arraypropertyname;
    	this.datavalue=datavalue;
        this.arrayindex=index;
        this.save();
    }
    
    public static Model.Finder<Long,Kvarrayproperty> find = new Finder<Long, Kvarrayproperty>(Long.class, Kvarrayproperty.class);
    
    //Get Property values
    public static String[] getarrayproperty(String arraypropertyname)
    {
    	List<Kvarrayproperty> retrievedlist=find.where().eq("arraypropertyname", arraypropertyname).orderBy("arrayindex").findList();
    	int totalcount=retrievedlist.size();
    	String[] fetchedArray=new String[totalcount];
    	for(int counter=0;counter<totalcount;counter++)
    	{
    		fetchedArray[counter]=retrievedlist.get(counter).datavalue;
    	}
    	return fetchedArray;
    }

    //Get Property values
    public static Integer[] getarraypropertyasInteger(String arraypropertyname)
    {
    	List<Kvarrayproperty> retrievedlist=find.where().eq("arraypropertyname", arraypropertyname).orderBy("arrayindex").findList();
    	int totalcount=retrievedlist.size();
    	Integer[] fetchedArray=new Integer[totalcount];
    	for(int counter=0;counter<totalcount;counter++)
    	{
    		fetchedArray[counter]=Integer.parseInt(retrievedlist.get(counter).datavalue);
    	}
    	return fetchedArray;
    }

    //Get Property values
    public static Double[] getarraypropertyasDouble(String arraypropertyname)
    {
    	List<Kvarrayproperty> retrievedlist=find.where().eq("arraypropertyname", arraypropertyname).orderBy("arrayindex").findList();
    	int totalcount=retrievedlist.size();
    	Double[] fetchedArray=new Double[totalcount];
    	for(int counter=0;counter<totalcount;counter++)
    	{
    		fetchedArray[counter]=Double.parseDouble(retrievedlist.get(counter).datavalue);
    	}
    	return fetchedArray;
    }

    //Get Property values
    public static Boolean[] getarraypropertyasBoolean(String arraypropertyname)
    {
    	List<Kvarrayproperty> retrievedlist=find.where().eq("arraypropertyname", arraypropertyname).orderBy("arrayindex").findList();
    	int totalcount=retrievedlist.size();
    	Boolean[] fetchedArray=new Boolean[totalcount];
    	for(int counter=0;counter<totalcount;counter++)
    	{
    		fetchedArray[counter]=Boolean.parseBoolean(retrievedlist.get(counter).datavalue);
    	}
    	return fetchedArray;
    }
    
    //
    public static String setKvarrayproperty(String arraypropertyname, String[] datavalue) {
    	Ebean.createSqlUpdate("delete from kvarrayproperty where arraypropertyname=:apn").setParameter("apn", arraypropertyname).execute();
    	int inputlength=datavalue.length;
	    	for(int counter=0;counter<inputlength;counter++)
	    		new Kvarrayproperty(arraypropertyname, datavalue[counter], counter);
    	return arraypropertyname;
    }
    
    public static String setinitialKvarrayproperty(String arraypropertyname, String[] datavalue) {
    	if(find.where().eq("arraypropertyname", arraypropertyname).findRowCount()<=0)
    		setKvarrayproperty(arraypropertyname, datavalue);
    	return arraypropertyname;
    }
    
    
    //Append Property values
    public static Kvarrayproperty appendvaluetoArray(String arraypropertyname,String nextvalue)
    {
    	SqlRow sqr= Ebean.createSqlQuery("select max(arrayindex) MAXVALUE from kvarrayproperty where arraypropertyname=:apn").setParameter("apn", arraypropertyname).findUnique();
    	int lastleft = sqr.getInteger("MAXVALUE");
    	return new Kvarrayproperty(arraypropertyname, nextvalue, lastleft+1);    	
    }

    //Update Property values
    public static boolean updatevalueinArray(String arraypropertyname,String newvalue,int index)
    {
    	Kvarrayproperty kvpa=find.where().eq("arraypropertyname", arraypropertyname).eq("arrayindex",index).findUnique();
    	if(kvpa==null)
    		return false;
    	kvpa.datavalue=newvalue;
    	kvpa.save();
    	return true;
    }
 
}