package model;


import java.util.Date;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.utils.IndexType;

@Entity("messages")
@Indexes({
		@Index(fields = @Field(value="content",type=IndexType.TEXT)),
		@Index(fields = {@Field(value="author_id"),@Field(value="publishDate")})
		})
public class Message{
	
	@Id
	private String id;
	private String content;
	private String publishPlace;
	private Date publishDate;

	private String author_id;
	
	//private String author_name;
	
	//private List<String> authorInfo;
	
	public Message(){
	}
	
	public Message(String content,String publishPlace,String username){
		setContent(content);
		setPublishPlace(publishPlace);
		setPublishDate(new Date());
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		if(content.length() <= 140){
			this.content = content;
		}
	}

	public String getPublishPlace() {
		return publishPlace;
	}

	public void setPublishPlace(String publishPlace) {
		if(publishPlace != null){
			this.publishPlace = publishPlace;
		}
		else{
			publishPlace = "";
		}
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public String getAuthor_id() {
		return author_id;
	}

	public void setAuthor_id(String author_id) {
		this.author_id = author_id;
	}
	
}