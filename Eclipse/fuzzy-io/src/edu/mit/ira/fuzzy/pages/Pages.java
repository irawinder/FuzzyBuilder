package edu.mit.ira.fuzzy.pages;

import edu.mit.ira.fuzzy.server.user.Register;
import edu.mit.ira.fuzzy.server.user.RegisterUtil;
import edu.mit.ira.fuzzy.server.user.UserPrefixStudy;
import edu.mit.ira.fuzzy.server.user.UserType;

public class Pages {
	
	public static String BODY_BACKGROUND_URL = "https://github.com/irawinder/FuzzyBuilder/blob/master/res/images/massing.png?raw=true";
	public static String MODEL_DIAGRAM_URL = "https://github.com/irawinder/FuzzyBuilder/blob/master/res/images/modelDiagram_sm.png?raw=true";
	public static String SITE_BASEMAP_URL = "https://github.com/irawinder/FuzzyBuilder/blob/master/res/images/beaverton_site.jpg?raw=true";
	public static String ZEBRA_TUTORIAL_URL = "https://www.youtube.com/embed/JaT714oQQJ8?cc_load_policy=1&vq=hd1080";
	public static String COBRA_TUTORIAL_URL = "https://www.youtube.com/embed/BA4LlQF8Ieo?cc_load_policy=1&vq=hd1080";
	public static String PANDA_TUTORIAL_URL = "https://www.youtube.com/embed/NgYlDAg1De8?cc_load_policy=1&vq=hd1080";
	public static String ENTRY_SURVEY_URL = "https://forms.gle/cK3dQozbQyt1S2zn7";
	public static String ZEBRA_EXIT_SURVEY_URL = "https://forms.gle/JkDfk8sqxWxtg8kw6";
	public static String COBRA_EXIT_SURVEY_URL = "https://forms.gle/rkLnBX2TgAiPHi8b7";
	public static String PANDA_EXIT_SURVEY_URL = "https://forms.gle/EY1ntPzmbRZrtC159";
	public static String OPENSUI_URL = "http://opensui.org";
	public static String THIS_URL = "http://fuzzy.glassmatrix.org";
	
	public static String OLD_TUTORIAL_URL = "https://youtu.be/cNoS-bhRPEw";
	public static String TIMELAPSE_URL = "https://youtu.be/z7514vh02u0";
	
	public static String generalSite() {
		String head = makeHead();
		String body = makeGeneralBody();
		return assemblePage(head, body);
	}
	
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

	public static String studySite(String user, String page, boolean deactivated) {
		String head = makeHead();
		String body;
		if (deactivated) {
			body = makeStudyBody(user, page, deactivated);
		} else {
			body = makeStudyBody(user, page, deactivated);
		}
		return assemblePage(head, body);
	}

	public static String nullSite(String errorCode, String errorMessage) {
		String head = makeHead();
		String body = makeNullBody(errorCode, errorMessage);
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
		
		String generalCSS = "text-align:left;"
				+ "margin-left: 40px;"
				+ "font-family:Helvetica, sans-serif;";
		
		String bodyTextCSS = "font-size:14px;"
				+ "font-style:normal;"
				+ "font-weight:normal;"
				+ "color:#525252;";
		
		head += "body {"
				+ "margin-bottom: 60px;"
				+ "margin-top: 40px;"
				+ "max-width: 600px;"
				+ "background-color:#ffffff;"
				+ "background-image:url(" + BODY_BACKGROUND_URL + ");"
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
		head += "iframe { margin-left: 40px; }";
		head += ".tab {display: inline-block; margin-left: 40px; }";

		head += "</style>";

		head +="</head>";

		return head;
	}
	
