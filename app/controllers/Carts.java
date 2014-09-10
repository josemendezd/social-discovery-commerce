package controllers;

import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.impl.client.HttpClients;

import models.Blog;
import models.Cart;
import models.Contributor;
import models.Item;
import models.Product;
import models.User;
import models.UserAddress;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;
import views.html.login;

import akismet.Akismet;
import akismet.AkismetComment;
import akismet.AkismetException;

import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.google.common.collect.Lists;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;


public class Carts extends Controller {
	
	public static final String FLASH_ERROR_KEY = "error";
	
	public static class CartVM {

		public Long id;
		public String name;
		public String description;
		public Double price;
		public Integer quantity;
		public Double amount;
		public Double total = 0.0;
		public String url;
		
		public CartVM(Long id, String productname, String description, Double pricetag, Integer quantity, Double total, String url) {
			this.id = id;
			this.name = productname;
			this.description = description;
			this.price = pricetag;
			this.quantity = quantity;
			this.amount = pricetag * quantity;
			this.total = total;
			this.url = url;
		}	
	}
	
	// This function add or updates the cart with the selected item for the logged in user
	public static Result addCart() {
		DynamicForm form = DynamicForm.form().bindFromRequest();
		
		String uuid = null;
		if (request().cookies() != null && request().cookies().get("uuid") != null) {
			uuid = request().cookies().get("uuid").value().toString();
		}
		
		Cart cartSearch = null;
		User localUser = Application.getLocalUser(session());
		if (localUser != null) { // For login User
			cartSearch = Cart.find.where().eq("user", localUser).eq("status", "Open").findUnique();
		} else if (uuid != null) { // For guest User
			cartSearch = Cart.find.where().eq("uuid", uuid).eq("status", "Open").findUnique();
		} else {
			uuid = UUID.randomUUID().toString();
			response().setCookie("uuid", uuid);
		}

		Date date = new Date();
		
		if (cartSearch == null) { // Search Cart will be null for first time.
			Item item = new Item();
			
			item.quantity = Integer.parseInt(form.get("quantity"));
			item.product = Product.find.byId(Long.parseLong(form.get("id")));
			item.save();
			
			Cart cart = new Cart();
			List<Item> items = Lists.newArrayList();
			if(localUser != null) { // For logged in user.
				cart.user = localUser;
				cart.uuid = null;
			} else {
				cart.uuid = uuid;
				cart.user = null;
			}
			items.add(item);
			cart.items = items;
			cart.status = "Open";
			cart.save();
			
			item.cart = cart;
			item.date = date;
			item.update();
			cartSearch = cart;
			
		} else {
			Item itemToUpdate = Item.find.where().eq("cart", cartSearch).eq("product", Product.find.byId(Long.parseLong(form.get("id")))).findUnique();
			if(itemToUpdate == null) {
				itemToUpdate = new Item();
				itemToUpdate.quantity = Integer.parseInt(form.get("quantity"));
				itemToUpdate.product = Product.find.byId(Long.parseLong(form.get("id")));
				itemToUpdate.save();
			} else {
				itemToUpdate.id = itemToUpdate.id;
				itemToUpdate.quantity = Integer.parseInt(form.get("quantity"));
			}
			
			List<Item> items = Item.find.where().eq("cart", cartSearch).findList();
			itemToUpdate.cart = cartSearch;
			itemToUpdate.date = date;
			itemToUpdate.update();
			items.add(itemToUpdate);
			cartSearch.items = items;
			cartSearch.update();
		}
		
		List<Item> items = Item.find.where().eq("cart", cartSearch).orderBy().desc("date").findList();
		List<CartVM> cartVMs = Lists.newArrayList();
		Double amount = 0.0;
		for(Item item : items) {
			item.product.refresh();
			amount = amount + (Double)(item.product.Pricetag * item.quantity);
		}
		
		for(Item item : items) {
			CartVM caVm = new CartVM(item.id,item.product.productname,item.product.description,item.product.Pricetag,item.quantity,amount,item.product.getproductimagethumb());
			cartVMs.add(caVm);
		}
		
		System.out.println(Json.toJson(cartVMs));
		
		return ok(Json.toJson(cartVMs));
	}
	
