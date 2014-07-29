package controllers;

import static play.data.Form.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.net.InetAddress;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.io.SerializedString;

import models.*;
import models.Admin.Contactus;
import models.Admin.Infopage;
import models.Notifications.SOFollows;
import models.Notifications.UserSubscriptions;
import play.Configuration;
import play.Logger;
import play.Play;
import play.Routes;
import play.api.mvc.MultipartFormData;
import play.api.mvc.SimpleResult;
import play.api.templates.Html;
import play.data.DynamicForm;
import play.data.Form;
import play.data.Form.*;
import play.mvc.*;
import play.mvc.BodyParser.Json;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Http.*;
import plugins.S3Plugin;
import providers.MyLoginUsernamePasswordAuthUser;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthUser;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;

import views.html.*;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import be.objectify.deadbolt.java.actions.SubjectPresent;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.feth.play.module.mail.Mailer;
import com.feth.play.module.mail.Mailer.Mail;
import com.feth.play.module.mail.Mailer.Mail.Body;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.AuthProvider;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.EmailIdentity;
import com.google.common.base.Strings;

import controllers.SPages.CONTACTUS;
import controllers.Useract.BLOG_EDIT_FORM;
import controllers.Useract.PAGE_CONTENT_FORM;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;


public class Application extends Controller {

	public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	public static final String USER_ROLE = "user";
	public static final String ADMIN_ROLE = "admin";
	public static final String MODERATOR_ROLE = "moderator";
	
	public static final String WebAddress=play.Play.application().configuration().getString("site.address");
	public static final String Captchaprivate=play.Play.application().configuration().getString("site.captcha.private");
	public static final String Captchapublic=play.Play.application().configuration().getString("site.captcha.public");
	
	//****************NAVIGATION FUNCTIONS************************************//
	
	public static Result index() {
		if(GHelp.VerifyACL(DInitial.CURRENT_ACL.ALL_USERS, null))
			return Useract.MyWatchList();
		else
			return Application.shop();
	}
	
	public static Result home() {return ok(shop.render());}
	
	public static Result discover() {
		DynamicForm bindedForm = play.data.Form.form().bindFromRequest();		
		String q =  bindedForm.get("q");
		String c =  bindedForm.get("c");
		
		if(!Strings.isNullOrEmpty(q)) {
			ctx().args.put("q", q);
		}
		
		Long cid = Category.root().id;
		if(!Strings.isNullOrEmpty(c)) {
			
			try {
				c = java.net.URLDecoder.decode(c, "UTF-8");
			} catch(Exception e) {}
			
			Category cat= Category.find.where().eq("cname", c).findUnique();
			
			if (cat != null) {
				cid=cat.id;
			}
		}
		
		if(!Strings.isNullOrEmpty(q) || !Strings.isNullOrEmpty(c)) {
			return ok(cshop.render(cid));
		}
		
		if(GHelp.VerifyACL(DInitial.CURRENT_ACL.ALL_USERS, null))
			return Useract.MyWatchList();
		else
			return ok(cshop.render(cid));
	}
	
	
	public static Result grabber() {		
			response().setContentType("text/javascript");
			return ok(views.txt.Tools.bookmarklet.render(routes.Assets.at("js/custom.js").absoluteURL(request()),routes.Assets.at("css/boozologygrabber.css").absoluteURL(request()),request()));		
	}
	
	public static Result shop() {
		return ok(cshop.render(Category.root().id));
	}
	
	public static Result cshop(String category) {
		String categorydecoded="Not Found";
		try{
			categorydecoded=java.net.URLDecoder.decode(category, "UTF-8");
		}
		catch(Exception e){}
		//Category c= Category.find.where().eq("cname", categorydecoded).or(Expr.isNull("pcategory"), Expr.eq("pcategory", Category.root())).findUnique();
		Category c= Category.find.where().eq("cname", categorydecoded).findUnique();
		Long cid=-1L;
		if(c!=null)
		{
			cid=c.id;
			return ok(cshop.render(cid));
		}else{
			return ok(shop.render());
		}
		
	}
	
	@SubjectPresent
	public static Result ShowGrabbed(String imageurl,Double price,String currency,String title,String pageurl)
	{
		String recievedparams=String.format("imageurl %s,price %f,Currency %s,tittle %s,pageurl %s", imageurl,price,currency,title,pageurl);
		return ok(views.html.Tools.confirm.render("Boozology Grab it!",title.equals("undefined")?null:title,pageurl.equals("undefined")?null:pageurl,imageurl,currency,price,Application.getLocalUser(session())));
	}
	
	public static Result PageRedirection(String urlpage) 
	{
		return redirect(urlpage);
	}
	
	public static Result BookMarkTool()
	{
		return ok(views.html.Tools.bookmark.render());
	}
	
