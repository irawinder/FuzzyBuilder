package edu.mit.ira.fuzzy.pages;

import edu.mit.ira.fuzzy.io.user.Register;

public class Pages {

	private static int NUM_STUDY_PAGES = 5;
	
	/*
	public static String generalSite() {
		String head = makeHead();
		String body = makeGeneralBody();
		return assemblePage(head, body);
	}
	*/
	
	public static String studyIntroSite() {
		String head = makeHead();
		String body = makeStudyIntroBody();
		return assemblePage(head, body);
	}
	
	public static String registrationSite(String feedback) {
		String head = makeHead();
		String body = makeRegistrationBody(feedback);
		return assemblePage(head, body);
	}
	
	public static String registrationCompleteSite(String userID, String email) {
		String head = makeHead();
		String body = makeRegistrationCompleteBody(userID, email);
		return assemblePage(head, body);
	}

	public static String studySite(String user, String page) {
		String head = makeHead();
		String body = makeStudyBody(user, page);
		return assemblePage(head, body);
	}

	public static String nullSite() {
		String head = makeHead();
		String body = makeNullBody();
		return assemblePage(head, body);
	}

	private static String assemblePage(String head, String body) {
		return "<!DOCTYPE html><html>" + head + body + "</html>";
	}

	private static String makeHead() {
		String head = "<head>";

		head += "<title>Beaverton</title>";

		head += "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">";
		head += "<meta name=\"keywords\" content=\"MIT, University of Tokyo, Ira Winder, Daniel Fink, Max Walker\">";
		head += "<meta name=\"description\" content=\"Beaverton is an experimental study conducted by Ira Winder from MIT.\">";

		head += "<style>";

		String imgUrl = "https://github.com/irawinder/FuzzyBuilder/blob/master/screenshots/massing.png?raw=true";
		
		String generalCSS = "text-align:left;"
				+ "margin-left: 40px;"
				+ "margin-right: 40px;"
				+ "font-family:Helvetica, sans-serif;";
		
		String bodyTextCSS = "font-size:14px;"
				+ "font-style:normal;"
				+ "font-weight:normal;"
				+ "color:#525252;";
		
		head += "body {"
				+ "margin-bottom: 60px;"
				+ "margin-top: 40px;"
				+ "max-width: 800px;"
				+ "background-color:#ffffff;"
				+ "background-image:url(" + imgUrl + ");"
				+ "background-repeat:no-repeat;"
				+ "background-position:center center;"
				+ "background-attachment:fixed;"
				+ "}";
		
		head += "h1 { " + generalCSS + "color:#696969; }";
		head += "h2 { " + generalCSS + "color:#696969; }";
		head += "h3 { " + generalCSS + "color:#696969; }";
		head += "p { " + generalCSS + bodyTextCSS + " }";
		head += "label { " + generalCSS + bodyTextCSS + " }";
		head += "input { " + generalCSS + bodyTextCSS + " }";
		head += "button { " + generalCSS + bodyTextCSS + " }";
		head += "li {" + generalCSS + bodyTextCSS + "}";
		head += "ul li { margin-bottom: 10px; }";
		head += "ol li { margin-bottom: 10px; }";
		head += "img { margin-left: 40px; }";

		head += "</style>";

		head +="</head>";

		return head;
	}
	
