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

function create_message_template(){
	
}
/*
$("#timeline").click(function(event){
		event.preventDefault();
		
		$.ajax({
			  type:    "POST",
			  url:     "rest/user/message",
			  data:    {},
			  async: false,
			  success: function() {
			       
			  }	
			});
	});
*/

function follow(type,username){
	$.ajax({
		  type:    "POST",
		  url:     "rest/user/"+type,
		  data:    {type: username},
		  async: false,
		  success: function() {
			   $("#allUsers").click();
		  }	
		});
}

function visualizeUsers(data,type){
	var i=0;
	var onclick = "follow("+"'"+type+"'"+','+"'"+data[i]+"'"+")";
	for(var row = 0; row < data.length; row++){
		$("#allUsers").append("<div class=\"col-md-12\" ><span><b><font size=\"4\">"+data[i]+
				"</font><b></span><button id="+type+" onclick="+onclick+">"+type+
				"</button><hr>");
		
		$(".col-md-8").append("<p></p>");
			i++;
	}
}

$("#users").click(function(event){
	$("#message").hide();
	$("#allStatistic").hide();
	$("#allUsers").empty();
	$("#allUsers").append("<b><span><font size=\"4\">List of All Users</font></span></b><br><br><br>");
	event.preventDefault();
	$.getJSON("rest/user/allUsers", function (data) {
	   visualizeUsers(data['following'],'Unfollow');
	   visualizeUsers(data['notfollowing'],'Follow');
	   $("#allUsers").show();
	});
});

	
$("#statistic").click(function(event){
	$("#floatitright").empty();
	$("#allStatistic").show();
	$("#message").hide();
	$("#allUsers").empty();
});

$("#firstTen").click(function(event){
	event.preventDefault();
	$("#floatitright").empty();
	
	$("#floatitright").append("<input class=\"form-control input-sm\" id=\"searchWord\" " +
								"placeholder=\"Search word\" type=\"text\" /><br><br><br>" +
								"<button onclick=\"submit()\">Submit</button>");
});
function submit(){
	var content = $("#searchWord").val();
	
	$.ajax({
		  type:    "POST",
		  url:     "rest/user/firstTen",
		  data:    {"content": content},
		  async: false,
		  success: function(data) {
			  var i=0;
				for(var row = 0; row < data.length; row++){
					var value = data[i].split("-");
					$("#floatitright").append("<div class=\"col-md-12\" ><span><b><font size=\"4\">"+value[0]+
							"</font><b></span><b>Messages: "+value[1]+"</b><hr>");
					$("#floatitright").append("<p></p>");
						i++;
				}
		  }	
		});	
};

$("#time").click(function (event){
	$("#floatitright").empty();
	event.preventDefault();

	var first = "1970-01-01T24:00:00.000Z/2025-01-01T04:00:00.999Z";
	var second = "1970-01-01T04:00:00.000Z/2025-01-01T08:00:00.999Z";
	var third = "1970-01-01T08:00:00.000Z/2025-01-01T16:00:00.999Z";
	var forth = "1970-01-01T16:00:00.000Z/2025-01-01T24:00:00.999Z";
	
	$.ajax({
		  type:    "POST",
		  url:     "rest/user/hour",
		  data: { "firstDate": first , "secondDate":second,
			  	  "thirdDate":third , "forthDate":forth},
		  async: false,
		  success: function(data) {
			 
				$("#floatitright").append("<table><tr><th>Interval</th><th>Procent of messages: </th>"+
						"<tr><td>24:00-4:00</td><td>"+data[0]+"</td></tr>"+
						"<tr><td>4:00-8:00</td><td>"+data[1]+"</td></tr>"+
						"<tr><td>8.00-16.00</td><td>"+data[2]+"</td></tr>"+
						"<tr><td>16.00-24.00</td><td>"+data[3]+"</td></tr>");
		  }
		});
});

$("#place").click(function (event){
	$("#floatitright").empty();
	event.preventDefault();

	var Sofia = "Sofia";
	var Varna = "Varna";
	var Burgas = "Burgas";
	var Other = "Other";
	
	$.ajax({
		  type:    "POST",
		  url:     "rest/user/hour",
		  data: { "Sofia": Sofia , "Varna":Varna,
			  	  "Burgas":Burgas , "Other":Other},
		  async: false,
		  success: function(data) {
			 
				$("#floatitright").append("<table><tr><th>Interval</th><th>Procent of messages: </th>"+
						"<tr><td>Sofia</td><td>"+data[0]+"</td></tr>"+
						"<tr><td>Varna</td><td>"+data[1]+"</td></tr>"+
						"<tr><td>Burgas</td><td>"+data[2]+"</td></tr>"+
						"<tr><td>Other</td><td>"+data[3]+"</td></tr>");
		  }
		});
});

