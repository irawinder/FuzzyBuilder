function postSurvey(user, type, data) {
	const xhttp = new XMLHttpRequest();
	xhttp.open("POST", "/survey/" + type + "?user=" + user, true);
	xhttp.send(JSON.stringify(data));
	xhttp.onload = function() {
		var message = "Survey Submitted. Please Click \"CONTINUE\"";
		document.getElementById("survey").innerHTML = "<p style=\"color: green\">" + message + "</p>";
    }
}

function surveyObject(q, a) {
	if (q != null) {
		if (a != null) {
			return {
				"q": q,
				"a": a
			};
		} else {
			document.getElementById("feedback").innerHTML = "You must complete all questions before submitting";
			return null;
		}
	} else {
		document.getElementById("feedback").innerHTML = "Site Error";
		return null;
	}
}

function noteObject(n) {
	if (n != null) {
		return {
			"n": n
		};
	} else {
		document.getElementById("feedback").innerHTML = "Site Error";
		return null;
	}
}

function getText(id) {
	return getText(id, true);
}

function getText(id, required) {
	let qID = "q" + id;
	let aID = "a" + id;
	var qElement = document.getElementById(qID);
	var aElement = document.getElementById(aID);
	if (qElement != null) {
		if (aElement != null ){
			var q = qElement.innerHTML;
			var a = aElement.value;
			if (a != "" || !required) {
				return surveyObject(q, a)
			} else {
				return surveyObject(q, null);
			}
		} else {
			return surveyObject(null, null)
		}
	} else {
		return surveyObject(null, null);
	}
}

function getRadio(id) {
	let qID = "q" + id;
	let aName = "a" + id;
	var qElement = document.getElementById(qID);
	if (qElement != null) {
		var q = qElement.innerHTML;
		var selected = document.querySelector('input[name="'  + aName + '"]:checked');
		if (selected != null) {
			var a = selected.value;
			return surveyObject(q, a);
		} else {
			return surveyObject(q, null);
		}
	} else {
		return surveyObject(null, null);
	}
}

function getNote(id) {
	var nID = "n" + id;
	var nElement = document.getElementById(nID);
	if (nElement != null) {
		var n = nElement.innerHTML;
		return noteObject(n);
	} else {
		return noteElement(null);
	}
}