	// This function gets the most recent entry of the cart of the current issue
	public static Result getCartList() {
		User localUser = Application.getLocalUser(session());
		
		String cookie = null;
		if (request().cookies().get("uuid") != null) {
			cookie = request().cookies().get("uuid").value().toString();
		}
		
		Cart cartSearch = null;
		if(localUser != null) { // For loged in user
			cartSearch = Cart.find.where().eq("user", localUser).eq("status", "Open").findUnique();	
		} else {
			cartSearch = Cart.find.where().eq("uuid", cookie).eq("status", "Open").findUnique();
		}

		List<Item> items = Item.find.where().eq("cart", cartSearch).orderBy().desc("date").findList();
		List<CartVM> cartVMs = Lists.newArrayList();

		
		Double amount = 0.0;
		for(Item item : items) {
			item.product.refresh();
			amount = amount + (Double)(item.product.Pricetag * item.quantity);
		}
		for(Item item : items) {
			CartVM caVm = new CartVM(item.id,item.product.productname,item.product.description,item.product.Pricetag,item.quantity,amount,item.product.getproductimagethumb());
			cartVMs.add(caVm);
		}
		
		return ok(Json.toJson(cartVMs));
	}
	
	
	// This function checks whether a cart is present for a logged in user or not
	public static boolean cartPresent() {
		User localUser = Application.getLocalUser(session());
		
		String cookie = null;
		if (request().cookies().get("uuid") != null) {
			cookie = request().cookies().get("uuid").value().toString();
		}
		
		if (localUser != null) {
			return  Cart.find.where().eq("user", localUser).eq("status", "Open").findUnique() != null ? true : false;	
		} else if(cookie == null) {
			return false;
		} else {
			return Cart.find.where().eq("uuid", cookie).eq("status", "Open").findUnique() != null ? true : false;
		}
		
	}
	
	// This function display the list of items in a cart
	public static Result cartListIndex() {
		return ok(views.html.cartItemsList.render());
	}

	// This function returns all the items present in the cart for the user
	public static Result getCartItems() {
		User localUser = Application.getLocalUser(session());
		String cookie = null;
		if (request().cookies().get("uuid") != null) {
			cookie = request().cookies().get("uuid").value().toString();
		}
		Cart cartSearch = null;
		if(localUser != null) {
			cartSearch = Cart.find.where().eq("user", localUser).eq("status", "Open").findUnique();	
		} else {
			cartSearch = Cart.find.where().eq("uuid", cookie).eq("status", "Open").findUnique();
		}
		List<Item> items = Item.find.where().eq("cart", cartSearch).orderBy("id").findList();
		List<CartVM> cartVMs = Lists.newArrayList();

		Double amount = 0.0;
		for(Item item : items) {
			item.product.refresh();
			amount = amount + (Double)(item.product.Pricetag * item.quantity);
		}
		for(Item item : items) {
			CartVM caVm = new CartVM(item.id,item.product.productname,item.product.description,item.product.Pricetag,item.quantity,amount,item.product.getproductimagethumb());
			cartVMs.add(caVm);
		}
		
		return ok(Json.toJson(cartVMs));
	}

	// This function delete the selected item from the cart
	public static Result deleteItem() {
		DynamicForm form = DynamicForm.form().bindFromRequest();
		User localUser = Application.getLocalUser(session());
		String cookie = null;
		if (request().cookies().get("uuid") != null) {
			cookie = request().cookies().get("uuid").value().toString();
		}
		Cart cartSearch = null;
		if(localUser != null) {
			cartSearch = Cart.find.where().eq("user", localUser).eq("status", "Open").findUnique();	
		} else {
			cartSearch = Cart.find.where().eq("uuid", cookie).eq("status", "Open").findUnique();
		}
		
		Item itemObj = Item.find.ref(Long.parseLong(form.get("id")));
		itemObj.delete();
		List<Item> items = Item.find.where().eq("cart", cartSearch).orderBy("id").findList();
		List<CartVM> cartVMs = Lists.newArrayList();

		Double amount = 0.0;
		for(Item item : items) {
			item.product.refresh();
			amount = amount + (Double)(item.product.Pricetag * item.quantity);
		}
		for(Item item : items) {
			CartVM caVm = new CartVM(item.id,item.product.productname,item.product.description,item.product.Pricetag,item.quantity,amount,item.product.getproductimagethumb());
			cartVMs.add(caVm);
		}
		
		return ok(Json.toJson(cartVMs));
	}
	
