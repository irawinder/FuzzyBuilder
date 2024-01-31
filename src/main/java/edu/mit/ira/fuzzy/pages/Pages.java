package edu.mit.ira.fuzzy.pages;

import java.util.ArrayList;

import edu.mit.ira.fuzzy.server.Server;
import edu.mit.ira.fuzzy.server.user.Register;
import edu.mit.ira.fuzzy.server.user.RegisterUtil;
import edu.mit.ira.fuzzy.server.user.UserPrefixStudy;
import edu.mit.ira.fuzzy.server.user.UserType;
import edu.mit.ira.fuzzy.survey.Survey;
import edu.mit.ira.fuzzy.survey.SurveyType;
import edu.mit.ira.fuzzy.survey.SurveyUtil;

public class Pages {
	
	private static String BODY_BACKGROUND_URL = "https://github.com/irawinder/FuzzyBuilder/blob/master/res/images/massing.png?raw=true";
	private static String MODEL_DIAGRAM_URL = "https://github.com/irawinder/FuzzyBuilder/blob/master/res/images/modelDiagram.png?raw=true";
	private static String SITE_BASEMAP_URL = "https://github.com/irawinder/FuzzyBuilder/blob/master/res/images/beaverton_site.jpg?raw=true";
	private static String ZEBRA_TUTORIAL_URL = "https://www.youtube.com/embed/JaT714oQQJ8?cc_load_policy=1&vq=hd1080";
	private static String COBRA_TUTORIAL_URL = "https://www.youtube.com/embed/BA4LlQF8Ieo?cc_load_policy=1&vq=hd1080";
	private static String PANDA_TUTORIAL_URL = "https://www.youtube.com/embed/NgYlDAg1De8?cc_load_policy=1&vq=hd1080";
	
	private static String OPENSUI_URL = "https://opensui.org";
	private static String THIS_URL = "https://beaverton.glassmatrix.org";
	private static String STUDY_CONTACT_URL = "beaverton <i>[at]</i> mit <i>[dot]</i> edu";
	private static String GENERAL_CONTACT_URL = "ira <i>[at]</i> mit <i>[dot]</i> edu";
	
	private static String OLD_TUTORIAL_URL = "https://youtu.be/cNoS-bhRPEw";
	private static String TIMELAPSE_URL = "https://youtu.be/z7514vh02u0";
	
	public static String generalSite() {
		String head = makeHead();
		String body = makeGeneralBody();
		return assemblePage(head, body);
	}
	
	public static String studyIntroSite() {
		String head = makeHead(new String[]{"goRegister.js"});
		String body = makeStudyIntroBody();
		return assemblePage(head, body);
	}
	
	public static String registrationSite(String feedback) {
		String head = makeHead(new String[]{"register.js", "survey.js"});
		String body = makeRegistrationBody(feedback);
		return assemblePage(head, body);
	}

	public static String studySite(String user, String page, boolean deactivated) {
		String head;
		if (page.equals("1")) {
			head = makeHead(new String[] {"survey.js", "entrySurvey.js"});
		} else if (page.equals("4")) {
			head = makeHead(new String[] {"survey.js", "exitSurvey.js"});
		} else {
			head = makeHead();
		}
		String body = makeStudyBody(user, page, deactivated);
		return assemblePage(head, body);
	}

	public static String nullSite(String errorCode, String errorMessage) {
		String head = makeHead();
		String body = makeNullBody(errorCode, errorMessage);
		return assemblePage(head, body);
	}

	private static String assemblePage(String head, String body) {
		return "<!DOCTYPE html><html lang=\"en\">" + head + body + "</html>";
	}
	
	/**
	 * generate html head with no javascript files
	 * @param scripts
	 * @return
	 */
	private static String makeHead() {
		return makeHead(new String[0]);
	}
	
