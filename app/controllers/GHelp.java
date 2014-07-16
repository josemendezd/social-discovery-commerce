package controllers;

import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.MatchesPattern;
import javax.imageio.ImageIO;
import javax.servlet.http.Part;
import javax.validation.Constraint;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.URL;

import com.feth.play.module.mail.Mailer;


import play.Logger;
import play.Play;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Pattern;

import models.Category;
import models.SecurityRole;
import models.User;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import providers.MyUsernamePasswordAuthProvider.MyIdentity;

public class GHelp extends Controller {
	
	public  static final  int feedsize=40;
	
	public static class USER_SETTINGS {
		
	@Required
	@NotNull
	public String indicator;
	
	@MinLength(2)
	@MaxLength(50)
	@Pattern(value="[A-Za-z]+")
	public String firstname,lastname;
	
	@MinLength(5)
	@MaxLength(15)
	public String password,repeatPassword;
	
	@MinLength(2)
	@MaxLength(50)
	@Pattern(value="[A-Za-z0-9]+")
	public String username;
	
	@Email
	public String email;
	
	@MaxLength(255)
	public String biography;
	
	@URL
	public String website,socialimage;
	
	@MaxLength(255)
	@Pattern("[a-zA-Z0-9\\s\\,\\-]+")
	public String location;
	
	
	public long gender;
	
	
    public boolean sofollows;
    public boolean socommentpr;
    public boolean socommentco;
    public boolean sorecommends;
    public boolean sosuggestsco;
    
    public boolean advertisements;
	
	
	public String validate() {
		int indicatorint=0;
		try{
			indicatorint =Integer.parseInt(indicator);
		}catch(NumberFormatException nfe) {
			return "Form has been Tampered!!!";
		}
		switch(indicatorint)
		{
		case DInitial.PROFILE_SETTINGS.FIRSTNAME:
			if(firstname==null)
				return "Not a valid First Name";
			break;
		case DInitial.PROFILE_SETTINGS.LASTNAME:
			if(lastname==null)
				return "Not a valid LASTNAME";
			break;
		case DInitial.PROFILE_SETTINGS.USERNAME:
			if(username==null)
				return "Not a valid USERNAME";
			else
				if(User.findByUserName(username) != null)
					return "Username already exists";
			break;
		case DInitial.PROFILE_SETTINGS.EMAIL:
			if(email==null)
				return "Not a valid EMAIL";
			else
				if(User.findByEmail(email) != null)
					return "This Email already exists";
				else
					return "Email Resetting is presently not supported :(";
		case DInitial.PROFILE_SETTINGS.PASSWORD:
			if (password == null || !password.equals(repeatPassword)) {
				return Messages
						.get("boozology.password.signup.error.passwords_not_same");
			}			
			break;
		case DInitial.PROFILE_SETTINGS.BIOGRAPHY:
			if(biography==null)
				return "Not a valid BIOGRAPHY!!";
			break;
		case DInitial.PROFILE_SETTINGS.GENDER:
			if(gender==0)
				return "Not a valid Gender!!";
			break;
		case DInitial.PROFILE_SETTINGS.EMAIL_NOTIFICATION:
			
			break;
		case DInitial.PROFILE_SETTINGS.LINKEDACCOUNTS:
			if(firstname==null)
				return "Not a valid First Name";
			break;
		case DInitial.PROFILE_SETTINGS.LOCATION:
			if(location==null)
				return "Not a valid LOCATION";
			break;
		case DInitial.PROFILE_SETTINGS.SOCIAL_IMAGE:
			if(socialimage==null)
				return "Not a valid SOCIAL IMAGE";
			break;
		case DInitial.PROFILE_SETTINGS.WEBSITE:
			if(website==null)
				return "Not a valid WEBSITE";
			break;
		default:
			return "Error , Invalid Input Recieved";

		}
		return null;
	}
	
	
	}
	