	//>>>>>>>>>>>>>>>>PUBLIC QUERY<<<<<<<<<<<<<<<<<<<<<<<<<<<
	//For Searching Products on shop page
	public static Result ProductQuery()
	{
		DynamicForm bindedForm = play.data.Form.form().bindFromRequest();		
		
	    int page = Integer.parseInt( bindedForm.get("page"));
	    int gendercont = Integer.parseInt( bindedForm.get("gendercont"));//To be passed
	    int sortseq = Integer.parseInt( bindedForm.get("sortseq"));
	    int pricerange = Integer.parseInt( bindedForm.get("pricerange"));//To be passed
	    int pagesize = Integer.parseInt( bindedForm.get("pagesize"));
	    int catgid = Integer.parseInt(bindedForm.get("catgid"));
	    String searchtext = bindedForm.get("searchtext");//To be passed	
	    if(searchtext!=null)
	    	searchtext=GHelp.FilteredSql(searchtext);
	    
		response().setContentType("application/json");
		switch(sortseq)
		{
		case 1:
			return ok(productjson.render(Eventlog.RelevanceFeed(page, pagesize,GHelp.filterSql(searchtext, gendercont, pricerange, catgid,""))));
		case 2:
			return ok(productjson.render(Product.RecentAdds(page, pagesize,GHelp.filterSql(searchtext, gendercont, pricerange, catgid,""))));
		case 3:
			return ok(productjson.render(Eventlog.Alltime(page, pagesize,GHelp.filterSql(searchtext, gendercont, pricerange, catgid,""))));
			
		}
		return ok(productjson.render(Product.RecentAdds(page, pagesize,GHelp.filterSql(searchtext, gendercont, pricerange, catgid,""))));
	}
		
		
	public static Result  ProductStatDetails (Long prid ,int lwt)
	{
		return TODO;
	}
	
	public static Result RunSearch(String searchkey,int index)
	{
		String searchtext=searchkey;
		if(searchtext!=null)
			searchtext=GHelp.FilteredSql(searchtext);
		return ok(views.html.search.render(searchtext,index,null));
	}
	
	public static Result KeySearch() 
	{
		
		DynamicForm bindedForm = play.data.Form.form().bindFromRequest();		
		
	    int page = Integer.parseInt( bindedForm.get("page"));
	    int searchtype = Integer.parseInt( bindedForm.get("searchtype"));
	    int pagesize = Integer.parseInt( bindedForm.get("pagesize"));
	    String searchtext = bindedForm.get("searchtext");//To be passed	
	    
	    if(searchtext!=null)
	    	searchtext=GHelp.FilteredSql(searchtext);
	    
		response().setContentType("application/json");
		
		switch(searchtype)
		{
			
		case DInitial.SEARCH_USER:
			return ok(views.html.Templates.json.userpane.render(Contributor.MatchedUsers(page, pagesize, searchtext), session()));
		case DInitial.SEARCH_COLLECTION:
			return ok(views.html.Templates.json.collectionpane.render(UserCollection.MatchedCollections(page, pagesize, searchtext), session()));
		case DInitial.SEARCH_STORE:
			return ok(views.html.Templates.json.storepane.render(Store.MatchedStores(page, pagesize, searchtext), session()));
		case DInitial.SEARCH_BLOG:
			return ok(views.html.Templates.json.blogpane.render(Blog.Searchbytitle(page, pagesize, searchtext), session()));
			
		}
		return badRequest();
		
	}
	
	public static Result PreloadProducts(int preloadsize)
	{
		response().setContentType("application/json");
		return ok(views.html.Templates.json.productlpane.render(Product.EarlyLoaded(preloadsize)));
	}
	
	//*****************SOLITARY CLASSES***************************************//
	public static Result  ProductPage(Long id ,Boolean isajax)
	{
		Product prid=Product.find.byId(id);
		if(prid!=null)
			return ok(product.render(prid,isajax));
		else
			return notFound();
	}
	
	public static Result  CollectionPage(Long id ,Boolean isajax)
	{
		UserCollection uc=UserCollection.find.byId(id);
		if(uc!=null)
			return ok(collectionpage.render(uc));
		else
			return notFound();
	}

	public static Result  StorePage(Long id ,Boolean isajax)
	{
		Store st=Store.find.byId(id);
		if(st!=null)
			return ok(storepage.render(st,session()));
		else
			return notFound();
	}

	public static Result  ContributorPage(Long id,Boolean isajax,Boolean isuser)
	{
		Contributor c=null;
		if(isuser)
		{	
			User u=User.find.byId(id);
			if(u!=null)
				c=u.contrib;
		}else{c=Contributor.find.byId(id);}
		if(c!=null)
			return ok(userprofile.render(c,DInitial.USER_RELATION_QUERY.RECENT,session()));
		return notFound();
	}
	public static Result  ContributorSelecttabPage(Long id,int selectedtab,Boolean isajax,Boolean isuser)
	{
		Contributor c=null;
		if(isuser)
		{	
			User u=User.find.byId(id);
			if(u!=null)
				c=u.contrib;
		}else{c=Contributor.find.byId(id);}
		if(c!=null)
			return ok(userprofile.render(c,selectedtab,session()));
		return notFound();
	}
	
	public static Result  SearchPage(Long id ,Boolean isajax)
	{
		FSearch fs=FSearch.find.byId(id);
		if(fs!=null)
			return  ok(views.html.search.render(fs.skey,DInitial.SEARCH_PRODUCT,fs));
		else
			return notFound();
	}
	
	
	//*******************SEARCH FUNCTIONS**************************************//
	
