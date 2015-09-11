package controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import models.Eventlog;
import models.Product;
import models.RankingListMaster;
import models.RankingListProduct;
import models.S3File;
import models.User;
import models.UserRate;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.AddProductToRankingListVM;
import viewmodel.CategoryVM;
import viewmodel.RankingListMasterVM;
import viewmodel.RankingListProductVM;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.annotation.Transactional;

public class RankingModuleController extends Controller {

    /******Ranking Module Implementation****/
	
	@Restrict(@Group(Application.USER_ROLE))
    public static Result renderRankingList() {
    	return ok(views.html.Ranking.rankingListPage.render());
    }
    
    public static Result RankingListCreateView() {
    	return ok(views.html.Ranking.createRankingList.render());
    }
    
    @Transactional
    public static Result doCreate() {
    	final DynamicForm dynaForm = play.data.Form.form().bindFromRequest();
    	RankingListMaster listMaster = new RankingListMaster();
    	listMaster.name = dynaForm.get("listName");
    	listMaster.description = dynaForm.get("description");
    	listMaster.tags = dynaForm.get("tags");
    	listMaster.totalNoOfVotes = 0L;
    	listMaster.featuredList = "N";
    	final User currentAuthUser = Application.getLocalUser(session());
    	listMaster.userId = currentAuthUser.id;
    	listMaster.save();
    	
    	return ok(Json.toJson(listMaster));
    }
    
	public static Result getRankingProducts() {
		DynamicForm bindedForm = play.data.Form.form().bindFromRequest();		
		
	    int page = Integer.parseInt( bindedForm.get("page"));
	    int gendercont = Integer.parseInt( bindedForm.get("gendercont"));//To be passed
	    int sortseq = Integer.parseInt( bindedForm.get("sortseq"));
	    int pricerange = Integer.parseInt( bindedForm.get("pricerange"));//To be passed
	    int pagesize = Integer.parseInt( bindedForm.get("pagesize"));
	    int catgid = Integer.parseInt(bindedForm.get("catgid"));
	    long listId = Integer.parseInt(bindedForm.get("listId"));
	    
	    RankingListMaster listmaster = RankingListMaster.find.byId(listId);
	    
	    String searchtext = bindedForm.get("searchtext");//To be passed	
	    if(searchtext!=null)
	    	searchtext=GHelp.FilteredSql(searchtext);
		response().setContentType("application/json");
		
		User user = (Application.getLocalUser(session()));
		boolean isAdmin = false;
		for(int i = 0; i < user.roles.size(); i++) {
			if(user.roles.get(i).roleName.equals("admin")) {
				isAdmin = true;
			}
		}
		List<AddProductToRankingListVM> rankingListProduct = new ArrayList<AddProductToRankingListVM>();
		List<Product> pList = Eventlog.RelevanceFeed(page, pagesize,GHelp.filterSql(searchtext, gendercont, pricerange, catgid,"")).getList();
			for(Product product: pList){
				AddProductToRankingListVM RankingList = new AddProductToRankingListVM();	
				if (!isAdmin) {
					if (listmaster.rankingListViews != null && !listmaster.rankingListViews.isEmpty()) {
		    			for(final RankingListProduct listProduct: listmaster.rankingListViews) {
		    				if(listProduct.userId == user.id && listProduct.product.id == product.id){
		    					RankingList.isVisible = true;
		    				}
		    			}
		    		}	
				} else {
					RankingList.isVisible = true;
				}
				RankingList.id = product.id;
				RankingList.listId = listId;
				RankingList.productName = product.productname;
				RankingList.productUrl = product.getproductimagethumb();
				rankingListProduct.add(RankingList);
			}
			return ok(Json.toJson(rankingListProduct));
	}
    