	private static String makeGeneralBody() {

		String body = "<body>";
		
		body += wrapText("h1", "FuzzyIO");
		body += wrapText("p", "A Real Estate Configuration Interface by MIT"
				+ "<br>Contact: Ira Winder<br><i>ira [at] mit [dot] edu</i>");
		body += "<hr>";
		
		body += wrapText("p", "FuzzyIO is a server for generating \"fuzzy\" resolution real estate development scenarios.");
		body += wrapText("p", "To interact with FuzzyIO, you need to use a \"front end\" user interface like OpenSUI.");
		body += wrapText("p", "<b>OpenSUI</b>:<br><a href=\"" + OPENSUI_URL + "\" target=\"_blank\">" + OPENSUI_URL + "</a>");

		body += wrapText("h2", "FuzzyIO Model");
		
		body += "<img style=\"width: 600px; max-width: 100%;\" src=\"" + MODEL_DIAGRAM_URL + "\" alt=\"Diagram of Model Components for FuzzyIO\">";
		
		body += wrapText("h2", "Glossary");

		body += "<ul>";

		body += wrapText("li", "A <b>Function</b> describes the principal activity within a volume of enclosed space. (e.g. \"Residential\" or \"Retail\")");
		body += wrapText("li", "A <b>Voxel</b> is a small, indivisible unit of enclosed space consisting of a square base, height, and single Function");
		body += wrapText("li", "A <b>Zone</b> is a grid-based array of Voxels with a common Function. If a Zone consists of multiple floors, all floors share the same footprint.");
		body += wrapText("li", "A <b>Volume</b> is a stack of Zones that all share a common footprint, but each zone may have a different Function and number of floors");
		body += wrapText("li", "A <b>Podium Volume</b> is a special Volume with a footprint defined by an off-set from a Parcel's edges and subtracting any Podium Exclusion Areas");
		body += wrapText("li", "A <b>Podium Exclusion Area</b> is an area, defined by a polygon, where Voxels cannot be placed.");
		body += wrapText("li", "A <b>Tower Volume</b> is a special Volume with a rectangular footprint. It must be placed within a Parcel.");
		body += wrapText("li", "A <b>Parcel</b> is the set of flat, grid-based Voxels that fit within the boundaries of a defined polygon. Parcels cannot overlap each other.");
		body += wrapText("li", "A <b>Scenario</b> is a specific configuration of Voxels specified by Parcels, Volumes, and Zones.");
		
		body += wrapText("li", "<b>Gross Land Area</b> (GLA) - Total area of land defined by all parcels [sqft]");
		body += wrapText("li", "<b>Gross Floor Area</b> (GFA) - Total floor area of all buildings [sqft]");
		body += wrapText("li", "<b>Floor Area Ratio</b> (FAR) - Ratio of Gross Above-ground Floor Area to Gross Land Area [sqft/sqft]");
		
		body += "</ul>";
		
		body += wrapText("h2", "Resources");

		body += "<ul>";
		body += wrapText("li", "Launch <a href=\"" + OPENSUI_URL + "\" target=\"_blank\">openSUI</a> (to use FuzzyIO)");
		body += wrapText("li", "<a href=\"" + OLD_TUTORIAL_URL + "\" target=\"_blank\">FuzzyIO Tutorial</a> on Youtube");
		body += wrapText("li", "<a href=\"" + TIMELAPSE_URL + "\" target=\"_blank\">FuzzyIO Usage</a> (Timelapse) on Youtube");
		body += "</ul>";

		body += "</body>";

		return body;
	}
	
	private static String studyBodyHeader() {
		String bodyText = "";
		bodyText += wrapText("h1", "Beaverton");
		bodyText += wrapText("p", "<i>A Research Study</i>");
		bodyText += wrapText("p", "<b>Univeristy of Tokyo</b> and <b>MIT</b><br>Contact: Ira Winder<br><i>ira [at] mit [dot] edu</i>");
		bodyText += "<hr>";
		return bodyText;
	}
	
