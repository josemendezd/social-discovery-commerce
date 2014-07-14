package controllers;

import java.util.Random;

import models.ImageBeer;
import models.ImageGadgets;
import models.ImageGlassWare;
import models.ImageLiquor;
import models.ImageMixology;
import models.ImageToys;
import models.ImageWine;
import play.mvc.Controller;

public class HomePageImages extends Controller {
	

	// This function returns the random path for the wine section
	public static String imagePathWine() {
		Random obj = new Random();
		int number  = obj.nextInt(5) + 1;
		ImageWine imageWine = ImageWine.find.byId(1L);
		if(imageWine!=null)
			return "/assets/" + imageWine.url;
		return null;
	}
	
	// This function returns the random path for the beer section
	public static String imagePathBeer() {
		Random obj = new Random();
		int number  = obj.nextInt(5) + 1;
		ImageBeer imageBeer = ImageBeer.find.byId(1L);
		if(imageBeer!=null)
		return "/assets/" + imageBeer.url;
		return null;
	}
	
	// This function returns the random path for the liquor section
	public static String imagePathLiquor() {
		Random obj = new Random();
		int number  = obj.nextInt(5) + 1;
		ImageLiquor imageLiquor = ImageLiquor.find.byId(1L);
		if(imageLiquor!=null)
		return "/assets/" + imageLiquor.url;
		return null;
	}
	
	// This function returns the random path for the mixology section
	public static String imagePathMixology() {
		Random obj = new Random();
		int number  = obj.nextInt(5) + 1;
		ImageMixology imageMixology = ImageMixology.find.byId(1L);
		if(imageMixology!=null)
		return "/assets/" + imageMixology.url;
		return null;
	}
	
	// This function returns the random path for the gadgets section
	public static String imagePathGadgets() {
		Random obj = new Random();
		int number  = obj.nextInt(5) + 1;
		ImageGadgets imageGadgets = ImageGadgets.find.byId(1L);
		if(imageGadgets!=null)
		return "/assets/" + imageGadgets.url;
		return null;
	}
	
	// This function returns the random path for the toys section
	public static String imagePathToys() {
		Random obj = new Random();
		int number  = obj.nextInt(5) + 1;
		ImageToys imageToys = ImageToys.find.byId(1L);
		if(imageToys!=null)
		return "/assets/" + imageToys.url;
		return null;
	}
	
	// This function returns the random path for the glass ware section
	public static String imagePathGlassWare() {
		Random obj = new Random();
		int number  = obj.nextInt(5) + 1;
		ImageGlassWare imageGlassWare = ImageGlassWare.find.byId(1L);
		if(imageGlassWare!=null)
		return "/assets/" + imageGlassWare.url;
		return null;
	}

}
