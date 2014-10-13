/**
 * 
 */
package providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import models.User;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.yaml.snakeyaml.Yaml;

import play.Logger;

/**
 * @author J Mendez
 *
 */
public class MyMadMimiMailer {

    public static final String MM_API_USER = "madmimi.api.username";
    public static final String MM_API_KEY = "madmimi.api.key";

	
    /**
     * 
     * @throws Exception
     */
    public static void testmailmadmimi() throws Exception {
    	//Logger.info("before sending testmailmadmimi "  );

        HttpClient httpClient =   HttpClients.createDefault();

        HttpPost httppost = new HttpPost("https://api.madmimi.com/mailer");
        ArrayList<NameValuePair> postParameters;
        postParameters = new ArrayList<NameValuePair>();
        
        postParameters.add(new BasicNameValuePair("username", MM_API_USER));
        postParameters.add(new BasicNameValuePair("api_key", MM_API_KEY));
        postParameters.add(new BasicNameValuePair("promotion_name", "Untitled Promotion"));
        postParameters.add(new BasicNameValuePair("recipient", "Jose Mendez <support@boozology.com>"));
        postParameters.add(new BasicNameValuePair("subject", "Mad mimi api testing from boozology"));
        postParameters.add(new BasicNameValuePair("from", "boozologycom@gmail.com"));
        String body = "--- \nname: Some YAML data\n";
        postParameters.add(new BasicNameValuePair("body", body));
        httppost.setEntity(new UrlEncodedFormEntity(postParameters));
        HttpResponse response = httpClient.execute( httppost );
    
    //    String responseString = new BasicResponseHandler().handleResponse(response);
      //  System.out.println(responseString);
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8")  ;
       // Logger.info("testmailmadmimi responseString :"+responseString);
    }

    public static boolean verifyEmail (String username,  String useremail,  String url) throws Exception {
    	Logger.info("before sending testmailmadmimi");

        HttpClient httpClient =   HttpClients.createDefault();

        HttpPost httppost = new HttpPost("https://api.madmimi.com/mailer");
        ArrayList<NameValuePair> postParameters;
        postParameters = new ArrayList<NameValuePair>();
   
      
  
        
        postParameters.add(new BasicNameValuePair("username", MM_API_USER));
        postParameters.add(new BasicNameValuePair("api_key", MM_API_KEY));
        postParameters.add(new BasicNameValuePair("promotion_name", "Verify Email"));
        postParameters.add(new BasicNameValuePair("recipient", username+"<"+useremail+">"));
        postParameters.add(new BasicNameValuePair("subject", "Verify your boozology account‏"));
        postParameters.add(new BasicNameValuePair("from", "no_reply@boozology.com"));
        Yaml yaml = new Yaml();
        //Object obj = yaml.load("nusername: "+username+"\nb: 2\nc:\n  - aaa\n  - bbb");
        Map<String, String> map = new HashMap<String, String>();
      
        map.put("username", username);
        
        map.put("useremail", useremail);
        map.put("verifylink", url  );
        String output = yaml.dump(map);
        //System.out.println(output);
      //  String body = "--- \r\nusername:"+username+"\r\n";
        postParameters.add(new BasicNameValuePair("body", output));
        httppost.setEntity(new UrlEncodedFormEntity(postParameters));
        HttpResponse response = httpClient.execute( httppost );
    
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8")  ;
        Logger.info("verifyEmail responseString :"+responseString);
        
        return true;
    }

    
    
