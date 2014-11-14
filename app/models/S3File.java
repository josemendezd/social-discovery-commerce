package models;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import controllers.DInitial;
import play.Logger;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import plugins.S3Plugin;

import javax.imageio.ImageIO;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

@Entity
public class S3File extends Model {

    @Id
    public Long id;

    @Required
    private String bucketname;

    @Required
    public String filequalifier;

    @Required
    public String filename;

    @Required
    public String filestate;//full,thumb-nails,etc.

    @Required
    public Long modelref;    

    @Transient
    public File file;
    
    public static Finder<Long, S3File> find = new Finder<Long, S3File>(Long.class, S3File.class);
    
    public S3File()
    {
    	
    }
    
    public S3File(String bucketname,String filequalifier,String filename,String filestate,Long modelref,File file )
    {
    	this.bucketname=bucketname;
    	this.filequalifier=filequalifier;
    	this.filename=filename;
    	this.filestate=filestate;
    	this.modelref=modelref;
    	this.file=file;
    	this.save();
    }
    
    public S3File(String bucketname,String filequalifier,String filename,String filestate,Long modelref)
    {
    	this.bucketname=bucketname;
    	this.filequalifier=filequalifier;
    	this.filename=filename;
    	this.filestate=filestate;
    	this.modelref=modelref;
    	super.save();
    }

    public URL getUrl() throws MalformedURLException {
    	String urlis="http://"+this.bucketname+S3Plugin.baseurl + getActualFileName();
        return new URL(urlis);
    }
    public String getWallpaperButton()  {
    	
        return this.bucketname;
    }    
    public String geturlstring(){
    	return "http://"+this.bucketname+S3Plugin.baseurl + getActualFileName();
    }
    
    public static String getUrl(String filequalifier,String filename,String filestate){
    	return "http://"+S3Plugin.s3Bucket+S3Plugin.baseurl + filequalifier + "/" + filestate + "_" + filename;
    }

    private String getActualFileName() {
        return filequalifier + "/" + filestate + "_" + filename;
    }
    
    public static String getbaseurl(){
    	return S3Plugin.protocol+S3Plugin.s3Bucket+S3Plugin.baseurl;
    }

    @Override
    public void save() {
        if (S3Plugin.amazonS3 == null) {
            Logger.error("Could not save because amazonS3 was null");
            throw new RuntimeException("Could not save");
        }
        else {
            this.bucketname = S3Plugin.s3Bucket;
            super.save(); // assigns an id
            savetoamazon(this);
        }
    }

    @Override
    public void delete() {
        if (S3Plugin.amazonS3 == null) {
            Logger.error("Could not delete because amazonS3 was null");
            throw new RuntimeException("Could not delete");
        }
        else {
            S3Plugin.amazonS3.deleteObject(bucketname, getActualFileName());
            super.delete();
        }
    }
    
    public static void savetoamazon(S3File s3f)
    {
    	PutObjectRequest putObjectRequest = new PutObjectRequest(s3f.bucketname, s3f.getActualFileName(), s3f.file);
        putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead); // public for all
        S3Plugin.amazonS3.putObject(putObjectRequest); // upload file
    }
    
    public static S3File findfile(Long fileid)
    {
    	return find.where().eq("id", fileid).findUnique();
    }
    
    public static S3File findfile(String bucketname,String filequalifier,String filename,String filestate,Long modelref)
    {
    	return find.where().eq("bucketname", bucketname).eq("filequalifier", filequalifier).eq("filename", filename).eq("filestate", filestate).eq("modelref", modelref).findUnique();
    }
    
    public static List<S3File> findfilelist(String bucketname,String filequalifier)
    {
    	return find.where().eq("bucketname", bucketname).eq("filequalifier", filequalifier).findList();
    }
    
    public static int findfilelistcount(String bucketname,String filequalifier)
    {	
    	Logger.info("int of wallpapers: " + find.where().eq("bucketname", bucketname).eq("filequalifier", filequalifier).findRowCount());
    	return find.where().eq("bucketname", bucketname).eq("filequalifier", filequalifier).findRowCount();
    }

    		
    	    public static List<S3File> findWallPaperURL(int count)
    	    {	
    	    	//Logger.info("int of wallpapers: " + find.where().eq("bucketname", bucketname).eq("filequalifier", filequalifier).findRowCount());
    	    	return find.where().eq("bucketname", "com.stag.boozology").eq("filequalifier", "Cover_URL").eq("modelref", count).findList();
    	    }    		
    	    public static List<S3File> findWallPaperButton(int count)
    	    {	
    	    	//Logger.info("int of wallpapers: " + find.where().eq("bucketname", bucketname).eq("filequalifier", filequalifier).findRowCount());
    	    	return find.where().eq("bucketname", "com.stag.boozology").eq("filequalifier", "Cover_Button").eq("modelref", count).findList();
    	    }   		
    public static S3File createfile(String bucketname,String filequalifier,String filename,String filestate,Long modelref,File file )
    {
    	S3File sf = S3File.findfile(bucketname, filequalifier, filename, filestate, modelref);
    	if(sf!=null)
    		sf.delete();
	    return new S3File(bucketname, filequalifier, filename, filestate, modelref, file);
    }
    
    public static File getimageresized(final File file,final String filename,final String filestate,int identity,int width,int height)
    {
    	
    	File outputfile;
		try {
			outputfile = File.createTempFile(filename, filename.substring(filename.length()-4));
			if(identity==DInitial.IMAGESTORESIZE.AS_IT_IS.Identifier)
				ImageIO.write( ImageIO.read( file ), "png", outputfile);
			else
				net.coobird.thumbnailator.Thumbnails.of(file).size(width, height).keepAspectRatio(true).outputQuality(1.0f).outputFormat("png").toFile(outputfile);
			return outputfile;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return file;
    }

}