	// This function update the quantity of an item present in the cart
	public static Result updateItem() {
		DynamicForm form = DynamicForm.form().bindFromRequest();
		Item itemObj = Item.find.ref(Long.parseLong(form.get("id")));
		itemObj.quantity = Integer.parseInt(form.get("quantity"));
		itemObj.date = new Date();
		itemObj.update();
		User localUser = Application.getLocalUser(session());
		String cookie = null;
		if (request().cookies().get("uuid") != null) {
			cookie = request().cookies().get("uuid").value().toString();
		}
		Cart cartSearch = null;
		if(localUser != null) {
			cartSearch = Cart.find.where().eq("user", localUser).eq("status", "Open").findUnique();	
		} else {
			cartSearch = Cart.find.where().eq("uuid", cookie).eq("status", "Open").findUnique();
		}
		List<Item> items = Item.find.where().eq("cart", cartSearch).orderBy("id").findList();
		List<CartVM> cartVMs = Lists.newArrayList();

		Double amount = 0.0;
		for(Item item : items) {
			item.product.refresh();
			amount = amount + (Double)(item.product.Pricetag * item.quantity);
		}
		for(Item item : items) {
			CartVM caVm = new CartVM(item.id,item.product.productname,item.product.description,item.product.Pricetag,item.quantity,amount,item.product.getproductimagethumb());
			cartVMs.add(caVm);
		}
		
		return ok(Json.toJson(cartVMs));
	}
	
	// This function checks whether the user is navigating to the complete cart section
	public static boolean cartViewPage() {
		String url = request().path();
		if(url.equals("/cart/user/lists") || url.equals("/cart/user/checkout") || url.equals("/cart/guest/checkout") || url.equals("/cart/new/user/checkout") 
				|| url.equals("/cart/user/details") || url.equals("/cart/new/user/details")
				|| url.equals("/cart/checkout/login") || url.equals("/")) {
			return false;
		} else {
		return true;
		}
	}
	
	// This function display the check out page for the logged in user
	public static Result checkOutPage() {
		User localUser = Application.getLocalUser(session());
		if (localUser != null) {
			UserAddress userAddress = UserAddress.find.where().eq("user", localUser).findUnique();
			return ok(views.html.checkOut.render(userAddress));
		} else {
			return ok(views.html.guestUserCheckOut.render());
		}
	}
	
	// This function display the check out page for the guest
	public static Result guestcheckOutPage() {
		UserAddress userAddress = null;
		return ok(views.html.checkOut.render(userAddress));
	}
	
	// This function saves the billing details of the user/guest
	public static Result checkOutUserDetails() {
		final Form<UserAddress> userform  = play.data.Form.form(UserAddress.class).bindFromRequest();
		UserAddress contactAddress = userform.get();
		String uuid = null;
		if (request().cookies().get("uuid") != null) {
			uuid = request().cookies().get("uuid").value().toString();
		}

		User localUser = Application.getLocalUser(session());
		UserAddress address = null;
		if(localUser != null) {
			address = UserAddress.find.where().eq("user", localUser).findUnique();
		} else {
			address = UserAddress.find.where().eq("uuid", uuid).findUnique();
		}
		
		if(address == null) {
			if(localUser != null) {  //save the reference of the logged in user
				contactAddress.user = localUser;
			} else {				// save the uuid of the guest user
				contactAddress.user = null;
				contactAddress.uuid = uuid;
			}
			contactAddress.save();	
		} else {
			contactAddress.id = address.id;
			contactAddress.update();
		}
		
		Cart cart = null;
		if(localUser != null) {
			cart = Cart.find.where().eq("user", localUser).eq("status", "Open").findUnique();
		}
		
		if(uuid != null) {
			cart = Cart.find.where().eq("uuid", uuid).eq("status", "Open").findUnique();
		}
		
		List<Item> items = Item.find.where().eq("cart", cart).findList();
		
		// Calculate the total billable amount
		Double amount = 0.0;
		for(Item item : items) {
			item.product.refresh();
			amount = amount + (Double)(item.product.Pricetag * item.quantity);
		}
		return ok(views.html.cartTransaction.render(amount));
	}
	
