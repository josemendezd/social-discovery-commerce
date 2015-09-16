/**
 * 
 */
package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * @author MindNerves Technologies
 *
 */
@Entity
public class RankingListProduct extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6532198164954447613L;

	public static Model.Finder<Long,RankingListProduct> find = new Finder<Long, RankingListProduct>(Long.class, RankingListProduct.class);
	
	@Id
	@GeneratedValue
	public Long id;
	
	@Constraints.Required
	@ManyToOne
	@JoinColumn(name="rankingListId", referencedColumnName="id")
	public RankingListMaster rankingList;
	
	@Override
	public String toString() {
		return "RankingListProduct [id=" + id + ", rankingList=" + rankingList
				+ ", product=" + product + ", totalVotes=" + totalVotes + "]";
	}

	@Constraints.Required
	@ManyToOne
	public Product product;
	
	@Constraints.Required
	public Long totalVotes;
	
	public Long userId;
	
}
