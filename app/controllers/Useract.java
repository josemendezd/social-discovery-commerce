package controllers;

import static play.data.Form.form;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.validation.constraints.NotNull;

import models.Blog;
import models.BlogComment;
import models.BlogImage;
import models.BlogLikes;
import models.Category;
import models.CollectionComment;
import models.Comment;
import models.Contributor;
import models.EventLogSql;
import models.Eventlog;
import models.FSearch;
import models.Follow;
import models.Product;
import models.S3File;
import models.Store;
import models.User;
import models.UserCollection;
import models.UserRate;
import models.Admin.Feedback;
import models.Admin.Infopage;
import models.Admin.Reportabuse;
import models.Notifications.SOFollows;
import models.Notifications.SORecommends;
import models.Notifications.SOSuggestsCo;

import org.hibernate.validator.constraints.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import play.Logger;
import play.api.templates.Html;
import play.data.DynamicForm;
import play.data.Form;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import plugins.S3Plugin;
import scala.concurrent.ExecutionContext;
import viewmodel.PRDetails;
import viewmodel.ProductRateDetail;
import views.html.productjson;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import be.objectify.deadbolt.java.actions.SubjectPresent;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.repackaged.com.google.common.base.Strings;

public class Useract  extends Controller {
	
	//..........NAVIGATION.................
	//>>>>>>>>>>SUB PART- QUERY<<<<<<<<<<<<
	
	private static final String COLLECTION_GALLERY_UPLOADS = "gallery/uploads/";

	@SubjectPresent
	public static Result  MyWatchList()
	{
		return ok(views.html.feed.render(Application.getContributor(session())));
	
	}
	@SubjectPresent
	public static Result  MyWatchListJson()
	{		
		DynamicForm bindedForm = play.data.Form.form().bindFromRequest();
		int page = Integer.parseInt( bindedForm.get("page"));
		int pagesize = Integer.parseInt( bindedForm.get("pagesize"));
		User user = Application.getLocalUser(session());
		if(user==null)
			return badRequest("Identity not found please relogin");
		/*
	    int gendercont = Integer.parseInt( bindedForm.get("gendercont"));//To be passed
	    int pricerange = Integer.parseInt( bindedForm.get("pricerange"));//To be passed	    
	    int catgid = Integer.parseInt(bindedForm.get("catgid"));//To be passed
	    String searchtext = bindedForm.get("searchtext");//To be passed	
	    if(searchtext!=null)
	    	searchtext=GHelp.FilteredSql(searchtext);
		response().setContentType("application/json");	    
		return ok(views.html.feedjson.render(EventLogSql.Feed(page, pagesize, Application.getContributor(session()),GHelp.filterSql(searchtext, gendercont, pricerange, catgid, "P."))));
		*/
		response().setContentType("application/json");
		return ok(productjson.render(Product.Usersfeed(page, pagesize, user)));
	}
	
	@SubjectPresent
	public static Result  myfriendsfeed()
	{
		return ok(views.html.friendsfeed.render(Application.getContributor(session())));
	
	}
	
	@SubjectPresent
	public static Result  myfriendsfeedjson()
	{
		DynamicForm bindedForm = play.data.Form.form().bindFromRequest();
		int page = Integer.parseInt( bindedForm.get("page"));
	    int gendercont = Integer.parseInt( bindedForm.get("gendercont"));//To be passed
	    int pricerange = Integer.parseInt( bindedForm.get("pricerange"));//To be passed
	    int pagesize = Integer.parseInt( bindedForm.get("pagesize"));
	    int catgid = Integer.parseInt(bindedForm.get("catgid"));//To be passed
	    String searchtext = bindedForm.get("searchtext");//To be passed	
	    if(searchtext!=null)
	    	searchtext=GHelp.FilteredSql(searchtext);
		response().setContentType("application/json");	    
		return ok(views.html.feedjson.render(EventLogSql.FriendFeed(page, pagesize, Application.getContributor(session()),GHelp.filterSql(searchtext, gendercont, pricerange, catgid, "P."))));
	}
	
	@SubjectPresent
	public static Result  SaveMySearch (String searchtext ,int gendercont ,int pricerange)
	{
		ObjectNode collectionnode =Json.newObject();
		Contributor c=Application.getContributor(session());
		//play.Logger.info(String.format("(%d,%d),(%d,%d)", pricerange,DInitial.PR.length,gendercont,DInitial.gender.size()));
		
		if(pricerange<DInitial.PR.length && gendercont<DInitial.gender.size() && pricerange >=0 &&gendercont >=0)
		{
			FSearch fs = FSearch.IsFollowingSearch(searchtext, gendercont, pricerange, c);
			if(fs==null)
			{
				FSearch.FollowSearch(searchtext, gendercont, pricerange, c);
				collectionnode.put("answer", true);
			}
			else
			{
				fs.delete();
				collectionnode.put("answer", false);
			}
			return ok(collectionnode);
		}else{collectionnode.put("answer", false);}
		return ok(collectionnode);
	}
	
	@SubjectPresent
	public static Result  FindthisSearch (String searchtext ,int gendercont ,int pricerange)
	{
		ObjectNode collectionnode =Json.newObject();
		Contributor c=Application.getContributor(session());
		if(pricerange<(DInitial.PR.length/2) && gendercont<DInitial.gender.size() && pricerange >=0 &&gendercont >=0)
		{
			FSearch fs = FSearch.IsFollowingSearch(searchtext, gendercont, pricerange, c);
			if(fs==null)
				collectionnode.put("answer", false);
			else
				collectionnode.put("answer", true);
			return ok(collectionnode);
		}else{collectionnode.put("answer", false);}
		return ok(collectionnode);
	}
	
	@SubjectPresent
	public static Result  CopySearch(Long id)
	{
		ObjectNode collectionnode =Json.newObject();
		Contributor c=Application.getContributor(session());
		FSearch fhs=FSearch.find.byId(id);
		FSearch fs=fhs.IsFollowingSearch(c);
		if(fs==null)
		{
			fhs.CopySearch(c);
			collectionnode.put("answer", true);
		}
		else
		{
			fs.delete();
			collectionnode.put("answer", false);
		}
		return ok(collectionnode);
	}
	
	@SubjectPresent
	public static Result  FindthisSearchbyId (Long id)
	{
		ObjectNode collectionnode =Json.newObject();
		Contributor c=Application.getContributor(session());
		FSearch fs = FSearch.find.byId(id);
		if(fs!=null){
		if(fs.IsFollowingSearch(c)==null)
			collectionnode.put("answer", false);
		else
			collectionnode.put("answer", true);
		}else{collectionnode.put("answer", false);}
		return ok(collectionnode);
	}
	
