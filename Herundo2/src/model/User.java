package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Property;

@Entity("users")
@Indexes({
		@Index(fields = {@Field("email"), @Field("password")}),
		@Index(fields = {@Field("username"), @Field("listOfFollowingUsers")})
		})
public class User{
	
	@Id
	private String id;
	
	private String username;
	private String password;
	private String email;
	
	@Property("registration_date")
	private Date registrationDate;
	
	boolean verified;
	
	ArrayList<FollowedUser> listOfFollowingUsers;
	
	
	public User() {
	}
	
	public User(String username,String password,String email){
		setUsername(username);
		setPassword(getHashedPassword(password));
		setEmail(email);
		setRegistrationDate(new Date());
		setListOfFollowingUsers(new ArrayList<FollowedUser>());
	}
		
	public User(String id,String classname,String username,String password,String email,ArrayList<FollowedUser> listOfFollowingUsers){
		setUsername(username);
		setPassword(getHashedPassword(password));
		setEmail(email);
		setRegistrationDate(new Date());
		setListOfFollowingUsers(listOfFollowingUsers);
	}
	
	private String getHashedPassword(String password){
		return DigestUtils.shaHex(password);
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public Date getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(Date date) {
		if(date != null){
			this.registrationDate = date;
		}
	}
	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	public Collection<FollowedUser> getListOfFollowingUsers() {
			return listOfFollowingUsers;
	}
	
	public void setListOfFollowingUsers(ArrayList<FollowedUser> listOfFollowingUsers) {
		if(listOfFollowingUsers.size() > 0){
			this.listOfFollowingUsers = listOfFollowingUsers;
		}
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password="
				+ password + ", email=" + email + ", registrationDate="
				+ registrationDate + ", verified=" + verified
				+ ", listOfFollowingUsers=" + listOfFollowingUsers + "]";
	}

	public String getId() {
		return id;
	}
		
}
