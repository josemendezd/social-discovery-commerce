/**
 * 
 */
package viewmodel;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author vinod
 *
 */
public class RankingListMasterVM {

	public Long id;
	
	public String name;

	public String description;
	
	public Long totalNoOfVotes;
	
	public Long totalNoOfProducts;
	
	public String tags; //comma seperated list of tags this Ranking List associated with
	
	public Timestamp createdDate;
	
	public Long userId;
	
	public List<RankingListProductVM> rankingListViews;
	
	public boolean isFeaturedList;
	
}
