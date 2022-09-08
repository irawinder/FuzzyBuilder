function entrySurvey(userID) {

}

function loadDoc() {
  const xhttp = new XMLHttpRequest();
  xhttp.onload = function() {
    document.getElementById("demo").innerHTML = this.responseText;
    }
  xhttp.open("GET", "opensui/init", true);
  xhttp.send();
}