	// This function display the page for new user creation and registration
	public static Result createUserCheckoutPage() {
		return ok(views.html.createUserCheckout.render());
	}
	
	// This function signup the new user and proceed to the transaction screen
	public static Result checkOutNewUserDetails() {
		final Form<UserAddress> userform  = play.data.Form.form(UserAddress.class).bindFromRequest();
		final Form<MySignup> filledForm = MyUsernamePasswordAuthProvider.SIGNUP_FORM.bindFromRequest(); // Checks the details of the user
		Result r=UsernamePasswordAuthProvider.handleSignup(ctx());	// sign up the new user
		UserAddress contactAddress = userform.get();

		String cookie = null;
		if (request().cookies().get("uuid") != null) {
			cookie = request().cookies().get("uuid").value().toString();
		}
		User localUser = Application.getLocalUser(session());

		
		Cart cart = Cart.find.where().eq("uuid", cookie).eq("status", "Open").findUnique();		// Substitute the guest selection items into the logged user cart
		cart.uuid = null;
		cart.user = localUser;
		cart.update();
		contactAddress.uuid = null;
		contactAddress.user = localUser;
		contactAddress.save();
		
		List<Item> items = Item.find.where().eq("cart", cart).findList();
		
		Double amount = 0.0;
		for(Item item : items) {
			item.product.refresh();
			amount = amount + (Double)(item.product.Pricetag * item.quantity);
		}
		
		return ok(views.html.cartTransaction.render(amount));
	}
	
	// This function checks the availability of the username
	
	public static Result checkUserNameAvailability() {
		DynamicForm form = DynamicForm.form().bindFromRequest();
		String q = form.get("q");
		User user = User.find.where().eq("name", q).findUnique();
		
		if(user == null) {
			return ok(Json.toJson(true));
		} else {
			return ok(Json.toJson("Username is not available"));
		}
	}
	
	// This function logs in the user and take to the transaction screen
	public static Result userLogin() {
		final Form<MyLogin> filledForm = MyUsernamePasswordAuthProvider.LOGIN_FORM.bindFromRequest();	//checks for errors if any
		if (filledForm.hasErrors()) {
			
			flash(FLASH_ERROR_KEY, filledForm.errorsAsJson().toString());
			return badRequest(login.render(filledForm));
		} else {
			
			Result r=UsernamePasswordAuthProvider.handleLogin(ctx());
			User localUser = Application.getLocalUser(session());
			
			if(localUser == null) {	// Returns error message on same screen if unauthorized access
				flash(FLASH_ERROR_KEY, "Username and Password Does not match Please try again !!!!!!");
				return ok(views.html.guestUserCheckOut.render());
			} else {
				String cookie = null;
				if (request().cookies().get("uuid") != null) {
					cookie = request().cookies().get("uuid").value().toString();
				}
				
				Cart cart = Cart.find.where().eq("uuid", cookie).eq("status", "Open").findUnique();	
				Cart userCart = Cart.find.where().eq("user", localUser).eq("status", "Open").findUnique();
				List<Item> items = Item.find.where().eq("cart", cart).findList();
				List<Item> userItems = Item.find.where().eq("cart", userCart).findList();
				for(Item item : userItems) {		// Replaces the cart item of the user with the items selected by the user as a guest
					Item.find.ref(item.id).delete();
				}
				if(userCart != null) {
					userCart.items = items;
					userCart.update();
					for(Item item : items) {
						item.cart = userCart;
						item.update();
					}
					Cart.find.ref(cart.id).delete();
				} else {
					cart.uuid = null;
					cart.user = localUser;
					cart.update();
				}
				
				// Checks for user address if predefined
				UserAddress userAddress = UserAddress.find.where().eq("user", localUser).findUnique();
				return ok(views.html.checkOut.render(userAddress));
			}
		}
	}
	
