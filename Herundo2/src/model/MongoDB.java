package model;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.aggregation.Accumulator;
import org.mongodb.morphia.aggregation.AggregationPipeline;
import org.mongodb.morphia.aggregation.Sort;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import static org.mongodb.morphia.aggregation.Group.grouping;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

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
	 
	 public void create_message(String username,String content,String place){
		 Message message = new Message(content, place, getUserByUsername(username).getId());
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
		 if(query.get().getListOfFollowingUsers() == null){
			 return new ArrayList<String>();
		 }
		 for(FollowedUser u: query.get().getListOfFollowingUsers()){
			 usernames.add(u.getUsername());
		 }
		 return usernames;
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
			 User u = getUserFromId(current.getId());
			 firstTenUsers.add(u.getUsername()+"-"+current.getCount());
		 }
		 return firstTenUsers;
	}

	 public int returnAllMessageForUser(String username){
		 return (int) this.datastore.find(Message.class).
				 filter("author_id",getUserByUsername(username).getId()).countAll();
		 
	 }
	 
	 public double getProcentIntervals(String beginHour,String endHour,String author_id){
		 MongoClient mongoClient = new MongoClient("localhost");
		 MongoDatabase db = mongoClient.getDatabase("hirundo");
		 DBObject command = new BasicDBObject();
		    command.put("eval", "db.messages.aggregate([{ $match :{ author_id :'"+author_id+"'} },"+
		    "{ $group : {  _id:{   date: {$dateToString : {format : \"%H:%M:%S\" , date : \"$publishDate\"}}  },  "
		    + "time: {$push: {$and: [  {$gte: [ { $dateToString : {format : \"%H:%M:%S\" , date : \"$publishDate\"}} "
		    + ",'"+beginHour+"']},{$lte: [ { $dateToString : {format : \"%H:%M:%S\" , date : \"$publishDate\"}} ,'"+endHour+"']}]}}}},"
		    + "{ $match:{\"time\":true}},{$group:{ _id: \"$time\", count: {$sum:1}}}])");
		    Document res = db.runCommand((Bson) command);
		    Document retval = (Document) res.get("retval");
		    ArrayList temp = (ArrayList) retval.get("_firstBatch");
		    
		    if(temp.size() == 0){
		    	return 0;
		    }
		    Document result = (Document) temp.get(0);
		    return (double) result.get("count");
	 }
	 
	 /*SECOND STATISTIC*/
	 public double messageProcentByHour(String username,String begin,String end){
		
		 double allMessagesForUser = returnAllMessageForUser(username);
		 double countOfReducedMessages = getProcentIntervals(begin,end,
				 								  getUserByUsername(username).getId());
		 if(allMessagesForUser == 0){
			 return 0;
		 }

		double percent = (100*countOfReducedMessages) / allMessagesForUser;
		 return Math.round(percent);
	 }

	 /*THIRD STATISTIC*/
	 public double messageProcentFromPlace(String username,String place){
		 Query<Message> query = this.datastore.find(Message.class);
		 query.and(
				 query.criteria("author_id").equal(getUserByUsername(username).getId()),
				 query.or(
						 query.criteria("publishPlace").equal(place),
						 query.criteria("publishPlace").equal(place.toLowerCase())
				 )
		 );
		 double allMessagesForUser = returnAllMessageForUser(username);
		 double countOfReducedMessages = query.countAll();
 
		 if(allMessagesForUser == 0){
			 return 0;
		 }
		 
		 double percent = (100 *countOfReducedMessages ) / allMessagesForUser;

		 return Math.round(percent);
	 }
	 
	 public double messageProcentForOthers(String username,List<String> alreadyKnownPlaces){
		 Query<Message> query = this.datastore.find(Message.class);
		 query.and(
				 query.criteria("author_id").equal(getUserByUsername(username).getId()),
				 query.criteria("publishPlace").notIn(alreadyKnownPlaces)
		 );
		 
		 int allMessagesForUser = returnAllMessageForUser(username);
		 int countOfReducedMessages = (int) query.countAll();
		 
		 if(allMessagesForUser == 0){
			 return 0;
		 }
		 
		 double percent = (100 *countOfReducedMessages ) / allMessagesForUser;
		 return percent;
	 }
	 
	 public User getUserFromId(String id){
		 ObjectId object_id = new ObjectId(id);
		 Query<User> user = this.datastore.find(User.class).
				 filter("_id", object_id);
		 return user.get();
	 }

	 public List<Message> getFollowingPeopleMessages(String username){
		 
		 List<String> followed_people_id = new ArrayList<String>();
		 
		 Query<User> query2 = this.datastore.find(User.class).
				 filter("username",username);
		 query2.retrievedFields(true, "listOfFollowingUsers");
		 
		 Collection<FollowedUser> followed_people = query2.get().getListOfFollowingUsers();
		 if(followed_people == null){
			 return null;
		 }
		 for(FollowedUser u: followed_people){
			 followed_people_id.add(u.getId());
		 }
		 
		 Query<Message> query = this.datastore.find(Message.class).
				 field("author_id").in(followed_people_id).order("-publishDate").limit(50);
		 System.out.println(query.asList());
		 return query.asList();
	 }
}