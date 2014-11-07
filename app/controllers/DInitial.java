package controllers;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import net.tanesha.recaptcha.ReCaptchaFactory;
import play.Logger;
import play.Play;

import com.amazonaws.services.identitymanagement.model.User;
import com.maxmind.geoip2.DatabaseReader;

import models.Kvarrayproperty;
import models.Kvproperty;
import models.Product;
import models.UserCollection;
import controllers.routes;

public class DInitial {
	
	public static double PR[][]={{0.0d,99999999999d},{0.0d,50},{50,500},{500,1000},{1000,10000},{10000,100000},{100000,99999999999d}};
	//Button text after love/like previously unlike/hate
	public static final String LWT[][]={{"Love","Unlike"},{"Want","Unwant"},{"Tried","Not tried"}};
	public static final Map<Long,String> gender;
    static
    {
        gender = new HashMap<Long, String>();
        gender.put(0L, "Any (Gender)");
        gender.put(1L, "Female");
        gender.put(2L,"Male");
    }
    
    /*
    
    public static class GENDER{
    	public static final int UNKNOWN=0,FEMALE=1,MALE=2;
    }
    public static final int PRODUCT_ADDED=1,PRODUCT_LOVED=2,PRODUCT_COLLECTED=3;
    public static final int GENERIC_PAGE_SIZE=42,GENERIC_BLOG_PAGE_SIZE=5;
    public static final int SEARCH_PRODUCT=0,SEARCH_USER=1,SEARCH_COLLECTION=2,SEARCH_STORE=3,SEARCH_BLOG=4;
    
    public static class PRODUCT_RELATION_QUERY{
    	public static final int LOVERS=1,WANTERS=2,CONSUMERS=3,COMMENTATORS=4,COLLECTIONS=5,STORES=6;
    }
    public static class USER_RELATION_QUERY{
    	public static final int PRODUCTS=0,LOVES=1,WANTS=2,TRIED=3,COMMENTS=4,COLLECTIONS=5,STORES=6,SEARCHES=7,FOLLOWING=8,FOLLOWERS=9,CUSTOMERS=10;
    }
    public static class FOLLOWERTYPE{
    	public static final int INDIVIDUAL=0,STORE=1,COLLECTION=2;
    }
    
    public static final int IMAGE_UPLOAD_FILE_SIZE_LIMIT=5242880;
    
    public static class PROFILE_SETTINGS{
    	public static final int FIRSTNAME=1,LASTNAME=2,USERNAME=3,PASSWORD=4,EMAIL=5,BIOGRAPHY=6,WEBSITE=7,LOCATION=8,LINKEDACCOUNTS=9,EMAIL_NOTIFICATION=10,GENDER=11,SOCIAL_IMAGE=12;
    }
    
    public static final String DefaultUserImage[]={routes.Assets.at("icons/user.png").url(),routes.Assets.at("icons/Female.png").url(),routes.Assets.at("icons/Male.png").url()};
    
    public static final int BLOG_LABELS_LIMIT=10;
    
    public static class CURRENT_ACL{
    	public static final int ADMINISTRATOR_ONLY=1,ADMIN_MODERATOR=2,ADMIN_MODERATOR_SELF=3,ADMIN_SELF=4,
    			MODERATOR_ONLY=7,SELF_ONLY=8,
    			ALL_USERS=5,GUESTS=6;
    }
    
    public static class REPORTABUSE{
    	public static final int ON_PRODUCT=1,ON_USER=2,ON_STORE=3,ON_BLOG=4;
    }
    
    public static class DEFAULTIMAGES{
    	public static final String EMPTY_COLLECTION="images/emptycollection.jpg";
    }
    
    public static class IMAGESTORESIZE{
    	public static final int AS_IT_IS=0,THUMBNAIL_BRICK=1,THUMBNAIL_VERYSMALL=2;
    }
    
    */
    
