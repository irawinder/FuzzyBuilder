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