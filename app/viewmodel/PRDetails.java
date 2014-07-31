package viewmodel;

import java.util.ArrayList;
import java.util.List;

public class PRDetails {
	public List<ProductRateDetail> details = new ArrayList<>();
	public Integer maxRating=0;
	
	public PRDetails(List<ProductRateDetail> details, Integer maxRating) {
		this.details = details;
		this.maxRating = maxRating;
	}
}
