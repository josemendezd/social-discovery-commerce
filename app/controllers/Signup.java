package controllers;

import java.util.*;
import java.util.Map.Entry;
import java.util.Map.*;
import java.util.concurrent.Callable;

import models.*;
import models.Admin.Ipblacklist;
import models.Notifications.UserSubscriptions;
import models.TokenAction.Type;
import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Akka;
import play.mvc.Controller;
import play.mvc.Result;
import providers.MyLoginUsernamePasswordAuthUser;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyIdentity;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthUser;
import scala.concurrent.Future;
import views.html.*;
import views.html.Templates.su.signin;
import views.html.account.ask_link;
import views.html.account.signup.*;

import be.objectify.deadbolt.java.actions.SubjectPresent;

import com.feth.play.module.pa.PlayAuthenticate;

import static play.data.Form.form;

public class Signup extends Controller {

	public static class PasswordReset extends Account.PasswordChange {

		public PasswordReset() {
		}

		public PasswordReset(final String token) {
			this.token = token;
		}

		public String token;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}
	}

	private static final Form<PasswordReset> PASSWORD_RESET_FORM = form(PasswordReset.class);

	public static Result unverified() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		return ok(unverified.render());
	}

	private static final Form<MyIdentity> FORGOT_PASSWORD_FORM = form(MyIdentity.class);

	public static Result forgotPassword(final String email) {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		Form<MyIdentity> form = FORGOT_PASSWORD_FORM;
		if (email != null && !email.trim().isEmpty()) {
			form = FORGOT_PASSWORD_FORM.fill(new MyIdentity(email));
		}
		return ok(password_forgot.render(form));
	}

	public static Result doForgotPassword() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MyIdentity> filledForm = FORGOT_PASSWORD_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not fill in his/her email
			return badRequest(password_forgot.render(filledForm));
		} else {
			// The email address given *BY AN UNKNWON PERSON* to the form - we
			// should find out if we actually have a user with this email
			// address and whether password login is enabled for him/her. Also
			// only send if the email address of the user has been verified.
			final String email = filledForm.get().email;

			// We don't want to expose whether a given email address is signed
			// up, so just say an email has been sent, even though it might not
			// be true - that's protecting our user privacy.
			

			final User user = User.findByEmail(email);
			if (user != null) {
				// yep, we have a user with this email that is active - we do
				// not know if the user owning that account has requested this
				// reset, though.
				final MyUsernamePasswordAuthProvider provider = MyUsernamePasswordAuthProvider
						.getProvider();
				// User exists
				//By Me ---Send in any case
				flash(Application.FLASH_MESSAGE_KEY,Messages.get("boozology.reset_password.message.instructions_sent",email));
				provider.sendPasswordResetMailing(user, ctx()); 
				/*
				if (user.emailValidated) {
					flash(Application.FLASH_MESSAGE_KEY,
							Messages.get(
									"boozology.reset_password.message.instructions_sent",
									email));
					provider.sendPasswordResetMailing(user, ctx());
					// In case you actually want to let (the unknown person)
					// know whether a user was found/an email was sent, use,
					// change the flash message
				} else {
					// We need to change the message here, otherwise the user
					// does not understand whats going on - we should not verify
					// with the password reset, as a "bad" user could then sign
					// up with a fake email via OAuth and get it verified by an
					// a unsuspecting user that clicks the link.
					flash(Application.FLASH_MESSAGE_KEY,
							Messages.get("boozology.reset_password.message.email_not_verified"));

					// You might want to re-send the verification email here...
					provider.sendVerifyEmailMailingAfterSignup(user, ctx());
				}
				*/
			}
			else{
				flash(Application.FLASH_ERROR_KEY,
						Messages.get(
								"boozology.reset_password.message.notregistered",
								email));
				return badRequest(password_forgot.render(filledForm));
			}

			return redirect(routes.Application.shop());
		}
	}

	/**
	 * Returns a token object if valid, null if not
	 * 
	 * @param token
	 * @param type
	 * @return
	 */
	private static TokenAction tokenIsValid(final String token, final Type type) {
		TokenAction ret = null;
		if (token != null && !token.trim().isEmpty()) {
			final TokenAction ta = TokenAction.findByToken(token, type);
			if (ta != null && ta.isValid()) {
				ret = ta;
			}
		}

		return ret;
	}

	public static Result resetPassword(final String token) {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final TokenAction ta = tokenIsValid(token, Type.PASSWORD_RESET);
		if (ta == null) {
			return badRequest(no_token_or_invalid.render());
		}

		return ok(password_reset.render(PASSWORD_RESET_FORM
				.fill(new PasswordReset(token))));
	}

	public static Result doResetPassword() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<PasswordReset> filledForm = PASSWORD_RESET_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(password_reset.render(filledForm));
		} else {
			final String token = filledForm.get().token;
			final String newPassword = filledForm.get().password;

			final TokenAction ta = tokenIsValid(token, Type.PASSWORD_RESET);
			if (ta == null) {
				return badRequest(no_token_or_invalid.render());
			}
			final User u = ta.targetUser;
			try {
				// Pass true for the second parameter if you want to
				// automatically create a password and the exception never to
				// happen
				com.feth.play.module.pa.controllers.Authenticate.noCache(response());
				u.changePassword(new MyUsernamePasswordAuthUser(newPassword),true);
				//u.resetPassword(new MyUsernamePasswordAuthUser(newPassword),false);
			} catch (final RuntimeException re) {
				flash(Application.FLASH_MESSAGE_KEY,
						Messages.get("boozology.reset_password.message.no_password_account"));
				return redirect(routes.Application.login());
			}
			final boolean login = MyUsernamePasswordAuthProvider.getProvider()
					.isLoginAfterPasswordReset();
			if (login) {
				// automatically log in
				flash(Application.FLASH_MESSAGE_KEY,
						Messages.get("boozology.reset_password.message.success.auto_login"));

				return PlayAuthenticate.loginAndRedirect(ctx(),
						new MyLoginUsernamePasswordAuthUser(u.email));
			} else {
				// send the user to the login page
				flash(Application.FLASH_MESSAGE_KEY,
						Messages.get("boozology.reset_password.message.success.manual_login"));
			}
			return redirect(routes.Application.login());
		}
	}

	public static Result oAuthDenied(final String getProviderKey) {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		return ok(oAuthDenied.render(getProviderKey));
	}

	public static Result exists() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		return ok(exists.render());
	}

	public static Result verify(final String token) {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final TokenAction ta = tokenIsValid(token, Type.EMAIL_VERIFICATION);
		if (ta == null) {
			return badRequest(no_token_or_invalid.render());
		}
		final String email = ta.targetUser.email;
		User.verify(ta.targetUser);
		flash(Application.FLASH_MESSAGE_KEY,
				Messages.get("boozology.verify_email.success", email));
		if (Application.getLocalUser(session()) != null) {
			return redirect(routes.Signup.PostSignUp());
		} else {
			return redirect(routes.Application.login());
		}
	}
	
	
	
	//================================INSERTED LATER=============================================
	//============================FLOW CHART POST SIGNUP=========================================
	//1. If user has signed in through any non email-providing agent,ask email ID
	//1A.If some body else has used the users email-ID, ask user to verify the ID
	
	@SubjectPresent
	public static Result PostSignUp()
	{
		
		Logger.info(String.format("++++ INSIDE POST SIGN UP ++++"));
		
		User signingin=Application.getLocalUser(session());
		String redirect_back=session().get(DInitial.REDIRECT_BACK_URL);
		String isajax=session().get("isajax");
		if(isajax==null)
			isajax="no";
		
		if(signingin.email==null)
		{
			Logger.info(String.format("++++ EMAIL = NULL ++++"));
			session().clear();
			session().put("clearid", signingin.id.toString());
			
			Logger.info(String.format("id to string: -" + signingin.id.toString() + "-" ));
			
			if(isajax.equals("no")){
				Logger.info(String.format("++++ ISAJAX EQUALS NO ++++"));
				return getemailform();//TODO:Redirect to Enter a valid Email-ID,and to re-login after the process,in parameter use routes.application.login;
			
			}
			
			}
		if(isajax.equals("yes"))
		{
			Logger.info(String.format("++++ ISAJAX EQUALS YES ++++"));
			Logger.info(isajax);
			session().remove("isajax");
			com.fasterxml.jackson.databind.node.ObjectNode redirectnode =play.libs.Json.newObject();
			redirectnode.put("redirectto", routes.Signup.PostSignUp().url());
			return ok(redirectnode);
		}
		
		if(redirect_back==null)
			redirect_back=routes.Application.index().url();
		else
			session().remove(DInitial.REDIRECT_BACK_URL);
		
		if(Ipblacklist.isblacklisted(request().remoteAddress()))
		{
			session().clear();
			return forbidden("This IP is blacklisted.Please contact support");
		}
		
		if(signingin.registerstatus==DInitial.SIGNUP_STAGE.CLEARED_ALL_STAGES)
		{
			if(Play.isDev())
				for(Entry<String, String> entry:session().entrySet())
				{
					Logger.info(String.format("SESSION - KEY:%s\tVALUE:%s", entry.getKey(),entry.getValue()));
				}
			return redirect(redirect_back);
		}
		
		switch(signingin.registerstatus)
		{
		case DInitial.SIGNUP_STAGE.JUST_REGISTERED:
			signingin.updatestatus(DInitial.SIGNUP_STAGE.CHOOSE_CATEGORY);
			if(Play.isProd())
			{
				MyUsernamePasswordAuthProvider.getProvider().sendVerifyEmailMailingAfterSignup(signingin, ctx());
				flash(Application.FLASH_MESSAGE_KEY, "An email has been sent for verification.Please consider to verify to get full access.");
			}
			return redirect(routes.Signup.walkthrough());
		case DInitial.SIGNUP_STAGE.CHOOSE_CATEGORY:
			return redirect(routes.Signup.selectcategory());
		case DInitial.SIGNUP_STAGE.CHOOSE_INFLUENCERS:
			return redirect(routes.Signup.selectinfluencers());
		case DInitial.SIGNUP_STAGE.FETCH_CONTACTS:
			return redirect(routes.Signup.importcontacts());
		case DInitial.SIGNUP_STAGE.FOLLOW_FRIENDS:
			return redirect(routes.Signup.selectcontacts());
		default:
			signingin.updatestatus(DInitial.SIGNUP_STAGE.CHOOSE_CATEGORY);
			return walkthrough();
		}
	}
	
	//GET REQUESTS
	
	public static Result getemailform()
	{
		Logger.info(String.format("++++ getemailform ++++"));
		
		flash(Application.FLASH_ERROR_KEY,"Your Signup provider doesn't provide email-id, please enter one and relogin.");
		return ok(views.html.account.signup.askusersemail.render());
	}

	@SubjectPresent	
	public static Result selectcategory ()
	{
		return ok(views.html.account.signup.askcategory.render(Category.TopLevelCategories(),Application.getLocalUser(session())));
	}
	
	@SubjectPresent	
	public static Result walkthrough ()
	{
		return ok(views.html.account.signup.walkthrough.render(Application.getLocalUser(session())));
	}
	
	

	@SubjectPresent	
	public static Result selectinfluencers()
	{
		return ok(views.html.account.signup.chooseinfluencers.render(Application.getLocalUser(session())));
	}
	
	@SubjectPresent	
	public static Result selectinfluencersjson(int page,int pagesize)
	{
		return ok(views.html.Templates.json.userpane.render(Contributor.GetTopInfluencers(page, pagesize, Application.getContributor(session())),session()));
	}

	@SubjectPresent	
	public static Result importcontacts()
	{
		return ok(views.html.account.signup.asktoimportcontacts.render());
	}

	@SubjectPresent	
	public static Result selectcontacts()
	{
		User signingin=Application.getLocalUser(session());
		signingin.updatestatus(DInitial.SIGNUP_STAGE.CLEARED_ALL_STAGES);
		flash(Application.FLASH_MESSAGE_KEY, "Love what you see? Click the \"Like\" heart. Others will like you back!");
		return redirect(routes.Useract.MyWatchList());
	}
	
	
	//This one is in response to post from email-entering-form
	
	public static Result GetUsersEmail()
	{
		User signingin=null;
		try{
			signingin=User.find.byId(Long.parseLong(session("clearid")));
		}catch (Exception e) {}		
		if(signingin==null)
		{
			flash(Application.FLASH_ERROR_KEY,"Feels like there has been tempering with cookies or cookies are disabled.Please restart login process.");
			return redirect(routes.Application.login());
		}
		if(signingin.email!=null)
			return redirect(routes.Application.login());
		final Form<MyIdentity> emailForm = play.data.Form.form(MyUsernamePasswordAuthProvider.MyIdentity.class).bindFromRequest();		
	    flash().clear();
	    if (emailForm.hasErrors()) {
			flash(Application.FLASH_ERROR_KEY, "Please, Enter valid Email ID!!!");
			return badRequest(views.html.account.signup.askusersemail.render());//Renter Email.render
		} else {
			String email = emailForm.get().email;
			User registeredearlier=User.findByEmail(email);
			if(registeredearlier!=null)
			{				
				flash(Application.FLASH_ERROR_KEY, "Sorry a user already registered with this email,if you still claim that its yours, then please use forgot password on login page.");
				signingin.updatestatus(DInitial.SIGNUP_STAGE.EMAIL_TO_BE_ENTERED );
				signingin.emailValidated = true;
				signingin.update();
				return badRequest(views.html.account.signup.askusersemail.render());//Redirect to Enter a valid Email-ID with a link to forgot password				
			}	
			signingin.setusersemail(email);
			signingin.updatestatus(DInitial.SIGNUP_STAGE.JUST_REGISTERED );
			signingin.emailValidated = true;
			signingin.update();
		}
	    session().remove("clearid");
	    flash(Application.FLASH_ERROR_KEY,"Email is now configured, Please try to login again");
		return  redirect(routes.Application.login());
	}
	
	@SubjectPresent	//TODO: Port method on Akka.
	public static Result SetUsersPreferredCategory()
	{
		User signingin=Application.getLocalUser(session());
		DynamicForm bindedForm = play.data.Form.form().bindFromRequest();
		
		Set<Category> categories =new HashSet<Category>();
		List<Category> toplevel = Category.TopLevelCategories();
		Map<String,String> ul= bindedForm.data();
		for (Entry<String,String> entry : ul.entrySet()) {
			try{
				Category c= Category.find.byId(Long.parseLong(entry.getValue()));
				if(c!=null && toplevel.contains(c))
					categories.add(c);
			}catch (Exception e){
				e.printStackTrace();
				categories.clear();
				break;
			}
		}
		if(categories.size()<1)
		{
			categories.clear();
			categories.addAll(toplevel);
		}
		signingin.savecategories(categories);
		if(signingin.registerstatus==DInitial.SIGNUP_STAGE.CLEARED_ALL_STAGES)
		{
			flash(Application.FLASH_MESSAGE_KEY, "Choices Saved.");
				return ok(profile.render(signingin));
			
		}
		signingin.updatestatus(DInitial.SIGNUP_STAGE.CHOOSE_INFLUENCERS);
		return redirect(routes.Signup.selectinfluencers());//
		
	}
	
	@SubjectPresent	//TODO: Port method on Akka.future.
	public static Result SetUsersInfluencers(String mode)
	{
		User signingin=Application.getLocalUser(session());
		if(signingin.registerstatus==DInitial.SIGNUP_STAGE.CHOOSE_INFLUENCERS && mode.equals("skip"))
			signingin.setdefaultinfluencers();
		
		if(signingin.registerstatus==DInitial.SIGNUP_STAGE.CLEARED_ALL_STAGES && mode.equals("done"))
		{
			flash(Application.FLASH_MESSAGE_KEY, "Choices Saved.");
			return ok(userprofile.render(signingin.contrib,DInitial.USER_RELATION_QUERY.FOLLOWING,session()));
		}
		signingin.updatestatus(DInitial.SIGNUP_STAGE.FOLLOW_FRIENDS);
		return redirect(routes.Signup.importcontacts());		
	}
	
	@SubjectPresent	//TODO: Port method on Akka.
	public static Result SetUsersContactList(String influencers)
	{
		User signingin=Application.getLocalUser(session());
		
		if(influencers.equals("chooseall"))
		{
			flash(Application.FLASH_MESSAGE_KEY, "Choices Saved.");
			signingin.setdefaultinfluencers();			
		}
		signingin.updatestatus(DInitial.SIGNUP_STAGE.CLEARED_ALL_STAGES);
		return redirect(routes.Useract.MyWatchList());//TODO:Go the mail invitation phase.		
	}
	
}