	@SubjectPresent
	public static Result  SetSearchPrivate (Long id)
	{
		ObjectNode collectionnode =Json.newObject();
		Contributor c=Application.getContributor(session());
		FSearch fs = FSearch.find.byId(id);
		if(fs!=null && fs.userid.equals(c)){
			if(fs.isprivate==true)
			{	
				fs.isprivate=false;
				fs.save();
				collectionnode.put("answer", false);
			}
			else{
				fs.isprivate=true;
				fs.save();
				collectionnode.put("answer", true);
			}
		}else{collectionnode.put("answer", false);}
		return ok(collectionnode);
	}
	
	
	
	
	//======================================NAVIGATION COMPLETE============================
	
	
	@SubjectPresent
	public static Result FollowAct(int actioncontxt,Long obid)
	{
		ObjectNode collectionnode =Json.newObject();
		Contributor c=Application.getContributor(session());
		switch(actioncontxt)
		{
		case DInitial.SEARCH_USER:
			Contributor c1=Contributor.find.byId(obid);
			
			if(c1!=null)
			{
				if(c1.equals(c))
				{collectionnode.put("answer", false);}
				else{
					if(!Follow.IsFollower(c1.id, c.id))
					{
						c1.AddFollower(c);
						collectionnode.put("answer", true);
					}
					else
					{
						if(c1.RemoveFollower(c))
						{
							SOFollows.FindandDeleteEvent(c, c1);
							collectionnode.put("answer", false);
						}
						else
							collectionnode.put("answer", true);
					}
				}
				return ok(collectionnode); 								
			}else{collectionnode.put("answer", false);}
			break;
		case DInitial.SEARCH_COLLECTION:
			UserCollection uc1=UserCollection.find.byId(obid);
			if(uc1!=null)
			{
				if(!Follow.IsCollectionFollower(uc1.id, c.id))
				{
					UserCollection.AddFollower(uc1, c);
					collectionnode.put("answer", true);
				}
				else
				{
					if(UserCollection.RemoveFollower(uc1, c))
						collectionnode.put("answer", false);
					else
						collectionnode.put("answer", true);	
				}
				return ok(collectionnode);				
			}else{collectionnode.put("answer", false);}
		case DInitial.SEARCH_STORE:
			Store s1=Store.find.byId(obid);
			if(s1!=null)
			{
				if(!Follow.IsStoreFollower(s1.id, c.id))
				{
					Store.AddFollower(s1, c);
					collectionnode.put("answer", true);
				}
				else
				{
					if(Store.RemoveFollower(s1, c))
						collectionnode.put("answer", false);
					else
						collectionnode.put("answer", true);	
				}
				return ok(collectionnode);
							
			}else{collectionnode.put("answer", false);}
			break;
		default:
			collectionnode.put("answer", false);
			break;
		}
		return badRequest(collectionnode);
		
	}
	
	@SubjectPresent
	public static Result TogglePrivacy(Long obid)
	{
		ObjectNode collectionnode =Json.newObject();
		Contributor c=Application.getContributor(session());
		
		UserCollection uc=UserCollection.find.byId(obid);
		if(uc!=null && (uc.contributor.equals(c)||uc.collectionadmin.contains(c)))
		{
			if(uc.isprivate)
			{
				uc.isprivate=false;
				collectionnode.put("answer", false);
			}
			else
			{
				uc.isprivate=true;
				collectionnode.put("answer", true);				
			}
			uc.save();
			return ok(collectionnode);				
		}else{collectionnode.put("answer", false);}
		
		return badRequest(collectionnode);
		
	}
	
	//..........PRODUCT CREATE.................
	
	public static class NEW_PRODUCT {
	
		@NotNull
		public String productname;
		
		@NotNull
	    public String currency;
	    
		@NotNull
	    public double pricetag;
		
		@NotNull
		@URL
		public String imagelocation;
		
		public String capturesite;
		
		public long categoryid;
		
		public long gender;
		
		public String description;
		
		public String location;
		
		public String servicearea;
		
		public Category getcategory()
		{
			return Category.find.byId(categoryid);
		}
		
		public String validate() {
			if(getcategory()==null||categoryid==1)
				return "Category Not Found!!!";
			if(gender>2||gender<0)
				return "unknown gender";
			if(productname.length()<2)
				return "Enter valid product name";
			return null;
		}
	}
	
	
	@SubjectPresent
	public static Result  CreateProduct()
	{
		User usr=Application.getLocalUser(session());
		if(usr==null)
			return unauthorized();
		final Form<Useract.NEW_PRODUCT> filledForm = play.data.Form.form(Useract.NEW_PRODUCT.class).bindFromRequest();
		

		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			return badRequest(filledForm.errorsAsJson());
			
		} 
		
		Useract.NEW_PRODUCT np=filledForm.bindFromRequest().get();
		
		
		
