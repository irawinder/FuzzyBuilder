package edu.mit.ira.fuzzy.server;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import edu.mit.ira.fuzzy.javascript.Javascript;
import edu.mit.ira.fuzzy.model.Development;
import edu.mit.ira.fuzzy.model.build.Build;
import edu.mit.ira.fuzzy.model.evaluate.Evaluate;
import edu.mit.ira.fuzzy.model.schema.Schema;
import edu.mit.ira.fuzzy.pages.Pages;
import edu.mit.ira.fuzzy.server.log.PerformanceLog;
import edu.mit.ira.fuzzy.server.log.ServerLog;
import edu.mit.ira.fuzzy.server.log.UserLog;
import edu.mit.ira.fuzzy.server.user.Register;
import edu.mit.ira.fuzzy.server.user.RegisterUtil;
import edu.mit.ira.fuzzy.server.user.UserPrefixAdmin;
import edu.mit.ira.fuzzy.server.user.UserPrefixStudy;
import edu.mit.ira.fuzzy.server.user.UserType;
import edu.mit.ira.fuzzy.survey.Survey;
import edu.mit.ira.fuzzy.survey.SurveyType;
import edu.mit.ira.opensui.data.ParseConfiguration;
import edu.mit.ira.opensui.objective.MultiObjective;
import edu.mit.ira.opensui.setting.Configuration;

/**
 * Server listens and responds to requests for fuzzy masses via HTTP
 *
 * @author Ira Winder
 *
 */
public class Server {
	
	public static final String NAME = "FuzzyIO";
	public static final String VERSION = "v1.5.10";
	public static final String AUTHOR = "Ira Winder, Daniel Fink, and Max Walker";
	public static final String SPONSOR = "MIT Center for Real Estate";
	public static final String CONTACT = "fuzzy-io@mit.edu";
	public static final String SYSTEM = "Java " + System.getProperty("java.version");
	
	public static final String RELATIVE_DATA_PATH = "res";
	private static final String REQUEST_FILE = "configuration.json";
	private static final String RESPONSE_FILE = "solution.json";
	private static final String SUMMARY_FILE = "summary.csv";
	
	// Resource identifiers
	public static final String RES_PING = "ping";
	public static final String RES_ROOT = "";
	public static final String RES_JS = "js";
	public static final String RES_REGISTER = "register";
	public static final String RES_SURVEY = "survey";
	public static final String RES_SIM = "opensui";
	public static final String RES_SIM_INIT = "init";
	public static final String RES_SIM_LIST = "list";
	public static final String RES_SIM_BASEMAP = "basemap";
	public static final String RES_SIM_BASEMAPS = "basemaps";
	public static final String RES_SIM_SUMMARY = "summary";
	public static final String RES_SIM_SAVE = "save";
	public static final String RES_SIM_LOAD = "load";
	public static final String RES_SIM_DELETE = "delete";
	public static final String RES_SIM_RUN = "run";
	
	// Server Objects
	private HttpServer server;
	private String info;
	
	// Model Objects
	private Configuration adminConfig, fullConfig, readOnlyConfig, guestConfig;
	
	/**
	 * Construct a new Server
	 * @param name name of server
	 * @param version version of server
	 * @param port port to open for HTTP Requests
	 * @throws IOException
	 */
	public Server(int port) throws IOException {
		server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/", new MyHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
		
		Register.init();
		
		 // save, delete, load, config, auto-load-basemap
		guestConfig = Schema.get(false, false, true, true, false);
		readOnlyConfig = Schema.get(false, false, true, false, true);
		fullConfig = Schema.get(true, false, true, true, true);
		adminConfig = Schema.get(true, true, true, true, false);
		
		info = "--- " + NAME + " " + VERSION + " ---\nActive on port: " + port;
		System.out.println(info);
	}

	/**
	 * Listen for an HTTP Requests
	 */
	private class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {
			