    public static boolean resetPassword (String username, String useremail,  String url) throws Exception {
    //	Logger.info("before sending testmailmadmimi");

        HttpClient httpClient =   HttpClients.createDefault();

        HttpPost httppost = new HttpPost("https://api.madmimi.com/mailer");
        ArrayList<NameValuePair> postParameters;
        postParameters = new ArrayList<NameValuePair>();

        postParameters.add(new BasicNameValuePair("username", MM_API_USER));
        postParameters.add(new BasicNameValuePair("api_key", MM_API_KEY));
        postParameters.add(new BasicNameValuePair("promotion_name", "Reset Password"));
        postParameters.add(new BasicNameValuePair("recipient", username+"<"+useremail+">"));
        postParameters.add(new BasicNameValuePair("subject", "Reset your password"));
        postParameters.add(new BasicNameValuePair("from", "no_reply@boozology.com"));
        Yaml yaml = new Yaml();
        //Object obj = yaml.load("nusername: "+username+"\nb: 2\nc:\n  - aaa\n  - bbb");
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("useremail", useremail);
        map.put("resetpassword", url  );
        String output = yaml.dump(map);
        //System.out.println(output);
      //  String body = "--- \r\nusername:"+username+"\r\n";
        postParameters.add(new BasicNameValuePair("body", output));
        httppost.setEntity(new UrlEncodedFormEntity(postParameters));
        HttpResponse response = httpClient.execute( httppost );
    
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8")  ;
    //    Logger.info("verifyEmail responseString :"+responseString);
        
        return true;
    }   
    
    
    
    
  
    public static boolean sendMyMailRecommendProduct (Map<String, String> map,  String mailto, String username ) throws Exception {
    	Logger.info("before sending sendMyNotification");

        HttpClient httpClient =   HttpClients.createDefault();

        HttpPost httppost = new HttpPost("https://api.madmimi.com/mailer");
        ArrayList<NameValuePair> postParameters;
        postParameters = new ArrayList<NameValuePair>();
      
       
        
        postParameters.add(new BasicNameValuePair("username", MM_API_USER));
        postParameters.add(new BasicNameValuePair("api_key", MM_API_KEY));
       
	        postParameters.add(new BasicNameValuePair("promotion_name", "Notify RecommendProduct"));
	        postParameters.add(new BasicNameValuePair("recipient","<"+mailto+">"));
	        postParameters.add(new BasicNameValuePair("subject",  "New recommendation from " + username +" on Boozology!"));
	        postParameters.add(new BasicNameValuePair("from", "no_reply@boozology.com"));
                
        Yaml yaml = new Yaml();
        //Object obj = yaml.load("nusername: "+username+"\nb: 2\nc:\n  - aaa\n  - bbb");
       // Map<String, String> map = new HashMap<String, String>();
       // map.put("username", username);
       // map.put("useremail", useremail);
       // map.put("verifylink", url  );
        
     //   Logger.info(" Inside method :  sendMyMailRecommendProduct ");
        
        String output = yaml.dump(map);
        //System.out.println(output);
      //  String body = "--- \r\nusername:"+username+"\r\n";
        postParameters.add(new BasicNameValuePair("body", output));
        httppost.setEntity(new UrlEncodedFormEntity(postParameters));
        HttpResponse response = httpClient.execute( httppost );
    
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8")  ;
      //  Logger.info("verifyEmail responseString :"+responseString);
        
        return true;
    }
    
    
    public static boolean sendSupportMail (String username, String useremail,  String text ) throws Exception {

        HttpClient httpClient =   HttpClients.createDefault();

        HttpPost httppost = new HttpPost("https://api.madmimi.com/mailer");
        ArrayList<NameValuePair> postParameters;
        postParameters = new ArrayList<NameValuePair>();

        postParameters.add(new BasicNameValuePair("username", MM_API_USER));
        postParameters.add(new BasicNameValuePair("api_key", MM_API_KEY));
        postParameters.add(new BasicNameValuePair("promotion_name", "Support mail"));
        postParameters.add(new BasicNameValuePair("recipient", "Support <support@boozology.com>"));
        postParameters.add(new BasicNameValuePair("subject", "Support:"+useremail));
        postParameters.add(new BasicNameValuePair("from", "no_reply@boozology.com"));
        Yaml yaml = new Yaml();
        //Object obj = yaml.load("nusername: "+username+"\nb: 2\nc:\n  - aaa\n  - bbb");
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("useremail", useremail);
        map.put("text", text  );
        String output = yaml.dump(map);
        //System.out.println(output);
      //  String body = "--- \r\nusername:"+username+"\r\n";
        postParameters.add(new BasicNameValuePair("body", output));
        httppost.setEntity(new UrlEncodedFormEntity(postParameters));
        HttpResponse response = httpClient.execute( httppost );
    
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8")  ;
        Logger.info("verifyEmail responseString :"+responseString);
        
        return true;
    }
    
    
    
