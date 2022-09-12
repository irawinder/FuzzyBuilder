function entrySurvey() {

	document.getElementById("feedback").innerHTML = "";

	var data = [];

	// Age
	var age = getRadio("_age");
	if (age === null) return;
	data.push(age);

	// Background
	var background = getRadio("_background");
	if (background === null) return;
	data.push(background);

	// Field Experience
	var fieldNote = getNote("_fieldExp");
	if (fieldNote === null) return;
	data.push(fieldNote);
	for (let i=1; i<=5; i++) {
		let id = "_fieldExp." + i;
		var field = getRadio(id);
		if (field === null) return;
		data.push(field);
	}

	// CAD Experience
	var cadExp = getRadio("_cadExp");
	if (cadExp === null) return;
	data.push(cadExp);

	// CAD Frequency
	var cadFreq = getRadio("_cadFreq");
	if (cadFreq === null) return;
	data.push(cadFreq);

	var userID = document.getElementById("userID").innerHTML;
	postSurvey(userID, "entry", data);
}

function postSurvey(user, type, data) {
	const xhttp = new XMLHttpRequest();
	xhttp.open("POST", "/survey/" + type + "?user=" + user, true);
	xhttp.send(JSON.stringify(data));
	xhttp.onload = function() {
    	document.getElementById("survey").innerHTML = "<p style=\"color: green\">Survey Submitted</p>";
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
	let qID = "q" + id;
	let aID = "a" + id;
	var qElement = document.getElementById(qID);
	var aElement = document.getElementById(aID);
	if (qElement != null) {
		if (aElement != null ){
			var q = qElement.innerHTML;
			var a = aElement.value;
			if (a != "") {
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