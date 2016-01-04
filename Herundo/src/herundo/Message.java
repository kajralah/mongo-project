package herundo;

import java.util.Date;

public class Message {
	
	private String content;
	private String publishPlace;
	private Date publishDate;
	private String username;
	
	public Message(String content,String publishPlace,String username){
		setContent(content);
		setPublishPlace(publishPlace);
		setPublishDate(new Date());
		setUsername(username);
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		if(username != null){
			this.username = username;
		}
	}
}