	private static String makeStudyIntroBody() {
		
		String body = "<body>";
		
		body += "<script> function register() { location.replace(\"/register\"); } </script>";
		
		body += studyBodyHeader();
		
		body += "<img style=\"width: 600px; max-width: 100%;\" src=\"" + MODEL_DIAGRAM_URL + "\" alt=\"Screenshot of Digital Model, FuzzyIO\">";
		
		body += wrapText("h2", "Introduction");
		
		body += wrapText("p", "Welcome to Beaverton, a research study!");

		body += wrapText("p", "The University of Tokyo and MIT are investigating how people make decisions using interactive models.");
		
		body += wrapText("p", "Participants will work individually, from the comfort of their own computer, "
				+ "to help design a mixed-use building in the hypothetical city of Beaverton.");
		
		body += wrapText("h2", "Investigators");
		
		body += wrapText("p", "<b>Ira Winder</b>"
				+ "<br>Project Researcher, Graduate School of Frontier Sciences, University of Tokyo"
				+ "<br>Research Affiliate, Engineering Systems Laboratory, MIT");
		
		body += wrapText("p", "<b>Kazuo Hiekata</b>"
				+ "<br>Professor, Graduate School of Frontier Sciences, University of Tokyo");
		
		body += wrapText("h2", "During the Experiment");
		
		body += wrapText("p", "Participants will perform the following activities over the course of 30-60 minutes:");
		
		body += "<ol>";
		
		body += wrapText("li", "First, participants will complete an entry survey where they can review and consent to the terms of the study.");
		
		body += wrapText("li", "Then, participants will watch a short tutorial and Youtube video explaining how to use the modeling software, \"FuzzyIO\".");
		
		body += wrapText("li", "After that, participants will follow a prompt to help design a mixed-use building.");
		
		body += wrapText("li", "At the finish, participants will complete a short exit survey.");
		
		body += "</ol>";
		
		body += wrapText("h2", "After the Experiment");
		
		body += wrapText("p", "At the end of the experiment, we want to reward you for your hard work.");
		
		body += wrapText("p", "As a special thank you, we will provide you with your own personal "
				+ "credentials to access an unlocked version of the real estate modeling software used in this study.");
		
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
				"<br><i>(There is no time limit, but this exercise may take <b>30 - 60 minutes</b>)</i>");

		body += "</ul>";

		body += wrapText("h2", "Get Started!");
		
		body += wrapText("p", "Click \"Continue to Registration\" to proceed.");
		
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
		
		body += "<label for=\"email1\">Email:</label><br>";
		
		body += "<input type=\"text\" id=\"email1\" style=\"width: 300px;\"><p style=\"color: red;\" id=\"email1_feedback\"></p><br><br>";
		
		body += "<label for=\"email2\">Confirm Email:</label><br>";
		
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
		
		body += "<p style=\"color: green;\">Please save or print a copy of this page for your records; you won't be able to see it again.</p>";
		
		body += wrapText("p", "<b>Email</b>:<br>" + email);
		
		body += wrapText("h2", "Personal Access Link");
		
		body += wrapText("p", "We have generated a unique personal access link, made just for you.");
		
		body += wrapText("p", "The begin the exercise, click the link to continue.");
		
		body += wrapText("p", "Remember, we would like you to complete the exercise in one sitting, so please make sure that you are ready.");
		
		body += wrapText("p", "<b>Personal Access Link</b>:<br>"
				+ "<a href=\"/?user=" + userID + "\">" + THIS_URL + "/?user=" + userID + "</a>");
		
		body += "</body>";
		
