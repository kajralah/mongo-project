package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.util.DateParser;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.aggregation.Accumulator;
import org.mongodb.morphia.aggregation.AggregationPipeline;
import org.mongodb.morphia.aggregation.Sort;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import static org.mongodb.morphia.aggregation.Group.grouping;












import com.mongodb.MongoClient;
import com.thoughtworks.xstream.converters.basic.DateConverter;

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
	 
	 public List<String> getAllFollowedUsersByUsername(String username){

		 List<String> usernames = new ArrayList<String>();
		 Query<User> query = this.datastore.find(User.class).
				 field("username").equal(username).
				 retrievedFields(true, "username","listOfFollowingUsers");
		 for(FollowedUser u: query.get().getListOfFollowingUsers()){
			 usernames.add(u.getUsername());
		 }
		 return usernames;
		/* return query.get().getListOfFollowingUsers();*/
	 }
	 
	 public List<String> getAllOtherUsers(String username){
		 List<String> usernames = new ArrayList<String>();
		 Query<User> query = this.datastore.find(User.class).
				 field("username").notEqual(username).
				 retrievedFields(true, "username");
		 for(User user : query.asList()){
			 usernames.add(user.getUsername());
		 }
		 return usernames;
	 }
	 
	 //text index don't get after whitespace ?? 
	 /*FIRST STATISTIC*/
	 public List<String> getFirstTenUser(String searchContent){
		 
		 List<String> firstTenUsers = new ArrayList<String>();
		
		 Query<Message> query = this.datastore.find(Message.class).search(searchContent);
	
		 AggregationPipeline agg  = this.datastore.createAggregation(Message.class).match(query)
				 .group("author_id",grouping("count", new Accumulator("$sum", 1)));
		 agg.sort(new Sort("count", -1));
		 agg.limit(10);
		 
		 Iterator<WrittenMessages> i = agg.aggregate(WrittenMessages.class);
		 while(i.hasNext()){
			 WrittenMessages current = i.next();
			 firstTenUsers.add(current.getId()+"-"+current.getCount());
		 }
		 return firstTenUsers;
	}

	 public int returnAllMessageForUser(String username){
		 return (int) this.datastore.find(Message.class).
				 filter("author_id", getUserByUsername(username).getId()).countAll();
	 }
	 
	 /*SECOND STATISTIC*/
	 public double messageProcentByHour(String username,String begin,String end) throws ParseException{
		 Date first_date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS\'Z\'").parse(begin);
		 Date last_date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS\'Z\'").parse(end);

		 Query<Message> query = this.datastore.find(Message.class);
		 	query.and(
				 query.criteria("author_id").equal(getUserByUsername(username).getId()),
				 query.and(
						 query.criteria("publishDate").greaterThanOrEq(first_date),
						 query.criteria("publishDate").lessThanOrEq(last_date)
						 )
			);
		 int allMessagesForUser = returnAllMessageForUser(username);
		 int countOfReducedMessages = (int) query.countAll();
		 
		 if(allMessagesForUser == 0){
			 return 0;
		 }
		 
		 double percent = (100*countOfReducedMessages) / allMessagesForUser;
		 return percent;
	 }
	 
	 /*THIRD STATISTIC*/
	 public double messageProcentFromPlace(String username,String place){
		 Query<Message> query = this.datastore.find(Message.class);
		 query.and(
				 query.criteria("author_id").equal(getUserByUsername(username).getId()),
				 query.criteria("publishPlace").equal(place)
		 );
		 int allMessagesForUser = returnAllMessageForUser(username);
		 int countOfReducedMessages = (int) query.countAll();
		 
		 if(allMessagesForUser == 0){
			 return 0;
		 }
		 
		 double percent = (100 *countOfReducedMessages ) / allMessagesForUser;
		 return percent;
	 }
	 
	 public List<Message> getFollowingPeopleMessages(String username){
		 
		 List<String> followed_people_id = new ArrayList<String>();
		 
		 Query<User> query2 = this.datastore.find(User.class).
				 filter("username",username);
		 query2.retrievedFields(true, "listOfFollowingUsers");
		 
		 Collection<FollowedUser> followed_people = query2.get().getListOfFollowingUsers();
		 
		 for(FollowedUser u: followed_people){
			 followed_people_id.add(u.getId());
		 }
		 
		 Query<Message> query = this.datastore.find(Message.class).
				 field("author_id").in(followed_people_id).order("-publishDate");
		 return query.asList();
	 }
}