    public static Result getRankingLists() {
    	
    	DynamicForm bindedForm = play.data.Form.form().bindFromRequest();
    	int pageSize = Integer.parseInt(bindedForm.get("pageSize"));
    	int pageNo= Integer.parseInt(bindedForm.get("pageNo"));
    	
    	response().setContentType("application/json");
    	Page<RankingListMaster> _rankingLists = Ebean.find(RankingListMaster.class).where().ne("featuredList", "Y").orderBy().desc("createdDate").findPagingList(pageSize).getPage(pageNo);
    	List<RankingListMaster> rankingLists = _rankingLists.getList(); 
    	
    	List<RankingListMasterVM> rankingList = new ArrayList<RankingListMasterVM>();
    	
    	for(final RankingListMaster listMaster: rankingLists) {
    		RankingListMasterVM listVm = new RankingListMasterVM();
    		listVm.id = listMaster.id;
    		listVm.createdDate = listMaster.createdDate;
    		listVm.description = listMaster.description;
    		listVm.name = listMaster.name;
    		listVm.tags = listMaster.tags;
    		listVm.totalNoOfVotes = listMaster.totalNoOfVotes;
    		listVm.totalNoOfProducts = (long) listMaster.rankingListViews.size();
    		listVm.rankingListViews = new ArrayList<RankingListProductVM>();
    		
    		Page<RankingListProduct> rankingListProductsPage = Ebean.find(RankingListProduct.class).where().eq("rankingList.id", listMaster.id).findPagingList(5).getPage(0);	
    		List<RankingListProduct> rankingListProducts = rankingListProductsPage.getList();
    		
			for(final RankingListProduct listProduct: rankingListProducts) {
    			RankingListProductVM productVm = new RankingListProductVM();
    			productVm.listId = listVm.id;
    			productVm.productId = listProduct.product.id;
    			productVm.totalVotes = listProduct.totalVotes;
    			productVm.productUrl = listProduct.product.getproductimagethumb();
    			listVm.rankingListViews.add(productVm);
			}
    		rankingList.add(listVm);
    	}

    	return ok(Json.toJson(rankingList));
    }
    
    public static Result getFeaturedRankingLists() {
    	response().setContentType("application/json");
    	List<RankingListMaster> rankingLists = Ebean.find(RankingListMaster.class).where().eq("featuredList", "Y").orderBy().desc("totalNoOfVotes").findList();
    	
    	List<RankingListMasterVM> rankingList = new ArrayList<RankingListMasterVM>();
    	
    	for(final RankingListMaster listMaster: rankingLists) {
    		RankingListMasterVM listVm = new RankingListMasterVM();
    		listVm.id = listMaster.id;
    		listVm.createdDate = listMaster.createdDate;
    		listVm.description = listMaster.description;
    		listVm.name = listMaster.name;
    		listVm.tags = listMaster.tags;
    		listVm.totalNoOfVotes = listMaster.totalNoOfVotes;
    		listVm.totalNoOfProducts = (long) listMaster.rankingListViews.size();
    		listVm.rankingListViews = new ArrayList<RankingListProductVM>();
    		listVm.isFeaturedList = true;
    		
    		if (listVm.rankingListViews != null && listVm.rankingListViews.isEmpty()) {
    			/*Collections.sort(listVm.rankingListViews, new Comparator() {
    				public int compare(Object rankingListViewsOne, Object rankingListViewsTwo) {
    				//use instanceof to verify the references are indeed of the type in question
    				return ((RankingListProductVM)rankingListViewsOne).totalVotes
    				.compareTo(((RankingListProductVM)rankingListViewsTwo).totalVotes);
    				}
    				});*/
    			int i = 0;
    			for(final RankingListProduct listProduct: listMaster.rankingListViews) {
        			RankingListProductVM productVm = new RankingListProductVM();
        			productVm.listId = listVm.id;
        			productVm.productId = listProduct.product.id;
        			productVm.totalVotes = listProduct.totalVotes;
        			productVm.productUrl = listProduct.product.getproductimagethumb();
        			listVm.rankingListViews.add(productVm);
        			i++;
        			if(i == 5){
        				break;
        			}
    			}
    		}
    		rankingList.add(listVm);
    		System.out.println(listVm.rankingListViews);
        	System.out.println(listVm.rankingListViews.size());
    	}

    	return ok(Json.toJson(rankingList));
    }
    
    public static Result getTopLevelCategories() {
    	
    	List<CategoryVM> categories = new ArrayList<CategoryVM>();
    	
		String sqlquery_ = "SELECT * FROM CATEGORY WHERE PCATEGORY_ID = 1";
		List<SqlRow> rows = Ebean.createSqlQuery(sqlquery_).findList();
		for(SqlRow row: rows) {
			CategoryVM category = new CategoryVM();
			category.catId = row.getLong("id");
			category.catName = row.getString("cname");
			categories.add(category);
		}
		
    	return ok(Json.toJson(categories));
    }
    