	public static Result QuerySuggestion(String query,Long catgid)
	{
		response().setContentType("application/json");
		return ok("{\"answer\":"+Ebean.createSqlQuery("select array_to_json(array(select PRODUCTNAME from product where PRODUCTNAME ilike :q "+"AND CATEGORY_ID IN ("+Category.ChildList(catgid)+")"+" LIMIT 8)) AS JO").setParameter("q", "%"+query+"%").findUnique().getString("JO")+"}");
	}
	
	public static Result  ProductSearch(Long id ,int subcat,String searchtext,int gendercont,int page,int pagesize)
	{
		Product prid=Product.find.byId(id);
		response().setContentType("application/json");
		if(prid!=null)
		{
			switch(subcat)
			{
				case DInitial.PRODUCT_RELATION_QUERY.LOVERS:
					return ok(views.html.Templates.json.userpane.render(Contributor.FilteredLWT(subcat, page, pagesize, searchtext, gendercont, prid),session()));
		        case DInitial.PRODUCT_RELATION_QUERY.WANTERS:
		        	return ok(views.html.Templates.json.userpane.render(Contributor.FilteredLWT(subcat, page, pagesize, searchtext, gendercont, prid),session()));
		        case DInitial.PRODUCT_RELATION_QUERY.CONSUMERS:
		        	return ok(views.html.Templates.json.userpane.render(Contributor.FilteredLWT(subcat, page, pagesize, searchtext, gendercont, prid),session()));
		        case DInitial.PRODUCT_RELATION_QUERY.COLLECTIONS:
		        	return ok(views.html.Templates.json.collectionpane.render(UserCollection.ProductCollectionPage(subcat, page, pagesize, searchtext, prid),session()));
		        case DInitial.PRODUCT_RELATION_QUERY.COMMENTATORS:
		        	String jsrep=Comment.ProductCommentPageJSON(prid);
		    		if(jsrep==null)
		    			jsrep=" ";
		        	return ok("{\"cmntgrid\":["+jsrep+"]}");
		        	//return ok(views.html.Templates.json.commentpane.render(Comment.ProductCommentPage(prid), session()));
		        default:
	        		break;
			}
		}		
		return notFound();
	}
	
	public static Result  CollectionSearch(Long id ,int subcat,String searchtext,int pricerange,int gendercont,int page,int pagesize)
	{
		response().setContentType("application/json");
		UserCollection uc=UserCollection.find.byId(id);
		if(uc!=null)
		{
			switch(subcat)
			{
			case DInitial.USER_RELATION_QUERY.PRODUCTS:
				return ok(productjson.render(UserCollection.FilteredProduct(page, pagesize, GHelp.FilteredSql(searchtext), gendercont, pricerange, uc.id)));
			case DInitial.USER_RELATION_QUERY.FOLLOWERS:
				return ok(views.html.Templates.json.userpane.render(UserCollection.FollowerFollowing(subcat, page, pagesize, searchtext, uc),session()));
			default:
				return notFound();
			}
		}
		else
			return notFound();
	}

	public static Result  StoreSearch(Long id ,int subcat,String searchtext,int pricerange,int gendercont,int page,int pagesize)
	{
		response().setContentType("application/json");
		Store st=Store.find.byId(id);
		if(st!=null)
		{
			switch(subcat)
			{
			case DInitial.USER_RELATION_QUERY.PRODUCTS:
				return ok(productjson.render(Store.FilteredProduct(page, pagesize,GHelp.FilteredSql(searchtext), gendercont, pricerange, st.id)));
			case DInitial.USER_RELATION_QUERY.COLLECTIONS:
				return ok(views.html.Templates.json.collectionpane.render(Store.StoreCollections(page, pagesize, searchtext, st),session()));
			case DInitial.USER_RELATION_QUERY.CUSTOMERS:
				return ok(views.html.Templates.json.userpane.render(Store.StoreCustomers(page, pagesize, searchtext, st),session()));
			case DInitial.USER_RELATION_QUERY.FOLLOWERS:
				return ok(views.html.Templates.json.userpane.render(Store.FollowerFollowing(subcat, page, pagesize, searchtext, st),session()));
			default:
				return notFound();
				
			}
			
		}
		else
			return notFound();
	
	}