			// Parse Request Header
			String method = t.getRequestMethod();
			String requestURI = t.getRequestURI().toString();
			String clientIP = t.getRemoteAddress().toString();
			String[] resource = ServerUtil.parseResource(requestURI);
			Map<String, String> requestParameters = ServerUtil.parseParameters(requestURI);
			String user = RegisterUtil.formalCase(requestParameters.get("user"));
			
			boolean pingResource = resource[0].equals(RES_PING);
			boolean jsResource = resource[0].equals(RES_JS) && resource.length > 1;
			boolean registerResource = resource[0].equals(RES_REGISTER);
			boolean surveyResource = resource[0].equals(RES_SURVEY) && resource.length > 1 && !user.equals(ServerUtil.DEFAULT_USER);
			boolean simResource = resource[0].equals(RES_SIM) && resource.length > 1;
			boolean siteResource = resource[0].equals(RES_ROOT);
			boolean deactivated = Register.isDeactivated(user);
			boolean permitted = Register.isActive(user) || RegisterUtil.ignoreCaseEquals(user, ServerUtil.DEFAULT_USER) || deactivated;
			
			// Log Request
			if (!pingResource) {
				ServerLog.add(t, "Request: " + method + " " +  requestURI);
			}
			
			// Options Requested
			if (method.equals("OPTIONS")) {
				
				// OPTIONS request is something browsers ask before 
				// allowing an external server to provide data
				String message = "HTTP Options Delivered";
				ServerUtil.packItShipIt(t, 200, message);

			// Request a simple ping
			} else if (pingResource) {
				pingRequest(t, method);

			// Javascript Resource Requested
			} else if (jsResource) {
				jsRequest(t, method, resource);

			// Survey Resource Requested
			} else if (surveyResource) {
				surveyRequest(t, method, resource, user, permitted, deactivated);
			
			// Registration Requested
			} else if (registerResource) {
				registerRequest(t, method, resource, requestParameters);
				
			// Web Resource Requested
			} else if (siteResource) {
				pageRequest(t, clientIP, method, resource, requestParameters, user, permitted, deactivated);
			
			// OpenSUI is requesting resource
			} else if (simResource) {
				simRequest(t, clientIP, method, resource, requestParameters, user, permitted, deactivated);
				
			// 404 Resource Note Found
			} else {
				String responseBody = Pages.nullSite("404", "Resource Not Found");
				ServerUtil.packItShipIt(t, 404, "Resource Not Found", responseBody, "text/html");
			}
		}
	}
	
	private void pingRequest(HttpExchange t, String method) throws IOException {
		
		// HTTP HEAD Request
		if (method.equals("HEAD")) {
			ServerUtil.echo(t, 200);
			
		// Method Not Allowed
		} else {
			String message = "Method Not Allowed";
			ServerUtil.packItShipIt(t, 405, message);
		}
	}
	
	private void jsRequest(HttpExchange t, String method, String[] resource) throws IOException {
		
		// HTTP GET Request
		if (method.equals("GET")) {
			
			String fileName = resource[1];
			String responseBody = Javascript.load(fileName);
			if (responseBody != null ) {
				ServerUtil.packItShipIt(t, 200, "Javascript Delivered", responseBody, "application/javascript");
				
			} else {
				String message = "Resource Not Found";
				ServerUtil.packItShipIt(t, 404, message);
			}
			
		// Method Not Allowed
		} else {
			String message = "Method Not Allowed";
			ServerUtil.packItShipIt(t, 405, message);
		}
	}
	
	private void registerRequest(HttpExchange t, String method, String[] resource, Map<String, String> params) throws IOException {
		
		String email = params.get("email").toLowerCase();
		
		if (method.equals("POST")) {
			
			if (email.equals(ServerUtil.DEFAULT_EMAIL)) {
				ServerUtil.packItShipIt(t, 400, "Bad Request", "Must Send Email as Parameter", "text/plain");
				
			} else {
				String userID = Register.makeUser(email, UserType.STUDY);
				String requestBody = ServerUtil.parseRequestBody(t);
				
				JSONArray consent = new JSONArray(requestBody);
				String agreement = consent.getJSONObject(0).getString("n");
				String fullName = consent.getJSONObject(1).getString("a");
				
				// Registration Successful
				if (userID != null) {
					Survey.save(userID, SurveyType.CONSENT, requestBody);
					String responseBody = Pages.makeRegistrationCompleteHTML(userID, agreement, fullName, email);
					ServerUtil.packItShipIt(t, 200, "Registration Successful", responseBody, "text/html");

				// Registration failed
				} else {

					// Email already active
					if (Register.isActiveEmail(email)) {
						ServerUtil.packItShipIt(t, 403, "Forbidden", "This email is already in use.", "text/plain");

						// Something else went wrong
					} else {
						ServerUtil.packItShipIt(t, 403, "Forbidden", "Something went wrong and we don't know why.", "text/plain");
					}
				}
			}
		
		} else if (method.equals("GET")) {
			
			// blank registration form (no email parameter is submitted)
			if (email.equals(ServerUtil.DEFAULT_EMAIL)) {
				ServerUtil.packItShipIt(t, 200, "Success", Pages.registrationSite(""), "text/html");
			
			} else {
				ServerUtil.packItShipIt(t, 400, "Bad Request");
			}
		
		// Method Not Allowed
		} else {
			String message = "Method Not Allowed";
			ServerUtil.packItShipIt(t, 405, message);
		}
	}

	private void surveyRequest(HttpExchange t, String method, String[] resource, String user, boolean permitted, boolean deactivated) throws IOException {
		
		// User has been deactivated
		if (deactivated) {
			ServerUtil.packItShipIt(t, 403, "Forbidden");
			return;

		// User is not valid
		} else if (!permitted) {
			ServerUtil.packItShipIt(t, 401, "Unauthorized");
			return;
		}
		
		// Parse Survey Type
		SurveyType surveyType = null;
		for (SurveyType sT : SurveyType.values()) {
			if (resource[1].equals(sT.lc())) {
				surveyType = sT;
			}
		}
		
		// Not a valid survey resource
		if (surveyType == null) {
			ServerUtil.packItShipIt(t, 404, "Resource Not Found");
			return;
		}

		// HTTP GET Request
		if (method.equals("POST")) {

			// Parse Request Body
			String requestBody = ServerUtil.parseRequestBody(t);
				
			// Cannot save over existing results
			if (Survey.exists(user, surveyType)) {
				ServerUtil.packItShipIt(t, 200, surveyType + " survey already exists.");
			
			// Attempt to save the survey
			} else if (Survey.save(user, surveyType, requestBody)) {
				ServerUtil.packItShipIt(t, 200, "survey saved");
			
			// Something went wrong
			} else {
				ServerUtil.packItShipIt(t, 400, surveyType + " survey could not be saved.");
			}

		// Check if survey file exists
		} else if (method.equals("GET")) {

			// Send "true" if file exists
			if (Survey.exists(user, surveyType)) {
				ServerUtil.packItShipIt(t, 200, surveyType + " survey does exist", "true", "text/plain");

			// Send "false" otherwise
			} else {
				ServerUtil.packItShipIt(t, 200, surveyType + " survey does not exist", "false", "text/plain");
			}

		// Method Not Allowed
		} else {
			String message = "Method Not Allowed";
			ServerUtil.packItShipIt(t, 405, message);
		}
	}
	
	private void pageRequest(HttpExchange t, String clientIP, String method, String[] resource, Map<String, String> params, String user, boolean permitted, boolean deactivated) throws IOException {
		
		String page = params.get("page");
		String htmlContent = "text/html";
		
		// Forbid if not permitted
		if (!permitted) {
			String responseBody = Pages.nullSite("401", "Unauthorized");
			ServerUtil.packItShipIt(t, 401, "Unauthorized", responseBody, "text/html");

		// HTTP GET Request
		} else if (method.equals("GET")) {
			
			// Redirect deactivated users to "finish" page
			if (deactivated) {
				String responseBody = Pages.studySite(user, "finish", deactivated);
				ServerUtil.packItShipIt(t, 200, "HTML Delivered", responseBody, htmlContent);
				UserLog.add(user, clientIP, "HOME", "Visited Study Page : FINISH");
			
			// General Web Resources
			} else if (resource[0].equals(RES_ROOT)) {	
				
				String responseBody, message;
				
				// Is Admin User
				if (RegisterUtil.hasPrefix(user, UserPrefixAdmin.SQUID)) {
					responseBody = Pages.generalSite();
					message = "HTML Delivered";
					UserLog.add(user, clientIP, "HOME", "Visited General Page : GENERAL");
					
				// Is other active user (e.g. study user)	
				} else if (Register.isActive(user)) {
					responseBody = Pages.studySite(user, page, false);
					message = "HTML Delivered";
					UserLog.add(user, clientIP, "HOME", "Visited Study Page : " + page.toUpperCase());
					
				// Is guest (unregistered) user (e.g. 'user' parameter is blank)
				} else if (user.equals(ServerUtil.DEFAULT_USER)){
					responseBody = Pages.studyIntroSite();
					message = "HTML Delivered";
					UserLog.add(user, clientIP, "HOME", "Visited Study Page : INTRO");
				}
				
				// No Resource available
				else {
					message = "Bad Request";
					responseBody = Pages.nullSite("400", message);
				}
				ServerUtil.packItShipIt(t, 200, message, responseBody, htmlContent);
			}
		
		// Method Not Allowed
		} else {
			String responseBody = Pages.nullSite("405", "Method Not Allowed");
			String contentType = "text/html";
			String message = "Method Not Allowed";
			ServerUtil.packItShipIt(t, 405, message, responseBody, contentType);
		}
	}
	
	private void simRequest(HttpExchange t, String clientIP, String method, String[] resource, Map<String, String> params, String user, boolean permitted, boolean deactivated) throws IOException {
		
		String scenario = params.get("scenario").toLowerCase();
		String basemap = params.get("filename").toLowerCase();
		String jsonContent = "application/json";
		
		// User has been deactivated
		if (deactivated) {
			ServerUtil.packItShipIt(t, 403, "Forbidden");
			return;
		
		// User is not valid
		} else if (!permitted) {
			ServerUtil.packItShipIt(t, 401, "Unauthorized");
			return;
		}
		
		// Parse Request Body
		String requestBody = ServerUtil.parseRequestBody(t);

		// HTTP GET Request
		if (method.equals("GET")) {

			// Send the setting configuration to the GUI
			if (resource[1].equals(RES_SIM_INIT)) {
				
				// Add global files to new user's scenarios
				boolean isCobra = RegisterUtil.hasPrefix(user, UserPrefixStudy.COBRA);
				if (!isCobra) addGlobalScenarios(user);
				
				// Send Schema to user
				String responseBody = schemaData(user);
				String message = "Settings Delivered to " + user;
				ServerUtil.packItShipIt(t, 200, message, responseBody, jsonContent);
				UserLog.add(user, clientIP, "LOGIN", "Initialize New Session");
			
			// Send a list of scenarios saved by this user
			} else if (resource[1].equals(RES_SIM_LIST)) {	
				String responseBody = ServerUtil.fileNames(RELATIVE_DATA_PATH + "/users/" + user + "/scenarios");
				String message = "Scenario Names Delivers for " + user;
				ServerUtil.packItShipIt(t, 200, message, responseBody, jsonContent);
			
			// Send a list of basemaps saved by this user
			} else if (resource[1].equals(RES_SIM_BASEMAPS)) {	
				String responseBody = ServerUtil.fileNames(RELATIVE_DATA_PATH + "/basemaps");
				String message = "Scenario Names Delivers for " + user;
				ServerUtil.packItShipIt(t, 200, message, responseBody, jsonContent);
			
			// Delete a Scenario
			} else if (resource[1].equals(RES_SIM_DELETE)) {
				
				// Delete the data for this scenario
				if (hasScenario(user, scenario)) {
					deleteScenario(user, scenario);
					String message = "Scenario " + scenario + " deleted for " + user;
					ServerUtil.packItShipIt(t, 200, message);
					UserLog.add(user, clientIP, "DELETE SCENARIO", scenario);
				
				// Scenario Not Found
				} else {
					ServerUtil.packItShipIt(t, 400, "Bad Request");
				}
			
			// Load a previously saved setting configuration
			} else if (resource[1].equals(RES_SIM_LOAD)) {
			
				// Send the default setting configuration to the GUI
				if (scenario.equals("default configuration")) {
					String userFeedback = "Scenario Loaded: " + scenario;
					String responseBody = schemaData(user, userFeedback);
					String message = "Settings Delivered to " + user;
					ServerUtil.packItShipIt(t, 200, message, responseBody, jsonContent);
					UserLog.add(user, clientIP, "LOAD SCENARIO", scenario);
				
				// Send the scenario to the GUI
				} else if (hasScenario(user, scenario)) {
					String responseBody = scenarioData(user, scenario, REQUEST_FILE);
					String message = "Scenario " + scenario + " loaded for " + user;
					ServerUtil.packItShipIt(t, 200, message, responseBody, jsonContent);
					UserLog.add(user, clientIP, "LOAD SCENARIO", scenario);
				
				// Scenario Not Found
				} else {
					ServerUtil.packItShipIt(t, 400, "Bad Request");
				}
				
			} else if (resource[1].equals(RES_SIM_BASEMAP)) {
			
				// Load the image as bytes
				byte[] imageAsBytes = null;
				String[] splitName = basemap.split("\\.");
				if (splitName.length == 2) {
					imageAsBytes = basemapData(basemap, splitName[1]);
				}
				
				// image loaded successfully
				if (imageAsBytes != null) {
					String contentType = "image/" + splitName[1];
					String message = "Basemap " + basemap + " sent to " + user;
					ServerUtil.packItShipIt(t, 200, message, imageAsBytes, contentType);
					UserLog.add(user, clientIP, "LOAD BASEMAP", basemap);
					
				// Basemap Not Found
				} else {
					ServerUtil.packItShipIt(t, 400, "Bad Request");
				}
			
			// Save summary of results
			} else if (resource[1].equals(RES_SIM_SUMMARY)) {
				String responseBody = summaryData(user);
				String contentType = "application/csv";
				String message = "Summary Delivered to " + user;
				ServerUtil.packItShipIt(t, 200, message, responseBody, contentType);
				UserLog.add(user, clientIP, "EXPORT CSV", "user exported CSV of model");
			
			// Resource Not Found
			} else {
				ServerUtil.packItShipIt(t, 404, "Resource Not Found");
			}
			
		// HTTP POST Request
		} else if (method.equals("POST")) {
			
			// Send the default setting configuration to the GUI
			if (resource[1].equals(RES_SIM_RUN)) {
				String feedback = null;
				String responseBody = solutionData(requestBody, feedback, user);
				String message = "Solution Delivered to " + user;
				ServerUtil.packItShipIt(t, 200, message, responseBody, jsonContent);
				UserLog.add(user, clientIP, "RUN", "Model Run");
			
			// Save a submitted setting configuration
			} else if (resource[1].equals(RES_SIM_SAVE)) {
				String userFeedback;
				String message = "Solution Delivered to " + user;
				boolean save;
				
				if(scenario.equals("")) {
					userFeedback = "You must give your scenario a name";
					message += "; Save Denied";
					save = false;
					
				} else if (user.equals("guest") || scenario.equals("default configuration")) {
					userFeedback = "You may not save scenario";
					message += "; Save Denied";
					save = false;
					
				} else {
					userFeedback = "Scenario saved as \"" + scenario + "\"";
					message += "; Saved scenario: " + scenario;
					save = true;
					UserLog.add(user, clientIP, "SAVE SCENARIO", scenario);
				}
				
				String responseBody = solutionData(requestBody, userFeedback, user);
				if (save) {
					saveScenario(user, scenario, REQUEST_FILE, requestBody);
					saveScenario(user, scenario, RESPONSE_FILE, responseBody);
				}
				ServerUtil.packItShipIt(t, 200, message, responseBody, jsonContent);

			// Other Resources not allowed
			} else {
				ServerUtil.packItShipIt(t, 404, "Resource Not Found");
			}

		// Method Not Allowed
		} else {
			ServerUtil.packItShipIt(t, 405, "Method Not Allowed");
		}
	}
	
	
	private String schemaData(String user) {
		return schemaData(user, null);
	}
	
	private String schemaData(String user, String userFeedback) {
		
		if (RegisterUtil.hasPrefix(user, UserPrefixStudy.ZEBRA)) {
			return schemaData(readOnlyConfig, userFeedback);
			
		} else if (RegisterUtil.hasPrefix(user, UserPrefixStudy.COBRA)) {
			return schemaData(fullConfig, userFeedback);
			
		} else if (RegisterUtil.hasPrefix(user, UserPrefixStudy.PANDA)) {
			return schemaData(fullConfig, userFeedback);
			
		} else if (RegisterUtil.hasPrefix(user, UserPrefixAdmin.SQUID)) {
			return schemaData(adminConfig, userFeedback);
		
		} else if (RegisterUtil.ignoreCaseEquals(user, "guest")) {
			return schemaData(guestConfig, userFeedback);
			
		} else {
			return schemaData(adminConfig, userFeedback);
		}
	}
	
	/**
	 * Get the Default Configuration Schema JSON string
	 * @return base config as JSON string
	 */
	private String schemaData(Configuration config, String feedback) {
		JSONObject settings = config.serialize();
		if (feedback != null) settings.put("feedback", feedback);
		return settings.toString(4);
	}
	
	/**
	 * Get the Solution as json string, appending any feedback
	 * @return solution as JSON string
	 */
	private String solutionData(String requestBody, String feedback, String user) {
		JSONObject solutionJSON = solutionJSON(requestBody, user);
		if (feedback != null) solutionJSON.put("feedback", feedback);
		return ServerUtil.wrapData(solutionJSON).toString();
	}
	
	/**
	 * Get the solution as a JSON Object
	 * @param requestBody
	 * @return
	 */
	private JSONObject solutionJSON(String requestBody, String user) {
		
		// Check for Body
		if (requestBody.length() == 0) {
			System.out.println("Warning: requestBody has no data");
		}

		// Generate Response Data
		Configuration config = ParseConfiguration.fromJson(requestBody);
		Development solution = Build.development(config);
		MultiObjective performance = Evaluate.development(solution);
		
		// Save Performance to Log
		PerformanceLog.add(user, performance.getLogHeader(), performance.getLogRow());
		
		// Save latest summary to CSV
		saveSummary(user, performance.toCSV());
		
		// Serialize the Response Data
		JSONObject dataJSON = solution.serialize();
		dataJSON.put("performance", performance.serialize());
		return dataJSON;
	}
	
	/**
	 * Check if a scenario of this names exists for this user
	 * @param user
	 * @param scenario
	 * @return
	 */
	public static boolean hasScenario(String user, String scenario) {
		String directoryName = RELATIVE_DATA_PATH + "/users/" + user + "/scenarios";
		File directory = new File(directoryName);
		if (directory.exists()) {
			String[] nameList = directory.list();
			for(int i=0; i<nameList.length; i++) {
				if(nameList[i].equals(scenario)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Delete a scenario
	 * @param user
	 * @param scenario
	 */
	private void deleteScenario(String user, String scenario) {
		String directoryName = RELATIVE_DATA_PATH + "/users/" + user + "/scenarios/" + scenario;
		File directory = new File(directoryName);
		if (directory.exists()) {
			ServerUtil.deleteDir(directory);
		}
	}
	
	/**
	 * Save a String of data to file
	 * @param user
	 * @param scenario
	 * @param fileName
	 * @param dataString
	 */
	private void saveScenario(String user, String scenario, String fileName, String dataString) {
		
		String directoryName = RELATIVE_DATA_PATH + "/users/" + user + "/scenarios/" + scenario;
		File directory = new File(directoryName);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		byte data[] = dataString.getBytes();
	    Path p = Paths.get(directoryName + "/" + fileName);
	    try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(p))) {
	      out.write(data, 0, data.length);
	    } catch (IOException x) {
	      System.err.println(x);
	    }
	}
	
	/**
	 * Adds all scenarios from the global folder to a given user's folder
	 * @param user
	 */
	private void addGlobalScenarios(String user) {

		String globalPath = RELATIVE_DATA_PATH + File.separator + "global" + File.separator + "scenarios";
		File globalDir = new File(globalPath);
		if(!globalDir.exists()) globalDir.mkdirs();

		String userPath = RELATIVE_DATA_PATH + File.separator + "users" + File.separator + user + File.separator + "scenarios";
		File userDir = new File(userPath);
		if(!userDir.exists()) userDir.mkdirs();

		for (String folderName : globalDir.list()) {
			File scenarioFolder = new File(globalDir.getPath() + File.separator + folderName);
			if (scenarioFolder.isDirectory()) {
				for (String fileName : scenarioFolder.list()) {
					File srcFile = new File(scenarioFolder.getPath() + File.separator + fileName);
					if (srcFile.isFile()) {
						String userFilePath = folderName + File.separator + fileName;
						File destFolder = new File(userPath + File.separator + folderName);
						if (!destFolder.exists()) destFolder.mkdirs();
						File destFile = new File(userPath + File.separator + userFilePath);

						if (!destFile.exists()) {
							try {
								Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Save Summary to File
	 * @param user
	 * @param dataString
	 */
	private void saveSummary(String user, String dataString) {
		
		String directoryName = RELATIVE_DATA_PATH + "/users/" + user;
		File directory = new File(directoryName);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		byte data[] = dataString.getBytes();
	    Path p = Paths.get(directoryName + "/" + SUMMARY_FILE);
	    try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(p))) {
	      out.write(data, 0, data.length);
	    } catch (IOException x) {
	      System.err.println(x);
	    }
	}
	
	/**
	 * Load summary data created by a user
	 * @param user
	 * @return
	 */
	private String summaryData(String user) {
		Path filePath = Path.of(RELATIVE_DATA_PATH + "/users/" + user + "/" + SUMMARY_FILE);
	    try {
			return Files.readString(filePath);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Load scenario data created by a user
	 * @param user
	 * @param scenario
	 * @param fileName
	 * @return
	 */
	private String scenarioData(String user, String scenario, String fileName) {
		Path filePath = Path.of(RELATIVE_DATA_PATH + "/users/" + user + "/scenarios/" + scenario + "/" + fileName);
	    try {
			return Files.readString(filePath);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Load Basemap as byte[]
	 * @param fileName
	 * @return
	 */
	private byte[] basemapData(String fileName, String type) {
		try {
			BufferedImage bImage = ImageIO.read(new File(RELATIVE_DATA_PATH + "/basemaps/" + fileName));
		    ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    ImageIO.write(bImage, type, bos );
		    return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
}