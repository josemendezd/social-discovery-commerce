package controllers;

import static play.data.Form.form;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import controllers.Useract.PAGE_CONTENT_FORM;
import play.data.Form;
import play.data.validation.Constraints.*;
import play.mvc.*;
import views.html.spages.*;


public class SPages extends Controller {	

	public static Result aboutus()
	{
		return  ok(Aboutus.render());
	}
	
	public static Result contactus()
	{
		return  ok(Contactus.render(CONTACTUS_FORM));
	}
	
	public static Result helpme()
	{
		return  ok(Help.render());
	}
	
	public static Result community()
	{
		return  ok(Community.render());
	}
	
	public static Result developer()
	{
		return  ok(Developer.render());
	}
	
	public static Result pncp()
	{
		return  ok(PnCP.render());
	}
	
	public static Result store()
	{
		return  ok(Store.render());
	}
	
	public static Result terms()
	{
		return  ok(Terms.render());
	}
	
	public static Result wboozology()
	{
		return  ok(WBoozology.render());
	}
	
	
	
	
	
	
	//***************VALIDATORS******************
	public static class CONTACTUS {
		@Required
		public Integer querytype;
		
		@Email
	    public String email;
	    
		@Required
	    public String firstname;
	    
		@Required
	    public String lastname;
	    
		@Required
	    public String subject;    
	     
	    @MaxLength(10000)
	    @MinLength(30)
	    public String content;
	    
	    public String recaptcha_challenge_field;
	    
	    @Required
	    public String recaptcha_response_field;
	    
	    public String validate() {
	    	if(!DInitial.QueryType.containsKey(querytype))
	    		return "Invalid Data entered";
	    	String remoteAddr = request().remoteAddress();
		    ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
		    reCaptcha.setPrivateKey(Application.Captchaprivate);
	    	ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, recaptcha_challenge_field, recaptcha_response_field);
		    if(!reCaptchaResponse.isValid())
		    	return "Captcha Entered was invalid.Please Retry.";
		    
	    	return null;
	    }
	}
	public static final Form<CONTACTUS> CONTACTUS_FORM = form(CONTACTUS.class);
}
