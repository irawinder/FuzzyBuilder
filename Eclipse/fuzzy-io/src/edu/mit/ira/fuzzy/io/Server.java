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
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import edu.mit.ira.fuzzy.model.Development;
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
	
	// Server Objects
	private static final String SERVER_SYSTEM = "Java " + System.getProperty("java.version");
	private String serverID;
	private String serverVersion;
	private HttpServer server;
	private String info;
	
	// Fuzzy Objects
	private Configuration guestConfig, readOnlyConfig, fullConfig, adminConfig;
	private Builder builder;
	private Evaluator evaluator;
	private Deserializer adapter;
	
	private String DEFAULT_USER = "guest";
	private String DEFAULT_SCENARIO = "defacto";
	private String DEFAULT_FILENAME = "default";
	private String REQUEST_FILE = "configuration.json";
	private String RESPONSE_FILE = "solution.json";
	private String SUMMARY_FILE = "summary.csv";
	
	// e.g. zebra123
	int VALID_PREFIX_LENGTH = 5;
	int VALID_USERNAME_LENGTH = 8;
	String[] VALID_USER_PREFIXES = {"zebra", "cobra", "panda", "squid"};
	
	/**
	 * Construct a new FuzzyIO Server
	 * @param name name of server
	 * @param version version of server
	 * @param port port to open for HTTP Requests
	 * @throws IOException
	 */
	public Server(String name, String version, String author, String sponsor, String contact, int port) throws IOException {
		this.serverID = name;
		this.serverVersion = version;
		server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/", new MyHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
		
		adminConfig = Schema.get(version, name, author, sponsor, contact, true, false, true, true); // save, delete, load, and config
		fullConfig = Schema.get(version, name, author, sponsor, contact, true, false, true, true); // save, delete, load, and config
		guestConfig = Schema.get(version, name, author, sponsor, contact, false, false, true, true); // !save, !delete, load, config
		readOnlyConfig = Schema.get(version, name, author, sponsor, contact, false, false, true, false); // !save, !delete load, !config
		
		builder = new Builder();
		evaluator = new Evaluator();
		adapter = new Deserializer();
		info = "--- FuzzyIO " + serverVersion + " ---\nActive on port: " + port;
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
			String requestProcess = process(requestURI);
			Map<String, String> requestParameters = parameters(requestURI);
			String user = requestParameters.get("user");
			String scenario = requestParameters.get("scenario");
			String filename = requestParameters.get("filename");
			
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
			log(t, "Request: " + requestMethod + " " +  requestURI + ", Request Length: " + requestLength);
			
			// check for valid user name;
			boolean valid = false;
			for (String prefix : VALID_USER_PREFIXES) {
				if (user.length() == VALID_USERNAME_LENGTH) {
					if (user.substring(0, VALID_PREFIX_LENGTH).equals(prefix)) 
						valid = true;
				}
			}
			
			if (!valid) 
			{
				packItShipIt(t, 403, "Forbidden");
			} 
			// HTTP GET Request
			else if (requestMethod.equals("GET")) 
			{
				if (requestProcess.equals("")) 
				{
					// Supply web content if root resource
					String responseBody = getHTML("index.txt");
					String contentType = "text/html";
					String message = "HTML Delivered";
					packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
				} 
				else if (requestProcess.equals("INIT")) 
				{
					// Send the setting configuration to the GUI
					String responseBody = getSchemaAsString(user);
					String contentType = "application/json";
					String message = "Settings Delivered to " + user;
					packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
				} 
				else if (requestProcess.equals("LIST"))
				{	
					// Send a list of scenarios saved by this user
					String responseBody = fileNames("./data/users/" + user + "/scenarios");
					String contentType = "application/json";
					String message = "Scenario Names Delivers for " + user;
					packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
				} 
				else if (requestProcess.equals("BASEMAPS"))
				{	
					// Send a list of scenarios saved by this user
					String responseBody = fileNames("./data/basemaps");
					String contentType = "application/json";
					String message = "Scenario Names Delivers for " + user;
					packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
				} 
				else if (requestProcess.equals("DELETE"))
				{
					// Load a previously saved setting configuration
					if (hasScenario(user, scenario)) {
						
						// Delete the data for this scenario
						deleteScenario(user, scenario);
						String message = "Scenario " + scenario + " deleted for " + user;
						packItShipIt(t, 200, message);
					} else {
						
						// Resource Not Found
						String responseBody = getHTML("404.txt");
						String contentType = "text/html";
						String message = "Resource not found";
						packItShipIt(t, 404, message, responseBody.getBytes(), contentType);
					}
				}
				else if (requestProcess.equals("LOAD")) 
				{	
					// Load a previously saved setting configuration
					if (scenario.equals("default configuration")) {
						
						// Send the default setting configuration to the GUI
						String userFeedback = "Scenario Loaded: " + scenario;
						String responseBody = getSchemaAsString(user, userFeedback);
						String contentType = "application/json";
						String message = "Settings Delivered to " + user;
						packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
					} else if (hasScenario(user, scenario)) {
						
						// Send the default setting configuration to the GUI
						String responseBody = loadData(user, scenario, REQUEST_FILE);
						String contentType = "application/json";
						String message = "Scenario " + scenario + " loaded for " + user;
						packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
					} else {
						
						// Resource Not Found
						String responseBody = getHTML("404.txt");
						String contentType = "text/html";
						String message = "Resource not found";
						packItShipIt(t, 404, message, responseBody.getBytes(), contentType);
					}
				} 
				else if (requestProcess.equals("BASEMAP")) 
				{
					// Load the image as bytes
					byte[] imageAsBytes = null;
					String[] splitName = filename.split("\\.");
					if (splitName.length == 2) {
						imageAsBytes = loadBasemap(filename, splitName[1]);
					}
					
					// image loaded successfully
					if (imageAsBytes != null) {
						
						// Send the image
						String contentType = "image/" + splitName[1];
						String message = "Basemap " + filename + " sent to " + user;
						packItShipIt(t, 200, message, imageAsBytes, contentType);
					} else {
						
						// Resource Not Found
						String responseBody = getHTML("404.txt");
						String contentType = "text/html";
						String message = "Resource not found";
						packItShipIt(t, 404, message, responseBody.getBytes(), contentType);
					}
				}
				else if (requestProcess.equals("SUMMARY"))
				{
					if (!user.equals(DEFAULT_USER)) {
						
						// Send the default setting configuration to the GUI
						String responseBody = loadSummary(user);
						String contentType = "application/csv";
						String message = "Summary Delivered to " + user;
						packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
						
					} else {
						
						// Resource Not Found
						String responseBody = getHTML("404.txt");
						String contentType = "text/html";
						String message = "Resource not found";
						packItShipIt(t, 404, message, responseBody.getBytes(), contentType);
					}
				}
				else
				{
					// Resource Not Found
					String responseBody = getHTML("404.txt");
					String contentType = "text/html";
					String message = "Resource not found";
					packItShipIt(t, 404, message, responseBody.getBytes(), contentType);
				}
			}
			
			// HTTP POST Request
			else if (requestMethod.equals("POST")) 
			{	
				if (requestProcess.equals("RUN")) 
				{
					// Send the default setting configuration to the GUI
					String responseBody = solution(requestBody, user);
					String contentType = "application/json";
					String message = "Solution Delivered to " + user;
					packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
				} 
				else if (requestProcess.equals("SAVE")) 
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
					}
					String responseBody = solution(requestBody, userFeedback, user);
					String contentType = "application/json";
					if (save) {
						saveScenario(user, scenario, REQUEST_FILE, requestBody);
						saveScenario(user, scenario, RESPONSE_FILE, responseBody);
					}
					packItShipIt(t, 200, message, responseBody.getBytes(), contentType);
				} 
				else
				{
					// Resource Not Found
					String message = "Resource not found";
					packItShipIt(t, 404, message);
				}
			}
			
			// HTTP OPTIONS Request
			else if (requestMethod.equals("OPTIONS")) {
				
				// OPTIONS request is something browsers ask before 
				// allowing an external server to provide data
				String message = "HTTP Options Delivered";
				packItShipIt(t, 200, message);
			}
			
			// HTTP Request (other)
			else  {
				
				// Other methods not allowed
				String message = "Method Not Allowed";
				packItShipIt(t, 405, message);
			}
		}
	}
	
	private String getSchemaAsString(String user) {
		if (user.substring(0, VALID_PREFIX_LENGTH).equals(VALID_USER_PREFIXES[0])) 
		{
			return guestSettings();
		} 
		else if (user.substring(0, VALID_PREFIX_LENGTH).equals(VALID_USER_PREFIXES[1])) 
		{
			return readOnlySettings();
		} 
		else if (user.substring(0, VALID_PREFIX_LENGTH).equals(VALID_USER_PREFIXES[2])) 
		{
			return fullSettings();
		} 
		else if (user.substring(0, VALID_PREFIX_LENGTH).equals(VALID_USER_PREFIXES[3])) 
		{
			return adminSettings();
		} 
		else 
		{
			return fullSettings();
		}
	}
	
	private String getSchemaAsString(String user, String userFeedback) {
		if (user.substring(0, VALID_PREFIX_LENGTH).equals(VALID_USER_PREFIXES[0])) 
		{
			return guestSettings(userFeedback);
		} 
		else if (user.substring(0, VALID_PREFIX_LENGTH).equals(VALID_USER_PREFIXES[1])) 
		{
			return readOnlySettings(userFeedback);
		} 
		else if (user.substring(0, VALID_PREFIX_LENGTH).equals(VALID_USER_PREFIXES[2])) 
		{
			return fullSettings(userFeedback);
		} 
		else if (user.substring(0, VALID_PREFIX_LENGTH).equals(VALID_USER_PREFIXES[3])) 
		{
			return adminSettings(userFeedback);
		} 
		else 
		{
			return fullSettings(userFeedback);
		}
	}
	
	/**
	 * Get parameters from URI
	 * @param requestURI
	 * @return
	 */
	private Map<String, String> parameters(String requestURI) {
		String[] process_params = requestURI.replace("?", ";").toLowerCase().split(";");
		Map<String, String> parameters = new HashMap<String, String>();
		if(process_params.length > 1) {
			String[] params = process_params[1].split("&");
			for (int i=0; i<params.length; i++) {
				String[] param = params[i].split("=");
				if (param.length == 2) {
					parameters.put(param[0], param[1].replace("%20", " "));
				} else {
					parameters.put(param[0], "");
				}
			}
		}
		if (!parameters.containsKey("user")) 
			parameters.put("user", DEFAULT_USER);
		if (!parameters.containsKey("scenario")) 
			parameters.put("scenario", DEFAULT_SCENARIO);
		if (!parameters.containsKey("filename")) 
			parameters.put("filename", DEFAULT_FILENAME);
		return parameters;
	}
	
	private String process(String requestURI) {
		String[] process_params = requestURI.replace("?", ";").split(";");
		return process_params[0].toUpperCase().replace("/", "");
	}
	
	/**
	 * Get the Default Configuration Schema JSON string
	 * @return base config as JSON string
	 */
	private String guestSettings() {
		return guestConfig.serialize().toString(4);
	}
	
	/**
	 * Get the Default Configuration Schema JSON string
	 * @return base config as JSON string
	 */
	private String guestSettings(String feedback) {
		JSONObject settings = guestConfig.serialize();
		settings.put("feedback", feedback);
		return settings.toString(4);
	}
	
	/**
	 * Get the Default Configuration Schema JSON string
	 * @return base config as JSON string
	 */
	private String fullSettings() {
		return fullConfig.serialize().toString(4);
	}
	
	/**
	 * Get the Default Configuration Schema JSON string
	 * @return base config as JSON string
	 */
	private String fullSettings(String feedback) {
		JSONObject settings = fullConfig.serialize();
		settings.put("feedback", feedback);
		return settings.toString(4);
	}
	
	/**
	 * Get the Default Configuration Schema JSON string
	 * @return base config as JSON string
	 */
	private String adminSettings() {
		return adminConfig.serialize().toString(4);
	}
	
	/**
	 * Get the Default Configuration Schema JSON string
	 * @return base config as JSON string
	 */
	private String adminSettings(String feedback) {
		JSONObject settings = adminConfig.serialize();
		settings.put("feedback", feedback);
		return settings.toString(4);
	}
	
	/**
	 * Get the Default Configuration Schema JSON string
	 * @return base config as JSON string
	 */
	private String readOnlySettings() {
		return readOnlyConfig.serialize().toString(4);
	}
	
	/**
	 * Get the Default Configuration Schema JSON string
	 * @return base config as JSON string
	 */
	private String readOnlySettings(String feedback) {
		JSONObject settings = readOnlyConfig.serialize();
		settings.put("feedback", feedback);
		return settings.toString(4);
	}
	
	/**
	 * Get the Solution as json string, appending any feedback
	 * @return solution as JSON string
	 */
	private String solution(String requestBody, String feedback, String user) {
		JSONObject solutionJSON = solutionJSON(requestBody, user);
		solutionJSON.put("feedback", feedback);
		return wrapApi(solutionJSON);
	}
	
	/**
	 * Get the Solution as json string
	 * @param requestBody
	 * @return
	 */
	private String solution(String requestBody, String user) {
		JSONObject solutionJSON = solutionJSON(requestBody, user);
		return wrapApi(solutionJSON);
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
	 * Return a list of filenames in a directory
	 * @return
	 */
	public String fileNames(String directoryName) {
		JSONArray names = new JSONArray();
		File directory = new File(directoryName);
		if (directory.exists()) {
			String[] nameList = directory.list();
			for(int i=0; i<nameList.length; i++) {
				names.put(i, nameList[i]);
			}
		}
		JSONObject fileNames = new JSONObject();
		fileNames.put("fileNames", names);
		return fileNames.toString(4);
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
			deleteDir(directory);
		}
	}
		
	/**
	 * Delete a directory and all of its contents	
	 * @param file
	 */
	private void deleteDir(File file) {
		File[] contents = file.listFiles();
	    if (contents != null) {
	        for (File f : contents) {
	            if (! Files.isSymbolicLink(f.toPath())) {
	                deleteDir(f);
	            }
	        }
	    }
	    file.delete();		
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
	private String loadSummary(String user) {
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
	private String loadData(String user, String scenario, String fileName) {
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
	private byte[] loadBasemap(String fileName, String type) {
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
	
	/**
	 * Attach Data and Headers to HttpResponse and send it off to the client
	 * @param t
	 * @param responseCode
	 * @param data a byte[] of responseBody, such as a JSON file
	 * @throws IOException
	 */
	private void packItShipIt(HttpExchange t, int responseCode, String responseMessage, byte[] responseBody, String contentType) throws IOException {
		makeHeaders(t, contentType);
		int responseLength = responseBody.length;
		t.sendResponseHeaders(responseCode, responseLength);
		OutputStream os = t.getResponseBody();
		os.write(responseBody, 0, responseBody.length);
		os.close();
		log(t, "Response: " + responseCode + ", " + responseMessage + ", Response Length: " + responseLength);
	}
	
	
	/**
	 * Attach Headers to HttpResponse and send it off to client
	 * @param t
	 * @param responseCode
	 * @throws IOException
	 */
	private void packItShipIt(HttpExchange t, int responseCode, String responseMessage) throws IOException {
		makeHeaders(t, "text/html");
		int responseLength = -1;
		t.sendResponseHeaders(responseCode, -1);
		log(t, "Response: " + responseCode + ", " + responseMessage + ", Response Length: " + responseLength);
	}
	
	/**
	 * Prints a log to console. Also returns the log as a string
	 * @param clientIP
	 * @param message
	 * @return
	 */
	private void log(HttpExchange t, String message) {
		
		// Create Log
		String clientIP = t.getRemoteAddress().toString();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		Date date = new Date(System.currentTimeMillis());
		String timeStamp = formatter.format(date);
		String log = timeStamp + " " + clientIP + " : " + message + "\n";
		
		// Write Log to Console
		System.out.print(log);
		
		// Write Log to File
		byte data[] = log.getBytes();
	    Path p = Paths.get("./logs.txt");
	    try (OutputStream out = new BufferedOutputStream(
	      Files.newOutputStream(p, StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
	      out.write(data, 0, data.length);
	    } catch (IOException x) {
	      System.err.println(x);
	    }
	}
    

	/**
	 * Format Headers for HTTP response
	 * 
	 * @param contentType the type of data attached on this response
	 * @return header fields for the response
	 */
	private void makeHeaders(HttpExchange t, String contentType) {

		Headers headers = t.getResponseHeaders();
		headers.set("Server", serverID + ", " + SERVER_SYSTEM);
		headers.set("Content-Type", contentType);
		headers.set("Connection", "close");
		headers.set("Access-Control-Allow-Origin", "*");
		headers.set("Access-Control-Allow-Headers", "*");
		headers.set("Access-Control-Allow-Methods", "*");
		headers.set("Access-Control-Allow-Credentials", "true");
		
		if (contentType.equals("application/csv")) {
			headers.set("Content-Disposition", "attachment; filename=\"summary.csv\"");
		}
	}

	/**
	 * Wrap JSON data with a standard JSON header and return as string
	 *
	 * @param data serialization of fuzzybuilder voxels, etc
	 * @return original data wrapped by a json header with information about the
	 *         data (api version, etc)
	 */
	private String wrapApi(JSONObject data) {

		// Compile Root JSON Object
		String apiVersion = serverVersion;
		JSONObject root = new JSONObject();
		root.put("apiVersion", apiVersion);
		root.put("data", data);

		return root.toString();
	}
	
	private String getHTML(String file) {
		Path fileName = Path.of("./" + file);
	    try {
			return Files.readString(fileName);
		} catch (IOException e) {
			e.printStackTrace();
			return "<!DOCTYPE html><html><body>" + serverID + ": " + serverVersion + "</body></html>";
		}
	}
}