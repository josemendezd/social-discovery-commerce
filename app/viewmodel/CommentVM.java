package viewmodel;

import java.util.Date;

import models.Comment;

public class CommentVM {
	public Long id;
	public Date postedAt;
	public String content;
	public Long post_id;
	public String prod_name;
	public String prod_url;
	
	public CommentVM(Comment c) {
		this.id = c.id;
		this.postedAt = c.postedAt;
		this.content = c.content;
		this.post_id = c.post.id;
		this.prod_name = c.post.productname;
	}
}
