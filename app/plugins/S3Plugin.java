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

    public static final String AWS_S3_BUCKET = "aws.s3.bucket";
    /*public static final String AWS_ACCESS_KEY = "AWSAccessKeyId";
    public static final String AWS_SECRET_KEY = "AWSSecretKey";*/
    public static final String AWS_ACCESS_KEY = "aws.access.key";
    public static final String AWS_SECRET_KEY = "aws.secret.key";
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
         String accessKey = application.configuration().getString(AWS_ACCESS_KEY);
        String secretKey = application.configuration().getString(AWS_SECRET_KEY);
		/*String accessKey = System.getenv(AWS_ACCESS_KEY);//application.configuration().getString(AWS_ACCESS_KEY);
        String secretKey = System.getenv(AWS_SECRET_KEY);//application.configuration().getString(AWS_SECRET_KEY);*/
        
        System.out.println("accessKey:" + accessKey);
        System.out.println("secretKey:" + secretKey);
        
        s3Bucket = application.configuration().getString(AWS_S3_BUCKET);
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
        return (application.configuration().keys().contains(AWS_ACCESS_KEY) &&
                application.configuration().keys().contains(AWS_SECRET_KEY) &&
                application.configuration().keys().contains(AWS_S3_BUCKET));
    }
    
}
