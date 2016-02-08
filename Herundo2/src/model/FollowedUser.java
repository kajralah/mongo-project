package model;

import org.mongodb.morphia.annotations.Id;

public class FollowedUser {
	
	@Id
	private String id;
	private String username;
	
	public FollowedUser(){}
	
	public FollowedUser(String id,String username) {
		this.id = id;
		this.username = username;
	}
	
	public FollowedUser(String classname,String id,String username) {
		this.id = id;
		this.username = username;
	}

	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	@Override
	public String toString() {
		return "FollowedUser [id=" + id + ", username=" + username + "]";
	}
	
}
