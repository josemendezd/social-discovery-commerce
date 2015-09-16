package viewmodel;


public class AddProductToRankingListVM {
	public Long id;
	public Long listId;
	public String productName;
	public String productUrl;
	public Boolean isVisible = false;
	@Override
	public String toString() {
		return "AddProductToRankingListVM [listId=" + listId + ", productName="
				+ productName + ", productUrl="
				+ productUrl + "]";
	}
	
	
	
}