	/**
	 * generate html head, injecting list of javascript files
	 * @param scripts
	 * @return
	 */
	private static String makeHead(String[] scripts) {
		String head = "<head>";

		head += "<title>Beaverton</title>";

		head += "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">";
		head += "<meta name=\"keywords\" content=\"MIT, University of Tokyo, Ira Winder\">";
		head += "<meta name=\"description\" content=\"A Study by MIT\">";
		head += "<meta property=’og:title’ content='Beaverton'/>";
		head += "<meta property=’og:image’ content='" + MODEL_DIAGRAM_URL + "’/>";
		head += "<meta property=’og:description’ content='A Study by MIT'/>";
		head += "<meta property=’og:url’ content='" + THIS_URL + "'/>";
		head += "<meta property='og:image:width' content='1920' />";
		head += "<meta property='og:image:height' content='1080' />";

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
		head += "textarea { " + generalCSS + bodyTextCSS + "resize: none; }";
		head += "input[type=button] { " + generalCSS + bodyTextCSS + " width: 200px; height: 50px; text-align: center; background-color: #85965c; color: white; font-size: 16px; }";
		head += "input[type=button]:hover { height: 50px; text-align: center; background-color: #5f6b41; color: white; font-size: 16px; }";
		head += "li {" + generalCSS + bodyTextCSS + "}";
		head += "ul li { margin-bottom: 10px; }";
		head += "ol li { margin-bottom: 10px; }";
		head += "img { margin-left: 40px; }";
		head += "iframe { margin-left: 40px; }";
		head += ".tab {display: inline-block; margin-left: 40px; }";
		head += ".column { width: 50%; }";

		head += "</style>";
		
		for (String fileName : scripts) {
			head += "<script src=\"js/" + fileName + "\"></script>";
		}
		
		head +="</head>";

		return head;
	}
	
	private static String makeGeneralBody() {

		String body = "<body>";
		
		body += wrapText("h1", Server.NAME);
		body += wrapText("p", "A Real Estate Configuration Interface by MIT"
				+ "<br>Contact: Ira Winder<br>" + GENERAL_CONTACT_URL);
		
		body += "<hr>";
		body += wrapText("p", Server.NAME + " is a server for generating \"fuzzy\" resolution real estate development scenarios.");
		body += wrapText("p", "To interact with " + Server.NAME + ", you need to use a \"front end\" user interface like OpenSUI.");
		body += wrapText("p", "<b>OpenSUI</b>:<br><a href=\"" + OPENSUI_URL + "\" target=\"_blank\">" + OPENSUI_URL + "</a>");

		body += wrapText("h2", "Model");
		body += "<img style=\"width: 600px; max-width: 100%;\" src=\"" + MODEL_DIAGRAM_URL + "\" alt=\"Diagram of Model Components for " + Server.NAME + "\">";
		
		body += glossary();
		
		body += wrapText("h2", "Resources");
		body += "<ul>";
		body += wrapText("li", "Launch <a href=\"" + OPENSUI_URL + "\" target=\"_blank\">openSUI</a> (to use " + Server.NAME + ")");
		body += wrapText("li", "<a href=\"" + OLD_TUTORIAL_URL + "\" target=\"_blank\">" + Server.NAME + " Tutorial</a> on Youtube");
		body += wrapText("li", "<a href=\"" + TIMELAPSE_URL + "\" target=\"_blank\">" + Server.NAME + " Usage</a> (Timelapse) on Youtube");
		body += "</ul>";
		
		body += "</body>";

		return body;
	}
	
	private static String glossary() {
		
		String bodyText = "";
		
		bodyText += wrapText("h2", "Glossary");
		bodyText += "<ul>";
		bodyText += wrapText("li", "A <b>Function</b> describes the principal activity within a volume of enclosed space. (Residential, Retail, Office, etc)");
		bodyText += wrapText("li", "A <b>Voxel</b> is a small, indivisible unit of enclosed space consisting of a square base, height, and single Function");
		bodyText += wrapText("li", "A <b>Zone</b> is an array of Voxels with a common Function. If a Zone consists of multiple floors, all floors share the same footprint.");
		bodyText += wrapText("li", "A <b>Volume</b> is a stack of Zones that all share a common footprint, but each zone may have a different Function and number of floors");
		bodyText += wrapText("li", "A <b>Podium Volume</b> is a special Volume with a footprint defined by an off-set from a Parcel's edges and subtracting any Podium Exclusion Areas");
		bodyText += wrapText("li", "A <b>Podium Exclusion Area</b> is an area, defined by a polygon, where Voxels cannot be placed.");
		bodyText += wrapText("li", "A <b>Tower Volume</b> is a special Volume with a rectangular footprint. It must be placed within a Parcel.");
		bodyText += wrapText("li", "A <b>Parcel</b> is the set of flat Voxels that fit within the boundaries of a defined polygon. Parcels cannot overlap each other.");
		bodyText += wrapText("li", "A <b>Scenario</b> is a specific configuration of Voxels specified by Parcels, Volumes, and Zones.");
		bodyText += wrapText("li", "<b>Gross Land Area</b> (GLA) - Total area of land defined by all parcels [sqft]");
		bodyText += wrapText("li", "<b>Gross Floor Area</b> (GFA) - Total floor area of all buildings [sqft]");
		bodyText += wrapText("li", "<b>Floor Area Ratio</b> (FAR) - Ratio of Gross Above-ground Floor Area to Gross Land Area [sqft/sqft]");
		bodyText += "</ul>";
		
		return bodyText;
	}
	
