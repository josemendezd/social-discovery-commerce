package viewmodel;


public class RankingListProductVM {
	public Long id;
	public Long listId;
	public String productName;
	public Long productId;
	public String productUrl;
	public Long totalVotes;
	public Long views;
	public Float productRating;
	public Double avgRating;
	public Long userId;
	
	@Override
	public String toString() {
		return "RankingListProductVM [listId=" + listId + ", productName="
				+ productName + ", productId=" + productId + ", productUrl="
				+ productUrl + ", totalVotes=" + totalVotes + ", views="
				+ views + ", productRating=" + productRating + ", userId=" + userId + "]";
	}
	
	
	
}