	/*
	private static String makeGeneralBody() {

		String body = "<body>";

		body += wrapText("h1", "FuzzyIO");
		body += wrapText("p", "FuzzyIO is a server for generating \"fuzzy\" resolution real estate development scenarios.");
		body += wrapText("p", "This is just the \"back end\". If you want to use fuzzy builder,  you need to use a \"front end\" user interface like <a href=\"http://opensui.org/\" target=\"_blank\">openSUI</a>.");

		body += wrapText("h2", "Model Components");
		body += wrapText("p", "A <b>Function</b> describes the principal activity within a volume of enclosed space. (e.g. \"Residential\" or \"Retail\")");
		body += wrapText("p", "A <b>Voxel</b> is a small, indivisible unit of enclosed space consisting of a square base, height, and single <b>Function</b>");

		body += wrapText("p", "A <b>Zone</b> is a grid-based array of <b>Voxels</b> with a common <b>Function</b>. If a <b>Zone</b> consists of multiple floors, all floors share the same footprint.");
		body += wrapText("p", "A <b>Volume</b> is a stack of <b>Zones</b> that all share a common footprint, but each zone may have a different <b>Function</b> and number of floors");
		body += wrapText("p", "A <b>Podium Volume</b> is a special <b>Volume</b> with a footprint defined by an off-set from a <b>Parcel</b>'s edges and subtracting any <b>Podium Exclusion Areas</b>");
		body += wrapText("p", "A <b>Podium Exclusion Area</b> is an area, defined by a polygon, where <b>Voxels</b> cannot be placed.");
		body += wrapText("p", "A <b>Tower Volume</b> is a special <b>Volume</b> with a rectangular footprint. It must be placed within a <b>Parcel</b>.");
		body += wrapText("p", "A <b>Parcel</b> is the set of flat, grid-based <b>Voxels</b> that fit within the boundaries of a defined polygon. Parcels cannot overlap each other.");
		body += wrapText("p", "A <b>Scenario</b> is a specific configuration of <b>Voxels</b> specified by <b>Parcels</b>, <b>Volumes</b>, and <b>Zones</b>.");

		body += wrapText("h2", "Resources");

		body += "<ul>";
		body += wrapText("li", "Launch <a href=\"http://opensui.org/\" target=\"_blank\">openSUI</a> (to use FuzzyIO)");
		body += wrapText("li", "<a href=\"https://youtu.be/cNoS-bhRPEw\" target=\"_blank\">FuzzyIO Tutorial</a> on Youtube");
		body += wrapText("li", "<a href=\"https://youtu.be/z7514vh02u0\" target=\"_blank\">FuzzyIO Usage</a> (Timelapse) on Youtube");
		body += "</ul>";

		body += wrapText("h2", "Contact");
		body += wrapText("p", "fuzzy-io <i>[at]</i> mit <i>[dot]</i> edu");

		body += "</body>";

		return body;
	}
	*/
	
	private static String studyBodyHeader() {
		String bodyText = "";
		bodyText += wrapText("h1", "Beaverton");
		bodyText += wrapText("p", "A Research Study by Ira Winder<br>MIT and Univeristy of Tokyo<br>ira [at] mit [dot] edu");
		bodyText += "<hr>";
		return bodyText;
	}
	
	private static String makeStudyIntroBody() {
		
		String body = "<body>";
		
		body += "<script> function register() { location.replace(\"/register\"); } </script>";
		
		body += studyBodyHeader();
		
		body += wrapText("h2", "Introduction");
		
		String imgUrl = "https://github.com/irawinder/FuzzyBuilder/blob/master/screenshots/v1.0-invert_sm.png?raw=true";
		body += "<img style=\"width: 600px; height: 375px;\" src=\"" + imgUrl + "\" alt=\"Screenshot of Digital Model, FuzzyIO\">";
		
		body += wrapText("p", "Welcome to Beaverton, a research study! We deeply appreciate your participation.");

		body += wrapText("p", "The University of Tokyo and MIT are investigating how people make decisions using interactive models.");
		
		body += wrapText("p", "In this exercise, we will ask you to help design a building in the hypothetical city of Beaverton.");
		
		body += wrapText("p", "As you work, we'll be collecting data as you interact with a digital model.");
		
		body += wrapText("h2", "Requirements");

		body += "<ul>";

		body += wrapText("li", "PC or laptop with speakers or earphones" +
				"<br><i>(Do not try this exercise on a tablet or smart phone)</i>");

		body += wrapText("li", "Reliable internet connection");

		body += wrapText("li", "Web browser (Chrome, Safari, Firefox, etc)" +
				"<br><i>(The browser you are using to read this text is probably fine)</i>");

		body += wrapText("li", "Keyboard and mouse" +
				"<br><i>(Track pad is fine, but you may have better experience using a two-button mouse)</i>");
		
		body += wrapText("li", "You must complete the exercise in <u>one sitting</u>" +
				"<br><i>(There is no time limit, but this exercise may take 30 - 90 minutes)</i>");

		body += "</ul>";

		body += wrapText("h2", "Get Started!");
		
		body += "<button onclick=\"register()\">Continue to Registration</button>";
		
		body += "</body>";
		
		return body;
	}
	
