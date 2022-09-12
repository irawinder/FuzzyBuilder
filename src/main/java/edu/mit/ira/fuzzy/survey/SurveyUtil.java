package edu.mit.ira.fuzzy.survey;

public class SurveyUtil {
	
	public static String radioHTML(String group, String id, String label, String value) {
		return radioHTML(group, id, label, value, "");
	}
	
	public static String radioHTML(String group, String aID, String label, String value, String leftMargin) {
		String lM = "";
		if (leftMargin.length() > 0) {
			lM = "style=\"margin-left: " + leftMargin + "px; ";
		}
		return "<input type=\"radio\" " + lM + "id=\"" + aID + "\" name=\"" + group + "\" value=\"" + value + "\">"
				+ "<label style=\"margin-left: 5px;\" for=\"" + aID + "\">" + label + "</label>";
	}
	
	public static String rangeHTML(String question, String id, String leftRange, String rightRange, int intervals) {
		String qID = "q" + id;
		String aID = "a" + id;
		String html = "<b><p id=\"" + qID + "\">" + question + "</p></b>";
		html += "<label><i>" + leftRange + "</i></label>";
		for (int i=1; i<=intervals; i++) {
			String iStr = "" + i;
			if (i==1) {
				html += SurveyUtil.radioHTML(aID, aID + "-" + iStr, iStr, iStr);
			} else {
				html += SurveyUtil.radioHTML(aID, aID + "-" + iStr, iStr, iStr, "30");
			}
		}
		html += "<label><i>" + rightRange + "</i></label>";
		return html + "<br><br>";
	}
	
	public static String choicesHTML(String question, String id, String[] labels) {
		String qID = "q" + id;
		String aID = "a" + id;
		String html = "<b><p id=\"" + qID + "\">" + question + "</p></b>";
		for (int i=1; i<=labels.length; i++) {
			html += SurveyUtil.radioHTML(aID, aID + "-" + i, labels[i-1], labels[i-1]);
			html += "<br>";
		}
		return html + "<br>";
	}
	
	public static String textHTML(String question, String id) {
		String qID = "q" + id;
		String aID = "a" + id;
		String html = "<p id=\"" + qID + "\">" + question + "</p>";
		html += "<input type=\"text\" id=\"" + aID + "\">";
		html += "<br><br>";
		return html;
	}
}