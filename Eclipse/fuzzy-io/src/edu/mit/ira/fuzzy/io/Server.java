package edu.mit.ira.fuzzy.io;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
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
	private Schema schema;
	private Configuration baseConfig;
	private Builder builder;
	private Evaluator evaluator;
	private Deserializer adapter;
	
	private String DEFAULT_USER = "guest";
	private String DEFAULT_SCENARIO = "defacto";
	
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
		
		schema = new Schema();
		baseConfig = schema.baseConfiguration(version, name, author, sponsor, contact);
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
			
			// HTTP GET Request
			if (requestMethod.equals("GET")) 
			{
				if (requestProcess.equals("")) 
				{
					// Supply web content if root resource
					String responseBody = getHTML("index.txt");
					String contentType = "text/html";
					String message = "HTML Delivered";
					packItShipIt(t, 200, message, responseBody, contentType);
				} 
				else if (requestProcess.equals("INIT")) 
				{
					// Send the default setting configuration to the GUI
					String responseBody = defaultSettings();
					String contentType = "application/json";
					String message = "Base Settings Delivered to " + user;
					packItShipIt(t, 200, message, responseBody, contentType);
				} 
				else if (requestProcess.equals("LOAD")) 
				{	
					//temp
					//
					//
					// Load a previously saved setting configuration
					String responseBody = getHTML("404.txt");
					String contentType = "text/html";
					String message = "Resource not found";
					packItShipIt(t, 404, message, responseBody, contentType);
				} 
				else
				{
					// Resource Not Found
					String responseBody = getHTML("404.txt");
					String contentType = "text/html";
					String message = "Resource not found";
					packItShipIt(t, 404, message, responseBody, contentType);
				}
			}
			
			// HTTP POST Request
			else if (requestMethod.equals("POST")) 
			{
				if (requestProcess.equals("RUN")) 
				{
					// Send the default setting configuration to the GUI
					String responseBody = solution(requestBody);
					String contentType = "application/json";
					String message = "Solution Delivered to " + user;
					packItShipIt(t, 200, message, responseBody, contentType);
				} 
				else if (requestProcess.equals("SAVE")) 
				{
					// Save a submitted setting configuration
					String userFeedback;
					String message = "Solution Delivered to " + user;
					boolean save;
					if(user.equals("") || user.equals("guest")) {
						userFeedback = "This user name may not save scenarios.";
						message += "; Save Denied";
						save = false;
					} else {
						userFeedback = "Scenario saved as \"" + scenario + "\"";
						message += "; Saved scenario: " + scenario;
						save = true;
					}
					String responseBody = solution(requestBody, userFeedback);
					String contentType = "application/json";
					if (save) {
						saveData(user, scenario, "request.json", requestBody);
						saveData(user, scenario, "response.json", responseBody);
					}
					packItShipIt(t, 200, message, responseBody, contentType);
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
					parameters.put(param[0], param[1]);
				}
			}
		}
		if (!parameters.containsKey("user")) 
			parameters.put("user", DEFAULT_USER);
		if (!parameters.containsKey("scenario")) 
			parameters.put("scenario", DEFAULT_SCENARIO);
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
	private String defaultSettings() {
		return baseConfig.serialize().toString(4);
	}
	
	/**
	 * Get the Solution as json string, appending any feedback
	 * @return solution as JSON string
	 */
	private String solution(String requestBody, String feedback) {
		JSONObject solutionJSON = solutionJSON(requestBody);
		solutionJSON.put("feedback", feedback);
		return wrapApi(solutionJSON);
	}
	
	/**
	 * Get the Solution as json string
	 * @param requestBody
	 * @return
	 */
	private String solution(String requestBody) {
		JSONObject solutionJSON = solutionJSON(requestBody);
		return wrapApi(solutionJSON);
	}
	
	/**
	 * Get the solution as a JSON Object
	 * @param requestBody
	 * @return
	 */
	private JSONObject solutionJSON(String requestBody) {
		
		// Check for Body
		if (requestBody.length() == 0) {
			System.out.println("Warning: requestBody has no data");
		}

		// Generate FuzzyIO Response Data
		Configuration config = adapter.parse(requestBody);
		Development solution = builder.build(config, schema);
		MultiObjective performance = evaluator.evaluate(solution);

		// Serialize the Response Data
		JSONObject dataJSON = solution.serialize();
		dataJSON.put("performance", performance.serialize());
		return dataJSON;
	}
	
	/**
	 * Save a String of data to file
	 * @param user
	 * @param scenario
	 * @param fileName
	 * @param dataString
	 */
	private void saveData(String user, String scenario, String fileName, String dataString) {
		
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
	 * Attach Data and Headers to HttpResponse and send it off to the client
	 * @param t
	 * @param responseCode
	 * @param data a string of responseBody, such as a JSON file
	 * @throws IOException
	 */
	private void packItShipIt(HttpExchange t, int responseCode, String responseMessage, String responseBody, String contentType) throws IOException {
		makeHeaders(t, contentType);
		int responseLength = responseBody.length();
		t.sendResponseHeaders(responseCode, responseLength);
		OutputStream os = t.getResponseBody();
		os.write(responseBody.getBytes());
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
		root.put("method", "FuzzyBuilder.build");
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
	
	private boolean isValidMethod(String method) {
		return method.equals("GET") || method.equals("POST") || method.equals("OPTIONS");
	}
}