	private static String makeRegistrationBody(String feedback) {

		String body = "<body>";
		
		body += "<script>";
		
		body += "function register() {"
				
				+ "document.getElementById(\"feedback\").innerHTML = \"\";"
				+ "document.getElementById(\"email1_feedback\").innerHTML = \"\";"
				+ "document.getElementById(\"email2_feedback\").innerHTML = \"\";"
				
				+ "var email1 = document.getElementById(\"email1\").value;"
				+ "var email2 = document.getElementById(\"email2\").value;"
				
				+ "var valid1 = false;"
				+ "if (email1.split(\"@\").length == 2)"
				+ "{"
				+ "if (email1.split(\"@\")[1].split(\".\").length == 2)"
				+ "{"
				+ "valid1 = true;"
				+ "}"
				+ "}"
				
				+ "var valid2 = false;"
				+ "if (email2.split(\"@\").length == 2)"
				+ "{"
				+ "if (email2.split(\"@\")[1].split(\".\").length == 2)"
				+ "{"
				+ "valid2 = true;"
				+ "}"
				+ "}"
				
				+ "if (!valid1)"
				+ "{"
				+ "document.getElementById(\"email1_feedback\").innerHTML = \"you must enter a valid email address\";"
				+ "}"
				+ "if (!valid2)"
				+ "{"
				+ "document.getElementById(\"email2_feedback\").innerHTML = \"you must enter a valid email address\";"
				+ "}"
				
				+ "if (email1.length == 0)"
				+ "{"
				+ "document.getElementById(\"feedback\").innerHTML = \"you must enter a valid email address\";"
				+ "}"
				+ "else if (email1 != email2)"
				+ "{"
				+ "document.getElementById(\"feedback\").innerHTML = \"email addresses must match\";"
				+ "}"
				+ "else"
				+ "{"
				+ "var email = email1.replace(\"@\", \"%40\");"
				+ "email = email.replace(\".\", \"%2E\");"
				+ "var url = \"/register?email=\" + email;"
				+ "location.replace(url); "
				+ "}"
				
				+ "}";
		
		body += "</script>";
		
		body += studyBodyHeader();
		
		body += wrapText("h2", "User Registration");
		
		body += "<label for=\"email1\">Please enter your email address:</label><br>";
		
		body += "<input type=\"text\" id=\"email1\" style=\"width: 300px;\"><p style=\"color: red;\" id=\"email1_feedback\"></p><br><br>";
		
		body += "<label for=\"email2\">Please enter your email address again:</label><br>";
		
		body += "<input onSubmit=\"register()\" type=\"text\" id=\"email2\" style=\"width: 300px;\"><p style=\"color: red;\" id=\"email2_feedback\"></p><br><br>";
		
		body += "<button onclick=\"register()\">Register</button>";
		
		body += "<p style=\"color: red;\" id=\"feedback\">" + feedback + "</p>";
		
		body += wrapText("p", "<i>Your personal information will not be shared.<br>Email addresses are only used for authentication,<br>or in the rare case that we need to contact you. </i>");
		
		body += "</body>";

		return body;
	}
	
	private static String makeRegistrationCompleteBody(String userID, String email) {

		String body = "<body>";
		
		body += studyBodyHeader();
		
		body += wrapText("h2", "Registration Complete!");
		
		body += wrapText("p", "Email: " + email);
		
		body += wrapText("p", "(You will not be able to register with this email address again!)");
		
		body += wrapText("h2", "Personal Access Link");
		
		body += wrapText("p", "We have generated a unique personal access link, just for you.");
		
		body += wrapText("p", "Please save this link in a safe place, you will not be able to navigate back to this page again.");
		
		body += wrapText("p", "To begin right away, click below.");
		
		body += wrapText("p", "<a href=\"/?user=" + userID + "\">http://fuzzy.glassmatrix.org/?user=" + userID + "</a>");
		
		body += "</body>";
		
		return body;
	}
	
