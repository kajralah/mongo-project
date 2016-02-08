package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.aggregation.Accumulator;
import org.mongodb.morphia.aggregation.AggregationPipeline;
import org.mongodb.morphia.aggregation.Sort;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import static org.mongodb.morphia.aggregation.Group.grouping;







import com.mongodb.MongoClient;

import edu.emory.mathcs.backport.java.util.Collections;

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
		 /*MapperOptions opt = new MapperOptions();
		 opt.setStoreNulls(true);*/
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
	 
	 public void create_message(String content,String place){
		 
		 //Klari = context.getCurrentUser.getuser_id;
		 
		 Message message = new Message(content, place, "Kali");
		 this.addMessage(message);
	 }	 
	 
	 private User getUserByUsername(String username){
		 Query<User> query = this.datastore.find(User.class);
			query.filter("username", username);
		 return query.get();
	 }
	 
	 public void followUser(String username,String following_user_username){
		Query<User> query = this.datastore.find(User.class);
			query.filter("username", username);
		User followedUser = getUserByUsername(following_user_username);
		
		FollowedUser followeduser = new FollowedUser(followedUser.getId(),followedUser.getUsername());

		UpdateOperations<User> ops =
				 this.datastore.createUpdateOperations(User.class).
				 add("listOfFollowingUsers",followeduser);
		this.datastore.update(query, ops);
	 }
	 
	 public void unfollowUser(String username,String unfollowedUser){
		 Query<User> query = this.datastore.find(User.class);
			query.filter("username", username);
		User followedUser = getUserByUsername(unfollowedUser);
		
		FollowedUser followeduser = new FollowedUser(followedUser.getId(),followedUser.getUsername());

		UpdateOperations<User> ops =
				 this.datastore.createUpdateOperations(User.class).
				 removeAll("listOfFollowingUsers",followeduser);
		this.datastore.update(query, ops);
	 }
	 
	 public User getAllFollowedUsersByUsername(String username){

		 Query<User> query = this.datastore.find(User.class).
				 field("username").equal(username).
				 retrievedFields(true, "username","listOfFollowingUsers");
		return query.get();
	 }
	 
	 //text index don't get after whitespace ?? 
	 /*FIRST STATISTIC*/
	 public Collection getFirstTenUser(String searchContent){
		 
		 List<String> firstTenUsers = new ArrayList<String>();
		
		 Query<Message> query = this.datastore.find(Message.class).search(searchContent);
	
		 AggregationPipeline agg  = this.datastore.createAggregation(Message.class).match(query)
				 .group("author_id",grouping("count", new Accumulator("$sum", 1)));
		 agg.sort(new Sort("count", -1));
		System.out.println(agg);
		 Iterator<WrittenMessages> i = agg.aggregate(WrittenMessages.class);
		 while(i.hasNext()){
			 firstTenUsers.add(i.next().getId());
		 }
		 return Collections.unmodifiableCollection(firstTenUsers);
	}
	 
	 public int returnAllMessageForUser(String username){
		 return (int) this.datastore.find(Message.class).
				 filter("author_id", getUserByUsername(username).getId()).countAll();
	 }
	 
	 /*SECOND STATISTIC*/
	 public int messageProcentByHour(String username,String beginHour,String endHour){
		 Query<Message> query = this.datastore.find(Message.class);
		 	query.and(
				 query.criteria("author_id").equal(getUserByUsername(username).getId()),
				 query.and(
						 query.criteria("publishDate").greaterThanOrEq(beginHour),
						 query.criteria("publishDate").lessThanOrEq(endHour)
						 )
			);
		 int allMessagesForUser = returnAllMessageForUser(username);
		 int countOfReducedMessages = (int) query.countAll();
		 
		 int percent = (100*countOfReducedMessages) / allMessagesForUser;
		 return percent;
	 }
	 
	 /*THIRD STATISTIC*/
	 public int messageProcentFromPlace(String username,String place){
		 Query<Message> query = this.datastore.find(Message.class);
		 query.and(
				 query.criteria("author_id").equal(getUserByUsername(username).getId()),
				 query.criteria("publishPlace").equal(place)
		 );
		 int allMessagesForUser = returnAllMessageForUser(username);
		 int countOfReducedMessages = (int) query.countAll();
		 
		 int percent = (100 *countOfReducedMessages ) / allMessagesForUser;
		 return percent;
	 }
	 
	 public List<Message> getFollowingPeopleMessages(String username){
		 
		 List<String> followed_people_id = new ArrayList<String>();
		 
		 Query<User> query2 = this.datastore.find(User.class).
				 filter("username",username);
		 query2.retrievedFields(true, "listOfFollowingUsers");
		 
		 List<FollowedUser> followed_people = query2.get().getListOfFollowingUsers();
		 
		 for(FollowedUser u: followed_people){
			 followed_people_id.add(u.getId());
		 }
		 followed_people_id.add("Kali");
		 
		 Query<Message> query = this.datastore.find(Message.class).
				 field("author_id").in(followed_people_id);
		 
		 return query.asList();
	 }
}
