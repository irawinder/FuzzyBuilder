package edu.mit.ira.fuzzy.io;

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

import edu.mit.ira.fuzzy.io.log.UserLog;
import edu.mit.ira.fuzzy.io.user.Register;
import edu.mit.ira.fuzzy.io.user.UserPrefixAdmin;
import edu.mit.ira.fuzzy.io.user.UserPrefixStudy;
import edu.mit.ira.fuzzy.io.user.UserType;
import edu.mit.ira.fuzzy.model.Development;
import edu.mit.ira.fuzzy.pages.Pages;
import edu.mit.ira.opensui.io.Deserializer;
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
	public static final String VERSION = "v1.3.19";
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
	private Configuration readOnlyConfig, fullConfig, adminConfig;
	private Builder builder;
	private Evaluator evaluator;
	private Deserializer adapter;
	
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
		
		readOnlyConfig = Schema.get(false, false, true, false); // !save, !delete load, !config
		fullConfig = Schema.get(true, false, true, true); // save, !delete, load, and config
		adminConfig = Schema.get(true, true, true, true); // save, delete, load, and config
		
		builder = new Builder();
		evaluator = new Evaluator();
		adapter = new Deserializer();
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
			String requestMethod = t.getRequestMethod();
			String requestURI = t.getRequestURI().toString();
			String clientIP = t.getRemoteAddress().toString();
			String requestResource = ServerUtil.resource(requestURI);
			Map<String, String> requestParameters = ServerUtil.parameters(requestURI);
			String user = requestParameters.get("user");
			String email = requestParameters.get("email").toLowerCase();
			String page = requestParameters.get("page");
			String scenario = requestParameters.get("scenario").toLowerCase();
			String basemap = requestParameters.get("filename").toLowerCase();
			
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
			String requestBody = buf.toString();
			int requestLength = requestBody.length();
			
			// Log Request
			ServerUtil.log(t, "Request: " + requestMethod + " " +  requestURI + ", Request Length: " + requestLength);
			
			boolean deactivated = Register.deactivated(user);
			boolean permit = Register.active(user) || user.equals(ServerUtil.DEFAULT_USER) || requestResource.equals("REGISTER");
			
			// Run if a deactivated user is trying to view the site
			if (deactivated) {
				String responseBody = Pages.studySite(user, "finish", deactivated);
				String contentType = "text/html";
				String message = "HTML Delivered";
				ServerUtil.packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
				UserLog.add(user, clientIP, "HOME", "Visited FuzzyIO Study Page : FINISH");
				System.out.println(user + " is deactivated");
			} 
			// User is not permitted to access any of the requested resources
			else if (!permit) 
			{
				ServerUtil.packItShipIt(t, 403, "Forbidden");
			}
			// HTTP GET Request
			else if (requestMethod.equals("GET")) 
			{
				// Homepage for serving public introduction and and study pages
				if (requestResource.equals("")) 
				{	
					String responseBody = "boochy";
					if (Register.hasPrefix(user, UserPrefixAdmin.SQUID)) {
						responseBody = Pages.generalSite();
					} else if (Register.active(user)) {
						responseBody = Pages.studySite(user, page, false);
						UserLog.add(user, clientIP, "HOME", "Visited FuzzyIO Home Page " + page);
					} else {
						responseBody = Pages.studyIntroSite();
					}
					String contentType = "text/html";
					String message = "HTML Delivered";
					ServerUtil.packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
				} 
				if (requestResource.equals("REGISTER")) 
				{	
					String responseBody;
					if (email.equals(ServerUtil.DEFAULT_EMAIL)) {
						responseBody = Pages.registrationSite("");
					} else {
						
						// Register new "study" user in system
						String userID = Register.makeUser(email, UserType.STUDY);
						
						if (userID != null) {
							responseBody = Pages.registrationCompleteSite(userID, email);
						} else {
							if (Register.emailExists(email)) {
								responseBody = Pages.registrationSite("This email has already been used.");
							} else {
								responseBody = Pages.registrationSite("Something went wrong and we can't register this email address. Please contact ira [at] mit [dot] edu for help.");
							}
						}
					}
					
					String contentType = "text/html";
					String message = "HTML Delivered";
					ServerUtil.packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
				} 
				else if (requestResource.equals("INIT")) 
				{
					// Add global files to new user's scenarios
					if (!Register.hasPrefix(user, UserPrefixStudy.COBRA)) addGlobalScenarios(user);
				
					// Send the setting configuration to the GUI
					String responseBody = schemaData(user);
					String contentType = "application/json";
					String message = "Settings Delivered to " + user;
					ServerUtil.packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
					UserLog.add(user, clientIP, "LOGIN", "Initialize New Session");
				} 
				else if (requestResource.equals("LIST"))
				{	
					// Send a list of scenarios saved by this user
					String responseBody = ServerUtil.fileNames("./data/users/" + user + "/scenarios");
					String contentType = "application/json";
					String message = "Scenario Names Delivers for " + user;
					ServerUtil.packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
				} 
				else if (requestResource.equals("BASEMAPS"))
				{	
					// Send a list of basemaps saved by this user
					String responseBody = ServerUtil.fileNames("./data/basemaps");
					String contentType = "application/json";
					String message = "Scenario Names Delivers for " + user;
					ServerUtil.packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
				} 
				else if (requestResource.equals("DELETE"))
				{
					// Load a previously saved setting configuration
					if (hasScenario(user, scenario)) {
						
						// Delete the data for this scenario
						deleteScenario(user, scenario);
						String message = "Scenario " + scenario + " deleted for " + user;
						ServerUtil.packItShipIt(t, 200, message);
						UserLog.add(user, clientIP, "DELETE SCENARIO", scenario);
					} else {
						
						// Resource Not Found
						String responseBody = Pages.nullSite();
						String contentType = "text/html";
						String message = "Resource not found";
						ServerUtil.packItShipIt(t, 404, message, responseBody.getBytes(), contentType);
					}
				}
				else if (requestResource.equals("LOAD")) 
				{	
					// Load a previously saved setting configuration
					if (scenario.equals("default configuration")) {
						
						// Send the default setting configuration to the GUI
						String userFeedback = "Scenario Loaded: " + scenario;
						String responseBody = schemaData(user, userFeedback);
						String contentType = "application/json";
						String message = "Settings Delivered to " + user;
						ServerUtil.packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
						UserLog.add(user, clientIP, "LOAD SCENARIO", scenario);
					} else if (hasScenario(user, scenario)) {
						
						// Send the scenario to the GUI
						String responseBody = scenarioData(user, scenario, REQUEST_FILE);
						String contentType = "application/json";
						String message = "Scenario " + scenario + " loaded for " + user;
						ServerUtil.packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
						UserLog.add(user, clientIP, "LOAD SCENARIO", scenario);
					} else {
						
						// Resource Not Found
						String responseBody = Pages.nullSite();
						String contentType = "text/html";
						String message = "Resource not found";
						ServerUtil.packItShipIt(t, 404, message, responseBody.getBytes(), contentType);
					}
				} 
				else if (requestResource.equals("BASEMAP")) 
				{
					// Load the image as bytes
					byte[] imageAsBytes = null;
					String[] splitName = basemap.split("\\.");
					if (splitName.length == 2) {
						imageAsBytes = basemapData(basemap, splitName[1]);
					}
					
					// image loaded successfully
					if (imageAsBytes != null) {
						
						// Send the image
						String contentType = "image/" + splitName[1];
						String message = "Basemap " + basemap + " sent to " + user;
						ServerUtil.packItShipIt(t, 200, message, imageAsBytes, contentType);
						UserLog.add(user, clientIP, "LOAD BASEMAP", basemap);
					} else {
						
						// Resource Not Found
						String responseBody = Pages.nullSite();
						String contentType = "text/html";
						String message = "Resource not found";
						ServerUtil.packItShipIt(t, 404, message, responseBody.getBytes(), contentType);
					}
				}
				else if (requestResource.equals("SUMMARY"))
				{
					// Send the default setting configuration to the GUI
					String responseBody = summaryData(user);
					String contentType = "application/csv";
					String message = "Summary Delivered to " + user;
					ServerUtil.packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
					UserLog.add(user, clientIP, "EXPORT CSV", "user exported CSV of model");
				}
				else
				{
					// Resource Not Found
					String responseBody = Pages.nullSite();
					String contentType = "text/html";
					String message = "Resource not found";
					ServerUtil.packItShipIt(t, 404, message, responseBody.getBytes(), contentType);
				}
			}
			
			// HTTP POST Request
			else if (requestMethod.equals("POST")) 
			{	
				if (requestResource.equals("RUN")) 
				{
					// Send the default setting configuration to the GUI
					String feedback = null;
					String responseBody = solutionData(requestBody, feedback, user);
					String contentType = "application/json";
					String message = "Solution Delivered to " + user;
					ServerUtil.packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
					UserLog.add(user, clientIP, "RUN", "Model Changed");
				} 
				else if (requestResource.equals("SAVE")) 
				{
					// Save a submitted setting configuration
					String userFeedback;
					String message = "Solution Delivered to " + user;
					boolean save;
					if(scenario.equals(""))
					{
						userFeedback = "You must give your scenario a name";
						message += "; Save Denied";
						save = false;
					}
					else if (user.equals("guest") || scenario.equals("default configuration")) 
					{
						userFeedback = "You may not save scenario";
						message += "; Save Denied";
						save = false;
					} 
					else 
					{
						userFeedback = "Scenario saved as \"" + scenario + "\"";
						message += "; Saved scenario: " + scenario;
						save = true;
						UserLog.add(user, clientIP, "SAVE SCENARIO", scenario);
					}
					String responseBody = solutionData(requestBody, userFeedback, user);
					String contentType = "application/json";
					if (save) {
						saveScenario(user, scenario, REQUEST_FILE, requestBody);
						saveScenario(user, scenario, RESPONSE_FILE, responseBody);
					}
					ServerUtil.packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
				} 
				else
				{
					// Resource Not Found
					String message = "Resource not found";
					ServerUtil.packItShipIt(t, 404, message);
				}
			}
			
			// HTTP OPTIONS Request
			else if (requestMethod.equals("OPTIONS")) {
				
				// OPTIONS request is something browsers ask before 
				// allowing an external server to provide data
				String message = "HTTP Options Delivered";
				ServerUtil.packItShipIt(t, 200, message);
			}
			
			// HTTP Request (other)
			else  {
				
				// Other methods not allowed
				String message = "Method Not Allowed";
				ServerUtil.packItShipIt(t, 405, message);
			}
		}
	}
	
	private String schemaData(String user) {
		return schemaData(user, null);
	}
	
	private String schemaData(String user, String userFeedback) {
		if (Register.hasPrefix(user, UserPrefixStudy.ZEBRA))
		{
			return schemaData(readOnlyConfig, userFeedback);
		} 
		else if (Register.hasPrefix(user, UserPrefixStudy.COBRA))
		{
			return schemaData(fullConfig, userFeedback);
		} 
		else if (Register.hasPrefix(user, UserPrefixStudy.PANDA))
		{
			return schemaData(fullConfig, userFeedback);
		} 
		else if (Register.hasPrefix(user, UserPrefixAdmin.SQUID))
		{
			return schemaData(adminConfig, userFeedback);
		} 
		else 
		{
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
		Configuration config = adapter.parse(requestBody);
		Development solution = builder.build(config);
		MultiObjective performance = evaluator.evaluate(solution);
		
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