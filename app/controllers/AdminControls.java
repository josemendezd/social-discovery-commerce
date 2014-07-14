package controllers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.persistence.Lob;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlUpdate;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.Contributor;
import models.Product;
import models.User;
import models.Admin.*;
import play.Logger;
import play.data.Form;
import play.mvc.*;
import scala.App;
import views.html.Admin.*;

public class AdminControls extends Controller {
	
	@Restrict(@Group(Application.ADMIN_ROLE))
	public static Result ipblacklistcontrol_view(int page, String sortBy, String order){return ipblacklistcontrol.view(page, sortBy, order);}
	
	@Restrict(@Group(Application.ADMIN_ROLE))
	public static Result ipblacklistcontrol_create(){return ipblacklistcontrol.create();}
	
	@Restrict(@Group(Application.ADMIN_ROLE))
	public static Result ipblacklistcontrol_delete(Long id,int pn){return ipblacklistcontrol.delete(id,pn);}
	
	public static class ipblacklistcontrol{
		public static class validator{
			public String lowerip;
		    
		    public String upperip;
		    
		    public String description;
		    
		    public String validate() {
		    	try{
		    		Ipblacklist.ipToLong(lowerip);
		    		if(upperip!=null)
		    			Ipblacklist.ipToLong(upperip);
		    		return null;
		    		
		    	}catch(Exception e){return "Not Valid URL";}
		    }
		}
		public static Result view(int page, String sortBy, String order)
		{
			return ok(views.html.Admin.Ipblock.render(Ipblacklist.RecentBlacklists(page, DInitial.GENERIC_PAGE_SIZE, sortBy, order), sortBy, order));
		}
		
		public static Result create()
		{
			User author = Application.getLocalUser(session());
			final Form<validator> filledform = play.data.Form.form(validator.class).bindFromRequest();
			if(!filledform.hasErrors())
			{
				validator v=filledform.get();
				if(v.upperip!=null && v.upperip.length()>6)
					try {
						Ipblacklist.AddnewBlacklist(author, Ipblacklist.ipToLong(v.lowerip.trim()), Ipblacklist.ipToLong(v.upperip.trim()), v.description);
						flash(Application.FLASH_MESSAGE_KEY,"Done Adding Range!!");
					} catch (UnknownHostException e) {
						flash(Application.FLASH_ERROR_KEY,"Invalid IPs Entered");
					}
				else
					try {
						Ipblacklist.AddnewBlacklistSingle(author, Ipblacklist.ipToLong(v.lowerip.trim()), v.description);
						flash(Application.FLASH_MESSAGE_KEY,"Done Adding IP!!");
					} catch (UnknownHostException e) {
						flash(Application.FLASH_ERROR_KEY,"Invalid IP Entered");
					}			
			}else{flash(Application.FLASH_ERROR_KEY,filledform.errorsAsJson().toString());}
			return redirect(routes.AdminControls.ipblacklistcontrol_view(0, "datecreated", "desc"));
		}
		
		public static Result delete(Long id,int pn)
		{
			Ipblacklist ipb=Ipblacklist.find.byId(id);
			if(ipb!=null)
				ipb.delete();
			else
				flash(Application.FLASH_ERROR_KEY,"Error deleting");
			return redirect(routes.AdminControls.ipblacklistcontrol_view(pn, "datecreated", "desc"));
		}
	}
	
	
	
	
	
	
	public static class userinfocontrol{
		public static Result view(int page, String sortBy, String order, String filter)
		{
			return TODO;
		}
	}
	
	
	
	@Restrict(@Group(Application.ADMIN_ROLE))
	public static Result usercontrol_activatedeactivate(Long contribid){return usercontrol.activatedeactivate(contribid);}
	
	public static class usercontrol{
		public static Result view(int page, String sortBy, String order, String filter)
		{
			return TODO;
		}
		
		public static Result update()
		{
			return TODO;
		}
		
		public static Result delete()
		{
			return TODO;
		}
		
