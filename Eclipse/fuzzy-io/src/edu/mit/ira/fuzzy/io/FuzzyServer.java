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

import edu.mit.ira.fuzzy.Development;
import edu.mit.ira.fuzzy.FuzzyBuilder;
import edu.mit.ira.fuzzy.FuzzySchema;
import edu.mit.ira.fuzzy.data.SettingGroup;
import edu.mit.ira.fuzzy.data.SettingGroupAdapter;

/**
 * Fuzzy Server listens and responds to requests for fuzzy masses via HTTP
 *
 * @author Ira Winder
 *
 */
public class FuzzyServer {
	private final String SERVER = "Processing Server (Java " + System.getProperty("java.version") + ")";
	private final String SERVER_VERSION = "1";
	private FuzzySchema schema;
	private FuzzyBuilder builder;
	private SettingGroupAdapter adapter;
	private String info;

	HttpServer server;

	/**
	 * Construct a new FuzzyServer
	 *
	 * @param port on the machine to open/reserve for this server
	 * @throws IOException
	 */
	public FuzzyServer(int port) throws IOException {

		server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/", new MyHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
		
		schema = new FuzzySchema();
		builder = new FuzzyBuilder();
		adapter = new SettingGroupAdapter();
		info = "--- FuzzyIO V" + SERVER_VERSION + " ---\nActive on port: " + port;
		System.out.println(info);
	}

	/**
	 * Listen for an HTTP Requests
	 */
	private class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {

			// Parse Request
			String requestURI = t.getRequestURI().toString();
			String requestMethod = t.getRequestMethod();
			String clientIP = t.getRemoteAddress().toString();

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
				if (requestMethod.equals("OPTIONS")) {

					packItShipIt(t, 200);
					log(clientIP, "Options Requested");

				} else if (requestMethod.equals("POST")) {
					if (requestBody.length() > 0) {
						SettingGroup settings = adapter.parse(requestBody);
						Development solution = builder.build(settings);
						if (solution == null) {
							packItShipIt(t, 200);
							log(clientIP, "Bad Request");
						}
						JSONObject dataJSON = solution.serialize();
						String data = wrapApi(dataJSON);
						packItShipIt(t, 200, data);
						log(clientIP, "Delivered " + 
								dataJSON.getJSONArray("voxels").length() + " voxels, " + 
								dataJSON.getJSONArray("shapes").length() + " shapes, and " + 
								dataJSON.getJSONArray("objectives").length() + " objectives");
					} else {
						packItShipIt(t, 400);
						log(clientIP, "This POST request has no body");
					}
				} else if (requestMethod.equals("GET")) {
					String data = schema.serialize().toString();
					packItShipIt(t, 200, data);
					log(clientIP, "Setting Schema Delivered");
				} else {
					packItShipIt(t, 405);
					log(clientIP, "Method Not Allowed");
				}
			} else {
				packItShipIt(t, 404);
				log(clientIP, "Resource Not Found");
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
	public void packItShipIt(HttpExchange t, int responseCode, String data) throws IOException {
		makeHeaders(t);
		t.sendResponseHeaders(responseCode, data.length());
		OutputStream os = t.getResponseBody();
		os.write(data.getBytes());
		os.close();
	}
	
	/**
	 * Attach Headers to HttpResponse and send it off to client
	 * @param t
	 * @param responseCode
	 * @throws IOException
	 */
	public void packItShipIt(HttpExchange t, int responseCode) throws IOException {
		makeHeaders(t);
		t.sendResponseHeaders(responseCode, -1);
	}
	
	/**
	 * Prints a log to console. Also returns the log as a string
	 * @param clientIP
	 * @param message
	 * @return
	 */
	public String log(String clientIP, String message) {

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
		headers.set("Server", SERVER);
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
		String apiVersion = SERVER_VERSION;
		JSONObject root = new JSONObject();
		root.put("apiVersion", apiVersion);
		root.put("method", "FuzzyBuilder.build");
		root.put("data", data);

		return root.toString();
	}
}