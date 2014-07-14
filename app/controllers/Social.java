package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.*;

import be.objectify.deadbolt.java.actions.SubjectPresent;

import com.restfb.*;
import com.restfb.types.User;

import play.Configuration;
import play.Logger;
import play.libs.WS;
import play.libs.WS.Response;
import play.mvc.*;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import views.html.shop;

import com.feth.play.module.pa.*;
import com.feth.play.module.pa.providers.oauth2.*;

import models.Contributor;
import models.LinkedAccount;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;


public class Social extends Controller {	
	
	public static class ConfStrings{
		public static twitter4j.conf.Configuration twitterConfiguration = (new ConfigurationBuilder()).setOAuthConsumerKey(PlayAuthenticate.getConfiguration().getString("twitter.consumerKey"))
				  .setOAuthConsumerSecret(PlayAuthenticate.getConfiguration().getString("twitter.consumerSecret"))
				  .setOAuthAccessToken(PlayAuthenticate.getConfiguration().getString("twitter.Access_token"))
				  .setOAuthAccessTokenSecret(PlayAuthenticate.getConfiguration().getString("twitter.Access_token_secret")).build();
		public static class FBC{
			public final static String FBCliendID = PlayAuthenticate.getConfiguration().getString("facebook.clientId"),
			FBCliendSecret = PlayAuthenticate.getConfiguration().getString("facebook.clientSecret");
		}
	}
	