		public static Result activatedeactivate(Long contribid)
		{
			Contributor contrib = Contributor.find.byId(contribid);
			Long idofcontrib=Application.getContributor(session()).id;
			Boolean activestatus=false;
			if(contrib!=null)
			{
				idofcontrib=contribid;
				User culprit = contrib.user;
				activestatus=culprit.active;
				if(activestatus)
					culprit.active=false;
				else
					culprit.active=true;
				culprit.save();
				flash(Application.FLASH_MESSAGE_KEY,"Successfully "+(activestatus?"Deactivated":"Activated")+" "+culprit.name);
				
			}
			else{flash(Application.FLASH_ERROR_KEY,"No Such user Present in record");}
			return redirect(routes.Application.ContributorPage(idofcontrib, false, false));
		}
	}
	
	@Restrict(@Group({Application.ADMIN_ROLE , Application.MODERATOR_ROLE}))
	public static Result productcontrol_activatedeactivate(Long prid){return usercontrol.activatedeactivate(prid);}
	
	public static class productcontrol{
		public static Result view(int page, String sortBy, String order, String filter)
		{
			return TODO;
		}
		
		public static Result update()
		{
			return TODO;
		}
		
		public static Result delete()
		{
			return TODO;
		}
		
		public static Result activatedeactivate(Long pid)
		{
			Product prod = Product.find.byId(pid);
			Long idofcontrib=Application.getContributor(session()).id;
			Boolean activestatus=false;
			if(prod!=null)
			{
				activestatus=prod.alive;
				if(activestatus)
					prod.alive=false;
				else
					prod.alive=true;
				prod.save();
				flash(Application.FLASH_MESSAGE_KEY,"Successfully "+(activestatus?"Deactivated":"Activated")+" "+prod.productname);
				
			}
			else{flash(Application.FLASH_ERROR_KEY,"No Such Product Present in record");}
			return redirect(routes.Application.ContributorPage(idofcontrib, false, false));
		}
	}
	
	public static class collectioncontrol{
		public static Result view(int page, String sortBy, String order, String filter)
		{
			return TODO;
		}
		
		public static Result update()
		{
			return TODO;
		}
		
		public static Result delete()
		{
			return TODO;
		}
	}
	
	@Restrict(@Group(Application.ADMIN_ROLE))
	public static Result sqlcontrol_view(){return sqlcontrol.view();}
	@Restrict(@Group(Application.ADMIN_ROLE))
	public static Result sqlcontrol_update(){return sqlcontrol.update();}
	
	public static class sqlcontrol{
		public static class validator{
			public String sqltext;
			public String validate() {
		    	if(sqltext.length()==0)
		    		return "NO SQL TO EXECUTE";
				return null;
		    }
		}
		public static Result view()
		{
			File f=new File(play.Play.application().path()+ File.separator +"conf"+File.separator+"evolutions"+File.separator+"default"+File.separator+"1.sql");
			String content="";
			try {
				FileReader reader = new FileReader(f);
				char[] chars = new char[(int) f.length()];
				reader.read(chars);
				content = new String(chars);
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return ok(views.html.Admin.Executesql.render(content));
		}
		
		public static Result update()
		{
			final Form<validator> filledform = play.data.Form.form(validator.class).bindFromRequest();
			String reply="";
			if(!filledform.hasErrors())
			{
				validator v=filledform.get();
				reply=v.sqltext;
				SqlUpdate sqlupdate = Ebean.createSqlUpdate(v.sqltext);
				Ebean.execute(sqlupdate);
				flash(Application.FLASH_MESSAGE_KEY,"Executed Successfully");
				
			}
			else{
				{flash(Application.FLASH_ERROR_KEY,filledform.errorsAsJson().toString());}
			}
			return ok(views.html.Admin.Executesql.render(reply));
		} 
	}

}

/*
public static class ipblacklistcontrol{
	public static Result view()
	{
		return ;
	}
	
	public static Result create()
	{
		return ;
	}
	
	public static Result delete()
	{
		return ;
	}
}
*/