	public static Result  ContributorSearch(Long id ,int subcat,String searchtext,int pricerange,int gendercont,int page,int pagesize)
	{
		response().setContentType("application/json");
		Contributor c=Contributor.find.byId(id);
		if(c!=null)
		{
			switch(subcat)
			{
			case DInitial.USER_RELATION_QUERY.LOVES:
			case DInitial.USER_RELATION_QUERY.WANTS:
			case DInitial.USER_RELATION_QUERY.TRIED:
				return ok(productjson.render(Contributor.UsersLWT(subcat, page, pagesize, searchtext, gendercont, pricerange, c)));
			case DInitial.USER_RELATION_QUERY.COLLECTIONS:
				return ok(views.html.Templates.json.collectionpane.render(Contributor.UsersPersonalCollections(page, pagesize, searchtext, c),session()));
			case DInitial.USER_RELATION_QUERY.COMMENTS:
			case DInitial.USER_RELATION_QUERY.FOLLOWING:
				return ok(views.html.Templates.json.userpane.render(Contributor.FollowerFollowing(subcat, page, pagesize, searchtext, c),session()));
			case DInitial.USER_RELATION_QUERY.FOLLOWERS:
				return ok(views.html.Templates.json.userpane.render(Contributor.FollowerFollowing(subcat, page, pagesize, searchtext, c),session()));
			case DInitial.USER_RELATION_QUERY.SEARCHES:
				return ok(views.html.Templates.json.searchpane.render(FSearch.FollowingSearches(page, pagesize, searchtext, c), session()));
			case DInitial.USER_RELATION_QUERY.STORES:
				return ok(views.html.Templates.json.storepane.render(Contributor.FollowingStores(page, pagesize, searchtext, c), session()));
			default:
				return notFound();
				
			}
			
		
		}else
			return notFound();
	}
	
	
	public static Result ProductGetRelated(Long id)
	{
		response().setContentType("application/json");
		Product p=Product.find.byId(id);
		if(p!=null)
			return ok(productjson.render(Product.RelatedProducts(0, 6, p)));
		else
			return notFound();
	}
	
	public static Result  SearchTotalProductCount(String searchtext ,int gendercont ,int pricerange)
	{
		ObjectNode collectionnode =play.libs.Json.newObject();
		collectionnode.put("answer",Product.MatchedProductsCount(GHelp.filterSql(searchtext, gendercont, pricerange, 0, "")));
		return ok(collectionnode);
	}
	
	
	//********************CATEGORY************************************************
	public static Result RetrieveCategoryList(Long id)
	{
		response().setContentType("application/json");
		Category c=Category.find.byId(id);
		if(c!=null)
		{
			String jsrep=Category.ChildJSON(c.id);
			if(jsrep==null)
				jsrep=" ";
			return ok("{\"child\":["+jsrep+"]}");
		}
		else
			return notFound();
	}
	
	//*******************BLOGGING FUNCTIONS***********************************//
	public static Result GetBlogList(int page,int pagesize)
	{
		return ok( blogpage.render( Blog.RecentBlogPage(page, pagesize)));
	}
	
	public static Result GetBlogListByTag(String tag,int page,int pagesize)
	{
		return ok( blogpage.render( Blog.Searchbytag(page, pagesize, tag)));
	}
	
	public static Result GetBlogPage(Long id)
	{
		Blog b=Blog.find.byId(id);
		if(b!=null)
			return ok(views.html.Templates.su.SingleBlogPage.render(b,false,0));
		else
			return notFound();
	}
	
	//@Restrict(@Group(Application.ADMIN_ROLE))
	@SubjectPresent
	public static Result  GetAddBlog(){
		User blogwriter=getLocalUser(session());
		boolean flag = false;
		if(!blogwriter.emailValidated) {
			//return ok(views.html.account.unverified.render());
			return ok(views.html.Admin.Blog.AddEdit2.render(Useract.NEW_BLOG_FORM,false));
			
		} else {
			for(int i = 0; i < blogwriter.roles.size(); i++) {
				if(blogwriter.roles.get(i).roleName.equals("moderator")) {
					flag = true;
				}
			}
			if(flag == true) {
				return ok(views.html.Admin.Blog.AddEdit2.render(Useract.NEW_BLOG_FORM,false));
			} else {
				try {
					String IPAddress = play.mvc.Controller.request().remoteAddress();
					String userlocation = DInitial.geoipreader.country(InetAddress.getByName(IPAddress)).getCountry().getName();
					System.out.println(userlocation);
				} catch (Exception e){
					e.printStackTrace();
					//return ok(views.html.account.unverified.render());
					return ok(views.html.Admin.Blog.AddEdit2.render(Useract.NEW_BLOG_FORM,false));
				}	
				return ok(views.html.Admin.Blog.AddEdit2.render(Useract.NEW_BLOG_FORM,false));
			}
		}
		
	}
	
	//@Restrict(@Group(Application.ADMIN_ROLE))
	@SubjectPresent
	public static Result  GetEditBlog(Long id){
		Blog b=Blog.find.byId(id);
		if(b==null ||!GHelp.VerifyACL(DInitial.CURRENT_ACL.ADMIN_SELF, b.author.user))
			return notFound();
		
		Useract.BLOG_EDIT_FORM bef=new BLOG_EDIT_FORM();
		
		String sqlquery="SELECT    blog.id AS blogid,    blog.title AS blogtittle,    blog.content AS blogtext,    string_agg(blog_labels.tag, ',') AS tags FROM    blog,    blog_blog_labels,    blog_labels WHERE    blog.id = blog_blog_labels.blog_id AND   blog_blog_labels.blog_labels_id = blog_labels.id AND   blog.id = :bid GROUP BY blog.id";
		com.avaje.ebean.SqlRow sqlr= Ebean.createSqlQuery(sqlquery).setParameter("bid", b.id).findUnique();
		
		bef.blogid = sqlr.getLong("blogid");
		bef.blogtittle = sqlr.getString("blogtittle");
		bef.blogtext = sqlr.getString("blogtext");
		bef.tags = sqlr.getString("tags");
				
		return ok(views.html.Admin.Blog.AddEdit2.render(Useract.EDIT_BLOG_FORM.fill(bef),true));
	}
	