    public static Result getCategoriesProduct() {
    	final DynamicForm dynaForm = play.data.Form.form().bindFromRequest();
    	Long categoryId = new Long(dynaForm.get("catId"));
    	String searchText =  dynaForm.get("searchTxt");
    	List<Product> productsList = null;
    	if (categoryId != 0) {
    		productsList = Product.find.where().and(Expr.eq("category_id", categoryId), Expr.like("category_id", searchText)).findList();
    	} else {
    		productsList = Product.find.where().like("productname", searchText).findList();
    	}
    	
    	return ok(Json.toJson(productsList));
    }
    
    @Restrict(@Group(Application.USER_ROLE))
    public static Result addEditProductToListPage(Long listId) {
    	RankingListMaster rankingList = RankingListMaster.find.byId(listId);
    	return ok(views.html.Ranking.addProductsToList.render(listId, rankingList.name));
    }
    
    @Transactional
    public static Result addProdToRankingList() {
    	final DynamicForm dynaForm = play.data.Form.form().bindFromRequest();
    	Product prod = new Product();
    	prod.id = Long.parseLong(dynaForm.get("productId"));
    	
    	RankingListMaster listMaster = new RankingListMaster();
    	listMaster.id = Long.parseLong(dynaForm.get("listId"));
    	
    	RankingListProduct listProduct = new RankingListProduct();
    	listProduct.product = prod;
    	listProduct.rankingList = listMaster;
    	listProduct.totalVotes = 0L;
    	listProduct.userId = (Application.getLocalUser(session())).id;
    	listProduct.save();
    	
    	return ok("");
    }
    
    @Transactional
    public static Result removeProdFromRankingList() {
    	final DynamicForm dynaForm = play.data.Form.form().bindFromRequest();
    	Long  prodId = Long.parseLong(dynaForm.get("productId"));
    	Long listId = Long.parseLong(dynaForm.get("listId"));
    	
    	RankingListProduct listProduct = Ebean.find(RankingListProduct.class).where().eq("rankingList.id", listId).where().eq("product.id", prodId).findUnique();
    	listProduct.delete();
    	return ok("");
    }
    
    @Restrict(@Group(Application.USER_ROLE))
    public static Result listSinglePageView(Long listId) {
    	return ok(views.html.Ranking.singleListPage.render(listId));
    }

    public static Result getRankingListProducts(Long listId) {
    	
    	DynamicForm bindedForm = play.data.Form.form().bindFromRequest();
    	int pageSize = Integer.parseInt(bindedForm.get("pageSize"));
    	int pageNo= Integer.parseInt(bindedForm.get("pageNo"));
    	
    	Page<RankingListProduct> listProductsPage = 
    			Ebean.find(RankingListProduct.class).where().eq("rankingList.id", listId).orderBy().desc("totalVotes").findPagingList(pageSize).getPage(pageNo);
    	
    	List<RankingListProduct> _listProducts = listProductsPage.getList();
    			
    	List<RankingListProductVM> listProducts = new ArrayList<RankingListProductVM>();
		
		for(RankingListProduct row: _listProducts) {
			RankingListProductVM pVm = new RankingListProductVM();
			pVm.listId = listId;
			pVm.totalVotes = row.totalVotes;
			pVm.productId = row.product.id;
			pVm.productName = row.product.productname;
			pVm.productUrl = S3File.getUrl(Product.class.getSimpleName(), row.product.ImageLocation, DInitial.IMAGESTORESIZE.AS_IT_IS.filestate);
			pVm.views = row.product.views;
			pVm.userId = row.userId;
			
			User user = Application.getLocalUser(session());
			if(user != null) {
				UserRate ur = UserRate.find.where().eq("user_id", user.id).eq("product_id", pVm.productId).findUnique();
				if (ur != null)
					pVm.productRating = ur.rate;
			}
			//get product average rating.
			pVm.avgRating = Application.getAverageProductRating(pVm.productId);
			
			
			listProducts.add(pVm);
		}
    	return ok(Json.toJson(listProducts));
    }
    
    public static Result getRankingListById(Long listId) {
    	RankingListMaster rankingList = RankingListMaster.find.byId(listId);
    	RankingListMasterVM listViewModel = new RankingListMasterVM();
    	listViewModel.name = rankingList.name;
    	listViewModel.description = rankingList.description;
    	
    	return ok(Json.toJson(listViewModel));
    }
    
