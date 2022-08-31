package edu.mit.ira.fuzzy.pages;

import java.io.File;

import edu.mit.ira.fuzzy.FuzzyIO;
import edu.mit.ira.fuzzy.io.Server;

public class Pages {

	private static int NUM_STUDY_PAGES = 6;

	public static String generalSite() {
		String head = makeHead();
		String body = makeGeneralBody();
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

		head += "body {margin-bottom: 60px;margin-top: 40px;max-width: 800px;background-color:#ffffff;background-image:url(" + imgUrl + ");background-repeat:no-repeat;background-position:center center;background-attachment:fixed;}";
		head += "h1 {text-align:left;margin-left: 40px;margin-right: 40px;font-family:Helvetica, sans-serif;color:#696969;}";
		head += "h2 {text-align:left;margin-left: 40px;margin-right: 40px;font-family:Helvetica, sans-serif;color:#696969;}";
		head += "h3 {text-align:left;margin-left: 40px;margin-right: 40px;font-family:Helvetica, sans-serif;color:#696969;}";
		head += "p {text-align:left;margin-left: 40px;margin-right: 40px;font-family:Helvetica, sans-serif;font-size:14px;font-style:normal;font-weight:normal;color:#525252;}";
		head += "li {text-align:left;margin-left: 40px;margin-right: 40px;font-family:Helvetica, sans-serif;font-size:14px;font-style:normal;font-weight:normal;color:#525252;}";
		head += "ul li { margin-bottom: 10px; }";
		head += "ol li { margin-bottom: 10px; }";

		head += "</style>";

		head +="</head>";

		return head;
	}

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