	public static Result  GetBlogComplainForm(Long id){
		return TODO;
	}
	
	
	//*****************CONTENT PAGE*******************************************//
	public static Result GetContentPage(String title)
	{
		Infopage ip=Infopage.findbytitle(title);
		if(ip!=null)
			return ok(contentpage.render(ip));
		else
		{
			if(GHelp.VerifyACL(DInitial.CURRENT_ACL.ADMINISTRATOR_ONLY, null))
			{
				PAGE_CONTENT_FORM pcf=new PAGE_CONTENT_FORM();
				pcf.pagetitle=title;
				pcf.pagecontent="<b>Write Anything for now. You can Inline Edit that Later</b>";
				final Form<PAGE_CONTENT_FORM> filledForm=play.data.Form.form(PAGE_CONTENT_FORM.class).fill(pcf);
				return ok(views.html.Admin.Content.AddEdit.render(filledForm));
			}
			return notFound();
		}
	}
	
	@Restrict(@Group(Application.ADMIN_ROLE))
	public static Result  GetAddContent(){
		return ok(views.html.Admin.Content.AddEdit.render(Useract.FORM_PAGE_CONTENT_FORM));
	}	
	
	//*****************COMMENT FETCH*******************************************//
	public static Result  GetBlogComments(Long id, Integer limit){
		Blog blog=Blog.find.byId(id);
		if(blog==null)
			return notFound();
		String jsrep=BlogComment.BlogCommentPageJSON(blog, limit);
		if(jsrep==null)
			jsrep=" ";
		return ok("{\"cmntgrid\":["+jsrep+"]}");		
	}
	
	public static Result  GetCollectionComments(Long id){
		UserCollection collection=UserCollection.find.byId(id);
		if(collection==null)
			return notFound();
		String jsrep=CollectionComment.CollectionCommentPageJSON(collection);
		if(jsrep==null)
			jsrep=" ";
		return ok("{\"cmntgrid\":["+jsrep+"]}");
		//return ok(views.html.Templates.json.commentpane.render(CollectionComment.CollectionCommentPage(collection), session()));
	}
	
	//*****************ADMIN PAGE**********************************************//
	@Restrict(@Group(Application.ADMIN_ROLE))
	public static Result  GetAdminPage(){
		return ok(views.html.Admin.AdminPage.render());
	}
	
	//*****************ACCOUNT FUNCTIONS**************************************//

	public static User getLocalUser(final Session session) {
		final AuthUser currentAuthUser = PlayAuthenticate.getUser(session);
		final User localUser = User.findByAuthUserIdentity(currentAuthUser);
		return localUser;
	}
	
	public static Contributor getContributor(final Session session)
	{
		User u=getLocalUser(session());
		if(u!=null)
			return u.contrib;
		return null;
	}

	@Restrict(@Group(Application.USER_ROLE))
	public static Result restricted() {
		final User localUser = getLocalUser(session());
		return ok(restricted.render(localUser));
	}

	@Restrict(@Group(Application.USER_ROLE))
	public static Result profile() {
		final User localUser = getLocalUser(session());
		return ok(profile.render(localUser));
	}
	
	@Restrict(@Group(Application.USER_ROLE))
	public static Result showprofileimage() {
		final User localUser = getLocalUser(session());
		return ok(views.html.Templates.UserProfileImageEdit.render(localUser));
	}
	
	@Restrict(@Group(Application.USER_ROLE))
	public static Result setprofileimage() {
		User localUser=getLocalUser(session());
		FilePart fp=request().body().asMultipartFormData().getFile("picture");
		String allotedname=localUser.getusersimagename(true);//;
		if(fp.getFile().length()>DInitial.IMAGE_UPLOAD_FILE_SIZE_LIMIT)
		{
			flash(Application.FLASH_ERROR_KEY, "File exceeds size limit.");
			return redirect(routes.Application.showprofileimage());
		}
		if(!DInitial.CONTENT_TYPES.isspecifiedcontenttype( fp.getContentType(),DInitial.CONTENT_TYPES.IMAGE_GPJ))
		{
			flash(FLASH_ERROR_KEY,"Unknown File Type!! Please use JPG,PNG");
			return redirect(routes.Application.showprofileimage()); 
		}
		localUser.savetocdn(allotedname, fp.getFile());
		fp.getFile().delete();
		localUser.setimage(allotedname);
		return redirect(routes.Application.profile());//redirect(routes.Application.showprofileimage());
	}
	
	@Restrict(@Group(Application.USER_ROLE))
	public static Result setmyprofile()
	{
		final User localUser = getLocalUser(session());
		//DynamicForm bindedForm = play.data.Form.form().bindFromRequest();
		
		final Form<GHelp.USER_SETTINGS> filledForm = play.data.Form.form(GHelp.USER_SETTINGS.class).bindFromRequest();
		

		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			return badRequest(filledForm.errorsAsJson());
			
		} 
		
		GHelp.USER_SETTINGS gu=filledForm.bindFromRequest().get();
		 
