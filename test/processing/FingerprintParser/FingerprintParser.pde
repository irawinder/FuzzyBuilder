import java.util.*;

String summary = "";

void setup() {
  
  // Load resources
  String s = File.separator;
  String line = "------------------------------------------------------------------------------------------";
  String absUsersPath = sketchPath() + s + "res" + s + "users";
  String relUsersPath = "res" + s + "users" + s;
  String relRegisterPath = "res" + s + "register" + s;
  File users = new File(absUsersPath);
  String[] deactivated = loadStrings(relRegisterPath + "deactivated.tsv");
  String[] register = loadStrings(relRegisterPath + "register.tsv");
  
  // Sample Counters
  int n = 0; int n_i = 0; // total, incomplete
  int z = 0; int z_i = 0; // zebra, incomplete
  int c = 0; int c_i = 0; // cobra, incomplete
  int p = 0; int p_i = 0; // panda, incomplete
  
  // Entry Summary
  HashMap<String, HashMap<String, Integer>> entrySummary = new HashMap<String, HashMap<String, Integer>>();
  HashMap<String, HashMap<String, Integer>> exitSummary = new HashMap<String, HashMap<String, Integer>>();
  
  summary += "Completed:\n" + line + "\n";
  
  String incomplete = "Incomplete:\n" + line + "\n";
  
  for (String user : users.list()) {
    String prefix = user.substring(0, 5);
    if (!user.equals(".DS_Store")) {
      
      // Load User-specific Resources
      String relUserPath = relUsersPath + user + s;
      String relSurveyPath = relUserPath + "surveys" + s;
      JSONArray consent = loadJSONArray(relSurveyPath + "consent.json");
      String name = consent.getJSONObject(1).getString("a");
      String email = email(user, register);
        
      // User Sample is complete
      if (complete(user, deactivated)) {
        
        // Load Additional User-specific resources
        JSONArray entry = loadJSONArray(relSurveyPath + "entry.json");
        JSONArray exit = loadJSONArray(relSurveyPath + "exit.json");
        String[] eventLog = loadStrings(relUserPath + "eventLog.tsv");
        String[] performanceLog = loadStrings(relUserPath + "performanceLog.tsv");
        String absScenariosPath = absUsersPath + s + user + s + "sceanrios";
        File scenarios = new File(absScenariosPath);
        
        // Count groups
        n++;
        if (prefix.equals("zebra")) {
          z++;
        } else if (prefix.equals("cobra")) {
          c++;
        } else if (prefix.equals("panda")) {
          p++;
        }
        
        // Print User Info
        summary += user + "\t" + name + ", " + email + "\n";
        
        // log entry data
        addAnswers(entrySummary, entry);
        
        // log exit data
        addAnswers(exitSummary, exit);
        
      // Incomplete User Sample
      } else {
        
        // Count groups
        n_i++;
        if (prefix.equals("zebra")) {
          z_i++;
        } else if (prefix.equals("cobra")) {
          c_i++;
        } else if (prefix.equals("panda")) {
          p_i++;
        }
        
        // Print User Info
        incomplete += user + "\t" + name + ", " + email + "\n";
      }
    }
  }
  summary += "\n" + incomplete;
  
  summary += "\n" + "Samples =  complete (incomplete)\n" + line + "\n";
  summary += "zebra = " + z + " (" + z_i + ")" + "\n";
  summary += "cobra = " + c + " (" + c_i + ")" + "\n";
  summary += "panda = " + p + " (" + p_i + ")" + "\n";
  summary += "N = " + n + " (" + n_i + ")" + "\n";
  
  summary += "\n" + "Entry Survey Summary\n" + line + "\n";
  addSurvey(entrySummary);
  
  summary += "\n" + "Exit Survey Summary\n" + line + "\n";
  addSurvey(exitSummary);
  
  print(summary);
  saveStrings("summary.txt", new String[]{summary});
}

boolean complete(String user, String[] deactivated) {
  for (String finished : deactivated) {
    if (finished.equals(user)) {
      return true;
    }
  }
  return false;
}

String email(String user, String[] register) {
  for (String reg : register) {
    String[] r = reg.split("\t");
    if (r[0].equals(user)) {
      return r[1];
    }
  }
  return "not registered";
}

void addSurvey(HashMap<String, HashMap<String, Integer>> eSummary) {
    for (String question : eSummary.keySet()) {
    HashMap<String, Integer> counts = eSummary.get(question);
    summary += "\n" + question + "\n";
    for (String answer : counts.keySet()) {
      summary += "Count: " + counts.get(answer) + "; Answer: " + answer + "\n";
    }
  }
}

void addAnswers(HashMap<String, HashMap<String, Integer>> eSummary, JSONArray eJson) {
  for (int i=0; i<eJson.size(); i++) {
    JSONObject element = eJson.getJSONObject(i);
    if (!element.isNull("q")) {
      String question = element.getString("q");
      String answer = element.getString("a");
      HashMap<String, Integer> counts;
      if (eSummary.containsKey(question)) {
        counts = eSummary.get(question);
        if (counts.containsKey(answer)) {
          int count = counts.get(answer) + 1;
          counts.put(answer, count);
        } else {
          counts.put(answer, 1);
        }
      } else {
        counts = new HashMap<String, Integer>();
        counts.put(answer, 1);
        eSummary.put(question, counts);
      }
    }
  }
}
