package controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import model.Message;
import model.MongoDB;
import model.User;

@Path("/user")
public class UserServices {
	
	MongoDB db = new MongoDB();

	private String noUser(){
			return "No such user";
	}
	
  @POST
  @Path("login")
  public String login(@FormParam("email") String email,
		  			  @FormParam("password") String password,
		  			  @Context HttpServletRequest req){
  	  HttpSession session= req.getSession(true);

	  User user = db.login(email, password);
	  if(user != null){
		  session.setAttribute("user", user.getUsername());
		  return user.getUsername();
	  }
	  else{
		  return "Error";
	  }
  }
  
  @POST
  @Path("register")
  public String register(@FormParam("username") String username,
		  				 @FormParam("email") String email ,
		  				 @FormParam("password") String password){
	  return db.register(email, password, username);
  }
  
  @Path("logout")
  @GET
  public String logoutUser(@Context HttpServletRequest req) throws ServletException{
	  HttpSession session= req.getSession(true);
	  session.setAttribute("user", null);
	  return "";
  }
  
  
  @Path("message")
  @POST
  public String addMessage(@FormParam("message") String message,
		  				 @FormParam("place") String place,
		  				 @Context HttpServletRequest req){
  	  HttpSession session= req.getSession(true);
	  String username = (String) session.getAttribute("user");
	  if(username == null){
		  return noUser();
	  }
	  db.create_message(username,message, place);
	  return "";
  }
  
  @Path("allUsers")
  @GET
  public String getAllusers(@Context HttpServletRequest req){
	  HttpSession session= req.getSession(true);
	  String username = (String) session.getAttribute("user");
	   
	   if(username == null){
		   return noUser();
	   }
	   JSONObject json = new JSONObject();
	   List<String> allUsers = db.getAllOtherUsers(username);
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
  public String followUser(@FormParam("param") String following_user,
		  				 @Context HttpServletRequest req){
	  HttpSession session= req.getSession(true);
	  String username = (String) session.getAttribute("user");
	  if(username == null){
		  return noUser();
	  }
	  db.followUser(username, following_user);
	  return "";
  }

  @Path("Unfollow")
  @POST
  public String UnfollowUser(@FormParam("param") String unfollowedUser,
		  				   @Context HttpServletRequest req){
	  HttpSession session= req.getSession(true);
	  String username = (String) session.getAttribute("user");
	  if(username == null){
		  return noUser();
	  }
	  db.unfollowUser(username, unfollowedUser);
	  return "";
  }
  
  
  @Path("firstTen")
  @GET
  public String getFirstTenUsers(@QueryParam("content")String content){
	  JSONObject json = new JSONObject();
	  json.put("content",db.getFirstTenUser(content));
	  return json.toJSONString();
  }
  
  
  @Path("hour")
  @GET
  public String getProcentFromInterval(@Context HttpServletRequest req) throws ParseException{
	  HttpSession session= req.getSession(true);
	  String username = (String) session.getAttribute("user");
	  if(username == null){
		 return noUser();
	 }
	
	String firstDate = "24:00:00/04:00:00";
	String secondDate = "04:00:00/08:00:00";
	String thirdDate = "08:00:00/16:00:00";
	String forthDate = "16:00:00/24:00:00";
	 
	 JSONObject json = new JSONObject();

	 String[] first_date = firstDate.split("/");
	 String[] second_date = secondDate.split("/");
	 String[] third_date = thirdDate.split("/");
	 String[] forth_date = forthDate.split("/");
	
	 double first = db.messageProcentByHour(username, first_date[0], first_date[1]);
	 double second = db.messageProcentByHour(username, second_date[0], second_date[1]);
	 double third = db.messageProcentByHour(username, third_date[0], third_date[1]);
	 double forth = db.messageProcentByHour(username, forth_date[0], forth_date[1]);	 
	 
	 json.put("first",first);
	 json.put("second",second);
	 json.put("third",third);
	 json.put("forth",forth);

	 return json.toJSONString();
  }
  
  @Path("place")
  @GET
  public String getProcentForPlaces(@Context UriInfo uriInfo,@Context HttpServletRequest req){
	  
	  List<String> places = uriInfo.getQueryParameters().get("places[]");

	  HttpSession session= req.getSession(true);
	  String username = (String) session.getAttribute("user");
	  if(username == null){
		  return noUser();
	  }
	  JSONObject json = new JSONObject();

	  List<String> firstPlaces = new ArrayList<String>();
	  firstPlaces.add(places.get(0));
	  firstPlaces.add(places.get(1));
	  firstPlaces.add(places.get(2));
	  firstPlaces.add(places.get(0).toLowerCase());
	  firstPlaces.add(places.get(1).toLowerCase());
	  firstPlaces.add(places.get(2).toLowerCase());
	  
	  double firstPlaceProcent = db.messageProcentFromPlace(username, places.get(0));
	  double secondPlaceProcent = db.messageProcentFromPlace(username, places.get(1));
	  double thirdPlaceProcent = db.messageProcentFromPlace(username, places.get(2));
	  double forthPlaceProcent = db.messageProcentForOthers(username,firstPlaces);
	  
	  json.put(places.get(0), firstPlaceProcent);
	  json.put(places.get(1),secondPlaceProcent);
	  json.put(places.get(2), thirdPlaceProcent);
	  json.put(places.get(3), forthPlaceProcent);
	  
	  return json.toJSONString();
  }
  
  @Path("timeline")
  @GET
  public String getTimeline(@Context HttpServletRequest req){
	  
	  HttpSession session= req.getSession(true);
	  String username = (String) session.getAttribute("user");
	  
	  if(username == null){
		  return noUser();
	  }
	  
	  List<Message> messages = db.getFollowingPeopleMessages(username);
	  
	  if(messages == null){
		  return "";
	  }
	  
	  List<String> contents = new ArrayList<String>();
	  List<String> messageUser = new ArrayList<String>();
	  List<String> publishPlace = new ArrayList<String>();
	  List<String> publishDate = new ArrayList<String>();
	  
	  JSONObject json = new JSONObject();

	  for(Message m: messages){
		  contents.add(m.getContent());
		  messageUser.add(db.getUserFromId(m.getAuthor_id()).getUsername());
		  publishDate.add(m.getPublishDate().toString());
		  publishPlace.add(m.getPublishPlace());	
	  }
	  
	  json.put("content", contents);
	  json.put("users", messageUser);
	  json.put("place", publishPlace);
	  json.put("date", publishDate);
	  
	  
	  return json.toJSONString();
  } 
  
} 