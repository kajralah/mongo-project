package controller;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import model.MongoDB;
import model.User;
import model.UserContext;

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
  
  @Path("logout")
  @GET
  public void logoutUser() throws ServletException{
  }
  
  @Path("message")
  @POST
  public void addMessage(@FormParam("message") String message,@FormParam("place") String place){
	  db.create_message(message, place);
  }

} 