    @Transactional
    public static Result voteForProductInList() {
    	
    	final DynamicForm dynaForm = play.data.Form.form().bindFromRequest();
    	Long productId = Long.parseLong(dynaForm.get("productId"));
    	Long listId = Long.parseLong(dynaForm.get("listId"));
    	String voteCount = dynaForm.get("noOfVotes");
    	Long productNoOfVotes = 1L;
    	Long listNoOfVotes = 1L;
    	RankingListProduct rankProduct = Ebean.find(RankingListProduct.class).where().and(Expr.eq("product_id", productId), Expr.eq("rankinglistid", listId)).findUnique();
    	RankingListMaster rankingList = RankingListMaster.find.byId(listId);
    	if (voteCount != null && !voteCount.equals("")) {
    		productNoOfVotes = Long.parseLong(dynaForm.get("noOfVotes"));
    		listNoOfVotes = rankingList.totalNoOfVotes - rankProduct.totalVotes + productNoOfVotes; 
    	} else {
        	Long existingCount = rankProduct.totalVotes;
        	productNoOfVotes = existingCount + 1;
        	listNoOfVotes = rankingList.totalNoOfVotes + 1;
    	}
    	
    	rankProduct.totalVotes = productNoOfVotes;
    	rankProduct.update();
    	
    	rankingList.totalNoOfVotes = listNoOfVotes;
    	rankingList.update();
    	
    	return ok("");
    }
    
    //@Restrict(@Group(Application.ADMIN_ROLE))
    public static Result renderAdminMainRankingPage() {
    	return ok(views.html.Ranking.adminMainRankingPage.render());	
    }
    
    public static Result searchRankingListByCriteria() {
    	final DynamicForm dynaForm = play.data.Form.form().bindFromRequest();
    	String searchTxt = dynaForm.get("searchTxt");
    	
    	List<RankingListMasterVM> masterRankingList = new ArrayList<RankingListMasterVM>();
    	
    	String sqlquery_ = "SELECT * FROM ranking_list_master WHERE name ilike '%" +searchTxt+ "%' or description ilike '%" +searchTxt+ "%'";
		List<SqlRow> rows = Ebean.createSqlQuery(sqlquery_).findList();
		for(SqlRow row: rows) {
			RankingListMasterVM masterVm = new RankingListMasterVM();
			masterVm.id = row.getLong("id");
			//masterVm.description = row.getString("description");
			masterVm.name = row.getString("name");
			masterVm.isFeaturedList = false;
			if ("Y".equals(row.getString("featured_list"))) {
				masterVm.isFeaturedList = true;
			}
			masterRankingList.add(masterVm);
			
		}
    	return ok(Json.toJson(masterRankingList));
    }
    
    @Transactional
    public static Result deleteRankingList(Long listId) {
    	
    	RankingListMaster rankingList = RankingListMaster.find.byId(listId);
    	rankingList.delete();
    	return ok("List deleted successfully!");
    }
    
    @Transactional
    public static Result addToFeaturedList(Long listId) {
    	
    	String sqlquery_ = "SELECT count(*) FROM ranking_list_master WHERE featured_list = 'Y'";
		SqlRow rows = Ebean.createSqlQuery(sqlquery_).findUnique();
		if (rows.getLong("count") < 4 ) {
			//good for modification
			RankingListMaster rankingList = RankingListMaster.find.byId(listId);
			rankingList.featuredList = "Y";
			rankingList.update();
		}
		
    	return ok("List deleted successfully!");
    }
    
    @Transactional
    public static Result removeFromFeaturedList(Long listId) { //
    	
		RankingListMaster rankingList = RankingListMaster.find.byId(listId);
		rankingList.featuredList = "N";
		rankingList.update();
		return ok("List deleted successfully!");
	}

    //@Restrict(@Group(Application.ADMIN_ROLE))
    public static Result renderAdminSingleListPage(Long listId) { 
    	RankingListMaster rankingList = RankingListMaster.find.byId(listId);
    	return ok(views.html.Ranking.adminSingleListPage.render(listId, rankingList.name));
    }
    
	public static Result averageRatingofProduct() {
		
		User user = Application.getLocalUser(session());
    	final DynamicForm dynaForm = play.data.Form.form().bindFromRequest();
    	Long  product_id = Long.parseLong(dynaForm.get("productId"));
    	Long value = Long.parseLong(dynaForm.get("noOfVotes"));
		
		Product product = Product.find.byId(product_id);
		
		UserRate.save(user, product, value);
		return ok();
	}
}
