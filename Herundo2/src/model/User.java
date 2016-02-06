package model;


import java.util.Collections;
import java.util.Date;
import java.util.List;

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
	List<User> listOfFollowingUsers;
	
	public User() {
	}
	
	public User(String username,String password,String email){
		setUsername(username);
		setPassword(getHashedPassword(password));
		setEmail(email);
		setRegistrationDate(new Date());
	}
	
	public User(String id,String classname,String username,String password,String email){
		setUsername(username);
		setPassword(getHashedPassword(password));
		setEmail(email);
		setRegistrationDate(new Date());
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
	public void setRegistrationDate(Date registrationDate) {
		if(registrationDate != null){
			this.registrationDate = registrationDate;
		}
	}
	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	public List<User> getListOfFollowingUsers() {
		return Collections.unmodifiableList(listOfFollowingUsers);
	}
	public void setListOfFollowingUsers(List<User> listOfFollowingUsers) {
		if(listOfFollowingUsers.size() > 0){
			this.listOfFollowingUsers = listOfFollowingUsers;
		}
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password="
				+ password + ", email=" + email + "]";
	}
		
}
