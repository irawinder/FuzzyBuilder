package edu.mit.ira.fuzzy.server;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import javax.imageio.ImageIO;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import edu.mit.ira.fuzzy.model.Development;
import edu.mit.ira.fuzzy.model.build.Build;
import edu.mit.ira.fuzzy.model.evaluate.Evaluate;
import edu.mit.ira.fuzzy.model.schema.Schema;
import edu.mit.ira.fuzzy.pages.Pages;
import edu.mit.ira.fuzzy.server.log.ServerLog;
import edu.mit.ira.fuzzy.server.log.UserLog;
import edu.mit.ira.fuzzy.server.user.Register;
import edu.mit.ira.fuzzy.server.user.RegisterUtil;
import edu.mit.ira.fuzzy.server.user.UserPrefixAdmin;
import edu.mit.ira.fuzzy.server.user.UserPrefixStudy;
import edu.mit.ira.fuzzy.server.user.UserType;
import edu.mit.ira.opensui.data.ParseConfiguration;
import edu.mit.ira.opensui.objective.MultiObjective;
import edu.mit.ira.opensui.setting.Configuration;

/**
 * Fuzzy Server listens and responds to requests for fuzzy masses via HTTP
 *
 * @author Ira Winder
 *
 */
public class Server {
	
	public static final String NAME = "FuzzyIO";
	public static final String VERSION = "v1.4.9";
	public static final String AUTHOR = "Ira Winder, Daniel Fink, and Max Walker";
	public static final String SPONSOR = "MIT Center for Real Estate";
	public static final String CONTACT = "fuzzy-io@mit.edu";
	public static final String SYSTEM = "Java " + System.getProperty("java.version");
	
	public static final String RELATIVE_DATA_PATH = "data";
	private static final String REQUEST_FILE = "configuration.json";
	private static final String RESPONSE_FILE = "solution.json";
	private static final String SUMMARY_FILE = "summary.csv";
	
	// Server Objects
	private HttpServer server;
	private String info;
	
	// Fuzzy Objects
	private Configuration adminConfig, fullConfig, readOnlyConfig, guestConfig;
	
