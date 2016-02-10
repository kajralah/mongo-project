package controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import model.MongoDB;
import model.User;

@Path("/user")
public class UserServices {
	
	MongoDB db = new MongoDB();
	
	//@Inject
	//private UserContext context;
	
  @POST
  @Path("login")
  public String login(@FormParam("email") String email,@FormParam("password") String password) throws ServletException{
	  User user = db.login(email, password);
	  if(user != null){
		  return user.getUsername();
	  }
	  else{
		  return "Error";
	  }
  }
  
  @POST
  @Path("register")
  public String register(@FormParam("username") String username,@FormParam("email") String email , @FormParam("password") String password){
	  return db.register(email, password, username);
  }
  /*
  @Path("logout")
  @GET
  public void logoutUser() throws ServletException{
  }
  */
  
  @Path("message")
  @POST
  public void addMessage(@FormParam("message") String message,@FormParam("place") String place){
	  db.create_message(message, place);
  }
  
  @Path("allUsers")
  @GET
  public String getAllusers(){
	   String username = "Klari";
	   JSONObject json = new JSONObject();
	   List<String> allUsers = db.getAllOtherUsers("Klari");
	   List<String> following = db.getAllFollowedUsersByUsername(username);
	   List<String> notFollowing = new ArrayList<String>();
	   for(String userUsername : allUsers){
			 if(!following.contains(userUsername)){
				 notFollowing.add(userUsername);
			 }
		 }
	   
	   json.put("following", following);
	   json.put("notfollowing", notFollowing);
	   
	   return json.toJSONString();
  }
  
  @Path("Follow")
  @POST
  public void followUser(@FormParam("Follow") String following_user){
	  String username = "Klari";
	  db.followUser(username, following_user);
  }

  @Path("Unfollow")
  @POST
  public void UnfollowUser(@FormParam("Unfollow") String unfollowedUser){
	  String username = "Klari";
	  db.unfollowUser("Klari", unfollowedUser);
  }
  
  @Path("firstTen")
  @POST
  public List<String> getFirstTenUsers(@FormParam("content") String content){
	  return db.getFirstTenUser(content);
  }
  
  
  @Path("hour")
  @POST
  public List<Double> getProcentFromInterval(@FormParam("firstDate") String firstDate,
		  							 @FormParam("secondDate") String secondDate,
		  							 @FormParam("thirdDate") String thirdDate,
		  							 @FormParam("forthDate") String forthDate) throws ParseException{
	 String[] first_date = firstDate.split("/");
	 String[] second_date = secondDate.split("/");
	 String[] third_date = thirdDate.split("/");
	 String[] forth_date = forthDate.split("/");
	 
	 double first = db.messageProcentByHour("Kali", first_date[0], first_date[1]);
	 double second = db.messageProcentByHour("Klari", second_date[0], second_date[1]);
	 double third = db.messageProcentByHour("Klari", third_date[0], third_date[1]);
	 double forth = db.messageProcentByHour("Klari", forth_date[0], forth_date[1]);
	 
	 List<Double> l = new ArrayList<Double>();
	 l.add(first);
	 l.add(second);
	 l.add(third);
	 l.add(forth);
	 System.out.println(l);
	 return l;
  }
  
} 