	private static String studyBodyHeader() {
		String bodyText = "";
		bodyText += wrapText("h1", "Beaverton");
		bodyText += wrapText("p", "A Research Study<br>by <b>University of Tokyo</b> and <b>MIT</b>");
		bodyText += "<hr>";
		return bodyText;
	}
	
	private static String studyBodyFooter() {
		String bodyText = "";
		bodyText += "<hr>";
		bodyText += wrapText("p", "Beaverton | <b>University of Tokyo</b> and <b>MIT</b> | Contact: " + STUDY_CONTACT_URL);
		return bodyText;
	}
	
	private static String makeStudyIntroBody() {
		
		String body = "<body>";
		body += studyBodyHeader();
		
		body += "<img style=\"width: 560px; max-width: 100%;\" src=\"" + MODEL_DIAGRAM_URL + "\" alt=\"Screenshot of Digital Model, " + Server.NAME + "\">";
		
		body += wrapText("h2", "Introduction");
		body += wrapText("p", "Welcome to Beaverton, a research study!");
		body += wrapText("p", "The University of Tokyo and MIT are investigating how people make decisions using interactive models.");
		body += wrapText("p", "Participants will work individually, from the comfort of their own computer, "
				+ "to help design a mixed-use building in the hypothetical city of Beaverton.");
		
		body += wrapText("h2", "Investigators");
		body += wrapText("p", "<b>Ira Winder</b>"
				+ "<br>Project Researcher, Graduate School of Frontier Sciences, University of Tokyo"
				+ "<br>Research Affiliate, Engineering Systems Laboratory, MIT"
				+ "<br>Contact: " + GENERAL_CONTACT_URL);
		body += wrapText("p", "<b>Kazuo Hiekata</b>"
				+ "<br>Professor, Graduate School of Frontier Sciences, University of Tokyo");
		body += wrapText("p", "<i>Approved by the University of Tokyo Research Ethics Committee (ref. 22-100)</i>");
		
		body += wrapText("h2", "During the Experiment");
		body += wrapText("p", "Participants will perform the following activities:");
		body += "<ol>";
		body += wrapText("li", "First, participants will complete an entry survey where they can review and consent to the terms of the study.");
		body += wrapText("li", "Then, participants will watch a short tutorial and Youtube video explaining how to use the modeling software, \"" + Server.NAME + "\".");
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
				"<br><i>(Do not try this exercise on a tablet or smartphone)</i>");
		body += wrapText("li", "Reliable internet connection");
		body += wrapText("li", "Web browser (Chrome, Safari, Firefox, etc)" +
				"<br><i>(The browser you are using to read this text is probably fine)</i>");
		body += wrapText("li", "Keyboard and mouse" +
				"<br><i>(Trackpad is fine, but you may have better experience using a two-button mouse)</i>");
		body += wrapText("li", "You must complete the exercise in <u>one sitting</u>" +
				"<br><i>(There is no time limit, but this exercise may take <b>30 - 60 minutes</b>)</i>");
		body += "</ul>";
		
		body += "<hr>";
		body += wrapText("h2", "Get Started!");
		body += wrapText("p", "Click \"Continue to Registration\" to proceed.");
		body += "<input type=\"button\" onclick=\"goRegister()\" value=\"Continue to Registration\">";
		body += "<br><br>";
		