		return body;
	}
	
	private static int NUM_STUDY_PAGES = 5;
	
	private static String makeStudyBody(String user, String page, boolean deactivated) {

		UserPrefixStudy ups = RegisterUtil.parseStudyPrefix(user);
		
		String body = "<body>";

		body += studyBodyHeader();
		
		if (!page.equals("finish")) {
			body += wrapText("p", "Your User ID: <b>" + user + "</b>");
			body += wrapText("p", "Page: " + page + " of " + NUM_STUDY_PAGES);
			body += "<hr>";
		}
		
		if (page.equals("1")) {

			body += wrapText("p", "Welcome!");
			
			body += wrapText("p", "Now that you've begun, please continue to the end until you are finished.");
			
			body += wrapText("h2", "User ID");

			body += wrapText("p", "You have been assigned an exclusive User ID: <b>" + user + "</b>");

			body += wrapText("p", "The User ID will remain at the top of every page for reference.");

			body += wrapText("h2", "Entry Survey");

			body += wrapText("p", "Now, please complete a short entry survey using <i>Google Forms</i>.");
			
			body += wrapText("p", "When you click \"Take Entry Survey\", the survey will open in a new tab.");

			body += "<input type=\"button\" value=\"Take Entry Survey\" onclick=\"window.open('" + ENTRY_SURVEY_URL + "', '_blank');\">";

			body += wrapText("p", "Once you've submitted the survey, come back here and click \"CONTINUE\".");

		} else if (page.equals("2")) {

			body += wrapText("h2", "FuzzyIO Model");
			
			body += wrapText("p", "During this exercise, it's important that you understand some vocabulary.");
			
			body += wrapText("p", "Review this diagram of a real estate development model, as well as a glossary of important terms.");
			
			body += "<img style=\"width: 600px; max-width: 100%;\" src=\"" + MODEL_DIAGRAM_URL + "\" alt=\"Diagram of Model Components for FuzzyIO\">";
			
			body += wrapText("h2", "FuzzyIO Glossary");

			body += "<ul>";

			body += wrapText("li", "A <b>Function</b> describes the principal activity within a volume of enclosed space. (Residential, Office, Retail, etc)");

			body += wrapText("li", "A <b>Voxel</b> is a small, indivisible unit of enclosed space consisting of a square base, height, and single Function");

			body += wrapText("li", "A <b>Zone</b> is a grid-based array of Voxels with a common Function. If a Zone consists of multiple floors, all floors share the same footprint.");

			body += wrapText("li", "A <b>Volume</b> is a stack of Zones that all share a common footprint, but each zone may have a different Function and number of floors");

			body += wrapText("li", "A <b>Podium Volume</b> is a special Volume with a footprint defined by an off-set from a Parcel's edges and subtracting any Podium Exclusion Areas");

			body += wrapText("li", "A <b>Podium Exclusion Area</b> is an area, defined by a polygon, where Podium Voxels cannot be placed.");

			body += wrapText("li", "A <b>Tower Volume</b> is a special Volume with a rectangular footprint. It must be placed within a Parcel.");

			body += wrapText("li", "A <b>Parcel</b> is the set of flat, grid-based Voxels that fit within the boundaries of a defined polygon. Parcels cannot overlap each other.");
			
			body += wrapText("li", "A <b>Scenario</b> is a specific configuration of Voxels specified by Parcels, Volumes, and Zones.");
			
			body += wrapText("li", "<b>Gross Land Area</b> (GLA) - Total area of land defined by all parcels [sqft]");

			body += wrapText("li", "<b>Gross Floor Area</b> (GFA) - Total floor area of all buildings [sqft]");

			body += wrapText("li", "<b>Floor Area Ratio</b> (FAR) - Ratio of Gross Above-ground Floor Area to Gross Land Area [sqft/sqft]");
			
			body += "</ul>";
			
			body += wrapText("h2", "FuzzyIO Tutorial");

			body += wrapText("p", "Next, we need you to prepare for your role by watching a quick tutorial video.");
			
			body += wrapText("p", "Please watch the entire video on <u>full screen</u> with the <u>sound on</u>.");
			
			body += wrapText("p", "If the video is blurry, make sure <b>Quality</b> is set to 1080p."
					+ "<br><br><span class=\"tab\"></span><i>Settings</i> > <i>Quality</i> > <i>1080p</i>");
			
			body += wrapText("p", "The video has English subtitles by default, "
					+ "but Youtube can also provide <b>Subtitles</b> in your preferred language."
					+ "<br><br><span class=\"tab\"></span><i>Settings</i> > <i>Subtitles/CC</i> > <i>Auto-translate</i>");

			String tutorialUrl;
			
			if (ups == UserPrefixStudy.ZEBRA) {
				
				tutorialUrl = ZEBRA_TUTORIAL_URL;
				
			} else if (ups == UserPrefixStudy.COBRA) {
				
				tutorialUrl = COBRA_TUTORIAL_URL;
			
			// Full tutorial for PANDA (or any other user that makes it to this page)
			} else {
				
				tutorialUrl = PANDA_TUTORIAL_URL;
				
			}

			body += "<iframe style=\"width: 560px; height: 315px; max-width: 100%\" src=\"" + tutorialUrl + "\" "
					+ "title=\"FuzzyIO Tutorial\" frameborder=\"0\" "
					+ "allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" "
					+ "allowfullscreen></iframe>";
			
			body += wrapText("p", "Click \"CONTINUE\" when you are done watching the video."
					+ "<br><i>(Feel free to come back to this page later for reference)</i>");

		} else if (page.equals("3")) {

			body += wrapText("h2", "Background");

			body += wrapText("p", "A generous benfactor recently donated a vacant peice of land (\"the Site\") to the City of Beaverton.");
			
			body += wrapText("p", "The only condition is that the site must be used to host a new <i>College for the Arts</i>, along with <i>new housing</i> for the local community.");
			
			body += wrapText("h2", "The Site");
			
			body += "<img style=\"width: 600px; max-width: 100%;\" src=\"" + SITE_BASEMAP_URL + "\" alt=\"Diagram of Model Components for FuzzyIO\">";
			
			body += wrapText("h2", "Your Role");

			body += wrapText("p", "As a trusted local community member, citizens have elected you to finalize the design of a new building for the site.");

			body += wrapText("p", "Aside from a few building requirements set by the city, the design of the building is completely up to you!");

			body += wrapText("p", "You will be given exclusive access to a web tool called FuzzyIO, allowing you to view different design sceanrios.");

			body += wrapText("p", "At the end, you will be asked to choose a single scenario.");
			
			body += wrapText("h2", "Requirements");

			body += "<ul>";

			body += wrapText("li", "The building must have AT LEAST <u>50,000</u> sqft of <b>residential function</b>");

			body += wrapText("li", "The building must have AT LEAST <u>80,000</u> sqft of <b>institutional function</b>");

			body += wrapText("li", "The building's Floor Area Ratio (FAR) must be NO MORE than <u>5.0</u>");

			body += "</ul>";
			
			body += "<hr>";
			
			body += wrapText("h2", "Get Started!");

			body += wrapText("p", "You may now do any or all of the following with FuzzyIO:");

			body += "<ol>";
			
			if (ups != UserPrefixStudy.COBRA) {
			
				body += wrapText("li", "Use FuzzyIO to load and analyze 3 pre-designed scenarios for the area marked \"SITE\"");
	
				body += "<ul>";
	
				body += wrapText("li", "option1");
	
				body += wrapText("li", "option2");
	
				body += wrapText("li", "option3");
	
				body += "</ul>";
			
			}
			
			if (ups == UserPrefixStudy.PANDA) {

				body += wrapText("li", "Use FuzzyIO to edit predesigned scenarios (A, B, or C) and save them as new scenarios with a differnt name of your choosing.");
			
			}
			
			if (ups != UserPrefixStudy.ZEBRA) {

				body += wrapText("li", "Use FuzzyIO to design scenarios from scratch, saving them with names of your choosing.");

				body += wrapText("li", "Use the \"Save Scenario\" toolbox to name and save variations.");
				
				body += wrapText("li", "You may make as many new scenarios as you like, so don't hold back!");
					
			}
			
			body += "</ol>";
			
			body += wrapText("p", "<i>If you experience any technical difficulties or errors during your exercise, you may reload FuzzyIO in your browser and log in again.</i>");

			body += wrapText("h2", "Using FuzzyIO");
			
			body += wrapText("p", "Take as long as you like, just be sure to finish everything in <u>one sitting</u>.</i>");
			
			body += wrapText("p", "Log in to FuzzyIO with the <b>User ID</b> at the top of this page. "
					+ "Clicking the link below will open FuzzyIO in a new tab, but feel free to come back and view this page while you work");

			body += wrapText("p", "<b>Fuzzy IO</b>:<br><a href=\"" + OPENSUI_URL + "\" target=\"_blank\">" + OPENSUI_URL + "</a>");
			
			body += wrapText("h2", "Submitting your Final Design");
			
			body += wrapText("p", "When you have identified your favorite scenario, you need to submit it.");
			
			if (ups == UserPrefixStudy.ZEBRA) {
			
				body += wrapText("p", "You may choose <b>one</b> of the pre-designed scenarios (Option 1, Option 2, or Option 3).");
			
			} else if (ups == UserPrefixStudy.COBRA) {
				
				body += wrapText("p", "You may choose the scenario currently on your screen, or any <b>one</b> of the saved scenarios you have created.");
				
			} else if (ups == UserPrefixStudy.PANDA) {
				
				body += wrapText("p", "You may choose <b>one</b> of the pre-designed scenarios (Option 1, Option 2, or Option 3), or a scenario of your own editing or making.");
				
			}
			
			if (ups != UserPrefixStudy.ZEBRA) {
				
				body += wrapText("p", "To submit your final decision, make sure the scenario of your choosing is loaded and visible."
						+ " Then, save the scenario with the name <b>final</b>");
	
				body += wrapText("p", "Confirm that the scenario <b>final</b> is saved by looking in the list of scenarios available to load; the word \"final\" should show up in the list.");
			
			} else {
				
				body += wrapText("p", "Make a note of the option you chose.");
				
			}
			
			body += wrapText("p", "When you've finished making your decision, click \"CONTINUE\".");

		} else if (page.equals("4")) {

			body += wrapText("h2", "Exit Survey");
			
			body += wrapText("p", "Thank you for helping the community of Beaverton!");

			body += wrapText("p", "Now that you've finished the exercise, we need you to complete one more short survey. We promise this is the last thing!");

			String postsurveyUrl;

			if (ups == UserPrefixStudy.ZEBRA) {

				postsurveyUrl = ZEBRA_EXIT_SURVEY_URL;

			} else if (ups == UserPrefixStudy.COBRA) {

				postsurveyUrl = COBRA_EXIT_SURVEY_URL;

			} else {

				postsurveyUrl = PANDA_EXIT_SURVEY_URL;

			}
			
			body += wrapText("p", "When you click \"Take Exit Survey\", the survey will open in a new tab.");
			
			body += "<input type=\"button\" value=\"Take Exit Survey\" onclick=\"window.open('" + postsurveyUrl + "', '_blank');\">";

			body += wrapText("p", "Once you've submitted the survey, come back here and click \"CONTINUE\".");

		} else if (page.equals("5")) {
			
			body += wrapText("h2", "Finishing Up");

			body += wrapText("p", "Have you completed everything in the exerise? If so, click \"I'm Finished\".");
			
			body += "<p style=\"color: red;\">WARNING: You will not be able to return to the experiment.</p>";
			
			String finishedUrl = "/?user=" + user + "&page=finish";
			
			body += "<input type=\"button\" value=\"I'm Finished!\" onclick=\"location.replace('" + finishedUrl + "');\">";
			
		} else if (page.equals("finish")) {
			
			String adminUserID = "";
			String email = Register.getEmail(user);
			if (deactivated) {
				adminUserID = Register.getUser(email);
			} else {
				Register.deactivateUser(user);
				if (email != null) {
					adminUserID = Register.makeUser(email, UserType.ADMIN);
				} else {
					adminUserID = "There was an error generating this ID";
				}
			}
			
			body += "<p style=\"color: red;\">Please save or print a copy of this page for your records.</p>";
			
			body += wrapText("h2", "Congratulations!");

			body += wrapText("p", "We hope you had a good time, and we are incredibly grateful for your help with this research.");
			
			body += wrapText("h2", "After the Experiment");
			
			body += wrapText("p", "If you would like to keep playing with FuzzyIO, please do!");
			
			body += wrapText("p", "For this purpose, we've generated a <i>New</i> User ID that you can use indefinitely.");
			
			body += wrapText("p", "Please note that the <i>Old</i> User ID is no longer usable.</i>");
			
			body += wrapText("p", "<b>Fuzzy IO</b>:<br><a href=\"" + OPENSUI_URL + "\" target=\"_blank\">" + OPENSUI_URL + "</a>");
			
			body += wrapText("p", "<b>New User ID</b>:<br>" + adminUserID);
			
			body += wrapText("p", "<b>Old User ID </b>:<br>" + user);
			
			body += wrapText("p", "<b>Email</b>:<br>" + email);
			
			body += wrapText("p", "If you have any questions or concerns about your participation, or have trouble using your new User ID, please contact Ira.");
			
			body += "<hr>";
			
			body += wrapText("p", "You may now close all browser windows related to this experiment.");
			
		} else {

			return makeNullBody("404", "Resource Not Found");

		}
		
		body += "<hr>";
		
		// Add Next Page Button
		if (!page.equals("finish")) {
			int pageInt = Integer.valueOf(page);
			int nextPage = pageInt + 1;
			int prevPage = pageInt - 1;
			String navigation = "";
				
			if (pageInt > 1 && pageInt <= NUM_STUDY_PAGES) 
				navigation += "< <a href=\"/?user=" + user + "&page=" + prevPage + "\">Go Back</a>";
			if (pageInt > 1 && pageInt < NUM_STUDY_PAGES) 
				navigation += "\t|\t";
			if (pageInt >= 1 && pageInt < NUM_STUDY_PAGES) 
				navigation += "<a href=\"/?user=" + user + "&page=" + nextPage + "\">CONTINUE</a> >";
			body += wrapText("p", navigation);
		}

		body += "</body>";

		return body;
	}

	private static String makeNullBody(String errorCode, String errorMessage) {

		String body = "<body>";

		body += wrapText("h1", errorCode);
		body += wrapText("p", errorMessage);

		body += "</body>";

		return body;
	}

	private static String wrapText(String method, String text) {
		return "<" + method + ">" + text + "</" + method + ">";
	}
}
