import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.Random;

import models.Category;
import models.Contributor;
import models.Eventlog;
import models.FSearch;
import models.ImageBeer;
import models.ImageGadgets;
import models.ImageGlassWare;
import models.ImageLiquor;
import models.ImageMixology;
import models.ImageToys;
import models.ImageWine;
import models.Product;
import models.S3File;
import models.SecurityRole;
import models.Store;
import models.User;
import models.UserCollection;
import models.UserPermission;
import models.Notifications.SOFollows;
import models.Notifications.UserSubscriptions;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.SqlUpdate;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;
import com.feth.play.module.pa.providers.AuthProvider;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.user.AuthUser;
import com.maxmind.geoip2.DatabaseReader;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import controllers.DInitial;
import controllers.routes;
import play.Application;
import play.Configuration;
import play.GlobalSettings;
import play.Logger;
import play.api.Play;
import play.data.Form;
import play.libs.Akka;
import play.libs.F;
import play.libs.F.Promise;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import plugins.S3Plugin;
import providers.MyLoginUsernamePasswordAuthUser;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthUser;
import providers.MyUsernamePasswordAuthProvider.MySignup;
import scala.concurrent.ExecutionContext;

public class Global extends GlobalSettings {
	
	public static final String  APP_ENV_LOCAL = "local";
	public static final String  APP_ENV_VAR = "CURRENT_APPNAME";

	public void onStart(Application app) {
		Logger.info("Application has started");	
		PlayAuthenticate.setResolver(new Resolver() {

			@Override
			public Call login() {
				// Your login page
				return routes.Application.login();
			}

			@Override
			public Call afterAuth() {
				// The user will be redirected to this page after authentication
				// if no original URL was saved
				return routes.Signup.PostSignUp();
			}

			@Override
			public Call afterLogout() {
				return routes.Application.index();
			}

			@Override
			public Call auth(final String provider) {
				// You can provide your own authentication implementation,
				// however the default should be sufficient for most cases
				return com.feth.play.module.pa.controllers.routes.Authenticate
						.authenticate(provider);
			}

			@Override
			public Call askMerge() {
				return routes.Account.askMerge();
			}

			@Override
			public Call askLink() {
				return routes.Account.askLink();
			}

			@Override
			public Call onException(final AuthException e) {
				if (e instanceof AccessDeniedException) {
					return routes.Signup
							.oAuthDenied(((AccessDeniedException) e)
									.getProviderKey());
				}

				// more custom problem handling here...
				return super.onException(e);
			}
		});
		
		
		initialData();
		//ProdTest();
		 	/*
		final ExecutionContext ec = akka.dispatch.ExecutionContexts.global();
    	akka.dispatch.Futures.future(new Callable<Boolean>() {
    	public Boolean call() {
    		ProdTest();   		
    		return true;
    	}
    	}, ec);
    	*/
		
		Promise<Boolean> initdataPromise = Promise.promise(new F.Function0<Boolean>() {
		    public Boolean apply() throws Throwable {
		    	ProdTest();
		        return Boolean.TRUE;
		    }

		});
		
	}

	@Override
	public Configuration onLoadConfig(play.Configuration configuration, java.io.File file, java.lang.ClassLoader classloader)
	{
		String prefix=System.getenv(APP_ENV_VAR);
		if(prefix==null)
			prefix=APP_ENV_LOCAL;
		Config baseConfig = configuration.getWrappedConfiguration().underlying();
		Config additionalConfig = baseConfig.withFallback(ConfigFactory.load(prefix+".conf"));	    
	    return new Configuration(additionalConfig);	    
	}

	
	private void initialData() {
		if (SecurityRole.find.findRowCount() == 0) {
			for (final String roleName : Arrays
					.asList(controllers.Application.USER_ROLE,controllers.Application.ADMIN_ROLE,controllers.Application.MODERATOR_ROLE)) {
				final SecurityRole role = new SecurityRole();
				role.roleName = roleName;
				role.save();
			}
		}
	}
	
	

