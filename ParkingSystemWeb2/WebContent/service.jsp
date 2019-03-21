<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">

<!-- jQuery library -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>

<!-- Latest compiled JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>
<script>
function validdd(){
	var model = document.forms["serviceform"]["service"];
	if(model.value==""){
		document.getElementById("e1").innerHTML = "Service time cannot be empty.";
		model.focus();
		return false;
	}
	if(!model.value.match(/^\d+$/)){
    	document.getElementById("e1").innerHTML = "Allowed input is numerical in hours.";
    	model.focus();
    	return false;
    }
	document.getElementById("e1").innerHTML = "";
	return true;
}
</script>
<style>
@import url('https://fonts.googleapis.com/css?family=Montserrat|Open+Sans');
body {
    font-family: 'Open Sans', sans-serif;
    font-family: 'Montserrat', sans-serif;
}
.page-header{
	border-bottom: 1px solid #428bca;
}
</style>
</head>
<body>
<div class="container">
<h2 class="page-header text-info">After slot creation</h2>
<div class="row">
<div class="col-sm-offset-3 col-sm-6">
<div class="page-header"><b>Entered Vehicle Details</b></div>
</div>
</div>
<div class="row">
<div class="col-sm-offset-3 col-sm-6">
<div>Model Name: <div class="text-info"><b><%= request.getParameter("name") %></b></div></div>
</div>
</div>
<div class="row">
<div class="col-sm-offset-3 col-sm-6">
<div>Type: <div class="text-info"><b><%= request.getParameter("type") %></b></div></div>
</div>
</div>
<div class="row">
<div class="col-sm-offset-3 col-sm-6">
<div>Reg No: <div class="text-info"><b><%= request.getParameter("regno") %></b></div></div>
</div>
</div>
<br>
<div class="row">
<form name="serviceform" class="col-sm-offset-3 col-sm-6" action="ticketservlet" method="post" onsubmit="return validdd()">
	<div class="form-group">
	<label>Enter the service time: (in hours)</label>
	<input class="form-control" type="number" name="service">
	<div id="e1"></div>
	</div>
	<br><br>
	<input class="form-control btn btn-info" type="submit" value="Submit">
</form>
</div>
</div>
</body>
</html>