	public static String getDomainName(String url){
		 try{
		       URI uri = new URI(url);
		       String domain = uri.getHost();
		       if(domain==null)
		    	   throw new URISyntaxException(url, "Not Valid");
		       return domain.startsWith("www.") ? domain.substring(4) : domain;
			 }
			 catch(Exception e)
			 {
				 if(url.toLowerCase().matches("^(https?|ftp)"))
				 {
				 String newurl=url.replaceFirst("http://", "");
				 return newurl = newurl.replaceFirst("/(.*)", "");
				 }
				 return null;
			 }
	     }
	public static String filterSql(String pn,long gd,int prindex,long catgid,String qualifier)
	{
		StringBuilder qstring=new StringBuilder();
		qstring.append(" "+qualifier+"PRODUCTNAME ILIKE '%"+pn+"%'");
		if(gd!=0)
        	qstring.append("AND "+qualifier+"GENDER = "+gd);
		qstring.append("AND "+qualifier+"PRICETAG  BETWEEN "+DInitial.PR[prindex][0]+"  AND "+DInitial.PR[prindex][1]);
        if(catgid!=0)
        	qstring.append("AND "+qualifier+"CATEGORY_ID IN ("+Category.ChildList(catgid)+")");
		return qstring.toString();    
	}
	
	public static String FilteredSql(String sqlcontent){
		return sqlcontent.replaceAll("_", "\\\\_").replaceAll("%", "\\\\%").replaceAll(" ", "%").replace("'", "''");
	}
	
	public static String Escape(String TobeEscaped) {
		return String.valueOf(org.codehaus.jackson.io.JsonStringEncoder.getInstance().quoteAsString(TobeEscaped));		
	}
	
	public static play.api.templates.Html EHtml(String TobeEscaped) {
		return play.api.templates.Html.apply( String.valueOf(org.codehaus.jackson.io.JsonStringEncoder.getInstance().quoteAsString(TobeEscaped)) );		
	}
	
	public static boolean VerifyACL(int ACL,User u)
	{
		User uc= Application.getLocalUser(session());
		if(uc==null)
			return DInitial.CURRENT_ACL.GUESTS==ACL;
		SecurityRole sra= SecurityRole.findByRoleName(controllers.Application.ADMIN_ROLE),
				srm= SecurityRole.findByRoleName(controllers.Application.MODERATOR_ROLE),
				sru= SecurityRole.findByRoleName(controllers.Application.USER_ROLE);
		switch(ACL)
		{
		case DInitial.CURRENT_ACL.ADMINISTRATOR_ONLY:
			return uc.roles.contains(sra);
		case DInitial.CURRENT_ACL.MODERATOR_ONLY:
			return uc.roles.contains(srm);
		case DInitial.CURRENT_ACL.ADMIN_MODERATOR:
			return uc.roles.contains(sra)||uc.roles.contains(srm);
		case DInitial.CURRENT_ACL.ADMIN_MODERATOR_SELF:
			if(u==null)
				return false;
			return uc.roles.contains(sra)||uc.roles.contains(srm)||u.equals(uc);
		case DInitial.CURRENT_ACL.ADMIN_SELF:
			if(u==null)
				return false;
			return uc.roles.contains(sra)||u.equals(uc);
		case DInitial.CURRENT_ACL.SELF_ONLY:
			if(u==null)
				return false;
			return u.equals(uc);		
		case DInitial.CURRENT_ACL.ALL_USERS:
			return uc.roles.contains(sru)||uc.roles.contains(sra)||uc.roles.contains(srm);
		}
		return false;
	}
	
	public static com.feth.play.module.mail.Mailer mailsender=Mailer.getCustomMailer
			(play.Play.application().configuration().getConfig("boozology-mailer"));
	
	public static String getEmailName(final String email, final String name) {
		return Mailer.getEmailName(email, name);
	}
	
	public static String getrandomname()
	{
		return UUID.randomUUID().toString().replace('-', '_');
	}
	