		int indicator =Integer.parseInt(gu.indicator) ;	
		response().setContentType("application/json");
		String responsemessage="Done!!";
		
		switch(indicator)
		{
		case DInitial.PROFILE_SETTINGS.FIRSTNAME:
			localUser.firstName=gu.firstname;						
			break;
		case DInitial.PROFILE_SETTINGS.LASTNAME:
			localUser.lastName=gu.lastname;
			break;
		case DInitial.PROFILE_SETTINGS.USERNAME:
			localUser.name=gu.username;		
					break;
		case DInitial.PROFILE_SETTINGS.EMAIL:
			localUser.email=gu.email;
			break;
		case DInitial.PROFILE_SETTINGS.PASSWORD:
			com.feth.play.module.pa.controllers.Authenticate.noCache(response());
			localUser.changePassword(new MyUsernamePasswordAuthUser(gu.password),true);
			break;
		case DInitial.PROFILE_SETTINGS.BIOGRAPHY:
			localUser.biography=gu.biography;
			break;
		case DInitial.PROFILE_SETTINGS.GENDER:
			localUser.gender=gu.gender;
			break;
		case DInitial.PROFILE_SETTINGS.EMAIL_NOTIFICATION:
				UserSubscriptions us=UserSubscriptions.getsubscriptions(localUser.contrib);
				us.Setsubscription(gu.sofollows, gu.socommentpr, gu.socommentco, gu.sorecommends, gu.sosuggestsco, gu.advertisements);
			break;
		case DInitial.PROFILE_SETTINGS.LINKEDACCOUNTS:
			
			break;
		case DInitial.PROFILE_SETTINGS.LOCATION:
			localUser.location=gu.location;
			break;
		case DInitial.PROFILE_SETTINGS.SOCIAL_IMAGE:
			String allotedname=localUser.getusersimagename(true);
			File tempstorage =GHelp.downloadandsaveimage(gu.socialimage, allotedname) ;
			if(tempstorage==null)
				return badRequest("{\"Setting image\":[\"Unable to find the image OR Image is larger than 2MB.\"]}");
			localUser.savetocdn(allotedname, tempstorage);
			tempstorage.delete();
				
			localUser.profileimage=allotedname;
			
			responsemessage+="Your image is set. Refresh page to see.";
			break;
		case DInitial.PROFILE_SETTINGS.WEBSITE:
			localUser.website=gu.website;
			break;
		
		}
		localUser.save();
		
