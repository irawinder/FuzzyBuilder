package edu.mit.ira.fuzzy.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import edu.mit.ira.fuzzy.server.log.ServerLog;

public class ServerUtil {

	public static String DEFAULT_USER = "guest";
	public static String DEFAULT_EMAIL = "foo@bar.com";
	private static String DEFAULT_PAGE = "1";
	private static String DEFAULT_SCENARIO = "defacto";
	private static String DEFAULT_BASEMAP = "default";
	
	/**
	 * Parse the resource requests (e.g. "/opensui/load" becomes {"opensui", "load"})
	 * @param requestURI
	 * @return
	 */
	public static String[] parseResource(String requestURI) {
		String grossResource = requestURI.replace("?", ";").split(";")[0];
		grossResource = grossResource.substring(1, grossResource.length());
		return grossResource.toLowerCase().split("/");
	}
	
	/**
	 * Get parameters from URI
	 * @param requestURI
	 * @return
	 */
	public static Map<String, String> parseParameters(String requestURI) {
		String[] process_params = requestURI.replace("?", ";").split(";");
		Map<String, String> parameters = new HashMap<String, String>();
		if(process_params.length > 1) {
			String[] params = process_params[1].split("&");
			for (int i=0; i<params.length; i++) {
				String[] param = params[i].split("=");
				if (param.length == 2) {
					parameters.put(param[0], param[1].replace("%20", " ").replace("%40", "@").replace("%2E", ".").replace("%2e", "."));
				} else {
					parameters.put(param[0], "");
				}
			}
		}
		if (!parameters.containsKey("user")) 
			parameters.put("user", DEFAULT_USER);
		if (!parameters.containsKey("email")) 
			parameters.put("email", DEFAULT_EMAIL);
		if (!parameters.containsKey("page")) 
			parameters.put("page", DEFAULT_PAGE);
		if (!parameters.containsKey("scenario")) 
			parameters.put("scenario", DEFAULT_SCENARIO);
		if (!parameters.containsKey("filename")) 
			parameters.put("filename", DEFAULT_BASEMAP);
		return parameters;
	}

	/**
	 * Format Headers for HTTP response
	 * 
	 * @param contentType the type of data attached on this response
	 * @return header fields for the response
	 */
	public static void setHeaders(HttpExchange t, String contentType) {

		Headers headers = t.getResponseHeaders();
		headers.set("Server", Server.NAME + ", " + Server.SYSTEM);
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
	public static JSONObject wrapData(JSONObject data) {

		// Compile Root JSON Object
		JSONObject root = new JSONObject();
		root.put("apiVersion", Server.VERSION);
		root.put("data", data);
		return root;
	}
	
	/**
	 * 
	 * @param t
	 * @param responseCode
	 * @param responseMessage
	 * @param responseBody as bytes
	 * @param contentType
	 * @throws IOException
	 */
	public static void packItShipIt(HttpExchange t, int responseCode, String responseMessage, byte[] responseBody, String contentType) throws IOException {
		ServerUtil.setHeaders(t, contentType);
		int responseLength = responseBody.length;
		t.sendResponseHeaders(responseCode, responseLength);
		OutputStream os = t.getResponseBody();
		os.write(responseBody, 0, responseBody.length);
		os.close();
		ServerLog.add(t, "Response: " + responseCode + " " + responseMessage + "; Response Length: " + responseLength);
	}
	
	/**
	 * Attach Data and Headers to HttpResponse and send it off to the client
	 * @param t
	 * @param responseCode
	 * @param responseMessage
	 * @param responseBody as String
	 * @param contentType
	 * @throws IOException
	 */
	public static void packItShipIt(HttpExchange t, int responseCode, String responseMessage, String responseBody, String contentType) throws IOException {
		packItShipIt(t, responseCode, responseMessage, responseBody.getBytes(), contentType);
	}
	
	/**
	 * Attach Headers to HttpResponse and send it off to client
	 * @param t
	 * @param responseCode
	 * @param responseMessage
	 * @throws IOException
	 */
	public static void packItShipIt(HttpExchange t, int responseCode, String responseMessage) throws IOException {
		ServerUtil.setHeaders(t, "text/html");
		int responseLength = -1;
		t.sendResponseHeaders(responseCode, -1);
		ServerLog.add(t, "Response: " + responseCode + " " + responseMessage + "; Response Length: " + responseLength);
	}
	
	
	/**
	 * Return a list of filenames in a directory as JSON Object
	 * @param directoryName
	 * @return
	 */
	public static String fileNames(String directoryName) {
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
	 * Delete a directory and all of its contents	
	 * @param file
	 */
	public static void deleteDir(File file) {
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
	 * Parse Request Body
	 * @param t
	 * @return
	 */
	public static String parseRequestBody(HttpExchange t) {
		try {
			InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			int b;
			StringBuilder buf = new StringBuilder(512);
			while ((b = br.read()) != -1) {
				buf.append((char) b);
			}
			br.close();
			isr.close();
			return buf.toString();
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
			
		} catch (IOException e) {
			e.printStackTrace();
			return "";
			
		}
	}
}