    public static boolean sendMyNotification (Map<String, String> map, User user, String myNotification ) throws Exception {
    	Logger.info("before sending sendMyNotification");

        HttpClient httpClient =   HttpClients.createDefault();

        HttpPost httppost = new HttpPost("https://api.madmimi.com/mailer");
        ArrayList<NameValuePair> postParameters;
        postParameters = new ArrayList<NameValuePair>();
        
        String username ;
        if (user.firstName == null){
        	username=user.name;
        }
        else{
         username= user.firstName + " "+ user.lastName;
        }
        String useremail=user.email;
        
        postParameters.add(new BasicNameValuePair("username", MM_API_USER));
        postParameters.add(new BasicNameValuePair("api_key", MM_API_KEY));
        
        if(myNotification.equals("CommentsPr")){
	        postParameters.add(new BasicNameValuePair("promotion_name", "Notify CommentsPr"));
	        postParameters.add(new BasicNameValuePair("recipient", username+"<"+useremail+">"));
	        postParameters.add(new BasicNameValuePair("subject",  "New comment on Boozology!"));
	        postParameters.add(new BasicNameValuePair("from", "no_reply@boozology.com"));
        }
        if (myNotification.equals("CommentsCo")){
    	        postParameters.add(new BasicNameValuePair("promotion_name", "Notify CommentsCo"));
    	        postParameters.add(new BasicNameValuePair("recipient", username+"<"+useremail+">"));
    	        postParameters.add(new BasicNameValuePair("subject",  "New comment on Boozology!"));
    	        postParameters.add(new BasicNameValuePair("from", "no_reply@boozology.com"));
            }
        if (myNotification.equals("SOFollows")){
	        postParameters.add(new BasicNameValuePair("promotion_name", "Notify SOFollows"));
	        postParameters.add(new BasicNameValuePair("recipient", username+"<"+useremail+">"));
	        postParameters.add(new BasicNameValuePair("subject",  "New follower on Boozology!"));
	        postParameters.add(new BasicNameValuePair("from", "no_reply@boozology.com"));
        }
        
        if (myNotification.equals("SORecommends")){
	        postParameters.add(new BasicNameValuePair("promotion_name", "Notify SORecommends"));
	        postParameters.add(new BasicNameValuePair("recipient", username+"<"+useremail+">"));
	        postParameters.add(new BasicNameValuePair("subject",  "New recommendation on Boozology!"));
	        postParameters.add(new BasicNameValuePair("from", "no_reply@boozology.com"));
        }        
        Yaml yaml = new Yaml();
        //Object obj = yaml.load("nusername: "+username+"\nb: 2\nc:\n  - aaa\n  - bbb");
       // Map<String, String> map = new HashMap<String, String>();
       // map.put("username", username);
       // map.put("useremail", useremail);
       // map.put("verifylink", url  );
        
        
        
        String output = yaml.dump(map);
        //System.out.println(output);
      //  String body = "--- \r\nusername:"+username+"\r\n";
        postParameters.add(new BasicNameValuePair("body", output));
        httppost.setEntity(new UrlEncodedFormEntity(postParameters));
        HttpResponse response = httpClient.execute( httppost );
    
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8")  ;
  //      Logger.info("verifyEmail responseString :"+responseString);
        
        return true;
    }
	
}
