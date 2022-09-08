function goRegister() { 
	location.replace("register");
}

function register() {
	document.getElementById("feedback").innerHTML = "";
	document.getElementById("email1_feedback").innerHTML = "";
	document.getElementById("email2_feedback").innerHTML = "";
	var email1 = document.getElementById("email1").value;
	var email2 = document.getElementById("email2").value;

	var valid1 = false;
	if (email1.split("@").length == 2) {
		if (email1.split("@")[1].split(".").length > 1) {
			valid1 = true;
		}
	}

	var valid2 = false;
	if (email2.split("@").length == 2) {
		if (email2.split("@")[1].split(".").length > 1) {
			valid2 = true;
		}
	}

	if (!valid1) {
		document.getElementById("email1_feedback").innerHTML = "you must enter a valid email address";
	}
	
	if (!valid2) {
		document.getElementById("email2_feedback").innerHTML = "you must enter a valid email address";
	}

	if (email1.length == 0) {
		document.getElementById("feedback").innerHTML = "you must enter a valid email address";

	} else if (email1 != email2) {
		document.getElementById("feedback").innerHTML = "email addresses must match";

	} else {
		var email = email1.replace("@", "%40");
		email = email.replace(".", "%2E");
		var url = "/register?email=" + email;
		location.replace(url); 
	}
}