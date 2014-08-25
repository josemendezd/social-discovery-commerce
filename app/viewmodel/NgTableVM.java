package viewmodel;

import java.util.List;

public class NgTableVM {
	public Long total;
	public List<CommentVM> comments;
	
	public NgTableVM(long total, List<CommentVM> comments) {
		this.total = total;
		this.comments = comments;
	}
}
