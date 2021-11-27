/**
 * Fuzzy Server listens and responds to requests for fuzzy masses via HTTP
 *
 * @author Ira Winder
 *
 */
class FuzzyServer {
  private final String SERVER = "Processing Server (Java " + System.getProperty("java.version") + ")";
  private final String SERVER_VERSION = "1";
  private final String HTTP_VERSION = "1.1";
  private Server server; 
  private FuzzyBuilder builder;
  private SettingGroupAdapter adapter;
  private String info;
  
  /**
   * Construct a new FuzzyServer
   *
   * @param port on the machine to open/reserve for this server
   */
  public FuzzyServer(int port) {
  
    // Start Server Objects
    this.server = new Server(FuzzyIO.this, port);
    this.builder = new FuzzyBuilder();
    this.adapter = new SettingGroupAdapter();
    this.info = "--- FuzzyIO V" + SERVER_VERSION + " ---\nActive on port: " + port;
    println("\n" + info);
  }
  
  /**
   * Listen for an HTTP Request every frame
   */
  public void listenForRequest() {
    
    // Receive word query from client (HTTP Request protocol)
    Client client = this.server.available();
    if (client != null) {
      
      String request = client.readString();
      String response = this.getResponse(request, client.ip());
      
      // Send the HTTP Response and Close the connection tot he client
      this.server.write(response);
      this.server.disconnect(client);
    }
  }
  
  /** 
  * Generate the response for a standard HTTP request
  *
  * @param request the raw HTTP 1.1 request including header and body
  * @param clientIP the IP address of the client who sent this request
  * @return an HTTP 1.1 server response with fuzzy data attached
  */
  String getResponse(String request, String clientIP) {
    
    String lineBreak = "\r\n";
    
    String[] message = request.split(lineBreak + lineBreak);
    
    String header = message[0];
    String method = header.split(lineBreak)[0].split(" ")[0];
    
    if (method.equals("OPTIONS")) {
      
      return formatResponse("200", "Success", "application/json", "");
      
    } else if (method.equals("POST")) {
      
      SettingGroup settings;
      Development solution;
      if (message.length > 1) {
        String body = message[1];
        settings = adapter.parse(body);
      } else  {
        settings = new SettingGroup();
      }
      fuzzy = this.builder.build(settings);
      JSONObject dataJSON = fuzzy.serialize();
      println(dataJSON.getJSONArray("voxels").size() + " voxels delivered to " + clientIP);
      String data = this.wrapApi(dataJSON);
      return formatResponse("200", "Success", "application/json", data);
      
    } else {
      
      return formatResponse("405", "Method Not Allowed", "application/json", "{}");
    }
  }
  
  /**
   * Format inputs as a standard HTTP 1.1 response
   *
   * @param statusCode the standard HTTP status code to tag this response with
   * @param reasonPhrase short message describing the reason for this code
   * @param contentType the type of data attached on this response
   * @param data data of interest to pass via this response
   * @return an entire HTTP response including header and body
   */
  private String formatResponse(String statusCode, String reasonPhrase, String contentType, String data) {
  
    // HTTP Response formatted according to 
    // https://www.tutorialspoint.com/http/http_responses.htm
    //
    String response = "";
    String lineBreak = "\r\n";
    response += "HTTP/" + HTTP_VERSION + " " + statusCode + " " + reasonPhrase + lineBreak;
    response += "Server: " + SERVER + lineBreak;
    response += "Content-Type: " + contentType + lineBreak;
    response += "Connection: close" + lineBreak;
    response += "Access-Control-Allow-Origin: *" + lineBreak;
    response += "Access-Control-Allow-Headers: *" + lineBreak;
    response += lineBreak;
    response += data;
    
    return response;
  }
  
  /**
   * Wrap JSON data with a standard JSON header and return as string
   *
   * @param data serialization of fuzzybuilder voxels, etc
   * @return original data wrapped by a json header with information about the data (api version, etc)
   */
  public String wrapApi(JSONObject data) {
    
    //Compile Root JSON Object
    String apiVersion = SERVER_VERSION;
    JSONObject root = new JSONObject();
    root.setString("apiVersion", apiVersion);
    root.setString("method", "FuzzyBuilder.build");
    root.setJSONObject("data", data);
    
    return root.toString();
  }
}