	private static void ProdTest()
	{
		if(DInitial.geoipreader==null && play.Play.isProd())
		{
			try{
				File f = play.Play.application().getFile(File.separator +"public"+File.separator+"dbfiles"+File.separator+"Country.mmdb");
				//File f=new File(play.Play.application().path()+ File.separator +"public"+File.separator+"dbfiles"+File.separator+"Country.mmdb");
				Logger.info("Initializing geoip with "+f.length());
				DInitial.geoipreader = new DatabaseReader(f);
				//Logger.info(DInitial.geoipreader.country(java.net.InetAddress.getByName("173.252.110.27")).getCountry().getName());
			}catch(Exception E)
			{
				//Logger.error(DInitial.geoipreader.toString());
				E.printStackTrace();
				Logger.error("Can not initialize geoip...");
			}
		}
		
		
		if(!Contributor.find.all().isEmpty())
		{
			Logger.info("Data ALREADY EXISTS...!!!");
			return ;
		}
		Logger.info("Setting up initial users...");
		//StringBuilder FinalResponse=new StringBuilder();
		
		Random rand=new Random();
		int user_count=2,user_collection=4,product_count=50;
		Map<String,String> ul = new HashMap<String, String>();
		ul.put("admin", "a1234");
		ul.put("moderator", "m1234");
		ul.put("user", "u1234");
		
		try
		{
			for (Entry<String, String> entry : ul.entrySet()) {
				for(int iden=0;iden<user_count;iden++)
				{
					Map<String,String> userinformation = new HashMap<String, String>();
					userinformation.put("email", "user"+iden+"@"+entry.getKey()+".com");
					userinformation.put("password", entry.getValue());
					userinformation.put("repeatPassword", entry.getValue());
					userinformation.put("name", entry.getKey()+iden);
					//Form<MySignup> filledForm = MyUsernamePasswordAuthProvider.SIGNUP_FORM.bind(userinformation);
					//MySignup msu=filledForm.get();
					MySignup msu = new MySignup();
					msu.email = "user"+iden+"@"+entry.getKey()+".com";
					msu.password = msu.repeatPassword = entry.getValue();
					msu.name = entry.getKey()+iden;
					//final AuthProvider ap = PlayAuthenticate.getProvider("password");
					MyUsernamePasswordAuthUser authUser = new MyUsernamePasswordAuthUser(msu);
					final User newUseri = User.create(authUser);
					//final MyLoginUsernamePasswordAuthUser newUser =new MyLoginUsernamePasswordAuthUser(authUser.getEmail());
					
					newUseri.profileimage="normaluser.png";
					newUseri.gender=rand.nextInt(3);
					newUseri.emailValidated =true;
					newUseri.update();
					if(entry.getKey()=="admin")
					{SecurityRole.setAdmin(newUseri);}
					if(entry.getKey()=="moderator")
					{SecurityRole.setModerator(newUseri);}
					
					
				}
			}
			Logger.info("User data recorder...");
			
			Logger.info("Inserting initial images...");
			
			ImageBeer.createInitial("bl/images/beer.png");
			ImageGadgets.createInitial("bl/images/gadgets.png");
			ImageGlassWare.createInitial("bl/images/glassware.png");
			ImageLiquor.createInitial("bl/images/liquor.png");
			ImageMixology.createInitial("bl/images/mixology.png");
			ImageToys.createInitial("bl/images/toys.png");
			ImageWine.createInitial("bl/images/wine.png");
			
			Logger.info("Initial images recorded...");
	
			List<Contributor> contl= Contributor.find.all();		
			List<UserCollection> uscl=new ArrayList<UserCollection>();
			List<Product> prl = new ArrayList<Product>();
			
			Logger.info("Adding subcategories...");
			Category.AddSubCategoryList(Category.CreateRootCategory("Wine"),Arrays.asList("Red Wine","White Wine","Rosé Wine","Sparkling Wine","Fortified Wine") );
			Category.AddSubCategoryList(Category.CreateRootCategory("Beer"),Arrays.asList("Ale","Bouza","Boza","Bozo","Cask ale","Cauim","Chhaang","Chicha","Fruit and vegetable beer","Gruit","Herb and spiced beer","Kellerbier","Kvass","Lager","Oshikundu","Pulque","Purl","Sahti","Smoked beer","Strong ale","Sour ale","Sulima","Wheat beer","Zwickelbier") );
			Category.AddSubCategoryList(Category.CreateRootCategory("Spirits"),Arrays.asList("Brandy","Cachaça","Gin","Rum","Schnapps","Tequila","Vodka","Whisky") );
			Category.AddSubCategoryList(Category.CreateRootCategory("Mixology"),Arrays.asList("Party Drinks","Romantic Drinks","Skill-Gain Drinks","Energy Drinks","Crazy Drinks") );
			Category.AddSubCategoryList(Category.CreateRootCategory("Toys & Trinkets"),Arrays.asList("A","B","C") );
			Category.AddSubCategoryList(Category.CreateRootCategory("Glassware"),Arrays.asList("Beer Mug","Brandy Snifter","Champagne Flute","Collins Glass","Cordial Glass","Highball Glass","Hurricane Glass","Irish Coffee Mug","Margarita Glass","Martini Glass","Old Fashioned Glass","Pilsner Glass","Pitcher","Punch Bowl","Shot Glass","Wine Glass") );
			
			
			List<Category> catl = Category.TopLevelCategories();
			//UserCollection List
			for(int iden=0;iden<user_count;iden++)
			{				
				int ltimes=(rand.nextInt(user_collection)+5);
				Contributor ctr=contl.get(iden);
				for(int j=0;j<ltimes;j++)
					uscl.add(new UserCollection(ctr, ("Collection "+ctr.user.id+" ")+j));
			}
			//Product List
			String pts=File.separator;
			String outputlocation =play.Play.application().path()+ pts +"public"+pts+"gallery"+pts+"fulls"+pts;
			Logger.info(outputlocation);
			File f = new File(outputlocation);
			if(!f.isDirectory())
				return;
			Logger.info(f.getAbsolutePath());
			File[] files=f.listFiles();
			Logger.info(""+files.length);
			int prodiden=0;
			for(File fx:files)
			{
				Logger.info(fx.getAbsolutePath());
				for(Contributor c:contl)
				{
					Product cpr=new Product(("Product "+c.user.id+" "+c.user.name+" ")+prodiden, "$", 12+rand.nextInt(150000), c.user, "http://"+prodiden+"avenue.amazon.com/productx",fx.getName(),catl.get(rand.nextInt(4)), true,"Random Initialized");
					if(cpr!=null)
					{
						//disabled....
						if(play.Play.isProd())
							cpr.savetocdn("productpic"+cpr.id+".png", fx);
						new S3File(S3Plugin.s3Bucket, Product.class.getSimpleName(), "productpic"+cpr.id+".png", DInitial.IMAGESTORESIZE.AS_IT_IS.filestate, cpr.id);
						new S3File(S3Plugin.s3Bucket, Product.class.getSimpleName(), "productpic"+cpr.id+".png", DInitial.IMAGESTORESIZE.THUMBNAIL_BRICK.filestate, cpr.id);
						prl.add(cpr);
						Eventlog.ReportEvent(DInitial.PRODUCT_ADDED, cpr, c, null, cpr.pstore);
					}
				}
				prodiden++;
			}
			
			
			
			//Retrieve inserted Products
			prl=Product.find.all();
			//Put into Collection
			for(UserCollection usc : uscl)
				for(Product pr:prl)
					if(rand.nextBoolean())
					{
						if(usc.AddProduct(pr,usc.contributor))
							Eventlog.ReportEvent(DInitial.PRODUCT_COLLECTED, pr, usc.contributor, usc, pr.pstore);
					}
			
			//Start Likes and Other Stuffs

			List<Store> stc=Store.find.all();
			
			for(Contributor c:contl)
			{			
				for(Product pr:prl)
				{
					if(rand.nextBoolean())
					{
						if(c.AddLover(pr))
							Eventlog.ReportEvent(DInitial.PRODUCT_LOVED, pr, c, null, pr.pstore);
					}
					if(rand.nextBoolean())
						c.AddOwner(pr);
					if(rand.nextBoolean())
						c.AddWanter(pr);
				}
				for(Store st:stc)
				{					
					if(rand.nextBoolean())
						Store.AddFollower(st, c);					
				}
				for(UserCollection usc : uscl)
				{					
					if(rand.nextBoolean())
						UserCollection.AddFollower(usc, c);					
				}
				
				if(rand.nextBoolean())
					FSearch.FollowSearch(""+(rand.nextInt(5)+1), rand.nextInt(2), rand.nextInt(4), c);
				c.save();
				
			}
			
			
			
			
			//Lets See Status
			
			for(Product pr:prl)
			{
				//pr.refresh();
				//FinalResponse.append(String.format("\n\tATTEMPT\tProduct:%s\tWant:%2d\tLove:%2d\tOwner:%2d\tUnderCollections:%2d\n", pr.productname,Contributor.find.where().eq("Wanted.id",pr.id).findList().size(),Contributor.find.where().eq("Liked.id",pr.id).findList().size(),Contributor.find.where().eq("Owned.id",pr.id).findList().size(),UserCollection.find.where().eq("productlist.id",pr.id).findList().size()  ));
				//FinalResponse.append(String.format("\tRESULTS\tProduct:%s\tWant:%2d\tLove:%2d\tOwner:%2d\tUnderCollections:%2d\n", pr.productname,Product.Wants(pr),Product.Likes(pr),Product.Owns(pr),Product.Collections(pr)));
			}
			

				for(Contributor c:contl)
				{
					for(Contributor cf:contl)
					{
						if(rand.nextBoolean() && !cf.equals(c))
							c.AddFollower(cf);
					}
				}
				
				for(Contributor c:contl)
				{
					//FinalResponse.append(String.format("\n\tUser:%s\t followed by \t %d users",c.user.email,Contributor.FollowersCount(c)));
					//FinalResponse.append(String.format("\tUser:%s\t is following\t %d users\n",c.user.email,Contributor.LeadersCount(c)));
				}
				
				Ebean.createSqlUpdate("UPDATE PRODUCT SET id=0 WHERE 1 IN (SELECT 0 FROM updateallimage()) AND id=0;\n ").execute();
			
			
		}
		catch(Exception E)
		{
			E.printStackTrace();
			//FinalResponse.append("Error " + E.toString()+"\n");
		}
		//FinalResponse.toString());
		Logger.info("Data initialization done...Having hangover...");	
		if(play.Play.isProd())
		{
			//System.out.print(FinalResponse.toString());
		}
	}
}