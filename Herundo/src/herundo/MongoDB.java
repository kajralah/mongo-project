package herundo;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDB {
	
	 private static final String DATABASE_NAME = "herundo";
	 private static final String server = "localhost";
	 private static MongoDatabase db;
	 private MongoClient mongoClient;
	 public MongoCollection<User> users;
     public MongoCollection<Message> messages;
	 
	 public MongoDB() {
		 mongoClient = new MongoClient(server);
	        db = mongoClient.getDatabase(DATABASE_NAME);
	        users = db.getCollection("users",User.class);
	        messages = db.getCollection("messages", Message.class);
	 }
	 
	 public MongoDatabase get() {
		 return db;
	 }
	 
	 public void addUser(User user){
		 users.insertOne(user);
	 }
	 
	 public User getUserByUsername(String username){
		User user = users.find(new Document("username",username)).first();
		return user;
	 }

}
