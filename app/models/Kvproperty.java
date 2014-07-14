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
@Table(name="kvproperty")
public class Kvproperty extends Model {
 
	@Id
	@GeneratedValue
	public Long id;
	
    @Required
    @Column(unique=true,name="propertyname")    
    public String propertyname;
    
    //Single Value
    @Column(name="stringvalue")
    public String stringvalue;
    
    @Column(name="valuetype")
    public String valuetype;   
    
    public Kvproperty(String propertyname, String stringvalue, String valuetype) {
    	this.propertyname=propertyname;
    	this.stringvalue=stringvalue;
    	this.valuetype=valuetype;
        this.save();
    }
    
    public static Model.Finder<Long,Kvproperty> find = new Finder<Long, Kvproperty>(Long.class, Kvproperty.class);
    
    //Get Property values
    public static String getpropertyvalue(String propertyname)
    {
    	Kvproperty retrievedkv =find.where().eq("propertyname", propertyname).findUnique();
    	if(retrievedkv==null)
	    	return null;
    	return retrievedkv.stringvalue;
    }

    //Get Property values
    public static Integer getpropertyvalueasInteger(String propertyname)
    {
    	Kvproperty retrievedkv =find.where().eq("propertyname", propertyname).findUnique();
    	if(retrievedkv==null)
	    	return null;
    	return Integer.parseInt(retrievedkv.stringvalue);
    }

    //Get Property values
    public static Double getpropertyvalueasDouble(String propertyname)
    {
    	Kvproperty retrievedkv =find.where().eq("propertyname", propertyname).findUnique();
    	if(retrievedkv==null)
	    	return null;
    	return Double.parseDouble(retrievedkv.stringvalue);
    }

    //Get Property values
    public static Boolean getpropertyvalueasBoolean(String propertyname)
    {
    	Kvproperty retrievedkv =find.where().eq("propertyname", propertyname).findUnique();
    	if(retrievedkv==null)
	    	return null;
    	return Boolean.parseBoolean(retrievedkv.stringvalue);
    	
    }
    
    public static Kvproperty setpropertyvalue(String propertyname,String propertyvalue, String valuetype)
    {
    	Kvproperty retrievedkv =find.where().eq("propertyname", propertyname).findUnique();
    	if(retrievedkv==null)
	    	return new Kvproperty(propertyname, propertyvalue,valuetype);
    	return retrievedkv;
    }
    
    //Update Property values
    public static boolean updatevalueinArray(String arraypropertyname,String newvalue)
    {
    	Kvproperty kvpa=find.where().eq("arraypropertyname", arraypropertyname).findUnique();
    	if(kvpa==null)
    		return false;
    	kvpa.stringvalue=newvalue;
    	kvpa.save();
    	return true;
    }
 
}