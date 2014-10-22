package plugins;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;

import play.Application;
import play.Logger;
import play.Play;
import play.Plugin;

public class S3Plugin extends Plugin {

    public static final String AWS_S3_BUCKET = play.Play.application().configuration().getString("aws.s3.bucket");
    // private static final String AWS_ACCESS_KEY = System.getenv("AWSAccessKeyId");
    // private static final String AWS_SECRET_KEY = System.getenv("AWSSecretKey");
    public static final String AWS_ACCESS_KEY = play.Play.application().configuration().getString("aws.access.key");
    public static final String AWS_SECRET_KEY = play.Play.application().configuration().getString("aws.secret.key");
	public static final String BASE_URL_FORMAT="base.url.format";
    private final Application application;

    public static AmazonS3 amazonS3;
    public static String baseurl;
    public static String protocol="http://";

    public static String s3Bucket;

    public S3Plugin(Application application) {
        this.application = application;
    }

    @Override
    public void onStart() {
        /* String accessKey = application.configuration().getString(AWS_ACCESS_KEY);
        String secretKey = application.configuration().getString(AWS_SECRET_KEY);*/
		String accessKey = AWS_ACCESS_KEY;
        String secretKey = AWS_SECRET_KEY;
        
        //application.configuration().getString(AWS_SECRET_KEY);
        
        s3Bucket = AWS_S3_BUCKET;
        baseurl = application.configuration().getString(BASE_URL_FORMAT);
        boolean dev=Play.isDev();
        
        if(!dev||true)	
        {
	        try{
		        if ((accessKey != null) && (secretKey != null)) {
		            AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
		            amazonS3 = new AmazonS3Client(awsCredentials);
		            if(!amazonS3.doesBucketExist(s3Bucket))
		            	amazonS3.createBucket(s3Bucket);
		            Logger.info("Using S3 Bucket: " + s3Bucket);
		        }
	        }catch(Exception e){e.printStackTrace();}
        }
    }

    @Override
    public boolean enabled() {
        return (AWS_ACCESS_KEY != null &&
                AWS_SECRET_KEY != null &&
                AWS_S3_BUCKET!=null);
    }
    
}
