$("#login").submit(function(event){
	event.preventDefault();

	var email = $("#email").val();
	var password = $("#password").val();
	
	$.ajax({
		  type:    "POST",
		  url:     "rest/user/login",
		  data:    {"email": email,"password":password},
		  async: false,
		  success: function(data) {
		        if(data === "Error"){
		        	alert("Wrong username or password!")
		        }
		        else{
		        	window.open("userPage.html",'_self',true)
		        }
		  }	
		});
});

$("#register").submit(function(event){
	event.preventDefault();
	var username = $("#register_username").val();
	var email = $("#register_email").val();
	var password = $("#register_pass").val();
	
	$.ajax({
		  type:    "POST",
		  url:     "rest/user/register",
		  data:    {"username":username,"email": email,"password":password},
		  async: false,
		  success: function(data) {
		       alert(data);
		  }	
		});
	$("#register")[0].reset();
	
});

$("#message").submit(function(event){
	event.preventDefault();
	
	var message = $("#message").val();
	var place = $("#place").val();
	
	$.ajax({
		  type:    "POST",
		  url:     "rest/user/message",
		  data:    {"message": message,"place":place},
		  async: false,
		  success: function() {
		       alert("Message successfully published");
		  }	
		});
});