		return ok("{\"answer\":\""+responsemessage+"\"}");
		
	}
	

	public static Result login() {
		if(getLocalUser(session()) != null)
			return redirect(routes.Application.index());
		return ok(login.render(MyUsernamePasswordAuthProvider.LOGIN_FORM));
	}

	public static Result doLogin() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		DynamicForm formcontents=play.data.Form.form().bindFromRequest();
		String Isajax= formcontents.get("isajax");
		String Redirect_to=formcontents.get("redirecturi");
		String emailid = formcontents.get("email");
		if(Redirect_to!=null)
			session().put(DInitial.REDIRECT_BACK_URL, Redirect_to);
		Boolean isajax = Boolean.parseBoolean(Isajax);
		if(isajax)
		{
			response().setContentType("application/json");
			session().put("isajax", "yes");
		}
		
		final Form<MyLogin> filledForm = MyUsernamePasswordAuthProvider.LOGIN_FORM
				.bindFromRequest();		
		User attemptinguser =emailid==null?null:User.findByEmail(emailid);
		if(attemptinguser!=null)
		{
			if(attemptinguser.lastattempt!=null && attemptinguser.failedattempt>0)
			{
				long lastattempt = attemptinguser.lastattempt.getTime();
				Date date = new Date();
				long minlasted= (date.getTime()-lastattempt)/(60*1000);
				if(attemptinguser.failedattempt>3 && minlasted<5)
				{
					session().clear();					
					return badRequest("{\"Error\":\""+"Maximum Attempts Done. Please try login after "+(5 -((date.getTime()-lastattempt)/(60*1000)))+" minutes\"}" );
				}
			}
		}
		if (filledForm.hasErrors()) {			
			// User did not fill everything properly
			if(isajax)
				return badRequest(filledForm.errorsAsJson());
			flash(FLASH_ERROR_KEY, filledForm.errorsAsJson().toString());
			return badRequest(login.render(filledForm));
		} else {
			
			// Everything was filled
			flash().clear();
			Result r=UsernamePasswordAuthProvider.handleLogin(ctx());
			//TODO:Remove hack			
			if(attemptinguser!=null)
			{
				if(flash().containsKey(FLASH_ERROR_KEY))//if equal to resolver.login
				{
					attemptinguser.failedattempt++;
					attemptinguser.lastattempt = new Date();
					attemptinguser.save();
				}
				if(flash().containsKey(FLASH_MESSAGE_KEY))//if equal to resolver.postauth
				{						
					attemptinguser.failedattempt = 0;
					attemptinguser.save();
				}
				
			}
			if(flash().containsKey(FLASH_ERROR_KEY))
				if(isajax)
					return badRequest("{\"Error\":\""+flash().get(Application.FLASH_ERROR_KEY)+"\"}" );
			
			return r;
		}
	}

	public static Result signup() {
		if(getLocalUser(session()) != null)
			return redirect(routes.Application.index());
		return ok(signup.render(MyUsernamePasswordAuthProvider.SIGNUP_FORM));
	}

	public static Result jsRoutes() {
		return ok(
				Routes.javascriptRouter("jsRoutes",
						controllers.routes.javascript.Signup.forgotPassword()						
						))
				.as("text/javascript");
	}

	public static Result doSignup() {
		
		DynamicForm bindedForm = play.data.Form.form().bindFromRequest();
		
		if(Play.isProd())
		{
			String remoteAddr = request().remoteAddress();
		    ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
		    reCaptcha.setPrivateKey(Captchaprivate);
		    String challenge = bindedForm.get("recaptcha_challenge_field");
		    String uresponse = bindedForm.get("recaptcha_response_field");
		    ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);
		    if(!reCaptchaResponse.isValid())
			{
				response().setContentType("application/json");
				return badRequest("{\"Captcha\":[\"Captcha Entered was invalid.Please Retry.\"]}");
			}
		}
		
		String Redirect_to=play.data.Form.form().bindFromRequest().get("redirecturi");
		if(Redirect_to!=null)
			session().put(DInitial.REDIRECT_BACK_URL, Redirect_to);
	    
	    Boolean isajax= Boolean.parseBoolean( play.data.Form.form().bindFromRequest().get("isajax"));
	    if(isajax)
		{
			response().setContentType("application/json");
			session().put("isajax", "yes");
		}
	    com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MySignup> filledForm = MyUsernamePasswordAuthProvider.SIGNUP_FORM
				.bindFromRequest();	
		
		if (filledForm.hasErrors()) {
			if(isajax)
				return badRequest(filledForm.errorsAsJson());
			
			flash(FLASH_ERROR_KEY, filledForm.errorsAsJson().toString());
			return badRequest(signup.render(filledForm));
			
		} else {
			flash().clear();
			Result r= UsernamePasswordAuthProvider.handleSignup(ctx());
			if(isajax)
				if(flash().containsKey(FLASH_ERROR_KEY))
					return badRequest("{\"Error\":\""+flash().get(Application.FLASH_ERROR_KEY)+"\"}" );			
			return r;
		}
	}
	
	
	@Restrict(@Group(Application.ADMIN_ROLE))
	public static Result  ChangeUserRole(Long conid,String role){
		Contributor c=Contributor.find.byId(conid);
		if(c==null)
			return badRequest("USER NOT FOUND");
		if(role.equals(ADMIN_ROLE))
			SecurityRole.setAdmin(c.user);
		if(role.equals(MODERATOR_ROLE))
			SecurityRole.setModerator(c.user);
		if(role.equals(USER_ROLE))
			SecurityRole.setUser(c.user);
		return redirect(routes.Application.ContributorPage(c.id, false, false));
	}	
	
	@Restrict(@Group(Application.ADMIN_ROLE))
	public static Result  GetAllWallpapers(){
			List<S3File> wpl = S3File.findfilelist(S3Plugin.s3Bucket, DInitial.WP);
			return ok(views.html.Admin.ViewWallpapers.render(wpl));
		
	}
	
	@Restrict(@Group(Application.ADMIN_ROLE))
	public static Result  UploadWallpapers(){
		FilePart fp=request().body().asMultipartFormData().getFile("picture");
		long count=S3File.findfilelistcount(S3Plugin.s3Bucket, DInitial.WP)+1;
		String allotedname=GHelp.getimageextension("Wallpaper"+count,fp.getContentType() );
		File uf=fp.getFile();
		S3File.createfile(S3Plugin.s3Bucket,DInitial.WP, allotedname, DInitial.IMAGESTORESIZE.AS_IT_IS.filestate, count,uf);
		uf.delete();
		return redirect(routes.Application.GetAllWallpapers());
	}
	
	@Restrict(@Group(Application.ADMIN_ROLE))
	public static Result  RemoveWallpapers(Long id){
		S3File s3f= S3File.findfile(id);
		if(s3f!=null)
		{
			s3f.delete();
			flash(FLASH_MESSAGE_KEY,"file successfully removed");
		}else{
			flash(FLASH_ERROR_KEY,"Sorry the delete was unsuccessful.Cannot find the file specified :(");
		}
		
		return redirect(routes.Application.GetAllWallpapers());
	}
	
	
	public static Result submitcontactus()
	{
		final Form<CONTACTUS> filledForm = controllers.SPages.CONTACTUS_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(views.html.spages.Contactus.render(filledForm));
		}else{
			CONTACTUS cu = filledForm.get();
			Contactus cus = Contactus.CreateQuery(cu.querytype, cu.email, cu.firstname, cu.lastname, cu.subject, cu.content);
			flash(FLASH_MESSAGE_KEY,"Your query has been successfully submited.");
			return redirect(routes.Application.index());
		}
	}
	
	
	
	
	
	
	
	//**********************UTILITIES*************************************
	

	public static String formatTimestamp(final long t) {
		return new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").format(new Date(t));
	}
	
	public static String timezoneformat(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(date);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//FOR TEST PURPOSES ONLY
	

	
	
	
	
	public static Result ProdTest()
	{
		if(play.api.Play.isDev(play.api.Play.current()))
		{
			//Logger.info(Ebean.createSqlQuery("select '{prediction:'||array_to_json(array(select PRODUCTNAME from product where PRODUCTNAME ilike '%p%' LIMIT 8))||'}' AS JO").findUnique().getString("JO"));
			//return QuerySuggestion("p",(long) 0);
			/*
			com.feth.play.module.mail.Mailer mailer=Mailer.getCustomMailer
					(play.Play.application().configuration().getConfig("boozology-mailer")); 
			//getDefaultMailer();
			final Body body =new Body("Test Message body");
			GHelp.mailsender.sendMail("Test Message Subject", body , "simple@example.com");
			
			return ok(testpage.render(Blog.RecentBlogPage(0, 40)));
			*/
			
		}
		return notFound();
		
	}
	
/*
	@SuppressWarnings("unused")
	private static Result ProdQuery() {
		StringBuilder Res=new StringBuilder();
		List<Contributor> cont= Contributor.find.all();		
		for(Contributor c :cont)
		{
			Res.append(c.user.email+">>>>>\n");
			List<Product> p=Product.find.where().eq("Founder.id",c.user.id).findList();
			for(Product pr:p)
				Res.append(String.format("\tRESULT\tProduct:%s\tWant:%2d\tLove:%2d\tOwner:%2d\tUnderCollections:%2d\n", pr.productname,Product.Wants(pr),Product.Likes(pr),Product.Owns(pr),Product.Collections(pr)));
		}
		for(Contributor c:cont)
		{
			Res.append(String.format("\n\nUser:%s",c.user.email));
			//List<Follow> Followerslist= Contributor.Followers(c);
			//List<Follow> Leaderslist= Contributor.Followers(c);
			Res.append(String.format("\n\tUser:%s \tfollowed by \t %d users",c.user.email,Contributor.FollowersCount(c)));
			
			//for(Follow fl :Followerslist)
				//Res.append(String.format("\n\t\t+\t%s",fl.follower.user.email));
				
			Res.append(String.format("\n\tUser:%s \tis following\t %d users\n",c.user.email,Contributor.LeadersCount(c)));
			
			//for(Follow fl :Followerslist)
				//Res.append(String.format("\t\t+\t%s",fl.follower.user.email));
				
			Res.append('\n');
			try{
			Page<EventLogSql> fd=EventLogSql.Feed(0, 100, c,"");
			boolean midlop=false;
			Res.append(String.format("\nUser %s contains %d rows\n",c.user.name, fd.getList().size()));
			do{
				if(midlop)
					fd=fd.next();
				for(EventLogSql els:fd.getList())
				{
					Res.append(String.format("%d\t%s\t%s\n",els.signify,els.info,els.productinv.productname));
				}
				midlop=true;
			}while(fd.hasNext());
			}catch(Exception E){Res.append("\n*********Error in : FEED\n"+EventLogSql.getCustomStackTrace(E) +"\n\n");}
		}		
		
		try{
			Page<Product> atf =null;
			Res.append("<<<<<<<<<<<<<<<<<<<<<Relevance feed>>>>>>>>>>>>>>>>>>>>>\n");
			int counter=0;
			do{
				if(atf==null)
					atf=Eventlog.RelevanceFeed(0, 50,GHelp.filterSql("", 0, 0, 0,""));
				else
					atf=atf.next();
				counter+=atf.getList().size();
				
				for(Product els:atf.getList())
				{
					//Res.append(String.format("%l\t%s\t%s\n",els.id,els.Founder.name,els.productname));
					Res.append(String.format("%d\t%s\t%s\n",els.id,els.productname,els.Founder.name));
				}
			}while(atf!=null && atf.hasNext());
			Res.append(String.format("\nRelevance feed  contains %d rows \n",counter));
		}catch(Exception E){Res.append("\n*********Error in : All Time Feed\n"+EventLogSql.getCustomStackTrace(E) +"\n\n");}
		
		
		
		try{
			Page<Product> atf =null;
			Res.append("<<<<<<<<<<<<<<<<<<<<<All Time Feed>>>>>>>>>>>>>>>>>>>>>\n");
			int counter=0;
			do{
				if(atf==null)
					atf=Eventlog.Alltime(0, 50,GHelp.filterSql("", 0, 0, 0,""));
				else
					atf=atf.next();
				counter+=atf.getList().size();
				
				for(Product els:atf.getList())
				{
					//Res.append(String.format("%l\t%s\t%s\n",els.id,els.Founder.name,els.productname));
					Res.append(String.format("%d\t%s\t%s\n",els.id,els.productname,els.Founder.name));
				}
			}while(atf!=null && atf.hasNext());
			Res.append(String.format("\nAll Time feed  contains %d rows \n",counter));
		}catch(Exception E){Res.append("\n*********Error in : All Time Feed\n"+EventLogSql.getCustomStackTrace(E) +"\n\n");}
		return ok(Res.toString());
	}

	public static Result  Tryhtm ()
	{
		return ok(views.html.Try.render());
	}
	*/

}