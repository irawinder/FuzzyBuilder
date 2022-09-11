function clickOnEnter(textID, buttonID) {
	var input = document.getElementById(textID);
	input.addEventListener("keypress", function(event) {
	  if (event.key === "Enter") {
	    event.preventDefault();
	    document.getElementById(buttonID).click();
	  }
	});
}