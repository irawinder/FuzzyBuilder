function exitSurvey() {

	document.getElementById("feedback").innerHTML = "";

	var data = [];

	// Scenario
	var scenario = getRadio("_scenario");
	if (scenario === null) return;
	data.push(scenario);

	// Influence
	var influence = getText("_influence");
	if (influence === null) return;
	data.push(influence);

	// Satisfaction
	var satisfaction = getRadio("_satisfaction");
	if (satisfaction === null) return;
	data.push(satisfaction);

	// Confidence
	var confidence = getRadio("_confidence");
	if (confidence === null) return;
	data.push(confidence);

	// Usability
	var usability = getRadio("_usability");
	if (usability === null) return;
	data.push(usability);

	// Learning
	var learning = getRadio("_learning");
	if (learning === null) return;
	data.push(learning);

	// Comments
	var required = false;
	var comments = getText("_comments", required);
	if (comments === null) return;
	data.push(comments);

	var userID = document.getElementById("userID").innerHTML;
	postSurvey(userID, "exit", data);
}