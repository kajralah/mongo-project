function ifNoUser(data){
	if(data === 'No such user'){
		alert("You're not logged in!");
		window.open("index.html","_self",false);
	}
}

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
	
	var place = $("#place").val();
	var message = $('textarea#message').val();
	$.ajax({
		  type:    "POST",
		  url:     "rest/user/message",
		  data:    {"message": message,"place":place},
		  async: false,
		  success: function(data) {
			   ifNoUser(data);
		       alert("Message successfully published");
		  }	
		});
});

function follow(type,username){
	var param = type.toString();
	$.ajax({
		  type:    "POST",
		  url:     "rest/user/"+type,
		  data:    {param : username},
		  async: false,
		  success: function(data) {
			   ifNoUser(data);
			   $("#timeline").click();
		  }	
		});
}

function visualizeUsers(data,type){
	var i=0;
	if(data != undefined){
	var onclick = "follow("+"'"+type+"'"+','+"'"+data[i]+"'"+")";
	for(var row = 0; row < data.length; row++){
		$("#allUsers").append("<div class=\"col-md-12\" ><span><b><font size=\"4\">"+data[i]+
				"</font><b></span><button id="+type+" onclick="+onclick+">"+type+
				"</button><hr>");
		
		$(".col-md-8").append("<p></p>");
			i++;
		}
	}
}

$("#users").click(function(event){
	$("#message").hide();
	$("#allStatistic").hide();
	$("#allUsers").empty();
	$("#allUsers").append("<b><span><font size=\"4\">List of All Users</font></span></b><br><br><br>");
	event.preventDefault();
	
	$.getJSON("rest/user/allUsers",
		  	  function (data) {
				ifNoUser(data);

				   if(data == ""){
					   return "";
				   }
				   visualizeUsers(data['following'],'Unfollow');
				   visualizeUsers(data['notfollowing'],'Follow');
				   $("#allUsers").show();
			  }
			);
});

	
$("#statistic").click(function(event){
	$("#floatitright").empty();
	$("#allStatistic").show();
	$("#message").hide();
	$("#allUsers").empty();
});

$("a#firstTen").click(function(event){
	event.preventDefault();
	$("#floatitright").empty();
	$("#floatitright").append("<input class=\"form-control input-sm\" id=\"searchWord\" " +
								"placeholder=\"Search word\" type=\"text\" /><br><br><br>" +
								"<button onclick=\"submit()\">Submit</button>");
});

function submit(){
	var content = $("#searchWord").val();
	$("#firstTenp").remove();
	$("#floatitright").append("<p id=\"firstTenp\"></p>");

	$.getJSON("rest/user/firstTen", {"content" : content},
		  	  function (data2) {
				ifNoUser(data2);
				var data = data2["content"];
				  var i=0;
					for(var row = 0; row < data.length; row++){
						var value = data[i].split("-");
						$("#firstTenp").append("<br><br><br><br>" +
								"<div class=\"col-md-12\" ><span><b><font size=\"4\">"+value[0]+
								"</font><b></span><span style=\"float:right\"><b>Messages: "+value[1]+"</b></span><hr>");
						$("#firstTenp").append("<p></p>");
							i++;
					}
			  }	
			);
};

$("a#time").click(function (event){
	$("#floatitright").empty();
	event.preventDefault();
	
	
	$.getJSON("rest/user/hour",
		  	  function (data) {
				ifNoUser(data);
				$("#floatitright").append("<table><tr><th>Interval</th><th>Procent of messages: </th>"+
										  "<tr><td>24:00-4:00</td><td>"+data["first"]+"%</td></tr>"+
				                          "<tr><td>4:00-8:00</td><td>"+data["second"]+"%</td></tr>"+
				                          "<tr><td>8.00-16.00</td><td>"+data["third"]+"%</td></tr>"+
				                          "<tr><td>16.00-24.00</td><td>"+data["forth"]+"%</td></tr></table>");
			  }
			);
});

$("a#place").click(function (event){
	event.preventDefault();
	$("#floatitright").empty();
	var places = ["Sofia","Varna","Burgas","Other"];
	
	$.getJSON("rest/user/place", {"places" : places},
	  	  function (data) {
			ifNoUser(data);
	  	  	$("#floatitright").append("<table>" +
	  	  							  "<tr><td><b>Place</b></td><td><b>Procent of messages: </b></td></tr>"+
	  	  							  "<tr><td><b>Sofia</b></td><td><b>"+data["Sofia"]+"%</b></td></tr>"+
	  	  							  "<tr><td><b>Varna</b></td><td><b>"+data["Varna"]+"%</b></td></tr>"+
	  	  							  "<tr><td><b>Burgas</b></td><td><b>"+data["Burgas"]+"%</b></td></tr>"+
	  	  							  "<tr><td><b>Other</b></td><td><b>"+data["Other"]+"%</b></td></tr>" +
	  	  							  "</table>");
		});
});


$("#timeline").click(function(event){
	$("#floatitright").hide();
	$("#allStatistic").hide();
	$("#message").hide();
	$("#allUsers").empty();
	
		event.preventDefault();
		$.ajax({
			  type:    "GET",
			  url:     "rest/user/timeline",
			  async: false,
			  success: function(data) {
				  ifNoUser(data);
				  if(data == ""){
					  return;
				  }
				  var info = $.parseJSON(data);
				  var table="";
				  table += "<table>";
				  table += "<tr><td><b>Content</b></td>";
				  table += "<td><b>User</b></td>";
				  table += "<td><b>Publish place</b></td>";
				  table += "<td><b>Publish date</b></td></tr>";
				  for(var i=0;i<info['content'].length;i++){
					  table += "<tr>";
					  table += "<td><b>"+info['content'][i]+"</b></td>";
					  table += "<td><b>"+info['users'][i]+"</b></td>";
					  table += "<td><b>"+info['place'][i]+"</b></td>";
					  table += "<td><b>"+info['date'][i]+"</b></td>";
					  table += "</tr>";
				  }
				  table += "</table>";
				  $("#allUsers").append(table);
			  }
		});
		$("#allUsers").show();

});

$("#logout").click(function(event){
	$.ajax({
		  type:    "GET",
		  url:     "rest/user/logout",
		  async: false,
		  success: function() {
			  alert("Logging out .. ");
			  window.open("index.html","_self",false);
		  }
	});
});


