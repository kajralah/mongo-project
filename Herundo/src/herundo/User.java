package herundo;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

public class User {
	
	private String username;
	private String password;
	private String email;
	private Date registrationDate;
	boolean verified;
	List<User> listOfFollowingUsers;
	
	public User(String username,String password,String email) {
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
}