	private static String makeStudyBody(String user, String page) {

		String userPrefix;
		if (user.length() > Register.CODE_LENGTH) {
			userPrefix = user.substring(0, user.length() - Register.CODE_LENGTH);
		} else {
			userPrefix = "";
		}
		
		String body = "<body>";

		body += studyBodyHeader();
		
		if (!page.equals("" + NUM_STUDY_PAGES)) {
			body += wrapText("p", "User ID: <b>" + user + "</b>");
			body += wrapText("p", "page: " + page + " of " + NUM_STUDY_PAGES);
			body += "<hr>";
		}

		if (page.equals("1")) {

			body += wrapText("h2", "User ID");
			
			body += wrapText("p", "First, we need to get some housekeeping out of the way.");

			body += wrapText("p", "You have been assigned an exclusive User ID.<br><i>(You can see it at the top of this web page -- it should be an animal followed by " + Register.CODE_LENGTH + " characters)</i>.");

			body += wrapText("p", "Please use this ID throughout the exercise, and don't share it with anyone.<br>The User ID will remain at the top of every page for reference.");

			body += wrapText("h2", "Pre-Survey");

			body += wrapText("p", "Now, please complete this pre-survey.");

			String presurveyUrl = "https://forms.gle/cK3dQozbQyt1S2zn7";

			body += wrapText("p", "--> <a href=\"" + presurveyUrl + "\" target=\"_blank\">Pre-Survey</a>");

			body += wrapText("p", "(The survey will open in a new tab. Come back here and click \"CONTINUE\" when you are done.)");

		} else if (page.equals("2")) {

			body += wrapText("h2", "FuzzyIO Tutorial");

			body += wrapText("p", "Now, we need you to prepare for your role by watching a quick tutorial video.");

			body += wrapText("p", "Please watch the entire video with the sound on, and do not skip any parts.");

			String tutorialUrl;
			
			if (userPrefix.equals(Register.USER_PREFIXES[0])) {
				
				// Needs to be updated
				tutorialUrl = "https://youtu.be/cNoS-bhRPEw";
				
			} else if (userPrefix.equals(Register.USER_PREFIXES[0])) {
				
				// Needs to be updated
				tutorialUrl = "https://youtu.be/cNoS-bhRPEw";
				
			} else if (userPrefix.equals(Register.USER_PREFIXES[0])) {
				
				// Needs to be updated
				tutorialUrl = "https://youtu.be/cNoS-bhRPEw";
				
			} else {
				
				tutorialUrl = "https://youtu.be/cNoS-bhRPEw";
				
			}

			body += wrapText("p", "--> <a href=\"" + tutorialUrl + "\" target=\"_blank\">FuzzyIO Tutorial</a>");

			body += wrapText("p", "(The video will open in a new tab. Come back here and click \"CONTINUE\" when you are done.)");

		} else if (page.equals("3")) {

			body += wrapText("h2", "Mission Background");

			body += wrapText("p", "Beaverton Needs You!");

			body += wrapText("p", "The owner of a vacant lot in Beaverton has decided to develop their land into a building.");

			body += wrapText("p", "In order for the project to move forward, however, the design must be finalized by the local community.");

			body += wrapText("h2", "Your Role");

			body += wrapText("p", "As a trusted local community member, citizens have elected you to finalize the design.");

			body += wrapText("p", "Aside from a few building requirements set by the city, the design of the building is completely up to you!");

			body += wrapText("p", "You will be given exclusive access to a web tool called FuzzyIO, allowing you to view different design sceanrios.");

			body += wrapText("p", "At the end, you will be asked to choose a single scenario.");

			body += wrapText("h2", "Requirements");

			body += "<ul>";

			body += wrapText("li", "The building must have AT LEAST <u>40,000</u> sqft of <b>residential function</b>");

			body += wrapText("li", "The building must have AT LEAST <u>10,000</u> sqft of <b>commercial function</b>");

			body += wrapText("li", "The building's Floor Area Ratio (FAR) must be NO MORE than <u>4.0</u>");

			body += "</ul>";

			body += wrapText("h2", "Glossary");

			body += "<ul>";

			body += wrapText("li", "<b>Gross Land Area</b> (GLA) - Total area of land defined by all parcels [sqft]");

			body += wrapText("li", "<b>Gross Floor Area</b> (GFA) - Total floor area of all buildings [sqft]");

			body += wrapText("li", "<b>Floor Area Ratio</b> (FAR) - Ratio of Gross Above-ground Floor Area to Gross Land Area [sqft/sqft]");

			body += wrapText("li", "A <b>Scenario</b> is a specific configuration of Voxels specified by Parcels, Volumes, and Zones.");

			body += wrapText("li", "A <b>Function</b> describes the principal activity within a volume of enclosed space. (e.g. \"Residential\" or \"Retail\")");

			body += wrapText("li", "A <b>Voxel</b> is a small, indivisible unit of enclosed space consisting of a square base, height, and single Function");

			body += wrapText("li", "A <b>Zone</b> is a grid-based array of Voxels< with a common Function. If a Zone consists of multiple floors, all floors share the same footprint.");

			body += wrapText("li", "A <b>Volume</b> is a stack of Zones that all share a common footprint, but each zone may have a different Function and number of floors");

			body += wrapText("li", "A <b>Podium Volume</b> is a special Volume with a footprint defined by an off-set from a Parcel's edges and subtracting any Podium Exclusion Areas");

			body += wrapText("li", "A <b>Podium Exclusion Area</b> is an area, defined by a polygon, where Voxels cannot be placed.");

			body += wrapText("li", "A <b>Tower Volume</b> is a special Volume with a rectangular footprint. It must be placed within a Parcel.");

			body += wrapText("li", "A <b>Parcel</b> is the set of flat, grid-based Voxels that fit within the boundaries of a defined polygon. Parcels cannot overlap each other.");

			body += "</ul>";

			body += wrapText("h2", "Get Started!");

			body += wrapText("p", "You may now do any or all of the following with FuzzyIO:");

			body += "<ol>";
			
			if (!userPrefix.equals(Register.USER_PREFIXES[1])) {
			
				body += wrapText("li", "Use FuzzyIO to load and analyze 3 pre-designed scenarios for the area marked \"SITE\"");
	
				body += "<ul>";
	
				body += wrapText("li", "option1");
	
				body += wrapText("li", "option2");
	
				body += wrapText("li", "option3");
	
				body += "</ul>";
			
			}
			
			if (userPrefix.equals(Register.USER_PREFIXES[2]) || userPrefix.equals(Register.USER_PREFIXES[3])) {

				body += wrapText("li", "Use FuzzyIO to edit predesigned scenarios (A, B, or C) and save them as a new scenarios with a differnt name of your choosing.");
			
			}
			
			if (!userPrefix.equals(Register.USER_PREFIXES[0])) {

				body += wrapText("li", "Use FuzzyIO to design scenarios from scratch, saving them with names of your choosing.");
			
			}
			
			if (!userPrefix.equals(Register.USER_PREFIXES[0])) {

				body += wrapText("li", "Use the \"Save Scenario\" toolbox to name and save variations.");
				
				body += wrapText("li", "You may make as many new scenarios as you like, so don't hold back!");
					
			}
			
			body += "</ol>";
			
			body += wrapText("p", "<i>If you experience any technical difficulties or errors during your exercise, you may reload FuzzyIO in your browser and log in again.</i>");

			body += wrapText("h2", "Using FuzzyIO");
			
			body += wrapText("p", "Take as long as you like, just be sure to finish everything in <u>one sitting</u>.</i>");
			
			body += wrapText("p", "Go ahead and login to FuzzyIO with the <b>User ID</b> at the top of this page.");
			
			String fuzzyUrl = "http://opensui.org";

			body += wrapText("p", "--> <a href=\"" + fuzzyUrl + "\" target=\"_blank\">FuzzyIO</a>");

			body += wrapText("p", "(FuzzyIO will open in a new tab, but feel free to view this page while you work)");
			
			body += wrapText("h2", "Submitting your Final Design");
			
			body += wrapText("p", "When you have identified your favorite scenario, you need to submit it.");
			
			if (userPrefix.equals(Register.USER_PREFIXES[0])) {
			
				body += wrapText("p", "You may choose <b>one</b> of the pre-designed scenarios (Option 1, Option 2, or Option 3).");
			
			} else if (userPrefix.equals(Register.USER_PREFIXES[1])) {
				
				body += wrapText("p", "You may choose the scenario currently on your screen, or any <b>one</b> of the saved scenarios you have created.");
				
			} else {
				
				body += wrapText("p", "You may choose <b>one</b> of the pre-designed scenarios (Option 1, Option 2, or Option 3), or a scenario of your own editing or making.");
				
			}
			
			if (!userPrefix.equals(Register.USER_PREFIXES[0])) {
				
				body += wrapText("p", "To submit your final decision, make sure the scenario of your choosing is loaded and visible."
						+ " Then, save the scenario with the name <b>final</b>");
	
				body += wrapText("p", "Confirm that the scenario <b>final</b> is saved by looking in the list of scenarios available to load; the word \"final\" should show up in the list.");
			
			} else {
				
				body += wrapText("p", "Make a note of the option you chose.");
				
			}
			
			body += wrapText("p", "When you've finished making your decision, click \"CONTINUE\".");

		} else if (page.equals("4")) {

			body += wrapText("h2", "Post-Survey");
			
			body += wrapText("p", "Thank you for helping the community of Beaverton!");

			body += wrapText("p", "Now that you've finished the exercise, we need you to complete one more short survey. We promise this is the last thing!");

			String postsurveyUrl;

			if (userPrefix.equals(Register.USER_PREFIXES[0])) {

				postsurveyUrl = "https://forms.gle/JkDfk8sqxWxtg8kw6";

			} else if (userPrefix.equals(Register.USER_PREFIXES[1])) {

				postsurveyUrl = "https://forms.gle/rkLnBX2TgAiPHi8b7";

			} else if (userPrefix.equals(Register.USER_PREFIXES[2])) {

				postsurveyUrl = "https://forms.gle/EY1ntPzmbRZrtC159";

			} else {

				postsurveyUrl = "https://ira.mit.edu";

			}

			body += wrapText("p", "--> <a href=\"" + postsurveyUrl + "\" target=\"_blank\">Post-Survey</a>");

			body += wrapText("p", "(The survey will open in a new tab. Come back here and click \"CONTINUE\" when you are done.)");

		} else if (page.equals("5")) {

			body += wrapText("h2", "Congratulations!");

			body += wrapText("p", "We hope you had a good time, and we are incredibly grateful for your help with this research.");
			
			/*
			body += wrapText("p", "If you would like to keep playing with FuzzyIO, please do!"
					+ "<br>For this purpose, we've generated a new User ID that you can use indefinitely."
					+ "<br>(The User ID you used during the experiment will no longer be available)");
			
			String adminUserID = Register.makeUser(email, "admin");
			body += wrapText("p", "User ID: " + adminUserID);
			
			*/
			
			body += wrapText("p", "If you have any questions or concerns about your participation, please contact Ira.");
			
			body += wrapText("p", "You may now close all browser windows related to this experiment.");

		} else {

			return makeNullBody();

		}

		body += "<hr>";

		// Add Next Page Button
		int pageInt = Integer.valueOf(page);
		int nextPage = pageInt + 1;
		int prevPage = pageInt - 1;
		String navigation = "";
		if (pageInt > 1 && pageInt <= NUM_STUDY_PAGES) navigation += "< <a href=\"/?user=" + user + "&page=" + prevPage + "\">Go Back</a>";
		if (pageInt > 1 && pageInt < NUM_STUDY_PAGES)navigation += "\t|\t";
		if (pageInt >= 1 && pageInt < NUM_STUDY_PAGES) navigation += "<a href=\"/?user=" + user + "&page=" + nextPage + "\">CONTINUE</a> >";
		body += wrapText("p", navigation);

		body += "</body>";

		return body;
	}

	private static String makeNullBody() {

		String body = "<body>";

		body += wrapText("h1", "404");
		body += wrapText("p", "Resource Not Found");

		body += "</body>";

		return body;
	}

	private static String wrapText(String method, String text) {
		return "<" + method + ">" + text + "</" + method + ">";
	}
}