		Product p=Product.CreateProduct(np.productname, np.currency, np.pricetag, usr, np.capturesite, np.imagelocation, np.getcategory(),true,np.gender,np.description);
		if(p!=null)
		{
				/*
				String allotedname=p.getproductimagename(true);
				File tempstorage =GHelp.downloadandsaveimage(np.imagelocation, allotedname) ;
				if(tempstorage==null)
					throw new FileNotFoundException();
				p.savetocdn(allotedname, tempstorage);
				tempstorage.delete();
				*/
				usr.contrib.AddLover(p);
				Eventlog.ReportEvent(DInitial.PRODUCT_LOVED, p, usr.contrib, null, p.pstore);
				flash(Application.FLASH_MESSAGE_KEY,"Successfully captured the product.");
				
		}
		else
		{
			//flash(Application.FLASH_ERROR_KEY,"Error capturing the product.");
			//Report to support
			return ok(Html.apply("<h1>Error Capturing the product.Reported to Support for resolution.</h1>"));
			
		}
		
		
		return redirect(routes.Application.ProductPage(p.id,false));	
		
	}
	
	@SubjectPresent
	public static Result  addoptions()
	{
		return ok(views.html.Tools.addproduct.render());
	
	}
	
	@SubjectPresent
	public static Result  addoptionsfromweb(String weburl)
	{
		try{
			Logger.info(weburl);
			java.net.URL url=new java.net.URL(weburl);
			final Map<String, List<String>> headerfield=DInitial.CONTENT_TYPES.getheaderfield(url);
			if(DInitial.CONTENT_TYPES.isvalidlengthtype(headerfield,DInitial.IMAGE_UPLOAD_FILE_SIZE_LIMIT , DInitial.CONTENT_TYPES.IMAGE_GPJ))
				return ok(views.html.Tools.confirm.render(null, null, null, weburl, "USD", 10.00, Application.getLocalUser(session())));
			if(DInitial.CONTENT_TYPES.isvalidtype(headerfield, DInitial.CONTENT_TYPES.HTML))
			{
				Document doc = Jsoup.connect(weburl).timeout(10000).get();
		        //Elements links = doc.select("a[href]");
		        Elements imgs = doc.select("img");
		        Element title = doc.select("title").first();
		        List<String> srclist=new ArrayList<String>();
		        String titlestring=null;
		        if(title!=null)
		        	titlestring=title.text();
		        Logger.debug(String.valueOf(imgs.size())+" page title: "+titlestring);
		        for(Element img:imgs)
		        {
		        	if (img.tagName().equals("img"))
		        	{
		        		srclist.add(img.attr("abs:src")) ;
		        		Logger.debug(img.attr("abs:src"));
		        	}
		        }
		        if(srclist.size()>0)
		        	return ok(views.html.Tools.showimages.render(srclist,titlestring.replace('\'', '`'),weburl));
		        else
		        	flash(Application.FLASH_ERROR_KEY,"No images found on this page.");
			}else{flash(Application.FLASH_ERROR_KEY,"URL is not valid Webpage.");}
		
		}catch(Exception e)
		{
			e.printStackTrace();
			flash(Application.FLASH_ERROR_KEY,"Doesn't seems like right URL.Please Recheck.");
		}
		
		return ok(views.html.Tools.addproduct.render());	
	}
	
	@SubjectPresent
	public static Result  addoptionsbyupload()
	{
		User localUser=Application.getLocalUser(session());
		FilePart fp=request().body().asMultipartFormData().getFile("picture");
		String allotedname=GHelp.getimageextension(localUser.id.toString(), fp.getContentType())
				,filename=fp.getFilename();
		
		if(fp.getFile().length()>DInitial.IMAGE_UPLOAD_FILE_SIZE_LIMIT)
		{
			flash(Application.FLASH_ERROR_KEY, "File exceeds size limit.");
			return Useract.addoptions();
		}
		if(!DInitial.CONTENT_TYPES.isspecifiedcontenttype( fp.getContentType(),DInitial.CONTENT_TYPES.IMAGE_GPJ))
		{
			flash(Application.FLASH_ERROR_KEY,"Unknown File Type!! Please use JPG,PNG");
			return Useract.addoptions(); 
		}
		S3File s3f= S3File.createfile(S3Plugin.s3Bucket, DInitial.AMAZON_DIRECTORIES.FOR_TEMP_PRODUCT, allotedname, DInitial.IMAGESTORESIZE.AS_IT_IS.filestate, localUser.id, fp.getFile());
		fp.getFile().delete();
		return ok(views.html.Tools.confirm.render("Add "+filename, null, null, s3f.geturlstring(), "USD", 10.00, localUser));
		//title: String,pn: String,purl: String,pil: String,currency:String,cp:Double,u:User
	}
	//..........PRODUCT ACTIONS................
	
	@SubjectPresent
	public static Result  Love(Long prid)
	{
		Contributor c=Application.getContributor(session());
		Product p=Product.find.byId(prid);
		if(p!=null && c.AddLover(p))
		{
			Eventlog.ReportEvent(DInitial.PRODUCT_LOVED, p, c, null, p.pstore);
			response().setContentType("application/json");
			return ok(views.html.Templates.json.productbrick.render(p));
		}
		return play.mvc.Results.unauthorized();
	}
	
	@SubjectPresent
	public static Result  Hate(Long prid)
	{
		Contributor c=Application.getContributor(session());
		Product p=Product.find.byId(prid);
		
		if(p!=null && c.RemoveLover(p))
		{
			Eventlog.RemoveEvent(DInitial.PRODUCT_LOVED, p, c);
			response().setContentType("application/json");
			return ok(views.html.Templates.json.productbrick.render(p));
		}
		return play.mvc.Results.unauthorized();
	}
	
	//....................WANT............
	@SubjectPresent
	public static Result Want(Long prid)
	{
		Contributor c=Application.getContributor(session());
		Product p=Product.find.byId(prid);
		if(p!=null && c.AddWanter(p))
		{
			response().setContentType("application/json");
			return ok(views.html.Templates.json.productbrick.render(p));
		}
		return play.mvc.Results.badRequest("Unable to Add in your want list :(");
	}
	@SubjectPresent
	public static Result  Nomore(Long prid)
	{
		Contributor c=Application.getContributor(session());
		Product p=Product.find.byId(prid);
		if(p!=null && c.RemoveWanter(p))
		{
			response().setContentType("application/json");
			return ok(views.html.Templates.json.productbrick.render(p));
		}
		return play.mvc.Results.badRequest();
	}
	
	//................OWN.............................
	@SubjectPresent
	public static Result Own(Long prid)
	{
		Contributor c=Application.getContributor(session());
		Product p=Product.find.byId(prid);
		if(p!=null && c.AddOwner(p))
		{
			response().setContentType("application/json");
			return ok(views.html.Templates.json.productbrick.render(p));
		}
		return play.mvc.Results.badRequest();
	}
	@SubjectPresent
	public static Result  Release(Long prid)
	{
		Contributor c=Application.getContributor(session());
		Product p=Product.find.byId(prid);
		if(p!=null && c.RemoveOwner(p))
		{
			response().setContentType("application/json");
			return ok(views.html.Templates.json.productbrick.render(p));
		}
		return play.mvc.Results.badRequest();
	}
	
	
	@SubjectPresent
	public static Result GetCollections(Long prid)//For all collection where this product exists.
	{
		Contributor c=Application.getContributor(session());
		Product p=Product.find.byId(prid);
		ObjectNode collectionnode =Json.newObject();
		if(p!=null)
		{ 
			
			for(UserCollection ucollect:c.UCollections)
			{
				collectionnode.putArray(ucollect.colname);
				//responsecollection.concat("<li>"+ucollect.colname+"</li>");
			}
			
			return ok(collectionnode);
		}			
		return badRequest("Failed Owner\n");
	}
	
	@SubjectPresent
	public static Result GetCollectionsImages()
	{
		Contributor c=Application.getContributor(session());
		ObjectNode collectionnode =Json.newObject();
		ArrayNode ms = collectionnode.putArray("images");
		c.refresh();
		for(UserCollection ucollect : c.UCollections) {
			if(!Strings.isNullOrEmpty(ucollect.coverimage)) {
				ObjectNode ob = Json.newObject();
				ob.put("url", ucollect.coverimage);
				ob.put("id", ucollect.id);
				ms.add(ob);
			}
		}
		return ok(collectionnode);
	}
	
	@SubjectPresent
	public static Result RecommendProduct(Long prid)//For all collection where this product exists.
	{
		Contributor c=Application.getContributor(session());
		Product p=Product.find.byId(prid);
		DynamicForm bindedForm = play.data.Form.form().bindFromRequest();
		String username=bindedForm.get("username");
		User u=User.findByUserName(username);
		ObjectNode collectionnode =Json.newObject();
		if(p!=null && u!=null)
		{
			SORecommends.ReportEvent(c, p, u.contrib);
			collectionnode.put("answer", "Recommended!!");
			return ok(collectionnode);
		}
		collectionnode.put("answer", "An Error Occured!!");
		return badRequest();		
	}
	
	@SubjectPresent
	public static Result MailRecommendProduct(Long prid)//For all collection where this product exists.
	{
		final Contributor c=Application.getContributor(session());
		final Product p=Product.find.byId(prid);
		DynamicForm bindedForm = play.data.Form.form().bindFromRequest();
		final String useremail=bindedForm.get("useremail");
		ObjectNode collectionnode =Json.newObject();
		if(p!=null && useremail!=null)
		{
			final ExecutionContext ec = akka.dispatch.ExecutionContexts.global();
	    	akka.dispatch.Futures.future(new Callable<Boolean>() {
	    	public Boolean call() {
			GHelp.mailsender.sendMail(Messages.get("boozology.notifications.sorecommends",c.user.name,p.productname),
	    			new com.feth.play.module.mail.Mailer.Mail.Body(views.html.Templates.MailTemplates.mailingproduct
	    					.render(c.user.name,Application.WebAddress+routes.Application.ProductPage(p.id, false).url())
	    					.toString(),views.txt.Templates.MailTemplates.mailingproduct
	    					.render(c.user.name,Application.WebAddress+routes.Application.ProductPage(p.id, false).url())
	    					.toString()),
	    			GHelp.getEmailName(useremail, useremail));
			return true;
	    	}
	    	}, ec);
			collectionnode.put("answer", "Recommended!!");
			return ok(collectionnode);
		}
		collectionnode.put("answer", "An Error Occured!!");
		return badRequest(collectionnode);		
	}
	
	//.................COLLECT..............................
	
	public static Result  UserCollectionList(int pageno)
	{
		Contributor c=Application.getContributor(session());
		UserCollection.UsersCollectionPage(c, pageno, DInitial.GENERIC_PAGE_SIZE);
		return TODO;
	}
	
	
	
	@SuppressWarnings("unused")
	@SubjectPresent
	public static Result  CreateCollection()
	{
		Contributor c=Application.getContributor(session());
		DynamicForm bindedForm = play.data.Form.form().bindFromRequest();
		String collectionname= bindedForm.get("collectionname");
		int len=collectionname.length(); 
		if(len>0 && len<40 && !UserCollection.CollectionExists(collectionname, c))
		{
			UserCollection uc =new UserCollection(c, collectionname);
			response().setContentType("application/json");
			if(uc!=null)
				return ok("{\"ncn\":{\"f1\":"+uc.id+",\"f2\":\""+GHelp.EHtml(uc.colname).toString()+"\",\"f3\":false}}");
			else
				return ok("{\"ncn\":false}");
				
			/*
			ObjectNode collectionnode =Json.newObject();
			if(uc!=null)
			{
				String srctree="{\"ncn\":{\"f1\":"+uc.id+",\"f2\":\""+GHelp.EHtml(uc.colname)+"\",\"f3\":false}}";
				  ObjectMapper mapper = new ObjectMapper();
				  JsonNode rootNode = null;
				try {
					rootNode = mapper.readValue(srctree, JsonNode.class);
				} catch (Exception e) {
					collectionnode.put("ncn", "false");
					return ok(collectionnode);
				}
				
				
				return ok(rootNode);
			}
			*/
		}
		
		return play.mvc.Results.badRequest();
	}
	
	@SubjectPresent
	public static Result  DeleteCollection(Long Collectionid)
	{
		Contributor c=Application.getContributor(session());
		UserCollection uc = UserCollection.find.byId(Collectionid);
		if(!(GHelp.VerifyACL(DInitial.CURRENT_ACL.ADMINISTRATOR_ONLY,null)||uc.contributor.equals(c))){
			return badRequest();
		}
		if(uc!=null)
		{
			Eventlog.RemoveAllCollectionEvent(uc);
			UserCollection.DeleteCollection(uc);			
			response().setContentType("application/json");
			return ok("{\"answer\":true}");
		}
		return play.mvc.Results.badRequest();
	}
	
	
	
	@SubjectPresent
	public static Result  AddToMyCollections()
	{
		Contributor c=Application.getContributor(session());
		if(!GHelp.VerifyACL(DInitial.CURRENT_ACL.ADMIN_SELF,c.user)){
			return badRequest();
		}
		DynamicForm bindedForm = play.data.Form.form().bindFromRequest();
		Long prid=Long.parseLong( bindedForm.get("prid")),Collectionid=Long.parseLong( bindedForm.get("Collectionid"));
		UserCollection uc = UserCollection.find.byId(Collectionid);
		Product p=Product.find.byId(prid);
		if(uc!=null && p!=null && uc.AddProduct(p,c))
		{
			Eventlog.ReportEvent(DInitial.PRODUCT_COLLECTED, p, Application.getContributor(session()), uc, p.pstore);
			
			response().setContentType("application/json");
			return ok("{\"answer\":true}");
		}
		return badRequest();
	}
	@SubjectPresent
	public static Result  DiscardFromMyCollections(Long prid, Long Collectionid)
	{
		Contributor c=Application.getContributor(session());
		if(!GHelp.VerifyACL(DInitial.CURRENT_ACL.ADMIN_SELF,c.user)){
			return badRequest();
		}
		Product p=Product.find.byId(prid);
		UserCollection uc = UserCollection.find.byId(Collectionid);
		if(p!=null && uc!=null && uc.RemoveProduct(p,c))
		{
			Eventlog.RemoveEventCollection(DInitial.PRODUCT_COLLECTED, p, c,uc);
			response().setContentType("application/json");
			return ok("{\"answer\":true}");
		}
		return badRequest();
	}
	
	@SubjectPresent
	public static Result  CopytoCollection(Long Collectionid)
	{
		Contributor c=Application.getContributor(session());
		DynamicForm bindedForm = play.data.Form.form().bindFromRequest();
		String colname =bindedForm.get("colname");
		UserCollection hc=UserCollection.find.byId(Collectionid);
		if(hc==null)
			return notFound();
		UserCollection uc = UserCollection.find.where().eq("colname", colname).eq("contributor", c).findUnique();
		if(uc==null)
			uc=new UserCollection(c, colname);
		if(uc!=null && uc.productlist.addAll(hc.productlist))
		{
			uc.save();//Eventlog.ReportEvent(DInitial.PRODUCT_COLLECTED, p, Application.getContributor(session()), uc, p.pstore);
			return ok("Added to the collection!");
		}
		return badRequest();
	}
	
	@SubjectPresent
	public static Result  AddAdmintoCollection(Long Collectionid)
	{
		Contributor c=Application.getContributor(session());
		UserCollection uc = UserCollection.find.byId(Collectionid);
		if(!(GHelp.VerifyACL(DInitial.CURRENT_ACL.ADMIN_MODERATOR,null)||UserCollection.IsCollectionAdmin(uc,c)))
			return notFound();
			
		DynamicForm bindedForm = play.data.Form.form().bindFromRequest();
		String username=bindedForm.get("username");
		User u=User.findByUserName(username);
		if(uc!=null &&!UserCollection.IsCollectionAdmin(uc, u.contrib)&& uc.collectionadmin.add(u.contrib))
		{
			uc.save();
			return ok("Added to the collection!");
		}
		return badRequest();
	}
	

	
	@SubjectPresent
	public static Result  GetProductCollectionStatus(Long prid)
	{
		Contributor c=Application.getContributor(session());
		Product p=Product.find.byId(prid);
		if(p!=null)
		{	
			String jsrep=UserCollection.UserCollectionStatus(c, p);
			if(jsrep==null)
				jsrep=" ";
			response().setContentType("application/json");
			return ok("{\"collectionlist\":[ "+ jsrep+" ]}");						
		}
		return badRequest();
	
	}
	
	@SubjectPresent
	public static Result  Suggestaproduct(Long Collectionid)
	{
		Contributor c=Application.getContributor(session());
		DynamicForm bindedForm = play.data.Form.form().bindFromRequest();
		Long productid =Long.parseLong(bindedForm.get("productid"));
		UserCollection hc=UserCollection.find.byId(Collectionid);
		if(hc==null)
			return notFound();
		Product pr = Product.find.byId(productid);
		if(pr==null)
			return badRequest();
		SOSuggestsCo.ReportEvent(c, pr, hc);
		return ok("Suggested!!");
	}
	
	@SubjectPresent
	public static Result SetCollectionCoverImage(long id)
	{
		UserCollection uc= UserCollection.find.byId(id);
		if(uc==null)
			return badRequest();
		
		FilePart fp=request().body().asMultipartFormData().getFile("picture");
		if(fp.getFile().length()>DInitial.IMAGE_UPLOAD_FILE_SIZE_LIMIT)
		{
			flash(Application.FLASH_ERROR_KEY, "File exceeds size limit.");
			return redirect(routes.Application.showprofileimage());
		}
		
		if(!DInitial.CONTENT_TYPES.isspecifiedcontenttype( fp.getContentType(),DInitial.CONTENT_TYPES.IMAGE_GPJ))
		{
			flash(Application.FLASH_ERROR_KEY,"Unknown File Type!! Please use JPG,PNG");
			return redirect(routes.Application.showprofileimage()); 
		}
		
		String allotedname=uc.getcoverimagename(true);
		
		uc.savetocdn(allotedname, fp.getFile());
		fp.getFile().delete();
		uc.SetCoverImage(uc.getcoverimageThumb());
		
		return redirect(routes.Application.CollectionPage(id, false));
		
	}
	
	@SubjectPresent
	public static Result CopyCollectionCoverImage(long id, long withId) {
		UserCollection uc = UserCollection.find.byId(id);
		UserCollection withUC = UserCollection.find.byId(withId);
		
		if(withUC == null || uc == null) {
			return badRequest();
		}
		File fp =GHelp.downloadandsaveimage(withUC.getcoverimage(), uc.getcoverimagename(false)) ;
		
		uc.savetocdn(uc.getcoverimagename(true), fp);
		fp.delete();
		
		uc.SetCoverImage(uc.getcoverimageThumb());
		return ok(uc.getcoverimage());
		
	}
	
	public static Result GetImage(String type, String name) {
		if("collection".equals(type)) {
			File file = new File(COLLECTION_GALLERY_UPLOADS+name);
			return ok(file);
		}
		return ok();
	}
	
	//................STORE ACTIONS..............................
	@SubjectPresent
	public static Result  SetCustomer(Long storeid)
	{
		Contributor c=Application.getContributor(session());		
		Store st=Store.find.byId(storeid);
		
		ObjectNode collectionnode =Json.newObject();
		
		if(st!=null)
		{
			if(st.IsCustomer(c))
			{
				if(st.RemoveCustomer(c))
					collectionnode.put("answer", false);
				else
					collectionnode.put("answer", true);
				
			}else{
				if(st.AddCustomer(c))
					collectionnode.put("answer", true);
				else
					collectionnode.put("answer", false);
			} 								
		}else{collectionnode.put("answer", false);}
		return ok(collectionnode);
	}
	
	//.................COMMENT............................
	@SubjectPresent
	public static Result  PutcommentonProduct()
	{
		DynamicForm bindedForm = play.data.Form.form().bindFromRequest();
		Long prid = Long.parseLong(bindedForm.get("product"));
		String content=bindedForm.get("cmnttxt");
		if(content==null)
			return badRequest();
		response().setContentType("application/json");	
		Contributor author=Application.getContributor(session());
		if(author.user.emailValidated == true) {
			Comment comment = Comment.find.where().eq("author.id", author.id).order().desc("postedAt").setMaxRows(1).findUnique();
			if(comment != null) {
				if(new Date().getTime() - comment.postedAt.getTime() > 60 * 1000) {
					Product post=Product.find.byId(prid);
					if(post!=null)
					{
						Comment cmt=Comment.AddComment(post, author, content.replace("\n", " "));
						
						if(cmt!=null)
						{
							models.Notifications.SOCommentsPr.ReportEvent(author, post, cmt);
							return ok("{\"answer\": "+ views.html.Templates.json.commentbrick.render(cmt.author,cmt.content,cmt.postedAt,cmt.id, session()).toString() +" }");
						}
					}
				}
			} else {
				Product post=Product.find.byId(prid);
				if(post!=null)
				{
					Comment cmt=Comment.AddComment(post, author, content.replace("\n", " "));
					
					if(cmt!=null)
					{
						models.Notifications.SOCommentsPr.ReportEvent(author, post, cmt);
						return ok("{\"answer\": "+ views.html.Templates.json.commentbrick.render(cmt.author,cmt.content,cmt.postedAt,cmt.id, session()).toString() +" }");
					}
				}
			}
		}
		return badRequest();
	}
	
	@SubjectPresent
	public static Result  Removecomment(Long cid)
	{
		Contributor c=Application.getContributor(session());
		Comment cmnt=Comment.find.byId(cid);
		if(cmnt==null)
			return badRequest();
		response().setContentType("application/json");
		if(!GHelp.VerifyACL(DInitial.CURRENT_ACL.ADMIN_MODERATOR_SELF,cmnt.author.user)){ return unauthorized(); }
		if(Comment.RemoveComment(cmnt,c))
			return ok("{\"answer\":true}");
		return badRequest();
	}
	
	@SubjectPresent
	public static Result  PutcommentonCollection()
	{
		DynamicForm bindedForm = play.data.Form.form().bindFromRequest();
		Long clid = Long.parseLong(bindedForm.get("collection"));
		String content=bindedForm.get("cmnttxt");
		if(content==null)
			return badRequest();
		response().setContentType("application/json");	
		Contributor author=Application.getContributor(session());
		UserCollection uc=UserCollection.find.byId(clid);
		if(uc!=null)
		{
			CollectionComment cmt=CollectionComment.AddComment(uc, author, content.replace("\n", " "));
			
			if(cmt!=null)
			{
				models.Notifications.SOCommentsCo.ReportEvent(author, uc, cmt);
				return ok("{\"answer\": "+ views.html.Templates.json.commentbrick.render(cmt.author,cmt.content,cmt.postedAt,cmt.id, session()).toString() +" }");
			}
		}
		return badRequest();
	}
	
	@SubjectPresent
	public static Result  RemoveCollectioncomment(Long cid)
	{
		Contributor c=Application.getContributor(session());
		CollectionComment cmnt=CollectionComment.find.byId(cid);
		if(cmnt==null)
			return badRequest();
		response().setContentType("application/json");
		if(!GHelp.VerifyACL(DInitial.CURRENT_ACL.ADMIN_MODERATOR_SELF,cmnt.author.user)){ return unauthorized(); }
		if(CollectionComment.RemoveComment(cmnt,c))
			return ok("{\"answer\":true}");
		return badRequest();
	}
	
	@SubjectPresent
	public static Result  AddBlogComment()
	{
		DynamicForm bindedForm = play.data.Form.form().bindFromRequest();
		Long prid = Long.parseLong(bindedForm.get("blog"));
		String content=bindedForm.get("cmnttxt");
		if(content==null)
			return badRequest();
		//response().setContentType("application/json");	
		Contributor author=Application.getContributor(session());
		Blog blg=Blog.find.byId(prid);
		if(blg!=null)
		{
			BlogComment cmt=BlogComment.AddComment(blg, author, content.replace("\n", " "));
			if(cmt!=null)
			{
				//models.Notifications.SOCommentsPr.ReportEvent(author, post, cmt);
				//return ok("{\"answer\": "+ views.html.Templates.json.commentbrick.render(cmt.author,cmt.content,cmt.postedAt,cmt.id, session()).toString() +" }");
//				return ok(views.html.Templates.su.SingleBlogPage.render(blg,false,0));
				return redirect("/blog/page/"+blg.id.toString());
			}
		}
		return badRequest();
	}
	
	@SubjectPresent
	public static Result  RemoveBlogComment(Long id)
	{
		Contributor c=Application.getContributor(session());
		BlogComment cmnt=BlogComment.find.byId(id);
		if(cmnt==null)
			return badRequest();
		response().setContentType("application/json");
		if(!GHelp.VerifyACL(DInitial.CURRENT_ACL.ADMIN_MODERATOR_SELF,cmnt.author.user)){ return unauthorized(); }
		if(BlogComment.RemoveComment(cmnt,c))
			return ok("{\"answer\":true}");;
		return internalServerError();
	}
	
	//BLOG FUNCTIONS
	
	public static class BLOG_FORM{
		@Required
		@MaxLength(255)
		public String blogtittle;
		
		@Required
		public String blogtext;
		
		@Required
		public String tags;		
	}
	public static class BLOG_EDIT_FORM extends BLOG_FORM{
		@Required
		public Long blogid;		
		public Blog getblog()
		{
			return Blog.find.byId(blogid);
		}		
		public String validate() {
			if(getblog()==null)
				return "UNKNOWN BLOG EDITED";
			return null;
		}		
	}
	
	public static final Form<BLOG_FORM> NEW_BLOG_FORM = form(BLOG_FORM.class);
	public static final Form<BLOG_EDIT_FORM> EDIT_BLOG_FORM = form(BLOG_EDIT_FORM.class);
	
	@SubjectPresent
	public static Result  AddBlog(){
		/*final Contributor localContributor = Application.getContributor(session());

		final Form<BLOG_FORM> filledForm = play.data.Form.form(BLOG_FORM.class).bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(views.html.Admin.Blog.AddEdit.render(filledForm,false));			
		} 
		
		BLOG_FORM bf=filledForm.bindFromRequest().get();
		Blog blognew= Blog.AddBlog(bf.blogtittle, localContributor, bf.blogtext);
		blognew.ApplyLabel(bf.tags);
		return redirect(routes.Application.GetBlogPage(blognew.id));*/
		
		final Contributor localContributor = Application.getContributor(session());
		MultipartFormData body = request().body().asMultipartFormData();
		
		String blgTitle = body.asFormUrlEncoded().get("blogtittle")[0];
		String blgText = body.asFormUrlEncoded().get("blogtext")[0];
		String tags =  body.asFormUrlEncoded().get("tags")[0];
		
		Blog blognew= Blog.AddBlog(blgTitle, localContributor, blgText);
		//Blog blognew= Blog.AddBlog(blgTitle, Contributor.find.byId(3L), blgText);
		blognew.ApplyLabel(tags);
		
		if(body.getFiles()!=null){
			for(MultipartFormData.FilePart part : body.getFiles()){
				if(part!=null){
					S3File s3File = new S3File();
					s3File.filename = part.getFilename();					
					s3File.file = part.getFile();
					s3File.filequalifier = "BlogImage";
					s3File.save();
					BlogImage img = new BlogImage();
					img.blogid = blognew.id;
					img.file = s3File;
					
					String cnt = part.getContentType(); 
					img.fileType = cnt;
					if(cnt.contains("video")){
						img.isVideo = true;
					}
					
					img.save();
				}
		        
		    }
		}
		return ok(blognew.id.toString());//return redirect(routes.Application.GetBlogPage(blognew.id));
		
	}
	
	@SubjectPresent
	public static Result  EditBlog(){
		/*final Contributor localContributor = Application.getContributor(session());		
		final Form<BLOG_EDIT_FORM> filledForm = play.data.Form.form(BLOG_EDIT_FORM.class).bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(views.html.Admin.Blog.AddEdit.render(filledForm,true));			
		} 
		
		BLOG_EDIT_FORM bef=filledForm.bindFromRequest().get();
		Blog bf=bef.getblog();
		
		if(bf==null ||!GHelp.VerifyACL(DInitial.CURRENT_ACL.ADMIN_SELF, bf.author.user))
			return notFound();
		
		if(Blog.EditBlog(bf, bef.blogtittle, localContributor, bef.blogtext))
			bf.ApplyLabel(bef.tags);
		else
			return internalServerError();
		return redirect(routes.Application.GetBlogPage(bf.id));*/
		final Contributor localContributor = Application.getContributor(session());
		MultipartFormData body = request().body().asMultipartFormData();
		
		String blgTitle = body.asFormUrlEncoded().get("blogtittle")[0];
		String blgText = body.asFormUrlEncoded().get("blogtext")[0];
		String tags =  body.asFormUrlEncoded().get("tags")[0];
		Blog bf= Blog.find.byId(Long.parseLong(body.asFormUrlEncoded().get("blogid")[0]));
		
		Blog.EditBlog(bf,blgTitle, localContributor, blgText);
		//Blog.EditBlog(bf,blgTitle, Contributor.find.byId(3L), blgText);
		bf.ApplyLabel(tags);

		
		if(body.getFiles()!=null){
			for(MultipartFormData.FilePart part : body.getFiles()){
				if(part!=null){
					S3File s3File = new S3File();
					s3File.filename = part.getFilename();					
					s3File.file = part.getFile();
					s3File.filequalifier = "BlogImage";
					s3File.save();
					BlogImage img = new BlogImage();
					img.blogid = bf.id;
					img.file = s3File;
					
					String cnt = part.getContentType();
					img.fileType = cnt;
					if(cnt.contains("video")){
						img.isVideo = true;
					}
					
					img.save();
				}
		        
		    }
		}
		return ok(bf.id.toString());//redirect(routes.Application.GetBlogPage(bf.id));
	}
	
	@SubjectPresent
	public static Result  RemoveBlog(Long id){
		Blog bg= Blog.find.byId(id);
		if(bg==null ||!GHelp.VerifyACL(DInitial.CURRENT_ACL.ADMIN_SELF, bg.author.user))
			return unauthorized();
		
		String blogname=bg.title;
		if(Blog.RemoveBlog(bg))
		{
			if(blogname.length()>20)
				blogname = blogname.substring(0,20);
			flash(Application.FLASH_MESSAGE_KEY,"Blog "+blogname+"... Removed.");
			return redirect(routes.Application.GetBlogList(0, DInitial.GENERIC_BLOG_PAGE_SIZE));
		}
		flash(Application.FLASH_ERROR_KEY,"Error removing blog "+blogname+".");
		return redirect(routes.Application.GetBlogPage(bg.id));			
	}
	
	public static Result getImagesForBlog(String blogids){
		Long blgid = Long.parseLong(blogids);
		Blog blg = Blog.find.byId(blgid);
		List<BlogImage> imgs = BlogImage.getImagesForBlog(blg);
		
		 ObjectNode result = Json.newObject();
		ArrayNode ms = result.putArray("images");
		 
		if(imgs!=null){
			for(BlogImage img:imgs){
				ObjectNode ob = Json.newObject();
				ob.put("url", img.getURL());
				ob.put("isVideo", img.isVideo);
				ms.add(ob);
			}
		}
		return ok(result);
	}
	/*
	@SubjectPresent
	public static Result likeBlog(Long blogid){
		final Contributor localContributor = Application.getContributor(session());
		BlogLikes.rateBlog(Blog.find.byId(blogid), localContributor, 1);
		return ok();
	}
	
	@SubjectPresent
	public static Result dislikeBlog(Long blogid){
		final Contributor localContributor = Application.getContributor(session());
		BlogLikes.rateBlog(Blog.find.byId(blogid), localContributor, -1);
		return ok();
	}*/
	public static Result updateLikeBlog(Long blogid){
		final Contributor localContributor = Application.getContributor(session());
		//BlogLikes.rateBlog(Blog.find.byId(blogid), localContributor, 1);
		if(localContributor!=null)
			BlogLikes.updateLike(Blog.find.byId(blogid), localContributor);
		return ok();
	}
	
	
	//BLOG ENDS
	
	
	//...................STORE FUNCTIONS......................................
	@SubjectPresent
	public static Result ClaimStore(Long id)
	{
		return TODO;
	}
	
	
	//..................STORE ENDS............................................
	
	//..................CONTENT FUNCTIONS.....................................
	
	public static class PAGE_CONTENT_FORM{
		
		public Long id;
		
		@Required
		@MaxLength(255)
	    public String pagetitle;
		
		@Required
	    public String pagecontent;			    
	    
		public String validate() {
			if(pagetitle==null)
				return "TITLE MISSING!!";
			if(pagecontent==null)
				return "CANNOT LEAVE EMPTY";			
			return null;
		}		
	}
	public static final Form<PAGE_CONTENT_FORM> FORM_PAGE_CONTENT_FORM = form(PAGE_CONTENT_FORM.class);
	
	@Restrict(@Group(Application.ADMIN_ROLE))
	public static Result  AddContentPage(){
		final Contributor localContributor = Application.getContributor(session());		
		final Form<PAGE_CONTENT_FORM> filledForm = play.data.Form.form(PAGE_CONTENT_FORM.class).bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(views.html.Admin.Content.AddEdit.render(filledForm));			
		} 
		
		PAGE_CONTENT_FORM pcf=filledForm.bindFromRequest().get();
		
		Infopage pagenew= Infopage.Createpage(pcf.pagetitle, pcf.pagecontent, localContributor); 
		return redirect(routes.Application.GetContentPage(pagenew.title));		
	}
	
	@Restrict(@Group(Application.ADMIN_ROLE))
	public static Result  SaveContentPage(){		
		final Form<PAGE_CONTENT_FORM> filledForm = play.data.Form.form(PAGE_CONTENT_FORM.class).bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(filledForm.errorsAsJson());			
		}		
		PAGE_CONTENT_FORM pcf=filledForm.bindFromRequest().get();
		
		if( !Infopage.Updatepage(pcf.pagetitle, pcf.pagecontent))
		{
			response().setContentType("application/json");
			return internalServerError("{\"InternalError\":[\"Failed to Update Page.\"]}");
		}
		return ok("{\"answer\":true}");		
	}
	
	@Restrict(@Group(Application.ADMIN_ROLE))
	public static Result  DeleteContentPage(String title){		
		Infopage pagedelete= Infopage.findbytitle(title);
		pagedelete.delete();
		return redirect(routes.Application.index());		
	}
	
	//....................CONTENT ENDS.......................................
	
	//...................REPORT SUGGESTION CONTACT ETC.......................
	
	public static class POSTSUGGESTION_FORM{
		
	    public String name;
	    
	    @Email
	    public String email;
	     
	    @Required
	    @MinLength(20)
	    public String content;
	    
	    public User getAuthor()
	    {
	    	return Application.getLocalUser(session());
	    }
	    
	    public boolean invalidJointCredents()
	    {
	    	if(email.length()<3 || name.length()<3)
	    		return true;
	    	return false;
	    }
	    
	    public String validate() {
			if(getAuthor()==null)
				if(invalidJointCredents())
					{
						if(name.length()<3)
							return "Name Invalid!!";
						else
							return "Email Invalid";					
					}
			return null;
		}
	}
	
	
	public static Result PostFeedback(){
		final Contributor localContributor = Application.getContributor(session());		
		final Form<POSTSUGGESTION_FORM> filledForm = play.data.Form.form(POSTSUGGESTION_FORM.class).bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(filledForm.errorsAsJson());			
		} 
		
		POSTSUGGESTION_FORM psf=filledForm.bindFromRequest().get();
		if(psf.getAuthor()!=null)
			Feedback.CreateFB(psf.getAuthor().contrib, psf.content);
		else
			Feedback.CreateFBAnonymous(psf.email, psf.name, psf.content);
		response().setContentType("application/json");
		return ok("{\"answer\":\"Thanks for your Valuable input\"}"); 
	}
	
	
	
	public static class REPORT_FORM{
		@Required
	    public Long contentid;
	    
	    @Required
	    public int ratype;
	    
	    @URL
	    public String frompage;
	    
	    @Required
	    @MinLength(20)
	    @MaxLength(255)
	    public String complaintext;
		
		public String VerifyID()
		{
			switch(ratype)
			{
			case DInitial.REPORTABUSE.ON_PRODUCT:
				if(Product.find.byId(contentid)==null)
					return "INVALID PRODUCT";
				break;
			case DInitial.REPORTABUSE.ON_BLOG:
				if(Blog.find.byId(contentid)==null)
					return "INVALID BLOG";
				break;
			case DInitial.REPORTABUSE.ON_STORE:
				if(Store.find.byId(contentid)==null)
					return "INVALID STORE";
				break;
			case DInitial.REPORTABUSE.ON_USER:
				if(Contributor.find.byId(contentid)==null)
					return "INVALID USER";
				break;
			default:
				return "APPLICATION UNKNOWN";				
			}
			return null;
		}		
		public String validate() {
			return VerifyID();
		}		
	}
	

	@SubjectPresent
	public static Result  GetComplainForm(Long id,int ratype, String purl){	
		REPORT_FORM rff=new REPORT_FORM();
		rff.contentid=id;rff.ratype=ratype;rff.frompage=purl;
		Form<REPORT_FORM> NEW_REPORT_FORM =Form.form(REPORT_FORM.class).fill(rff);
		return ok(views.html.Templates.su.reportabuse.render(NEW_REPORT_FORM));
	}
	
	@SubjectPresent
	public static Result LodgeAComplain(){
		final Contributor localContributor = Application.getContributor(session());		
		final Form<REPORT_FORM> filledForm = play.data.Form.form(REPORT_FORM.class).bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(filledForm.errorsAsJson());			
		}		
		REPORT_FORM rf=filledForm.bindFromRequest().get();
		Reportabuse.CreateAbuse(rf.contentid, rf.ratype, rf.frompage, localContributor,rf.complaintext);
		return ok(Html.apply("<script>parent.$().msgbox('<h1 style=\"color:green\">Thanks for informing</h1>',2000);</script>")); 
	}
	
	
	
	
	//...................REPORT SUGGESTION CONTACT OVER.......................
	
	
	
	
	
	public static Map<Integer,String> ReviewStatus(Product p)
	{		
		int[] lwtc={0,0,0,0};
		Map<Integer,String> StatusString = new HashMap<Integer, String>();
		User usr=Application.getLocalUser(session());
		if(usr!=null)
		{
			Contributor ctr=usr.contrib;
			
			if(Contributor.find.where().eq("id", ctr.id).eq("Liked.id", p.id).findRowCount()>0)
				lwtc[0]=1;
			if(Contributor.find.where().eq("id", ctr.id).eq("Wanted.id", p.id).findRowCount()>0)
				lwtc[1]=1;
			if(Contributor.find.where().eq("id", ctr.id).eq("Owned.id", p.id).findRowCount()>0)
				lwtc[2]=1;
		}
		
		StatusString.put(new Integer(0), DInitial.LWT[0][lwtc[0]]);
		StatusString.put(new Integer(1), DInitial.LWT[1][lwtc[1]]);
		StatusString.put(new Integer(2), DInitial.LWT[2][lwtc[2]]);
		return StatusString;
		
	}
	
	
	//.............TEST.......................
	@SubjectPresent
	public static Result MyStats()
	{
		Contributor c=Application.getContributor(session());
		StringBuilder statb=new StringBuilder(
				("<script> $(document).ready(" +
				"function(){" +
				"$( `a` ).click( " +
				"function(e) {" +
				"var link = $(this).attr('href');" +
				"e.preventDefault();" +
				"$.get(link,function(data,status){" +
				" console.log('Successful! ' + data);" +
				" $(`#feeds`).load(`mystat`);" +
				" });" +
				"});\n});</script>").replace('`', '"'));
		statb.append("<div id=`feeds`><b>UserName: ".replace('`', '"')+c.user.email+"</b><br/>");
		statb.append("<ul>User likes:- "+c.Liked.size()+" products");
		//Button text after love/like previously unlike/hate
		for(Product px: c.Liked)
			statb.append(String.format("<li><a href=\"unlike?prid=%s\">%s</a></li>",px.id,px.productname));		
		statb.append("</ul>");
		
		List<Product> pxc=Product.find.all();
		pxc.removeAll(c.Liked);
		statb.append("<ul>User Hasn't liked:- "+pxc.size()+" products");		
		for(Product px: pxc)
			statb.append(String.format("<li><a href=\"love?prid=%s\">%s</a></li>",px.id,px.productname));				
		statb.append("</ul></div>");
				return ok(views.html.main.render("EntryTest","",Html.apply(statb.toString())));
	}
	

	public static Result rateProduct(Long product_id) {
		
		User user = Application.getLocalUser(session());
		
		DynamicForm form = form().bindFromRequest();
		String value = form.get("value");
		
		Product product = Product.find.byId(product_id);
		
		UserRate.save(user, product, Float.parseFloat(value));
		return ok(value);
	}
	
	public static Result removeRatings(Long product_id) {
		User user = Application.getLocalUser(session());
		UserRate ur = UserRate.find.where().eq("user_id", user.id).eq("product_id", product_id).findUnique();
		ur.delete();
		return ok();
	}
	
	public static Result getProductRatingDetails(Long product_id) {
		String sqlQuery = "select rate,count(rate) as count from userrate where product_id= :product_id group by rate";
		SqlQuery query = Ebean.createSqlQuery(sqlQuery);
		query.setParameter("product_id", product_id);
		List<SqlRow> rows = query.findList();
		
		List<ProductRateDetail> details = new ArrayList<>();
		Integer maxValue = 0;
		
		for( Integer i=1; i <= 5; i++) {
			boolean isFilled = false;
			
			for( SqlRow row : rows ) {
				if(i == row.getInteger("rate")) {
					ProductRateDetail pd = new ProductRateDetail(row.getInteger("rate"), row.getInteger("count"));
					details.add(pd);
					
					if( row.getInteger("count") > maxValue) {
						maxValue = row.getInteger("count");
					}
					isFilled = true;
					break;
				}
			}
			if(isFilled == false) {
				ProductRateDetail pd = new ProductRateDetail(i, 0);
				details.add(pd);
			}
		}
		
		PRDetails prDetails = new PRDetails(details, maxValue);
		return ok(Json.toJson(prDetails));
	}
}