	// This function do the card transaction and returns the response of it 
	public static Result cardTransctionStatus() {
		DynamicForm form = DynamicForm.form().bindFromRequest();
		String message = null;
		User localUser = Application.getLocalUser(session());
		Cart cart = null;
		if(localUser == null) {
			String cookie = null;
			if (request().cookies().get("uuid") != null) {
				cookie = request().cookies().get("uuid").value().toString();
				cart = Cart.find.where().eq("uuid", cookie).eq("status", "Open").findUnique();
			}
			
		} else {
			cart = Cart.find.where().eq("user", localUser).eq("status", "Open").findUnique();
		}
		
		if (cart == null) {
			flash(FLASH_ERROR_KEY, "Due to some issues , we are unable to procced with your order. Your Transaction in not initiated");
        	return ok(views.html.transactionResult.render(message));
		}
		List<Item> items = Item.find.where().eq("cart", cart).findList();
		
		Double amount = 0.0;
		for(Item item : items) {
			item.product.refresh();
			amount = amount + (Double)(item.product.Pricetag * item.quantity);
		}
		
		//Stripe.apiKey = "sk_test_8JI50t8ReBHpGxYweWpESYyq";
		String key = Play.application().configuration().getString("stripe.api.key");
		
		Stripe.apiKey = key;
        Map<String, Object> chargeMap = new HashMap<String, Object>();
        chargeMap.put("amount", amount.intValue());
        chargeMap.put("currency", "usd");
        Map<String, Object> cardMap = new HashMap<String, Object>();
        cardMap.put("number", form.get("cardNumber"));
        cardMap.put("exp_month", Integer.parseInt(form.get("month")));
        cardMap.put("exp_year", Integer.parseInt(form.get("year")));
        chargeMap.put("card", cardMap);
        Charge charge = null;
        try {
            charge = Charge.create(chargeMap);
        }catch (com.stripe.exception.CardException e) {
        	flash(FLASH_ERROR_KEY, e.getMessage());
        	return ok(views.html.transactionResult.render(message));
        } 
        catch (StripeException e) {
            e.printStackTrace();
            flash(FLASH_ERROR_KEY, e.getMessage());
            return ok(views.html.transactionResult.render(message));
        } catch (Exception e) {
        	flash(FLASH_ERROR_KEY, e.getMessage());
        	return ok(views.html.transactionResult.render(message));
        }
        
        cart.status = "Transaction Made";		// Update the status of the cart and mark it as closed after successfull transaction
        cart.update();
        message = Json.toJson(charge).get("id").toString();
		return ok(views.html.transactionResult.render(message));
	}
	
	
	public static Result itemCount() {
		User localUser = Application.getLocalUser(session());
		Cart cart = null;
		if(localUser == null) {
			String cookie = null;
			if (request().cookies().get("uuid") != null) {
				cookie = request().cookies().get("uuid").value().toString();
				cart = Cart.find.where().eq("uuid", cookie).eq("status", "Open").findUnique();
			}
			
		} else {
			cart = Cart.find.where().eq("user", localUser).eq("status", "Open").findUnique();
		}
		List<Item> items = Lists.newArrayList();
		List<CartVM> cartVMs = Lists.newArrayList();
		if (cart == null) {
        	return ok(Json.toJson(items));
		}
		items = Item.find.where().eq("cart", cart).findList();
		
		Double amount = 0.0;
		for(Item item : items) {
			item.product.refresh();
			amount = amount + (Double)(item.product.Pricetag * item.quantity);
		}
		for(Item item : items) {
			CartVM caVm = new CartVM(item.id,item.product.productname,item.product.description,item.product.Pricetag,item.quantity,amount,item.product.getproductimagethumb());
			cartVMs.add(caVm);
		}
		
		return ok(Json.toJson(cartVMs));
	}
	
	
	// This function checks the location of the user and decides the visibility of the 
	// post comment section on the single product page
	public static boolean productPostView() {
		try {
			String IPAddress = play.mvc.Controller.request().remoteAddress();
			String userlocation = DInitial.geoipreader.country(InetAddress.getByName(IPAddress)).getCountry().getName();
			System.out.println(userlocation);
		} catch (Exception e){
			e.printStackTrace();
			return true; 
		}	
		return true;
	}
	
	
	private final static String validApiKey;
	private final static String validApiConsumer;
	