	private static String makeStudyBody(String user, String page) {

		String userPrefix = user.substring(0, Server.VALID_PREFIX_LENGTH);
		
		int minutes;
		if (userPrefix.equals(Server.VALID_USER_PREFIXES[0])) {
			minutes = 30;
		} else {
			minutes = 50;
		}
		
		String body = "<body>";

		body += wrapText("h1", "Beaverton");
		body += wrapText("p", "User ID: <b>" + user + "</b>");
		body += wrapText("p", "page: " + page + " of " + NUM_STUDY_PAGES);
		body += "<hr>";

		if (page.equals("1")) {

			body += wrapText("h2", "Introduction");
			
			body += wrapText("p", "Welcome to Beaverton, a research study!");

			body += wrapText("p", "We deeply appreciate your participation.");

			body += wrapText("p", "The University of Tokyo and MIT are investigating how people make decisions using interactive models.");

			body += wrapText("p", "In this exercise, we will ask you to help design a building in the hypothetical city of Beaverton.");

			body += wrapText("h2", "Requirements");

			body += "<ul>";

			body += wrapText("li", "PC or laptop with speakers or earphones" +
					"<br><i>(Do not try this exercise on a tablet or smart phone)</i>");

			body += wrapText("li", "Reliable internet connection");

			body += wrapText("li", "Web browser (Chrome, Safari, Firefox, etc)" +
					"<br><i>(The browser you are using to read this text is probably fine)</i>");

			body += wrapText("li", "Keyboard and mouse" +
					"<br><i>(Track pad is fine, but you may have better experience using a two-button mouse)</i>");

			body += "</ul>";

			body += wrapText("p", "This exercise may take about " + minutes + "-" + (minutes+20) + " minutes.");

			body += wrapText("p", "Once you start, please continue to the end without pausing.");

			body += wrapText("p", "Please do not close this browser window for the duration of the exercise. ");

			body += wrapText("h2", "Get Started!");

			body += wrapText("p", "When you are ready, click \"CONTINUE\" to get started!");


		} else if (page.equals("2")) {

			body += wrapText("h2", "User ID");
			
			body += wrapText("p", "First, we need to get some housekeeping out of the way.");

			body += wrapText("p", "You have been assigned an exclusive User ID <i>(You can see it at the top of this web page -- it should be an animal followed by 6 characters)</i>.");

			body += wrapText("p", "Please use this ID throughout the exercise, and don't share it with anyone. It will be at the top of every page for reference.");

			body += wrapText("h2", "Pre-Survey");

			body += wrapText("p", "Next, please complete this pre-survey.");

			String presurveyUrl = "https://forms.gle/cK3dQozbQyt1S2zn7";

			body += wrapText("p", "--> <a href=\"" + presurveyUrl + "\" target=\"_blank\">Pre-Survey</a>");

			body += wrapText("p", "(The survey will open in a new tab. Come back here and click \"CONTINUE\" when you are done.)");

		} else if (page.equals("3")) {

			body += wrapText("h2", "FuzzyIO Tutorial");

			body += wrapText("p", "Now, we need you to prepare for your role by watching a quick tutorial video.");

			body += wrapText("p", "Please watch the entire video with the sound on, and do not skip any parts.");

			String tutorialUrl;
			
			if (userPrefix.equals(Server.VALID_USER_PREFIXES[0])) {
				
				// Needs to be updated
				tutorialUrl = "https://youtu.be/cNoS-bhRPEw";
				
			} else if (userPrefix.equals(Server.VALID_USER_PREFIXES[0])) {
				
				// Needs to be updated
				tutorialUrl = "https://youtu.be/cNoS-bhRPEw";
				
			} else if (userPrefix.equals(Server.VALID_USER_PREFIXES[0])) {
				
				// Needs to be updated
				tutorialUrl = "https://youtu.be/cNoS-bhRPEw";
				
			} else {
				
				tutorialUrl = "https://youtu.be/cNoS-bhRPEw";
				
			}

			body += wrapText("p", "--> <a href=\"" + tutorialUrl + "\" target=\"_blank\">FuzzyIO Tutorial</a>");

			body += wrapText("p", "(The video will open in a new tab. Come back here and click \"CONTINUE\" when you are done.)");

		} else if (page.equals("4")) {

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

			body += wrapText("li", "<b>Floor Area Ratio</b> (FAR) - Ratio of Gross Floor Area (GFA) to Gross Land Area (GLA) [sqft/sqft]");

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
			
			if (!userPrefix.equals(Server.VALID_USER_PREFIXES[1])) {
			
				body += wrapText("li", "Use FuzzyIO to load and analyze 3 pre-designed scenarios for the area marked \"SITE\"");
	
				body += "<ul>";
	
				body += wrapText("li", "Option_A");
	
				body += wrapText("li", "Option_B");
	
				body += wrapText("li", "Option_C");
	
				body += "</ul>";
			
			}
			
			if (userPrefix.equals(Server.VALID_USER_PREFIXES[2]) || userPrefix.equals(Server.VALID_USER_PREFIXES[3])) {

				body += wrapText("li", "Use FuzzyIO to edit predesigned scenarios (A, B, or C) and save them as a new scenarios with a differnt name of your choosing.");
			
			}
			
			if (!userPrefix.equals(Server.VALID_USER_PREFIXES[0])) {

				body += wrapText("li", "Use FuzzyIO to design scenarios from scratch, saving them with names of your choosing.");
			
			}
			
			if (!userPrefix.equals(Server.VALID_USER_PREFIXES[0])) {

				body += wrapText("li", "Use the \"Save Scenario\" toolbox often to name and save variations. You may make as many new scenarios as you like, so don't hold back!");
				
			}
			
			body += "</ol>";
			
			body += wrapText("p", "<i>If you experience any technical difficulties or errors during your exercise, you may reload FuzzyIO in your browser and log in again.</i>");

			body += wrapText("h2", "Using FuzzyIO");
			
			body += wrapText("p", "<b>Set a timer for <u>" + minutes + " minutes</u></b>."
					+ "<br><i>It's okay if you finish sooner than that, or even take a little longer. Just be sure to finish everything in one session.</i>");
			
			body += wrapText("p", "Go ahead and login to FuzzyIO with the <b>User ID</b> at the top of this page.");
			
			String fuzzyUrl = "http://opensui.org";

			body += wrapText("p", "--> <a href=\"" + fuzzyUrl + "\" target=\"_blank\">FuzzyIO</a>");

			body += wrapText("p", "(FuzzyIO will open in a new tab, but feel free to view this page while you work)");
			
			body += wrapText("h2", "Submitting your Final Design");
			
			body += wrapText("p", "At the end of " + minutes + " minutes (or sooner, if you wish), you need to choose a single scenario.");
			
			if (userPrefix.equals(Server.VALID_USER_PREFIXES[0])) {
			
				body += wrapText("p", "You may choose <b>one</b> of the pre-designed scenarios (Option A, Option B, or Option C).");
			
			} else if (userPrefix.equals(Server.VALID_USER_PREFIXES[1])) {
				
				body += wrapText("p", "You may choose the scenario currently on your screen, or any <b>one</b> of the saved scenarios you have created.");
				
			} else {
				
				body += wrapText("p", "You may choose <b>one</b> of the pre-designed scenarios (Option A, Option B, or Option C), or a scenario of your own editing or making.");
				
			}
			
			if (!userPrefix.equals(Server.VALID_USER_PREFIXES[0])) {
				
				body += wrapText("p", "To submit your final decision, make sure the scenario of your choosing is loaded and visible."
						+ " Then, save the scenario with the name <b>final</b>");
	
				body += wrapText("p", "Confirm that the scenario <b>final</b> is saved by looking in the list of scenarios available to load; the word \"final\" should show up in the list.");
			
			} else {
				
				body += wrapText("p", "Make a note to yourself or write down which option that you choose. You'll need to remember this a little later.");
				
			}
			
			body += wrapText("p", "When you've made your final decision, click \"CONTINUE\".");

		} else if (page.equals("5")) {

			body += wrapText("h2", "Post-Survey");
			
			body += wrapText("p", "Thank you for helping the community of Beaverton!");

			body += wrapText("p", "Now that you've finished the exercise, we need you to complete one more short survey. We promise this is the last thing!");

			String postsurveyUrl;

			if (userPrefix.equals(Server.VALID_USER_PREFIXES[0])) {

				postsurveyUrl = "https://forms.gle/JkDfk8sqxWxtg8kw6";

			} else if (userPrefix.equals(Server.VALID_USER_PREFIXES[1])) {

				postsurveyUrl = "https://forms.gle/rkLnBX2TgAiPHi8b7";

			} else if (userPrefix.equals(Server.VALID_USER_PREFIXES[2])) {

				postsurveyUrl = "https://forms.gle/EY1ntPzmbRZrtC159";

			} else {

				postsurveyUrl = "https://ira.mit.edu";

			}

			body += wrapText("p", "--> <a href=\"" + postsurveyUrl + "\" target=\"_blank\">Post-Survey</a>");

			body += wrapText("p", "(The survey will open in a new tab. Come back here and click \"CONTINUE\" when you are done.)");

		} else if (page.equals("6")) {

			body += wrapText("h2", "Congratualtions and Thank you!");

			body += wrapText("p", "We hope you had a good time, and we are incredibly grateful for your help with this research.");

			body += wrapText("p", "If you have any questions or concerns about your participation, you may contact Ira Winder at the following address:");

			body += wrapText("p", "ira [at] mit [dot] edu");

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
		if (pageInt > 1 && pageInt <= 6) navigation += "< <a href=\"/?user=" + user + "&page=" + prevPage + "\">Go Back</a>";
		if (pageInt > 1 && pageInt < 6)navigation += "\t|\t";
		if (pageInt >= 1 && pageInt < 6) navigation += "<a href=\"/?user=" + user + "&page=" + nextPage + "\">CONTINUE</a> >";
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
