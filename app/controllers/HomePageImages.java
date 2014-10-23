package controllers;

import models.ImageBeer;
import models.ImageGadgets;
import models.ImageGlassWare;
import models.ImageLiquor;
import models.ImageMixology;
import models.ImageToys;
import models.ImageWine;
import play.mvc.Controller;
import plugins.S3Plugin;

public class HomePageImages extends Controller {
	public static final String AWS_S3_BUCKET = "aws.s3.bucket";
	public static final String BASE_URL_FORMAT = "base.url.format";
	public static String assestsPath = "http://" + play.Play.application().configuration().getString(AWS_S3_BUCKET) + play.Play.application().configuration().getString(BASE_URL_FORMAT) + "assets/";
									 //  "http://" + play.Play.application().configuration().getString(AWS_S3_BUCKET) + play.Play.application().configuration().getString(BASE_URL_FORMAT);
	// This function returns the random path for the wine section
	public static String imagePathWine() {
		ImageWine imageWine = ImageWine.getRandomImage();
		if(imageWine!=null)
			return imageWine.url;
		return null;
	}
	
	// This function returns the random path for the beer section
	public static String imagePathBeer() {
		ImageBeer imageBeer = ImageBeer.getRandomImage();
		if(imageBeer!=null)
		return imageBeer.url;
		return null;
	}
	
	// This function returns the random path for the liquor section
	public static String imagePathLiquor() {
		ImageLiquor imageLiquor = ImageLiquor.getRandomImage();
		if(imageLiquor!=null)
		return imageLiquor.url;
		return null;
	}
	
	// This function returns the random path for the mixology section
	public static String imagePathMixology() {
		ImageMixology imageMixology = ImageMixology.getRandomImage();
		if(imageMixology!=null)
		return imageMixology.url;
		return null;
	}
	
	// This function returns the random path for the gadgets section
	public static String imagePathGadgets() {
		ImageGadgets imageGadgets = ImageGadgets.getRandomImage();
		if(imageGadgets!=null)
		return imageGadgets.url;
		return null;
	}
	
	// This function returns the random path for the toys section
	public static String imagePathToys() {
		ImageToys imageToys = ImageToys.getRandomImage();
		if(imageToys!=null)
		return imageToys.url;
		return null;
	}
	
	// This function returns the random path for the glass ware section
	public static String imagePathGlassWare() {
		ImageGlassWare imageGlassWare = ImageGlassWare.getRandomImage();
		if(imageGlassWare!=null)
		return imageGlassWare.url;
		return null;
	}
	
	// This function returns the favicon icon
	public static String imagePathFavicon() {
		return "http://" + play.Play.application().configuration().getString(S3Plugin.AWS_S3_BUCKET) + play.Play.application().configuration().getString(S3Plugin.BASE_URL_FORMAT) + "assets/boozology_favicon.png";
	}
	
	public static String imagePathWelcome() {
		return HomePageImages.assestsPath + "welcome_walk.png ";
	}
	
	public static String imagePathBlog() {
		return HomePageImages.assestsPath + "blog_walk.png ";
	}
	 
	public static String imagePathFollow() {
		return HomePageImages.assestsPath + "follow_walk.png ";
	}
	
	public static String imagePathProduct() {
		return HomePageImages.assestsPath + "products_walk.png ";
	}
	
	public static String imagePathCollection() {
		return HomePageImages.assestsPath + "collection_walk.png ";
	}
	
	
	

}