	static {
		validApiKey = Play.application().configuration().getString("AkismetApiKey");//System.getProperty("akismetApiKey");
		validApiConsumer = "http://" + request().host();//System.getProperty("akismetConsumer");
		
		if(validApiKey == null || validApiConsumer == null)
			throw new RuntimeException("Both api key and consumer must be specified!");
	}
	
	public static boolean akismetValidationForComment(Long prid, String commentText) throws AkismetException {
		final Akismet akismet = new Akismet(validApiKey, validApiConsumer);	
		
		Contributor author=Application.getContributor(session());
		
		AkismetComment comment = new AkismetComment();
		comment.setUserIp(request().remoteAddress());
		comment.setUserAgent(request().getHeader("User-Agent"));
		comment.setReferrer(request().getHeader("Referer"));
		comment.setPermalink("http://" + request().host()+ "/product/page/" + prid);
		comment.setType("comment");
		comment.setAuthor(author.user.firstName);
		//comment.setAuthorEmail(author.user.email);
		//comment.setAuthorUrl("http://" + request().host()+ "/contributor/page/" + author.user.id);
		comment.setContent(commentText);
		
		if(akismet.checkComment(comment)) {
			return true;
		}
		return false;
	}
	
	public static boolean akismetValidationForBlogComment(Long prid, String commentText) throws AkismetException {
		final Akismet akismet = new Akismet(validApiKey, validApiConsumer);	
		
		Contributor author=Application.getContributor(session());
		
		AkismetComment comment = new AkismetComment();
		comment.setUserIp(request().remoteAddress());
		comment.setUserAgent(request().getHeader("User-Agent"));
		comment.setReferrer(request().getHeader("Referer"));
		comment.setPermalink("http://" + request().host()+ "/blog/page/" + prid);
		comment.setType("comment");
		comment.setAuthor(author.user.firstName);
		//comment.setAuthorEmail(author.user.email);
		//comment.setAuthorUrl("http://" + request().host()+ "/contributor/page/" + author.user.id);
		comment.setContent(commentText);
		
		if(akismet.checkComment(comment)) {
			return true;
		}
		return false;
	}
	
	public static boolean akismetValidationForProductDesc(String descText) throws AkismetException {
		final Akismet akismet = new Akismet(validApiKey, validApiConsumer);	
		
		Contributor author=Application.getContributor(session());
		
		AkismetComment comment = new AkismetComment();
		comment.setUserIp(request().remoteAddress());
		comment.setUserAgent(request().getHeader("User-Agent"));
		comment.setReferrer(request().getHeader("Referer"));
		comment.setType("blog-post");
		comment.setAuthor(author.user.firstName);
		//comment.setAuthorEmail(author.user.email);
		//comment.setAuthorUrl("http://" + request().host()+ "/contributor/page/" + author.user.id);
		comment.setContent(descText);
		
		if(akismet.checkComment(comment)) {
			return true;
		}
		return false;
	}
	// This function checks whether the blog has to be displayed or not 
	/*public static boolean blogPostView(BuzzVM buzz) {
		boolean flag = false;
		Blog blog = Blog.find.byId(buzz.id);
		
		for(int i = 0 ; i < blog.author.user.roles.size() ; i++) {
			if(blog.author.user.roles.get(i).roleName.equals("moderator")) {
				flag = true;
			}
		}
		
		if(flag ==  true) {
			return true;
		} else {
			return false;
		}
	}*/
	
}