	/**
	 * Construct a new FuzzyIO Server
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
			
			System.out.println(user);
			
			// Log Request
			ServerLog.add(t, "Request: " + method + " " +  requestURI);
			
			boolean simResource = resource[0].equals("OPENSUI") && resource.length > 1;
			boolean htmlResource = resource[0].equals("") || resource[0].equals("REGISTER");
			boolean deactivated = Register.isDeactivated(user);
			boolean permitted = Register.isActive(user) || RegisterUtil.ignoreCaseEquals(user, ServerUtil.DEFAULT_USER) || deactivated;
			
			// Options Requested
			if (method.equals("OPTIONS")) {
				
				// OPTIONS request is something browsers ask before 
				// allowing an external server to provide data
				String message = "HTTP Options Delivered";
				ServerUtil.packItShipIt(t, 200, message);
			
			// Web Resource Requested
			} else if (htmlResource) {
				htmlRequest(t, clientIP, method, resource, requestParameters, user, permitted, deactivated);
			
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
	
	private void htmlRequest(HttpExchange t, String clientIP, String method, String[] resource, Map<String, String> params, String user, boolean permitted, boolean deactivated) throws IOException {
		
		String email = params.get("email").toLowerCase();
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
				UserLog.add(user, clientIP, "HOME", "Visited FuzzyIO Study Page : FINISH");
			
			// General Web Resources
			} else if (resource[0].equals("")) {	
				
				String responseBody, message;
				
				// Is Admin User
				if (RegisterUtil.hasPrefix(user, UserPrefixAdmin.SQUID)) {
					responseBody = Pages.generalSite();
					message = "HTML Delivered";
					UserLog.add(user, clientIP, "HOME", "Visited FuzzyIO General Page : GENERAL");
					
				// Is other active user (e.g. study user)	
				} else if (Register.isActive(user)) {
					responseBody = Pages.studySite(user, page, false);
					message = "HTML Delivered";
					UserLog.add(user, clientIP, "HOME", "Visited FuzzyIO Study Page : " + page.toUpperCase());
					
				// Is guest (unregistered) user (e.g. 'user' parameter is blank)
				} else if (user.equals(ServerUtil.DEFAULT_USER)){
					responseBody = Pages.studyIntroSite();
					message = "HTML Delivered";
					UserLog.add(user, clientIP, "HOME", "Visited FuzzyIO Study Page : INTRO");
				}
				
				// No Resource available
				else {
					message = "Bad Request";
					responseBody = Pages.nullSite("400", message);
				}
				ServerUtil.packItShipIt(t, 200, message, responseBody, htmlContent);
			}
			
			// Client is trying to register an email address
			else if (resource[0].equals("REGISTER")) 
			{	
				String responseBody;
				
				// blank registration form (no email parameter is submitted)
				if (email.equals(ServerUtil.DEFAULT_EMAIL)) {
					responseBody = Pages.registrationSite("");
					
				// Try to Register new "study" user in system
				} else {
					String userID = Register.makeUser(email, UserType.STUDY);
					
					// Registration Successful
					if (userID != null) {
						responseBody = Pages.registrationCompleteSite(userID, email);
						
					// Registration failed
					} else {
						
						// Email already active
						if (Register.isActiveEmail(email)) {
							responseBody = Pages.registrationSite("This email has already been used.");
						
						// Something else went wrong
						} else {
							responseBody = Pages.registrationSite("Something went wrong and we can't register this email address. Please contact ira [at] mit [dot] edu for help.");
						}
					}
				}
				ServerUtil.packItShipIt(t, 200, "Success", responseBody, htmlContent);
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
		String requestBody = "";
		
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
		InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
		BufferedReader br = new BufferedReader(isr);
		int b;
		StringBuilder buf = new StringBuilder(512);
		while ((b = br.read()) != -1) {
			buf.append((char) b);
		}
		br.close();
		isr.close();
		requestBody = buf.toString();

		// HTTP GET Request
		if (method.equals("GET")) {

			// Send the setting configuration to the GUI
			if (resource[1].equals("INIT")) {
				
				// Add global files to new user's scenarios
				boolean isCobra = RegisterUtil.hasPrefix(user, UserPrefixStudy.COBRA);
				if (!isCobra) addGlobalScenarios(user);
				
				// Send Schema to user
				String responseBody = schemaData(user);
				String message = "Settings Delivered to " + user;
				ServerUtil.packItShipIt(t, 200, message, responseBody, jsonContent);
				UserLog.add(user, clientIP, "LOGIN", "Initialize New Session");
			
			// Send a list of scenarios saved by this user
			} else if (resource[1].equals("LIST")) {	
				String responseBody = ServerUtil.fileNames("./data/users/" + user + "/scenarios");
				String message = "Scenario Names Delivers for " + user;
				ServerUtil.packItShipIt(t, 200, message, responseBody, jsonContent);
			
			// Send a list of basemaps saved by this user
			} else if (resource[1].equals("BASEMAPS")) {	
				String responseBody = ServerUtil.fileNames("./data/basemaps");
				String message = "Scenario Names Delivers for " + user;
				ServerUtil.packItShipIt(t, 200, message, responseBody, jsonContent);
			
			// Delete a Scenario
			} else if (resource[1].equals("DELETE")) {
				
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
			} else if (resource[1].equals("LOAD")) {
			
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
				
			} else if (resource[1].equals("BASEMAP")) {
			
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
			
			// Send the default setting configuration to the GUI
			} else if (resource[1].equals("SUMMARY")) {
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
			if (resource[1].equals("RUN")) {
				String feedback = null;
				String responseBody = solutionData(requestBody, feedback, user);
				String message = "Solution Delivered to " + user;
				ServerUtil.packItShipIt(t, 200, message, responseBody, jsonContent);
				UserLog.add(user, clientIP, "RUN", "Model Changed");
			
			// Save a submitted setting configuration
			} else if (resource[1].equals("SAVE")) {
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

		// Generate FuzzyIO Response Data
		Configuration config = ParseConfiguration.fromJson(requestBody);
		Development solution = Build.development(config);
		MultiObjective performance = Evaluate.development(solution);
		
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
	private boolean hasScenario(String user, String scenario) {
		String directoryName = "./data/users/" + user + "/scenarios";
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
		String directoryName = "./data/users/" + user + "/scenarios/" + scenario;
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
		
		String directoryName = "./data/users/" + user + "/scenarios/" + scenario;
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

		String globalPath = "data" + File.separator + "global" + File.separator + "scenarios";
		File globalDir = new File(globalPath);
		if(!globalDir.exists()) globalDir.mkdirs();

		String userPath = "data" + File.separator + "users" + File.separator + user + File.separator + "scenarios";
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
		
		String directoryName = "./data/users/" + user;
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
		Path filePath = Path.of("./data/users/" + user + "/" + SUMMARY_FILE);
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
		Path filePath = Path.of("./data/users/" + user + "/scenarios/" + scenario + "/" + fileName);
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
			BufferedImage bImage = ImageIO.read(new File("./data/basemaps/" + fileName));
		    ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    ImageIO.write(bImage, type, bos );
		    return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
}