	//********************************************FB***********************************
	public static List<NameValuePair> getfbparams(String app_id,String app_secret,String post_auth_url,String code) {
		final List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(OAuth2AuthProvider.Constants.CLIENT_ID, app_id));
		params.add(new BasicNameValuePair(OAuth2AuthProvider.Constants.REDIRECT_URI,post_auth_url));
		params.add(new BasicNameValuePair(OAuth2AuthProvider.Constants.CLIENT_SECRET, app_secret));
		params.add(new BasicNameValuePair(OAuth2AuthProvider.Constants.GRANT_TYPE,
				OAuth2AuthProvider.Constants.AUTHORIZATION_CODE));
		params.add(new BasicNameValuePair(OAuth2AuthProvider.Constants.CODE, code));
		return params;
	}
	
	@SubjectPresent
	public static Result GetFBAccessCode(String returnurl)
	{
		String sessionable="";
		try{sessionable=java.net.URLDecoder.decode(returnurl,"UTF-8");}catch (Exception e) {sessionable=returnurl;}
		session().put("fb.rd", sessionable);
		return redirect("https://www.facebook.com/dialog/oauth?client_id="+ConfStrings.FBC.FBCliendID+"&redirect_uri="+returnurl+"& scope=publish_stream,create_event");
	}
	
	public static String GetFBAccessToken(String code)
	{
		final String app_id =  ConfStrings.FBC.FBCliendID,
				app_secret =  ConfStrings.FBC.FBCliendSecret,
				post_auth_url = session("fb.rd");
		session().remove("fb.rd");
		String token_url = "https://graph.facebook.com/oauth/access_token";
		
		Logger.debug(URLEncodedUtils.format(getfbparams(app_id, app_secret, post_auth_url, code), "UTF-8"));
		
		final Response r = WS.url(token_url)
				.setHeader("Content-Type", "application/x-www-form-urlencoded")
				.post(URLEncodedUtils.format(getfbparams(app_id, app_secret, post_auth_url, code), "UTF-8")).get(PlayAuthenticate.TIMEOUT);
		
		
		if (r.getStatus() >= 400) {
			Logger.debug("400: "+ r.asJson().get("error").get("message").asText() );
			return null;
		} else {
			final String query = r.getBody();
			Logger.debug(query);
			final List<NameValuePair> pairs = URLEncodedUtils.parse(
					URI.create("/?" + query), "utf-8");
			if (pairs.size() < 2) {
				Logger.debug("pairsize: "+ "failed to get access token");
				return null;
			}
			final Map<String, String> m = new HashMap<String, String>(
					pairs.size());
			for (final NameValuePair nameValuePair : pairs) {
				m.put(nameValuePair.getName(), nameValuePair.getValue());
			}
			return m.get("access_token");
		}		
	}
	
	@SubjectPresent
	public static Result getfbfriends()
	{
		String code = request().getQueryString("code");
		if(code==null)
			return badRequest();
		return ok(views.html.account.signup.provider.asktofollowcontactsfb.render(routes.Social.getfbfriendsjson(code).absoluteURL(request())));
	}
	
	@SubjectPresent
	public static Result getfbfriendsjson(String code)
	{
		
		String accessToken= GetFBAccessToken(code);
		if(accessToken==null)
			return badRequest();
		FacebookClient facebookClient =new DefaultFacebookClient(accessToken);
		//User user = facebookClient.fetchObject("me", User.class);
		//Logger.info(String.format(" FirstName:%s & LastName:%s & Name:%s & Email:%s & Id:%s & UserName:%s & Birthday:%s & Email Verified: %b ", user.getFirstName(),user.getLastName(),user.getName(),user.getEmail(),user.getId(),user.getUsername(),user.getBirthday(),user.getVerified()));
		Connection<com.restfb.types.User> myFriends = facebookClient.fetchConnection("me/friends", com.restfb.types.User.class);
		List<User> friends=  myFriends.getData();
		StringBuilder csvfoundids=new StringBuilder("'ABC',");
		for(User u:friends)
		{
			csvfoundids.append("'"+u.getId()+"',");
			//Logger.info(String.format(" FirstName:%s & LastName:%s & Name:%s & Email:%s & Id:%s & UserName:%s ", u.getFirstName(),u.getLastName(),u.getName(),u.getEmail(),u.getId(),u.getUsername()));
		}
		csvfoundids.append("'ABC'");
		return ok(views.html.Templates.json.userlpane.render(Contributor.GetSocialFriends("facebook", csvfoundids.toString()), session()));
		
	}
	
	
	
	
	//******************************************TWITTER******************************************
	
	@SubjectPresent
	public static Result gettwitterfriends(){
		models.User tu=Application.getLocalUser(session());
		LinkedAccount ltu=LinkedAccount.findByProviderKey(tu, "twitter");
		if(ltu==null)
		{
			flash(Application.FLASH_ERROR_KEY,"Sorry!! You are not registered with the Twitter account. Please link a Twitter account in your settings page.");
			return redirect(routes.Signup.importcontacts());
		}
		Logger.info(String.format("Inside get twitter friends"));
		return ok(views.html.account.signup.asktofollowcontacts.render(routes.Social.gettwitterfriendsjson().absoluteURL(request())));
	}
	
	@SubjectPresent
	public static Result gettwitterfriendsjson()
	{
		
		TwitterFactory tf = new TwitterFactory(ConfStrings.twitterConfiguration);
		Twitter twitter = tf.getInstance();
		models.User tu=Application.getLocalUser(session());
		LinkedAccount ltu=LinkedAccount.findByProviderKey(tu, "twitter");
		if(ltu==null)
			return badRequest();
		try{
		Long twitterid=Long.parseLong(ltu.providerUserId);
		Logger.info(String.format(" before pagable response"	));
		PagableResponseList<twitter4j.User> t4jfriends = twitter.getFriendsList(twitterid, -1L);
		Logger.info(String.format(" after pagable response"	));
		Logger.info(String.format(" size of t4jfriends:"+ t4jfriends.size()  +"  "));
		StringBuilder csvfoundids=new StringBuilder("'ABC',");
		for(twitter4j.User u: t4jfriends)
		{
			csvfoundids.append("'"+u.getId()+"',");
			//Logger.info(String.format("id user:'"+u.getId()+"',"));
		}
		csvfoundids.append("'ABC'");
		return ok(views.html.Templates.json.userlpane.render(Contributor.GetSocialFriends("twitter", csvfoundids.toString()), session()));
		//return ok(views.html.Templates.json.userlpane.render( csvfoundids, session() );
		}catch(Exception e) {
		  Logger.debug("Exception occured:- "+e.getMessage());
		  return badRequest();
		}
		
	}
	
	//*******************************************YAHOO*********************************************
	
	public static Result verifyyahoo()
	{
		return ok("Go ahead with verification");
	}
}