		body += studyBodyFooter();
		body += "</body>";
		
		return body;
	}
	
	private static String makeRegistrationBody(String feedback) {

		String body = "<body>";
		body += studyBodyHeader();
		
		body += "<span id=\"reg_form\">";

		body += wrapText("h2", "User Registration");
		body += "<label for=\"email1\">Email:</label><br><br>";
		body += "<input type=\"text\" id=\"email1\" style=\"width: 300px; max-width: 70%;\">";
		body += "<p style=\"color: red;\" id=\"email1_feedback\"></p><br>";
		body += "<label for=\"email2\">Confirm Email:</label><br><br>";
		body += "<input type=\"text\" id=\"email2\" style=\"width: 300px; max-width: 70%;\">";
		body += "<p style=\"color: red;\" id=\"email2_feedback\"></p><br>";
		
		// Informed Consent
		body += wrapText("h2", "Informed Consent");
		body += wrapText("p", "Please read carefully and type your full name to agree:");
		String agreement = "I voluntarily agree to take part in this study. I understand that I am free to withdraw from this study at any time, "
				+ "without reason and without cost. I understand I will not receive financial compensation for my participation in this study. "
				+ "I understand that any personally identifiable information collected during this study will be kept private and will not be shared.";
		body += "<p id=\"n_agreement\">" + agreement + "</p><br>";

		String consent = "Full Name";
		body += SurveyUtil.shortTextHTML(consent, "_consent");
		
		body += "<input id=\"register\" type=\"button\" onclick=\"register()\" value=\"Register\">";
		body += "<p style=\"color: red;\" id=\"feedback\">" + feedback + "</p>";
		
		body += wrapText("p", "<i>Your personal information will not be shared."
				+ "<br>Email addresses are only used for authentication,"
				+ "<br>or in the rare case that we need to contact you. </i>");
		body += "<br><br>";
		
		String fileName = "clickOnEnter.js";
		body += "<script src=\"js/" + fileName + "\"></script>";
		body += "<script>clickOnEnter(\"email1\", \"register\");</script>";
		body += "<script>clickOnEnter(\"email2\", \"register\");</script>";
		body += "<script>clickOnEnter(\"a_consent\", \"register\");</script>";
		
		body += "</span>";
		
		body += studyBodyFooter();
		body += "</body>";

		return body;
	}
	
	public static String makeRegistrationCompleteHTML(String userID, String agreement, String fullName, String email) {

		String html = "";
		
		html += wrapText("h2", "Registration Complete!");
		html += "<p style=\"color: green;\">Please save or print a copy of this page for your records;<br>you won't be able to see it again.</p>";
		html += wrapText("p", "<i>" + agreement + "</i>");
		html += wrapText("p", "<b>Signed</b>:<br>" + fullName);
		html += wrapText("p", "<b>Email</b>:<br>" + email);
		
		html += wrapText("h2", "Personal Access Link");
		html += wrapText("p", "We have generated a unique personal access link, made just for you.");
		html += wrapText("p", "The begin the exercise, click the link to continue.");
		html += wrapText("p", "Remember, we would like you to complete the exercise in one sitting, so please make sure that you are ready.");
		html += wrapText("p", "<b>Personal Access Link</b>:<br>"
				+ "<a href=\"/?user=" + userID + "\">" + THIS_URL + "/?user=" + userID + "</a>");
		html += "<br><br>";
		
		return html;
	}
	
	private static int NUM_STUDY_PAGES = 5;
	
	private static String makeStudyBody(String user, String page, boolean deactivated) {

		UserPrefixStudy ups = RegisterUtil.parseStudyPrefix(user);
		
		String body = "<body>";
		body += studyBodyHeader();
		
		if (!page.equals("finish")) {
			body += wrapText("p", "Your User ID: <b><span id=\"userID\">" + user + "</span></b>");
			body += wrapText("p", "Page: " + page + " of " + NUM_STUDY_PAGES);
			body += "<hr>";
		}
		
		// Page 1
		if (page.equals("1")) {
			body += wrapText("p", "Welcome!");
			body += wrapText("p", "Now that you've begun, please continue to the end until you are finished.");
			
			body += wrapText("h2", "Entry Survey");
			body += "<span id=\"survey\">";
			
			if(Survey.exists(user, SurveyType.ENTRY)) {
				
				body += "<p style=\"color: green\">You have already completed the entry survey. Thanks!</p>";
				
			} else {
				
				// Age
				String _age = "What is your age?";
				body += SurveyUtil.choicesHTML(_age, "_age", new String[]{
						"18 - 24", 
						"25 - 34", 
						"35 - 44", 
						"45 - 54", 
						"55 - 64", 
						"65 +"
				});
				
				// Background
				String _background = "Do you have any background in architecture, urban planning, or real estate?";
				body += SurveyUtil.choicesHTML(_background, "_background", new String[]{
						"Yes", 
						"No"
				});
				
				// Field Expertise
				String _fieldExp = "Please rate your expertise in the following fields:";
				body += "<p id=\"n_fieldExp\">" + _fieldExp + "</p>";
				body += SurveyUtil.rangeHTML("Architecture", "_fieldExp.1", "None", "Expert", 5);
				body += SurveyUtil.rangeHTML("Urban Planning", "_fieldExp.2", "None", "Expert", 5);
				body += SurveyUtil.rangeHTML("Real Estate Development", "_fieldExp.3", "None", "Expert", 5);
				body += SurveyUtil.rangeHTML("Computer Science", "_fieldExp.4", "None", "Expert", 5);
				body += SurveyUtil.rangeHTML("Other Engineering", "_fieldExp.5", "None", "Expert", 5);
				
				body += "<br>";
				
				// CAD Expertise
				String _cadExp = "Please rate your experience using computer-aided design or spatial software such as AutoCAD, ArcGIS, or SketchUp:";
				body += SurveyUtil.rangeHTML(_cadExp, "_cadExp", "None", "Expert", 5);
				
				// CAD Frequency
				String _cadFreq = "How often do you use computer-aided design or spatial modeling software?";
				body += SurveyUtil.choicesHTML(_cadFreq, "_cadFreq", new String[] {
						"At least once per week",
						"At least once per month",
						"At least once per year",
						"Rarely or Never"
				});
				
				
				body += "<br><input type=\"button\" onclick=\"entrySurvey()\" value=\"Submit\"><br><br>";
			}
			
			body += "</span>";
			body += "<p id=\"feedback\" style=\"color: red;\"></p>";

		// Page 2
		} else if (page.equals("2")) {
			body += wrapText("h2", Server.NAME + " Model");
			body += wrapText("p", "During this exercise, it's important that you understand some vocabulary.");
			body += wrapText("p", "Review this diagram of a real estate development model, as well as a glossary of important terms.");
			body += "<img style=\"width: 560px; max-width: 100%;\" src=\"" + MODEL_DIAGRAM_URL + "\" alt=\"Diagram of Model Components for " + Server.NAME + "\">";
			
			body += glossary();
			
			body += wrapText("h2", "Tutorial");
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
			
			} else {
				tutorialUrl = PANDA_TUTORIAL_URL;
			}

			body += "<iframe style=\"width: 560px; height: 315px; max-width: 100%\" src=\"" + tutorialUrl + "\" "
					+ "title=\"" + Server.NAME + " Tutorial\" frameborder=\"0\" "
					+ "allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" "
					+ "allowfullscreen></iframe>";
			body += wrapText("p", "Click \"CONTINUE\" when you are done watching the video."
					+ "<br><i>(Feel free to come back to this page later for reference)</i>");

		// Page 3
		} else if (page.equals("3")) {
			body += wrapText("h2", "Background");
			body += wrapText("p", "A generous benfactor recently donated a vacant piece of land (\"The Site\") to the City of Beaverton.");
			body += wrapText("p", "The only condition is that the site must be used to host a new <i>College for the Arts</i>, along with <i>new housing</i> for the local community.");
			
			body += wrapText("h2", "The Site");
			body += "<img style=\"width: 560px; max-width: 100%;\" src=\"" + SITE_BASEMAP_URL + "\" alt=\"Diagram of Model Components for " + Server.NAME + "\">";
			
			body += wrapText("h2", "Requirements");
			body += "<ul>";
			body += wrapText("li", "The Site must have AT LEAST <u>50,000</u> sqft of <b>residential function</b>");
			body += wrapText("li", "The Site must have AT LEAST <u>80,000</u> sqft of <b>institutional function</b> (Arts College)");
			body += wrapText("li", "The Site's Floor Area Ratio (FAR) must be NO MORE than <u>5.0</u>");
			body += "</ul>";
			
			body += wrapText("h2", "Your Role");
			body += "<ul>";
			body += wrapText("li", "As a trusted local community member, citizens have elected you to finalize the design for the site.");
			body += wrapText("li", "Aside from the requirements above, the design of the site is completely up to you.");
			body += wrapText("li", "Ultimately, you must choose a single scenario.");
			body += "</ul>";
			
			body += "<hr>";
			body += wrapText("h2", "Get Started!");
			body += wrapText("p", "You may now do any or all of the following with " + Server.NAME + ":");
			body += "<ol>";
			
			if (ups != UserPrefixStudy.COBRA) {
				body += wrapText("li", "Use " + Server.NAME + " to load and analyze pre-designed scenarios:");
				body += "<ul>";
				body += wrapText("li", "Option 1");
				body += wrapText("li", "Option 2");
				body += wrapText("li", "Option 3");
				body += "</ul>";
			
			}
			
			if (ups == UserPrefixStudy.PANDA) {
				body += wrapText("li", "Use " + Server.NAME + " to edit predesigned scenarios (Option 1, 2, or 3) and save them as new scenarios with a differnt name of your choosing.");
			}
			
			if (ups != UserPrefixStudy.ZEBRA) {
				body += wrapText("li", "Use " + Server.NAME + " to design scenarios from scratch, saving them with names of your choosing.");
				body += wrapText("li", "Use the \"Save Scenario\" toolbox to name and save variations.");
				body += wrapText("li", "You may make as many new scenarios as you like, so don't hold back!");
			}
			body += "</ol>";

			body += wrapText("h2", "Using " + Server.NAME);
			body += wrapText("p", "Take as long as you like, just be sure to finish everything in <u>one sitting</u>.</i>");
			body += wrapText("p", "Log in to " + Server.NAME + " with the <b>User ID</b> at the top of this page. "
					+ "Clicking the button below will open " + Server.NAME + " in a new tab, but feel free to come back and view this page while you work");
			//body += wrapText("p", "<b>" + Server.NAME + "</b>:<br><a href=\"" + OPENSUI_URL + "\" target=\"_blank\">" + OPENSUI_URL + "</a>");
			body += "<input "
					+ "type=\"button\" "
					+ "style=\"width: 560px; max-width: 93%;\" "
					+ "value=\"Click Here To Open " + Server.NAME + "\" "
					+ "onclick=\"window.open('" + OPENSUI_URL + "', '_blank');\">";
			
			body += wrapText("p", "<i>If you experience any technical difficulties or errors during your exercise, you may reload " + Server.NAME + " in your browser and log in again.</i>");
			
			body += wrapText("h2", "Submitting your Final Design");
			body += wrapText("p", "To finish, you need to select your preferred favorite scenario.");
			
			if (ups == UserPrefixStudy.ZEBRA) {
				body += wrapText("p", "You may choose <b>one</b> of the pre-designed scenarios (Option 1, Option 2, or Option 3).");
			
			} else if (ups == UserPrefixStudy.COBRA) {
				body += wrapText("p", "You may choose the scenario currently on your screen, or any <b>one</b> of the saved scenarios you have created.");
				
			} else if (ups == UserPrefixStudy.PANDA) {
				body += wrapText("p", "You may choose <b>one</b> of the pre-designed scenarios (Option 1, Option 2, or Option 3), or a scenario that you made or edited.");
			}
			
			if (ups != UserPrefixStudy.ZEBRA) {
				body += wrapText("p", "To submit your final decision, make sure the scenario of your choosing is loaded and visible on the screen."
						+ " Then, save the scenario with the name <b>final</b>");
				body += wrapText("p", "Confirm that the scenario <b>final</b> is saved by looking in the list of scenarios available to load; the word \"final\" should show up in the list.");
			
			} else {
				body += wrapText("p", "Make a note of the option you chose.");
				
			}
			body += wrapText("p", "When you've finished making your decision, click \"CONTINUE\".");
		
		// Page 4
		} else if (page.equals("4")) {
			body += wrapText("p", "Thank you for helping the community of Beaverton!");

			body += wrapText("h2", "Exit Survey");
			body += "<span id=\"survey\">";
			
			// Survey Is Already Completed
			if(Survey.exists(user, SurveyType.EXIT)) {
				body += "<p style=\"color: green\">You have already completed the exit survey. Thanks!</p>";
			
			// Build the Exit Survey
			} else {
				body += wrapText("p", "Now that you've finished the exercise, we need you to complete one more short survey. We promise this is the last thing!");
				
				ArrayList<String> scenarioOptions = new ArrayList<String>();
				if (ups != UserPrefixStudy.COBRA) {
					scenarioOptions.add("I chose Option 1");
					scenarioOptions.add("I chose Option 2");
					scenarioOptions.add("I chose Option 3");
				}
				if (ups == UserPrefixStudy.PANDA) {
					scenarioOptions.add("I chose a modified version of Option 1");
					scenarioOptions.add("I chose a modified version of Option 2");
					scenarioOptions.add("I chose a modified version of Option 3");
				}
				if (ups != UserPrefixStudy.ZEBRA) {
					scenarioOptions.add("I chose my own completely original scenario");
				}
				scenarioOptions.add("I could not choose a scenario (please elaborate below)");
				String[] sOptions = new String[scenarioOptions.size()];
				for (int i=0; i<scenarioOptions.size(); i++) {
					sOptions[i] = scenarioOptions.get(i);
				}
				
				// Scenario
				String _scenario = "What best describes the scenario you ultimately chose?";
				body += SurveyUtil.choicesHTML(_scenario, "_scenario", sOptions);
				
				// Influence
				String _influence = "Describe what factors most influenced your final decision.";
				body += SurveyUtil.longTextHTML(_influence, "_influence");
				
				// Satisfaction
				String _satisfaction = "How *satisfied* are you with your final scenario?";
				body += SurveyUtil.rangeHTML(_satisfaction, "_satisfaction", "Not Satisfied", "Very Satisfied", 5);
				
				// Confidence
				String _confidence = "How *confident* are you that the community of Beaverton will like your final scenario?";
				body += SurveyUtil.rangeHTML(_confidence, "_confidence", "Not Confident", "Very Confident", 5);
				
				// Usability
				String _usability = "How would you describe the usability of the modeling tool, \"" + Server.NAME + "\"?";
				body += SurveyUtil.rangeHTML(_usability, "_usability", "Not Usable", "Easy to Use", 5);
				
				// Learning
				String _learning = "Did this exercise allow you to learn something new about real estate design?";
				body += SurveyUtil.rangeHTML(_learning, "_learning", "Learned Nothing", "Learned A lot", 5);
				
				// Comments
				String _comments = "Please write any other notes or feedback about your experience.";
				body += SurveyUtil.longTextHTML(_comments, "_comments");
				
				body += "<br><input type=\"button\" onclick=\"exitSurvey()\" value=\"Submit\"><br><br>";
			}
			
			body += "</span>";
			body += "<p id=\"feedback\" style=\"color: red;\"></p>";
		
		// Page 5
		} else if (page.equals("5")) {
			body += wrapText("h2", "Finishing Up");
			
			boolean consentComplete = Survey.exists(user, SurveyType.CONSENT);
			boolean entryComplete = Survey.exists(user, SurveyType.ENTRY);
			boolean exitComplete = Survey.exists(user,  SurveyType.EXIT);
			boolean finalComplete = Server.hasScenario(user, "final") || (ups == UserPrefixStudy.ZEBRA && exitComplete);
			
			body += "<p>";
			body += completionStatus(consentComplete, "Informed Consent") + "<br>";
			body += completionStatus(entryComplete, "Entry Survey") + "<br>";
			body += completionStatus(exitComplete, "Exit Survey") + "<br>";
			body += completionStatus(finalComplete, "Choose Final Scenario");
			body += "</p>";
			
			if (consentComplete && entryComplete && exitComplete && finalComplete) {
				body += wrapText("p", "It looks like you've finished everything!"
						+ "<br>Click \"I'm Finished\" to end the experiment."
						+ "<br><i>You will not be able to return.</i>");
				String finishedUrl = "/?user=" + user + "&page=finish";
				body += "<input type=\"button\" value=\"I'm Finished!\" onclick=\"location.replace('" + finishedUrl + "');\">";
			} else {
				body += wrapText("p", "Some items appear to be missing."
						+ "<br>Please go back and finish them before ending the experiment."
						+ "<br>Come back to this page or refresh when you are done.");
			}
			
		
		// Last Page
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
			
			body += wrapText("h2", "Congratulations!");
			body += "<p style=\"color: green;\">Please save or print a copy of this page for your records.</p>";
			body += wrapText("p", "We hope you had a good time, and we are incredibly grateful for your help with this research.");
			
			body += wrapText("h2", "Pass it Forward");
			body += wrapText("p", "Please invite your friends and colleagues to participate in this experiment.");
			body += wrapText("p", "<b>Beaverton Study</b>:<br><a href=\"" + THIS_URL + "\" target=\"_blank\">" + THIS_URL + "</a>");
			body += wrapText("p", "We appreciate that you don't share your experience with anyone, unless they have also completed this exercise.");
			
			body += wrapText("h2", "Keep Using " + Server.NAME);
			body += wrapText("p", "The User ID you had during this exercise, <b>" + user + "</b>, is now deactivated.</i>");
			body += wrapText("p", "However, we've generated a <i>new</i> User ID, <b>" + adminUserID + "</b>, just for you, that you can use indefinitely. "
					+ "Please don't share it with anyone.");
			body += wrapText("p", "This new User ID will allow you to use certain features that may have been unlocked during the exercise. "
					+ "(For example, the ability edit and delete scenarios)");
			body += wrapText("p", "If you would like to keep using " + Server.NAME + ", please do so with your new User ID.");
			body += wrapText("p", "<b>Your Email</b>:<br>" + email);
			body += wrapText("p", "<b>New User ID</b>:<br>" + adminUserID);
			body += wrapText("p", "<b>Fuzzy IO</b>:<br><a href=\"" + OPENSUI_URL + "\" target=\"_blank\">" + OPENSUI_URL + "</a>");
			body += wrapText("p", "If you have any questions or concerns about your participation, or have trouble using your new User ID, please contact Ira.");
			
			body += "<hr>";
			body += wrapText("p", "You may now close all browser windows related to this experiment.");
			
		} else {
			return makeNullBody("404", "Resource Not Found");

		}
		body += studyNavigation(user, page);
		body += studyBodyFooter();
		body += "</body>";

		return body;
	}
	
	private static String completionStatus(boolean status, String item) {
		if (status) {
			return "<span style=\"color: green;\">[Complete] " + item + "</span>";
		} else {
			return "<span style=\"color: red;\">[Incomplete] " + item + "</span>";
		}
	}
	
	private static String studyNavigation(String user, String page) {
		
		String bodyText = "";
		
		// Add Next Page Button
		if (!page.equals("finish")) {
			
			int pageInt = Integer.valueOf(page);
			
			String leftNav = "<p>---</p>";
			String rightNav = "<p style=\"float: right;\">---</p>";
			if (pageInt > 1 && pageInt <= NUM_STUDY_PAGES) {
				int prevPage = pageInt - 1;
				leftNav = "<p>< <a href=\"/?user=" + user + "&page=" + prevPage + "\">Go Back</a></p>";
			}
			if (pageInt >= 1 && pageInt < NUM_STUDY_PAGES) {
				int nextPage = pageInt + 1;
				rightNav = "<p style=\"float: right;\"><a href=\"/?user=" + user + "&page=" + nextPage + "\">CONTINUE</a> ></p>";
			}
			
			bodyText += "<hr>";
			bodyText += "<div class=\"row\">";
			bodyText += "<div class=\"column\" style=\"float: left\">" + leftNav + "</div>";
			bodyText += "<div class=\"column\" style=\"float: right\">" + rightNav + "</div>";
			bodyText += "</div>";
		}
		
		return bodyText;
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