    public static class GENDER{
    	public static final int 
    	UNKNOWN=Kvproperty.getpropertyvalueasInteger(Kvproperty.setpropertyvalue("GENDER.UNKNOWN", String.valueOf(0), Integer.class.getName()).propertyname),
    	FEMALE=Kvproperty.getpropertyvalueasInteger(Kvproperty.setpropertyvalue("GENDER.FEMALE", String.valueOf(1), Integer.class.getName()).propertyname),
    	MALE=Kvproperty.getpropertyvalueasInteger(Kvproperty.setpropertyvalue("GENDER.MALE", String.valueOf(2), Integer.class.getName()).propertyname);
    }
    public static final int 
	    PRODUCT_ADDED=1,
	    PRODUCT_LOVED=2,
	    PRODUCT_COLLECTED=3;
	    
    public static final int 
    	GENERIC_PAGE_SIZE=Kvproperty.getpropertyvalueasInteger(Kvproperty.setpropertyvalue("GENERIC_PAGE_SIZE", String.valueOf(42), Integer.class.getName()).propertyname),
    	GENERIC_BLOG_PAGE_SIZE=Kvproperty.getpropertyvalueasInteger(Kvproperty.setpropertyvalue("GENERIC_BLOG_PAGE_SIZE", String.valueOf(5), Integer.class.getName()).propertyname);
    
    public static final int 
    	SEARCH_PRODUCT=0,
    	SEARCH_USER=1,
    	SEARCH_COLLECTION=2,
    	SEARCH_STORE=3,
    	SEARCH_BLOG=4;
    
    public static class PRODUCT_RELATION_QUERY{
    	public static final int 
    	LOVERS=1,
    	WANTERS=2,
    	CONSUMERS=3,
    	COMMENTATORS=4,
    	COLLECTIONS=5,
    	STORES=6;
    }
    public static class USER_RELATION_QUERY{
    	public static final int 
    	PRODUCTS=0,
    	LOVES=1,
    	WANTS=2,
    	TRIED=3,
    	COMMENTS=4,
    	COLLECTIONS=5,
    	STORES=6,
    	SEARCHES=7,
    	FOLLOWING=8,
    	FOLLOWERS=9,
    	CUSTOMERS=10,
    	RECENT=11;
    }
    public static class FOLLOWERTYPE{
    	public static final int 
    	INDIVIDUAL=0,
    	STORE=1,
    	COLLECTION=2;
    }
    
    public static final int IMAGE_UPLOAD_FILE_SIZE_LIMIT=2097152;
    
    public static class PROFILE_SETTINGS{
    	public static final int 
    	FIRSTNAME=1,
    	LASTNAME=2,
    	USERNAME=3,
    	PASSWORD=4,
    	EMAIL=5,
    	BIOGRAPHY=6,
    	WEBSITE=7,
    	LOCATION=8,
    	LINKEDACCOUNTS=9,
    	EMAIL_NOTIFICATION=10,
    	GENDER=11,
    	SOCIAL_IMAGE=12;
    }
    
    public static final String[] DefaultUserImage=
    	Kvarrayproperty.getarrayproperty(Kvarrayproperty.setinitialKvarrayproperty("DefaultUserImage",	new String[]{"normaluser.png","Female.png","Male.png"}));
    
    public static final int BLOG_LABELS_LIMIT=10;
    
    public static final int PRODUCT_LABELS_LIMIT=10;
    
    public static List<Long> productIds;
    
    public static class CURRENT_ACL{
    	public static final int 
    	ADMINISTRATOR_ONLY=1,
    	ADMIN_MODERATOR=2,
    	ADMIN_MODERATOR_SELF=3,
    	ADMIN_SELF=4,
    	MODERATOR_ONLY=7,
    	SELF_ONLY=8,
    	ALL_USERS=5,
    	GUESTS=6;
    }
    
    public static class REPORTABUSE{
    	public static final int 
    	ON_PRODUCT=1,
    	ON_USER=2,
    	ON_STORE=3,
    	ON_BLOG=4;
    }
    
