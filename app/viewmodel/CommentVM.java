package viewmodel;

import java.util.Date;

import models.BlogComment;
import models.Comment;
import models.Product;

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
	
	public CommentVM(BlogComment c) {
		this.id = c.id;
		this.postedAt = c.postedAt;
		this.content = c.content;
		this.post_id = c.post.id;
		this.prod_name = c.post.title;
	}
	
	public CommentVM(Product p) {
		this.id = p.id;
		this.postedAt = p.timeofadd;
		this.content = p.description;
		this.post_id = p.id;
		this.prod_name = p.productname;
	}
}