	public static String copyImage(String sourcePath, String destPath) {
		File infile = new File(sourcePath);
		String ext = sourcePath.substring(sourcePath.lastIndexOf(".")) ;
		File outfile = new File(destPath + ext);
		try {
			generateThumbnailImageVersion(infile, outfile);
			//String outfilePath = outfile.getPath().replace("\\", "/");
			return routes.Assets.at(outfile.getPath().replace("public/", "")).absoluteURL(request());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String uploadImage(final String allotedname,String imagecontext,String filepath,play.mvc.Http.MultipartFormData body,int sizeflag)
	{
		FilePart picture = body.getFile("picture");		
		if (picture != null) {
			File infile = picture.getFile();
			//String picturenametest=picture.getFilename().toLowerCase();
			
			if(picture.getContentType().contains("image/")) {
				if(infile.length() > DInitial.IMAGE_UPLOAD_FILE_SIZE_LIMIT)
					flash(Application.FLASH_ERROR_KEY, "File exceeds size limit.");
				else				
				try {
				String picturename=picture.getFilename();
				try{
					picturename=URLEncoder.encode(picture.getFilename(),"UTF-8") ;
					Logger.info(picturename);
				}catch(Exception e){picturename=picture.getFilename();}
				if(!(picturename.toLowerCase().endsWith(".jpg")||picturename.toLowerCase().endsWith(".png")||picturename.toLowerCase().endsWith(".bmp")))
				{
					flash(Application.FLASH_ERROR_KEY, "Sorry invalid type of file uploaded.");
					return null;					
				}
				if(imagecontext.equals("custom"))
				{
					picturename=picturename.substring(picturename.lastIndexOf(".")) ;
					picturename=allotedname+picturename;
				}else{
				int picturenamelength=picturename.length();
				if(picturenamelength>240)
					picturename.substring(picturenamelength-237);
				}
				String outputlocation = URLDecoder.decode(filepath, "UTF-8") +picturename;
				Logger.info(outputlocation);
				File outputfile=new File(outputlocation);
				//com.google.common.io.Files.copy(file, outputfile);
				generateThumbnailImageVersion(infile, outputfile);
				/*
				FileChannel uploadedfilechannel= new FileInputStream(file).getChannel();
	            FileChannel savedfilechannel = new FileOutputStream(outputfile).getChannel();
	            savedfilechannel.transferFrom(uploadedfilechannel, 0, uploadedfilechannel.size());
	            savedfilechannel.force(true);
	            savedfilechannel.close();
				uploadedfilechannel.close();
				*/
				
				flash(Application.FLASH_MESSAGE_KEY, "Successfully Uploaded!! ");
				return routes.Assets.at(outputfile.getPath().replace("public/", "")).absoluteURL(request());				
				
				} catch(Exception e) {
					e.printStackTrace();
					flash(Application.FLASH_ERROR_KEY, "Unable to Save image.Please report to Support.");
				}
			}else{ flash(Application.FLASH_ERROR_KEY, "Not a valid file type."); }
		}else {
		    flash(Application.FLASH_ERROR_KEY, "Missing file");		        
		  }
		return null;
	}
	
	private static void generateThumbnailImageVersion(File file, File outputfile)
			throws IOException {
		if(outputfile.exists() && outputfile.isFile() ) {
			Logger.info("===File already present");
			outputfile.delete();
			if(!outputfile.exists()) {
				Logger.info("===File deleted");
			}
		}
		
		net.coobird.thumbnailator.Thumbnails.of(file).allowOverwrite(true).size(160, 160).keepAspectRatio(true).outputQuality(1.0f).toFile(outputfile);
		if(outputfile.exists() && outputfile.isFile() ) {
			Logger.info("===File created at " + outputfile.getPath());
		}
		
	}
	
	public static String getimageextension(String allotedname,String contenttype)
	{
		String ct=contenttype.replace("image/", "");
		if(ct.equals("png"))
			return allotedname+".png";
		if(ct.equals("bmp"))
			return allotedname+".bmp";
		if(ct.equals("gif"))
			return allotedname+".gif";
		if(ct.equals("jpeg")||ct.equals("jpg"))
			return allotedname+".jpg";
		return null;
	}
	
	public static File downloadandsaveimage(String imagelocation,String allotedname)
	{
		java.net.URL webimagelocation;
		File tempstorage=null;
		try {
			tempstorage = File.createTempFile(allotedname,".jpg");
			webimagelocation = new java.net.URL(imagelocation);
			final Map<String, List<String>> headerfield=DInitial.CONTENT_TYPES.getheaderfield(webimagelocation);
			if(!DInitial.CONTENT_TYPES.isvalidlengthtype(headerfield, DInitial.IMAGE_UPLOAD_FILE_SIZE_LIMIT, DInitial.CONTENT_TYPES.IMAGE_GPJ)
				&& !DInitial.CONTENT_TYPES.isvalidlengthtype(headerfield, DInitial.IMAGE_UPLOAD_FILE_SIZE_LIMIT, DInitial.CONTENT_TYPES.OCTET_STREAM)
			)
				return null;
			ReadableByteChannel uploadedfilechannel= Channels.newChannel(webimagelocation.openStream());
	        FileChannel savedfilechannel = new FileOutputStream(tempstorage).getChannel();
	        savedfilechannel.transferFrom(uploadedfilechannel, 0, Long.MAX_VALUE);
	        savedfilechannel.close();
			uploadedfilechannel.close();
			BufferedImage image=ImageIO.read(tempstorage);
		    if (image == null) {
		    	Logger.debug("Not an image");
		    	tempstorage.delete();
				return null;
		    }			
		} catch (Exception e) {
			if(tempstorage!=null)
				tempstorage.delete();
			e.printStackTrace();
			return null;
		}
		return tempstorage;
		
	}
	
	
	/**************************************************FOR TESTS HELPERS*********************************************************************/
	
	public static String readfa(File f) {
		StringBuilder fs=new StringBuilder();
		try{
		fs.append("lastModifiedTime: " + f.lastModified()+"<br>");

		fs.append("isDirectory: " + f.isDirectory()+"<br>");
		fs.append("isRegularFile: " + f.isFile()+"<br>");
		fs.append("size: " + f.length()+"<br>");
		fs.append("Can Read: " + f.canRead()+"<br>");
		fs.append("Exists: " + f.exists()+"<br>");
		fs.append("Hidden: " + f.isHidden()+"<br>");
		fs.append("Can Write: " + f.canWrite()+"<br>");
		/*
		Boolean islockedstate=false;
		try{
			
		}catch(Exception e){
			islockedstate=true;
			e.printStackTrace();
		}finally {
		}
		fs.append("IsLocked: " + islockedstate+"<br>");
		*/
		fs.append("Absolute Path: " + f.getAbsolutePath()+"<br>");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return fs.toString();
	}
	
	
	
	public static class StringDump {

	    /**
	     * Uses reflection and recursion to dump the contents of the given object using a custom, JSON-like notation (but not JSON). Does not format static fields.<p>
	     * @see #dump(Object, boolean, IdentityHashMap, int)
	     * @param object the {@code Object} to dump using reflection and recursion
	     * @return a custom-formatted string representing the internal values of the parsed object
	     */
	    public static String dump(Object object) {
	        return dump(object, false, new IdentityHashMap<Object, Object>(), 0);
	    }

	    /**
	     * Uses reflection and recursion to dump the contents of the given object using a custom, JSON-like notation (but not JSON).<p>
	     * Parses all fields of the runtime class including super class fields, which are successively prefixed with "{@code super.}" at each level.<p>
	     * {@code Number}s, {@code enum}s, and {@code null} references are formatted using the standard {@link String#valueOf()} method.
	     * {@code CharSequences}s are wrapped with quotes.<p>
	     * The recursive call invokes only one method on each recursive call, so limit of the object-graph depth is one-to-one with the stack overflow limit.<p>
	     * Backwards references are tracked using a "visitor map" which is an instance of {@link IdentityHashMap}.
	     * When an existing object reference is encountered the {@code "sysId"} is printed and the recursion ends.<p>
	     * 
	     * @param object             the {@code Object} to dump using reflection and recursion
	     * @param isIncludingStatics {@code true} if {@code static} fields should be dumped, {@code false} to skip them
	     * @return a custom-formatted string representing the internal values of the parsed object
	     */
	    public static String dump(Object object, boolean isIncludingStatics) {
	        return dump(object, isIncludingStatics, new IdentityHashMap<Object, Object>(), 0);
	    }

	    private static String dump(Object object, boolean isIncludingStatics, IdentityHashMap<Object, Object> visitorMap, int tabCount) {
	        if (object == null ||
	                object instanceof Number || object instanceof Character || object instanceof Boolean ||
	                object.getClass().isPrimitive() || object.getClass().isEnum()) {
	            return String.valueOf(object);
	        }

	        StringBuilder builder = new StringBuilder();
	        int           sysId   = System.identityHashCode(object);
	        if (object instanceof CharSequence) {
	            builder.append("\"").append(object).append("\"");
	        }
	        else if (visitorMap.containsKey(object)) {
	            builder.append("(sysId#").append(sysId).append(")");
	        }
	        else {
	            visitorMap.put(object, object);

	            StringBuilder tabs = new StringBuilder();
	            for (int t = 0; t < tabCount; t++) {
	                tabs.append("\t");
	            }
	            if (object.getClass().isArray()) {
	                builder.append("[").append(object.getClass().getName()).append(":sysId#").append(sysId);
	                int length = Array.getLength(object);
	                for (int i = 0; i < length; i++) {
	                    Object arrayObject = Array.get(object, i);
	                    String dump        = dump(arrayObject, isIncludingStatics, visitorMap, tabCount + 1);
	                    builder.append("\n\t").append(tabs).append("\"").append(i).append("\":").append(dump);
	                }
	                builder.append(length == 0 ? "" : "\n").append(length == 0 ? "" : tabs).append("]");
	            }
	            else {
	                // enumerate the desired fields of the object before accessing
	                TreeMap<String, Field> fieldMap    = new TreeMap<String, Field>();  // can modify this to change or omit the sort order
	                StringBuilder          superPrefix = new StringBuilder();
	                for (Class<?> clazz = object.getClass(); clazz != null && !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
	                    Field[] fields = clazz.getDeclaredFields();
	                    for (int i = 0; i < fields.length; i++) {
	                        Field field = fields[i];
	                        if (isIncludingStatics || !Modifier.isStatic(field.getModifiers())) {
	                            fieldMap.put(superPrefix + field.getName(), field);
	                        }
	                    }
	                    superPrefix.append("super.");
	                }

	                builder.append("{").append(object.getClass().getName()).append(":sysId#").append(sysId);
	                for (Entry<String, Field> entry : fieldMap.entrySet()) {
	                    String name  = entry.getKey();
	                    Field  field = entry.getValue();
	                    String dump;
	                    try {
	                        boolean wasAccessible = field.isAccessible();
	                        field.setAccessible(true);
	                        Object  fieldObject   = field.get(object);
	                        field.setAccessible(wasAccessible);  // the accessibility flag should be restored to its prior ClassLoader state
	                        dump                  = dump(fieldObject, isIncludingStatics, visitorMap, tabCount + 1);
	                    }
	                    catch (Throwable e) {
	                        dump = "!" + e.getClass().getName() + ":" + e.getMessage();
	                    }
	                    builder.append("\n\t").append(tabs).append("\"").append(name).append("\":").append(dump);
	                }
	                builder.append(fieldMap.isEmpty() ? "" : "\n").append(fieldMap.isEmpty() ? "" : tabs).append("}");
	            }
	        }
	        return builder.toString();
	    }
	}
}
