/**
 * 
 */
package models;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import com.avaje.ebean.annotation.CreatedTimestamp;

/**
 * @author Mindnerves Technologies
 *
 */
@Entity
public class RankingListMaster extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6546064596468345658L;

	@Id
	@GeneratedValue
	public Long id;
	
	@Constraints.Required
	public String name;

	@Constraints.Required
	public String description;
	
	@Constraints.Required
	public Long totalNoOfVotes;
	
	@Constraints.Required
	public String tags; //comma seperated list of tags this Ranking List associated with
	
	@Constraints.Required
	@CreatedTimestamp
	public Timestamp createdDate;
	
	@Constraints.Required
	
	//@ManyToMany(targetEntity=User.class,mappedBy="id", cascade = CascadeType.ALL)
	public Long userId;
	
	@Column(name="featured_list")
	public String featuredList;
	
	@OneToMany(mappedBy="rankingList", cascade=CascadeType.ALL)
	public List<RankingListProduct> rankingListViews;

	public RankingListMaster(final String name, final String description,
			final Long totalNoOfVotes, final String tags, final Timestamp createdDate,
			final Long userId, final List<RankingListProduct> rankingListViews) {
		super();
		this.name = name;
		this.description = description;
		this.totalNoOfVotes = totalNoOfVotes;
		this.tags = tags;
		this.createdDate = createdDate;
		this.userId = userId;
		this.rankingListViews = rankingListViews;
	}
	
	public RankingListMaster() {
		
	}
	
	@Override
	public String toString() {
		return "RankingListMaster [id=" + id + ", name=" + name
				+ ", description=" + description + ", totalNoOfVotes="
				+ totalNoOfVotes + ", tags=" + tags + ", createdDate="
				+ createdDate + ", userId=" + userId + ", rankingListViews="
				+ rankingListViews + "]";
	}

	public static Model.Finder<Long,RankingListMaster> find = new Finder<Long, RankingListMaster>(Long.class, RankingListMaster.class);
	
}
