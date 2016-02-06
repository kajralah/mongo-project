package model;

import org.apache.commons.codec.digest.DigestUtils;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.mongodb.MongoClient;

public class MongoDB{
	
	 private static final String DATABASE_NAME = "hirundo";
	 private static final String server = "localhost";
     private Morphia morphia;
     private Datastore datastore;
	 
	 public MongoDB() {
		 	this.morphia = new Morphia();
			morphia.map(User.class);
			morphia.map(Message.class);
			this.datastore = this.morphia.createDatastore(new MongoClient(server), DATABASE_NAME);
	        this.datastore.ensureIndexes();
	 }
	 
	 public void addUser(User user){
		 this.datastore.save(user);
	 }
	 
	 public void addMessage(Message message){
		 this.datastore.save(message);
	 }
	 
	 public User login(String email,String password){
		Query<User> query = this.datastore.find(User.class);
		query.filter("email", email);
		query.filter("password",DigestUtils.shaHex(password));
		User user = query.get();
		return user;
	 }
	 
	 public boolean isUniqueUsername(String username){
		 User user = this.datastore.find(User.class,"username",username).get();
		 if(user == null){
			 return true;
		 }
		return false;
	 }
	 
	 public String register(String email,String password,String username){
		 if(login(email,password) != null){
			 return "You're already registered!";
		 }
		 if(isUniqueUsername(username) == true){
			 addUser(new User(username,password,email));
			 return "You're now registered";
		 }
		 return "Username is taken";
	 }
	 
}
