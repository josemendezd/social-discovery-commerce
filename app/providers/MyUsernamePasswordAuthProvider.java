package providers;

import com.feth.play.module.mail.Mailer.Mail.Body;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import controllers.routes;
import models.LinkedAccount;
import models.TokenAction;
import models.TokenAction.Type;
import models.User;
import play.Application;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Call;
import play.mvc.Http.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;


import static play.data.Form.form;

public class MyUsernamePasswordAuthProvider
		extends
		UsernamePasswordAuthProvider<String, MyLoginUsernamePasswordAuthUser, MyUsernamePasswordAuthUser, MyUsernamePasswordAuthProvider.MyLogin, MyUsernamePasswordAuthProvider.MySignup> {

	private static final String SETTING_KEY_VERIFICATION_LINK_SECURE = SETTING_KEY_MAIL
			+ "." + "verificationLink.secure";
	private static final String SETTING_KEY_PASSWORD_RESET_LINK_SECURE = SETTING_KEY_MAIL
			+ "." + "passwordResetLink.secure";
	private static final String SETTING_KEY_LINK_LOGIN_AFTER_PASSWORD_RESET = "loginAfterPasswordReset";

	private static final String EMAIL_TEMPLATE_FALLBACK_LANGUAGE = "en";

	@Override
	protected List<String> neededSettingKeys() {
		final List<String> needed = new ArrayList<String>(
				super.neededSettingKeys());
		needed.add(SETTING_KEY_VERIFICATION_LINK_SECURE);
		needed.add(SETTING_KEY_PASSWORD_RESET_LINK_SECURE);
		needed.add(SETTING_KEY_LINK_LOGIN_AFTER_PASSWORD_RESET);
		return needed;
	}

	public static MyUsernamePasswordAuthProvider getProvider() {
		return (MyUsernamePasswordAuthProvider) PlayAuthenticate
				.getProvider(UsernamePasswordAuthProvider.PROVIDER_KEY);
	}

	public static class MyIdentity {

		public MyIdentity() {
		}

		public MyIdentity(final String email) {
			this.email = email;
		}

		@Required
		@Email
		public String email;

	}

	public static class MyLogin extends MyIdentity
			implements
			com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.UsernamePassword {

		@Required
		@MinLength(5)
		@MaxLength(15)
		public String password;

		@Override
		public String getEmail() {
			return email;
		}

		@Override
		public String getPassword() {
			return password;
		}
	}

	public static class MySignup extends MyLogin {

		@Required
		@MinLength(5)
		public String repeatPassword;

		@Required
		@MinLength(2)
		@MaxLength(50)
		@Pattern(value="[A-Za-z0-9]+")		
		public String name;
		
		
		public String validate() {//check function jsglobals.scala
			if (password == null || !password.equals(repeatPassword)) {
				return Messages
						.get("boozology.password.signup.error.passwords_not_same");
			}
			if(User.findByUserName(name)!=null)
				return "UserName already Taken. Please choose another";
			if(User.findByEmail(email)!=null)
				return "User email already Taken. Please choose another";
			return null;
		}
	}

	public static final Form<MySignup> SIGNUP_FORM = Form.form(MySignup.class);
	public static final Form<MyLogin> LOGIN_FORM = Form.form(MyLogin.class);

	public MyUsernamePasswordAuthProvider(Application app) {
		super(app);
	}

	protected Form<MySignup> getSignupForm() {
		return SIGNUP_FORM;
	}

	protected Form<MyLogin> getLoginForm() {
		return LOGIN_FORM;
	}

	@Override
	protected com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.SignupResult signupUser(final MyUsernamePasswordAuthUser user) {
		final User u = User.findByUsernamePasswordIdentity(user);
		
		if (u != null) {
			
			Logger.info("here user exists ");
			
			/*
			//FOR EMAIL VALIDATION UNCOMMENT THIS BLOCK
			if (u.emailValidated) {
				// This user exists, has its email validated and is active
				Logger.info("here user exists,  validated ");
				return SignupResult.USER_EXISTS;
			} else {
				// this user exists, is active but has not yet validated its
				// email
				Logger.info("here user exists, is active but has not yet validated ");
				return SignupResult.USER_EXISTS_UNVERIFIED;
			}
			*/
			return SignupResult.USER_EXISTS;
		}
		
		// The user either does not exist or is inactive - create a new one
		@SuppressWarnings("unused")
		final User newUser = User.create(user);
		// Usually the email should be verified before allowing login, however
		// if you return
		
		final String token = generateVerificationRecord(newUser);
		//=ctx().request();
	//	Http.Request request = Http.Request.current();
		
		final boolean isSecure = getConfiguration().getBoolean(
				SETTING_KEY_VERIFICATION_LINK_SECURE);
		final String url = routes.Signup.verify(token).absoluteURL(
				play.mvc.Http.Context.current().request(), isSecure);		
		boolean mailRes=false;
			try {
				String myTempUserName;
				if(newUser.firstName==null ){
					myTempUserName=newUser.name;
				}
				else{
					myTempUserName=newUser.firstName+ " "+newUser.lastName;
				}
			 mailRes=MyMadMimiMailer.verifyEmail(myTempUserName, newUser.email,  url);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!mailRes){
				Logger.info("Error: verify email was not sent. method signupUser at MyUserNamePasswordAuthProvider ");
			}
				
			// TODO Auto-generated catch block
		
		
		return SignupResult.USER_CREATED;//NO VALIDATION
		// then the user gets logged in directly
		//return SignupResult.USER_CREATED_UNVERIFIED;//ENABLE FOR VALIDATION
	}

	@Override
	protected com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.LoginResult loginUser(
			final MyLoginUsernamePasswordAuthUser authUser) {
		final User u = User.findByUsernamePasswordIdentity(authUser);
		if (u == null) {
			return LoginResult.NOT_FOUND;
		} else {
			/*//FOR VALIDATION BASED LOGIN
			if (!u.emailValidated) {
				return LoginResult.USER_UNVERIFIED;
			} else {
				for (final LinkedAccount acc : u.linkedAccounts) {
					if (getKey().equals(acc.providerKey)) {
						if (authUser.checkPassword(acc.providerUserId,
								authUser.getPassword())) {
							// Password was correct
							return LoginResult.USER_LOGGED_IN;
						} else {
							// if you don't return here,
							// you would allow the user to have
							// multiple passwords defined
							// usually we don't want this
							return LoginResult.WRONG_PASSWORD;
						}
					}
				}
				return LoginResult.WRONG_PASSWORD;
			}
			*/
			for (final LinkedAccount acc : u.linkedAccounts) {
				if (getKey().equals(acc.providerKey)) {
					if (authUser.checkPassword(acc.providerUserId,
							authUser.getPassword())) {
						// Password was correct
						return LoginResult.USER_LOGGED_IN;
					} else {
						// if you don't return here,
						// you would allow the user to have
						// multiple passwords defined
						// usually we don't want this
						return LoginResult.WRONG_PASSWORD;
					}
				}
			}
			return LoginResult.WRONG_PASSWORD;
		}
	}

	@Override
	protected Call userExists(final UsernamePasswordAuthUser authUser) {
		return routes.Signup.exists();
	}

	@Override
	protected Call userUnverified(final UsernamePasswordAuthUser authUser) {
		return routes.Signup.unverified();
	}

	@Override
	protected MyUsernamePasswordAuthUser buildSignupAuthUser(
			final MySignup signup, final Context ctx) {
		return new MyUsernamePasswordAuthUser(signup);
	}

	@Override
	protected MyLoginUsernamePasswordAuthUser buildLoginAuthUser(
			final MyLogin login, final Context ctx) {
		return new MyLoginUsernamePasswordAuthUser(login.getPassword(),
				login.getEmail());
	}
	

	@Override
	protected MyLoginUsernamePasswordAuthUser transformAuthUser(final MyUsernamePasswordAuthUser authUser, final Context context) {
		return new MyLoginUsernamePasswordAuthUser(authUser.getEmail());
	}

	@Override
	protected String getVerifyEmailMailingSubject(
			final MyUsernamePasswordAuthUser user, final Context ctx) {
		return Messages.get("boozology.password.verify_signup.subject");
	}

	@Override
	protected String onLoginUserNotFound(final Context context) {
		context.flash()
				.put(controllers.Application.FLASH_ERROR_KEY,
						Messages.get("boozology.password.login.unknown_user_or_pw"));
		return super.onLoginUserNotFound(context);
	}

	@Override
	protected Body getVerifyEmailMailingBody(final String token,
			final MyUsernamePasswordAuthUser user, final Context ctx) {

		final boolean isSecure = getConfiguration().getBoolean(
				SETTING_KEY_VERIFICATION_LINK_SECURE);
		final String url = routes.Signup.verify(token).absoluteURL(
				ctx.request(), isSecure);

		final Lang lang = Lang.preferred(ctx.request().acceptLanguages());
		final String langCode = lang.code();

		final String html = getEmailTemplate(
				"views.html.account.signup.email.verify_email", langCode, url,
				token, user.getName(), user.getEmail());
		final String text = getEmailTemplate(
				"views.txt.account.signup.email.verify_email", langCode, url,
				token, user.getName(), user.getEmail());

		return new Body(text, html);
	}

	private static String generateToken() {
		return UUID.randomUUID().toString();
	}

	@Override
	protected String generateVerificationRecord(
			final MyUsernamePasswordAuthUser user) {
		return generateVerificationRecord(User.findByAuthUserIdentity(user));
	}

	protected String generateVerificationRecord(final User user) {
		final String token = generateToken();
		// Do database actions, etc.
		TokenAction.create(Type.EMAIL_VERIFICATION, token, user);
		return token;
	}

	protected String generatePasswordResetRecord(final User u) {
		final String token = generateToken();
		TokenAction.create(Type.PASSWORD_RESET, token, u);
		return token;
	}

	protected String getPasswordResetMailingSubject(final User user,
			final Context ctx) {
		return Messages.get("boozology.password.reset_email.subject");
	}

	protected Body getPasswordResetMailingBody(final String token,
			final User user, final Context ctx) {

		final boolean isSecure = getConfiguration().getBoolean(
				SETTING_KEY_PASSWORD_RESET_LINK_SECURE);
		final String url = routes.Signup.resetPassword(token).absoluteURL(
				ctx.request(), isSecure);

		final Lang lang = Lang.preferred(ctx.request().acceptLanguages());
		final String langCode = lang.code();

		final String html = getEmailTemplate(
				"views.html.account.email.password_reset", langCode, url,
				token, user.name, user.email);
		final String text = getEmailTemplate(
				"views.txt.account.email.password_reset", langCode, url, token,
				user.name, user.email);

		return new Body(text, html);
	}

	public void sendPasswordResetMailing(final User user, final Context ctx) {
		final String token = generatePasswordResetRecord(user);
		//final String subject = getPasswordResetMailingSubject(user, ctx);
		//final Body body = getPasswordResetMailingBody(token, user, ctx);
		//mailer.sendMail(subject, body, getEmailName(user));
		
		final boolean isSecure = getConfiguration().getBoolean(
				SETTING_KEY_VERIFICATION_LINK_SECURE);
		final String url = routes.Signup.resetPassword(token).absoluteURL(
				ctx.request(), isSecure);
		
		
		//Logger.info(" getEmailName " + getEmailName(user));
		
		String myTempUserName ;
		if(user.firstName==null){
			myTempUserName=user.name;
		}
		else{
			myTempUserName=user.firstName+ " "+user.lastName;
		}
		
		try {
			boolean mailRes=MyMadMimiMailer.resetPassword(myTempUserName, user.email,  url);
			Logger.info("after sending email result: "+mailRes);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	public boolean isLoginAfterPasswordReset() {
		return getConfiguration().getBoolean(
				SETTING_KEY_LINK_LOGIN_AFTER_PASSWORD_RESET);
	}

	protected String getVerifyEmailMailingSubjectAfterSignup(final User user,
			final Context ctx) {
		return Messages.get("boozology.password.verify_email.subject");
	}

	protected String getEmailTemplate(final String template,
			final String langCode, final String url, final String token,
			final String name, final String email) {
		Class<?> cls = null;
		String ret = null;
		try {
			cls = Class.forName(template + "_" + langCode);
		} catch (ClassNotFoundException e) {
			Logger.warn("Template: '"
					+ template
					+ "_"
					+ langCode
					+ "' was not found! Trying to use English fallback template instead.");
		}
		if (cls == null) {
			try {
				cls = Class.forName(template + "_"
						+ EMAIL_TEMPLATE_FALLBACK_LANGUAGE);
			} catch (ClassNotFoundException e) {
				Logger.error("Fallback template: '" + template + "_"
						+ EMAIL_TEMPLATE_FALLBACK_LANGUAGE
						+ "' was not found either!");
			}
		}
		if (cls != null) {
			Method htmlRender = null;
			try {
				htmlRender = cls.getMethod("render", String.class,
						String.class, String.class, String.class);
				ret = htmlRender.invoke(null, url, token, name, email)
						.toString();

			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	protected Body getVerifyEmailMailingBodyAfterSignup(final String token,
			final User user, final Context ctx) {

		final boolean isSecure = getConfiguration().getBoolean(
				SETTING_KEY_VERIFICATION_LINK_SECURE);
		final String url = routes.Signup.verify(token).absoluteURL(
				ctx.request(), isSecure);

		final Lang lang = Lang.preferred(ctx.request().acceptLanguages());
		final String langCode = lang.code();

		final String html = getEmailTemplate(
				"views.html.account.email.verify_email", langCode, url, token,
				user.name, user.email);
		final String text = getEmailTemplate(
				"views.txt.account.email.verify_email", langCode, url, token,
				user.name, user.email);

		return new Body(text, html);
	}

	public void sendVerifyEmailMailingAfterSignup(final User user,
			final Context ctx) {

		final String subject = getVerifyEmailMailingSubjectAfterSignup(user,
				ctx);
		final String token = generateVerificationRecord(user);

		
		final boolean isSecure = getConfiguration().getBoolean(
				SETTING_KEY_VERIFICATION_LINK_SECURE);
		final String url = routes.Signup.verify(token).absoluteURL(
				play.mvc.Http.Context.current().request(), isSecure);		
		
			try {
				
						String myTempUserName;
						if(user.firstName.isEmpty()){
							myTempUserName=user.name;
						}
						else{
							myTempUserName=user.firstName+ " "+user.lastName;
						}
					boolean mailRes=MyMadMimiMailer.verifyEmail(myTempUserName, user.email,  url);
					//Logger.info("after sending email result: "+mailRes);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
	}

	private String getEmailName(final User user) {
		return getEmailName(user.email, user.name);
	}
}
