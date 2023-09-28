import java.util.*;

String summary = "";

int totalZebra = 0;
int totalCobra = 0;
int totalPanda = 0;
  
int totalZebraSatisfaction = 0;
int totalCobraSatisfaction = 0;
int totalPandaSatisfaction = 0;

int totalZebraConfidence = 0;
int totalCobraConfidence = 0;
int totalPandaConfidence = 0;

Table data;
  
void setup() {
  
  // Init Table
  data = new Table();
  data.addColumn("id");
  data.addColumn("group");
  
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
  
  // Entry/Exit Summary
  HashMap<String, HashMap<String, Integer>> entrySummary = new HashMap<String, HashMap<String, Integer>>();
  HashMap<String, HashMap<String, Integer>> exitSummary = new HashMap<String, HashMap<String, Integer>>();
  
  summary += "Completed:\n" + line + "\n";
  
  String incomplete = "Incomplete:\n" + line + "\n";
  
  HashMap<String, ArrayList<Integer>> runs = new HashMap<String, ArrayList<Integer>>();
  HashMap<String, ArrayList<Integer>> saves = new HashMap<String, ArrayList<Integer>>();
  HashMap<String, ArrayList<Integer>> loads = new HashMap<String, ArrayList<Integer>>();
  HashMap<String, ArrayList<Integer>> homes = new HashMap<String, ArrayList<Integer>>();
  
  HashMap<String, Integer> userMinTime = new HashMap<String, Integer>();
  HashMap<String, Integer> userMaxTime = new HashMap<String, Integer>();
  
  int minTime = 1000000000;
  int maxTime = -1000000000;
  int maxDuration = -1000000000;
  
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
        
        TableRow dataRow = data.addRow();
        dataRow.setString("id", user);
        
        // local min/max times of user
        userMinTime.put(user, 1000000000);
        userMaxTime.put(user, -1000000000);
        
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
          dataRow.setString("group", "zebra");
          z++;
        } else if (prefix.equals("cobra")) {
          dataRow.setString("group", "cobra");
          c++;
        } else if (prefix.equals("panda")) {
          dataRow.setString("group", "panda");
          p++;
        }
        
        // Print User Info
        summary += user + "\t" + name + ", " + email + "\n";
        
        // log entry data
        addAnswers(user, entrySummary, entry, false, dataRow);
        
        // log exit data
        addAnswers(user, exitSummary, exit, true, dataRow);
        
        // log run, save, load data
        ArrayList<Integer> rn = new ArrayList<Integer>();
        ArrayList<Integer> ld = new ArrayList<Integer>();
        ArrayList<Integer> sv = new ArrayList<Integer>();
        ArrayList<Integer> hm = new ArrayList<Integer>();
        runs.put(user, rn);
        saves.put(user, sv);
        loads.put(user, ld);
        homes.put(user, hm);
        for (String event : eventLog) {
          String[] fields = event.split("\t");
          if (!fields[0].equals("user")) {
            int time = toTime(fields[1]);
            if (fields[3].equals("RUN")) {
              rn.add(time);
            }else if (fields[3].equals("LOAD SCENARIO")) {
              ld.add(time);
            } else if (fields[3].equals("SAVE SCENARIO")) {
              sv.add(time);
            } else if (fields[3].equals("HOME")) {
              hm.add(time);
            }
            if (time < minTime) minTime = time;
            if (time > maxTime) maxTime = time;
            
            if (time < userMinTime.get(user)) userMinTime.put(user, time);
            if (time > userMaxTime.get(user)) userMaxTime.put(user, time);
          }
        }
        
        int duration = userMaxTime.get(user) - userMinTime.get(user);
        if (maxDuration < duration) maxDuration = duration;
        
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
  
  float avgZebraSatisfaction = (float) totalZebraSatisfaction / totalZebra;
  float avgCobraSatisfaction = (float) totalCobraSatisfaction / totalCobra;
  float avgPandaSatisfaction = (float) totalPandaSatisfaction / totalPanda;
  
  float avgZebraConfidence = (float) totalZebraConfidence / totalZebra;
  float avgCobraConfidence = (float) totalCobraConfidence / totalCobra;
  float avgPandaConfidence = (float) totalPandaConfidence / totalPanda;
  
  summary += "\n" + "Average Satisfaction\n" + line + "\n";
  summary += "Zebra: " + avgZebraSatisfaction + "\n";
  summary += "Cobra: " + avgCobraSatisfaction + "\n";
  summary += "Panda: " + avgPandaSatisfaction + "\n";
  
  summary += "\n" + "Average Confidence\n" + line + "\n";
  summary += "Zebra: " + avgZebraConfidence + "\n";
  summary += "Cobra: " + avgCobraConfidence + "\n";
  summary += "Panda: " + avgPandaConfidence + "\n";
  
  print(summary);
  saveStrings("summary.txt", new String[]{summary});
  
  saveTable(data, "data.tsv", "tsv");
  
  size(2500, 1500);
  background(255);
  
  int num = runs.keySet().size();
  int w = width - 70;
  int h = height / num - 20;
  int index = 0;
  
  // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  //
  // Artifically shorten max duration!!! (update this if data changes!!!
  //
  // maxDuration *= 0.6;
  
  for (int i=0; i<3; i++) {
    
    String drawingPrefix = "";
    switch(i) {
      case 0:
        drawingPrefix = "zebra";
        break;
      case 1:
        drawingPrefix = "cobra";
        break;
      case 2:
        drawingPrefix = "panda";
        break;
    }
    
    for (String user: runs.keySet()) {
      
      String prefix = user.substring(0, 5);
      boolean isDrawing = drawingPrefix.equals(prefix);
      
      if(isDrawing) {
        
        int x = 50;
        int y = 20 + (h + 20)*index;
        
        int localMinTime = userMinTime.get(user);
      
        fill(50);
        text(user, x, y - 5);
        noFill();
        stroke(230);
        line(x - 10, y + 0.25*h, x + w, y + 0.25*h);
        line(x - 10, y + 0.50*h, x + w, y + 0.50*h);
        line(x - 10, y + 0.75*h, x + w, y + 0.75*h);
        fill(#FF0000);
        noStroke();
        for (Integer time : runs.get(user)) {
          float dx = w * (float) (time - localMinTime) / (float) maxDuration;
          circle(x + dx, y + 0.25 * h, 3);
        }
        fill(#00CC00);
        noStroke();
        for (Integer time : loads.get(user)) {
          float dx = w * (float) (time - localMinTime) / (float) maxDuration;
          circle(x + dx, y + 0.50 * h, 3);
        }
        fill(#0000FF);
        noStroke();
        for (Integer time : saves.get(user)) {
          float dx = w * (float) (time - localMinTime) / (float) maxDuration;
          circle(x + dx, y + 0.75 * h, 3);
        }
        stroke(#CCCCCC);
        for (Integer time : homes.get(user)) {
          float dx = w * (float) (time - localMinTime) / (float) maxDuration;
          line(x + dx, y, x + dx, y + h);
          //circle(x + dx, y + 0.75 * h, 3);
        }
        fill(#FF0000);
        text("run", 10, y + 0.25 * h + 3);
        fill(#00CC00);
        text("load", 10, y + 0.50 * h + 3);
        fill(#0000FF);
        text("save", 10, y + 0.75 * h + 3);
        
        index++;
      }
    }
  }
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

void addAnswers(String user, HashMap<String, HashMap<String, Integer>> eSummary, JSONArray eJson, boolean exit, TableRow row) {
  String qS = "How *satisfied* are you with your final scenario?";
  String qC = "How *confident* are you that the community of Beaverton will like your final scenario?";
  if (exit) {
    if (user.substring(0, 1).equals("z")) {
      totalZebra++;
    } else if (user.substring(0, 1).equals("c")) {
      totalCobra++;
    } else if (user.substring(0, 1).equals("p")) {
      totalPanda++;
    }
  }
  for (int i=0; i<eJson.size(); i++) {
    JSONObject element = eJson.getJSONObject(i);
    if (!element.isNull("q")) {
      String question = element.getString("q");
      String answer = element.getString("a");
      
      try {
        data.getString(0, question); 
      } catch (Exception e) {
        data.addColumn(question);
      }
      
      row.setString(question, answer.replace("\n", "; "));
      
      if (question.equals(qS)) {
        if (user.substring(0, 1).equals("z")) {
          totalZebraSatisfaction += element.getInt("a");
        } else if (user.substring(0, 1).equals("c")) {
          totalCobraSatisfaction += element.getInt("a");
        } else if (user.substring(0, 1).equals("p")) {
          totalPandaSatisfaction += element.getInt("a");
        }
        
      }
      if (question.equals(qC)) {
        if (user.substring(0, 1).equals("z")) {
          totalZebraConfidence += element.getInt("a");
        } else if (user.substring(0, 1).equals("c")) {
          totalCobraConfidence += element.getInt("a");
        } else if (user.substring(0, 1).equals("p")) {
          totalPandaConfidence += element.getInt("a");
        }
      }
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

Integer toTime (String timeStamp) {
  String[] timeString = timeStamp.substring(timeStamp.length() - 12, timeStamp.length()-4).split(":");
  return (int) (3600 * Float.parseFloat(timeString[0]) + 60 * Float.parseFloat(timeString[1]) + Float.parseFloat(timeString[2]));
}
