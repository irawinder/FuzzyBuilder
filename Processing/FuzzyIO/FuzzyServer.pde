class FuzzyServer {

  // Server Objects
  private final String SERVER_VERSION = "1";
  private Server server; 
  String info;
  
  public FuzzyServer(int port) {
  
    // Start Server Objects
    this.server = new Server(FuzzyIO.this, port);
    
    this.info = "--- FuzzyIO V" + SERVER_VERSION + " ---\nActive on port: " + port;
    println("\n" + info);
  }
  
  /** Listen for HTTP requests and generate a response
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
  
  /** Parse a standard HTTP request
  * @return an HTTP server response
  */
  String getResponse(String request, String clientIP) {
    
    String[] message = request.split("\r\n\r\n");
    // Only consider the top line of a multi-line request
    // Reason: Browsers like Chrome will automatically add a bunch of junk after the first line
    String header = message[0];
    
    if (message.length > 1) {
      String body = message[1];
      SettingGroup settings = fuzzy.parseSettingGroup(body);
      fuzzy.build(settings);
    }
    
    return formatResponse("200", "Request Recieved", "application/json", "{\"success\": true}");
  }
  
  private String formatResponse(String statusCode, String reasonPhrase, String contentType, String data) {
  
  // HTTP Response formatted according to 
  // https://www.tutorialspoint.com/http/http_responses.htm
  //
  String response = "";
  response += "HTTP/" + HTTP_VERSION + " " + statusCode + " " + reasonPhrase + "\r\n";
  response += "Server: " + SERVER + "\r\n";
  response += "Content-Type: " + contentType + "\r\n";
  response += "Connection: close\r\n";
  response += "\r\n";
  response += data;
  
  return response;
}
}
