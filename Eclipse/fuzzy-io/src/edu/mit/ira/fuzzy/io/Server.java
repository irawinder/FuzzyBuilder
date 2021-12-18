package edu.mit.ira.fuzzy.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import edu.mit.ira.fuzzy.model.Development;
import edu.mit.ira.fuzzy.objective.MultiObjective;
import edu.mit.ira.fuzzy.setting.Setting;
import edu.mit.ira.fuzzy.setting.Deserializer;

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
	public Server(String name, String version, String author, String sponsor, String contact, int port) throws IOException {
		this.serverID = name;
		this.serverVersion = version;
		server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/", new MyHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
		
		schema = new Schema(serverVersion, serverID, author, sponsor, contact);
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

			// Parse and Log Request Header
			String requestURI = t.getRequestURI().toString();
			String requestMethod = t.getRequestMethod();
			log(t, requestMethod + " " +  requestURI);
			
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

			// Format Response Headers and Body
			if (requestURI.equals("/")) {
				
				// OPTIONS request is something browsers ask before 
				// allowing an external server to provide data
				if (requestMethod.equals("OPTIONS")) {
					packItShipIt(t, 200, "Options Delivered");
				
				// POST request is how settings are submitted to 
				// FuzzyIO via an external GUI (e.g. openSUI) 
				} else if (requestMethod.equals("POST")) {
					if (requestBody.length() > 0) {
						
						// Generate FuzzyIO Response Data
						Setting settings = adapter.parse(requestBody);
						Development solution = builder.build(settings);
						MultiObjective performance = evaluator.evaluate(solution);
						
						// Serialize the Response Data
						JSONObject dataJSON = solution.serialize();
						dataJSON.put("performance", performance.serialize());
						String data = wrapApi(dataJSON);
						String message = "Solution Delivered";
						if (solution.error != null) message += " with errors";
						packItShipIt(t, 200, message, data);
					} else {
						packItShipIt(t, 400, "POST request has no body");
					}
				
				// GET request is initially made to retrieve default setting schema
				} else if (requestMethod.equals("GET")) {
					String data = schema.serialize().toString(4);
					packItShipIt(t, 200, "Setting Schema Delivered", data);
					
				// No other request methods are allowed
				} else {
					packItShipIt(t, 405, "Method Not Allowed");
				}
				
			// URI is not valid
			} else {
				packItShipIt(t, 404, "Resource Not Found");
			}
		}
	}

	/**
	 * Attach Data and Headers to HttpResponse and send it off to the client
	 * @param t
	 * @param responseCode
	 * @param data a string of data, such as a JSON file
	 * @throws IOException
	 */
	public void packItShipIt(HttpExchange t, int responseCode, String responseMessage, String data) throws IOException {
		makeHeaders(t);
		int responseLength = data.length();
		t.sendResponseHeaders(responseCode, responseLength);
		OutputStream os = t.getResponseBody();
		os.write(data.getBytes());
		os.close();
		log(t, "Response Code: " + responseCode + ", " + responseMessage + ", Response Length: " + responseLength);
	}
	
	/**
	 * Attach Headers to HttpResponse and send it off to client
	 * @param t
	 * @param responseCode
	 * @throws IOException
	 */
	public void packItShipIt(HttpExchange t, int responseCode, String responseMessage) throws IOException {
		makeHeaders(t);
		int responseLength = -1;
		t.sendResponseHeaders(responseCode, -1);
		log(t, "Response Code: " + responseCode + ", " + responseMessage + ", Response Length: " + responseLength);
	}
	
	/**
	 * Prints a log to console. Also returns the log as a string
	 * @param clientIP
	 * @param message
	 * @return
	 */
	public String log(HttpExchange t, String message) {
		
		// Log Response
		String clientIP = t.getRemoteAddress().toString();
				
		// Time
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		Date date = new Date(System.currentTimeMillis());
		String timeStamp = formatter.format(date);

		String log = timeStamp + " " + clientIP + " : " + message;
		System.out.println(log);
		return log;
	}

	/**
	 * Format Headers for HTTP response
	 * 
	 * @param contentType the type of data attached on this response
	 * @return header fields for the response
	 */
	private void makeHeaders(HttpExchange t) {

		Headers headers = t.getResponseHeaders();
		headers.set("Server", serverID + ", " + SERVER_SYSTEM);
		headers.set("Content-Type", "application/json");
		headers.set("Connection", "close");
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
}