    public static class DEFAULTIMAGES{
    	public static final String 
    	EMPTY_COLLECTION="images/emptycollection.jpg";
    }
    
    public static class IMAGESTORESIZE{
    	public static class 	AS_IT_IS{
    		public static final int Identifier=0,width=1600,height=900;
    		public static final String filestate="full";
    	}
    	public static class 	THUMBNAIL_BRICK{
    		public static final int Identifier=1,width=231,height=231;
    		public static final String filestate="thumb";
    	}
    	public static class 	THUMBNAIL_VERYSMALL{
    		public static final int Identifier=2,width=48,height=48;
    		public static final String filestate="vsmall";
    	}
    } 
    
    public static final String WP="WallPapers";
    
    public static String[] WallPapers=
    		Kvarrayproperty.getarrayproperty(Kvarrayproperty.setinitialKvarrayproperty(WP,	new String[]{routes.Assets.at("gallery/wallpapers/1.jpg").url(),routes.Assets.at("gallery/wallpapers/2.jpg").url(),routes.Assets.at("gallery/wallpapers/3.jpg").url()}));
    
    public static String WallPaperDirectory=Kvproperty.getpropertyvalue(Kvproperty.setpropertyvalue("WallPaperDirectory", "public/gallery/wallpapers/", String.class.getName()).propertyname) ;
    
    public static class SIGNUP_STAGE{
    	public static final int JUST_REGISTERED=0x17, EMAIL_TO_BE_ENTERED=0x34,EMAIL_CONFLICTED=0x57,CHOOSE_CATEGORY=0x149,CHOOSE_INFLUENCERS=0x320,FETCH_CONTACTS=0x416,FOLLOW_FRIENDS=0x545,CLEARED_ALL_STAGES=0x600;
    }
    
    public static final String REDIRECT_BACK_URL="rbu";
    
    public static DatabaseReader geoipreader=null;
    
    public static class PRODUCT_ADD_MODE{
    	public static final int FROM_WEB=1,FROM_IMAGE=2,FROM_UPLOAD=3,FROM_GRABBER=4;
    }
    
    public static class CONTENT_TYPES{
    	public static final int HTML=1,IMAGE_GPJ=2,IMAGE=3,UNKNOWN=4,TEXT=5,JSON=6,JS=7,CSS=8,OCTET_STREAM=9;
    	public static final String R_HTML="^((text||application)/html?).*",R_IMAGE_GPJ="^(image/(jpe?g|png|gif))",R_IMAGE="^(image/.*)",R_TEXT="^(text/plain)",R_JSON="^(text/json)",R_JS="^(text/(js||javascript))",R_CSS="^(text/css)",R_OCTET_STREAM="^(application/octet-stream)";
    	public static int getcontenttype(final String contenttype)
    	{
    		String ct=contenttype.trim().toLowerCase();
    		if(ct.matches(R_HTML))
    			return HTML;
    		if(ct.matches(R_IMAGE_GPJ))
    			return IMAGE_GPJ;
    		if(ct.matches(R_IMAGE))
    			return IMAGE;
    		if(ct.matches(R_TEXT))
    			return TEXT;
    		if(ct.matches(R_JSON))
    			return JSON;
    		if(ct.matches(R_JS))
    			return JS;
    		if(ct.matches(R_CSS))
    			return CSS;
    		if(ct.matches(R_OCTET_STREAM))
    			return OCTET_STREAM;
    		return UNKNOWN;    		
    	}
    	
    	public static Boolean isspecifiedcontenttype(final String contenttype,final int desiredtype)
    	{
    		return getcontenttype(contenttype)==desiredtype;
    	}
    	
    	public static Map<String, List<String>> getheaderfield(final java.net.URL url) throws IOException
    	{
    		return url.openConnection().getHeaderFields();
    	}
    	
    	public static Boolean isvalidtype(final Map<String, List<String>> headerfield,final int typeallowed )
    	{
    		Boolean response=true;
    		if(Play.isDev())
    		{
    			for (Entry<String, List<String>> si : headerfield.entrySet())
            	{
            		System.out.printf("KEY :%s :-",si.getKey());
            		for(String sii:si.getValue())
            		{
            			System.out.printf("\t\t VALUE :%s",sii);
            		}
            		System.out.println();
            	}
    			
    		}
    		try {
    			if(headerfield.containsKey("Content-Type") && response)
            	{
    				List<String> ls=headerfield.get("Content-Type");
            		if(!ls.isEmpty())
            			response&=isspecifiedcontenttype(ls.get(0), typeallowed);       			
            		else
            			response&=false;
            		if(Play.isDev()){Logger.debug(ls.get(0));}
            	}else{response=false;}
    			return response;
			} catch (Exception e) {
				return response=false;
			}
    	}
    	
    	public static Boolean isvalidlengthtype(final Map<String, List<String>> headerfield,final int lenlimit,final int typeallowed )
    	{
    		Boolean response=true;
    		if(Play.isDev())
    		{
    			for (Entry<String, List<String>> si : headerfield.entrySet())
            	{
            		System.out.printf("KEY :%s :-",si.getKey());
            		for(String sii:si.getValue())
            		{
            			System.out.printf("\t\t VALUE :%s",sii);
            		}
            		System.out.println();
            	}
    			
    		}
    		try {
    			if(headerfield.containsKey("Content-Length"))
            	{
    				List<String> ls=headerfield.get("Content-Length");
    				if(!ls.isEmpty())
    				{
    					int retlen=Integer.parseInt(ls.get(0));
    					response&=(retlen>0 && retlen<=lenlimit);
    					if(Play.isDev()){Logger.debug(ls.get(0));}
    				}
    				else
    					response&=false;        		    
            	}else{response=false;}
    			if(headerfield.containsKey("Content-Type") && response)
            	{
    				List<String> ls=headerfield.get("Content-Type");
            		if(!ls.isEmpty())
            			response&=isspecifiedcontenttype(ls.get(0), typeallowed);       			
            		else
            			response&=false;
            		if(Play.isDev()){Logger.debug(ls.get(0));}
            	}else{response=false;}
    			return response;
			} catch (Exception e) {
				return response=false;
			}
    	}
    }
    
    
    public static class AMAZON_DIRECTORIES{
    	public static final String FOR_USER=User.class.getSimpleName(),FOR_PRODUCT=Product.class.getSimpleName(),FOR_COLLECTION=UserCollection.class.getSimpleName(),FOR_TEMP_PRODUCT="tmp/product/fromuser";
    }
    
    public static class JSGLOBAL{
    	public static final String POST_AUTHORIZE_PENDING ="papen",ISNOT_LOGGEDIN="neelog";
    }
    
    public static class QUERYTYPES {
    	public static final int OTHERQUERY =1,PAYMENTRELATED=2,ORDERRELATED=3,RECEIVEDITEM=4,FEATURESNASSISTANCE=5,SELLERELATED=6;
    }
    
    public static final Map<Integer,String> QueryType;
    static
    {
    	QueryType = new HashMap<Integer, String>();
    	QueryType.put(QUERYTYPES.OTHERQUERY, "General Questions");
    	QueryType.put(QUERYTYPES.PAYMENTRELATED, "Report a Bug");
    	QueryType.put(QUERYTYPES.ORDERRELATED,"Friends Related");    	
    	QueryType.put(QUERYTYPES.RECEIVEDITEM,"Product Related");
    	QueryType.put(QUERYTYPES.FEATURESNASSISTANCE,"Blog Related");
    	QueryType.put(QUERYTYPES.SELLERELATED,"Problem With Site");
    	
    }
    
    //public static final String Recaptchahtml= ReCaptchaFactory.newReCaptcha(controllers.Application.Captchapublic, controllers.Application.Captchaprivate, false).createRecaptchaHtml(null, null).replaceAll("\\p{C}", " ").replaceAll("/script", "--VBS");
    
}
