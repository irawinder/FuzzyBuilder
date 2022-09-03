package edu.mit.ira.fuzzy.io.log;

import java.io.File;

import edu.mit.ira.fuzzy.io.Server;

/** A class for saving user phrases to text file
 * @author Ira Winder
 */
public class UserLog {
	
	private static String LOG_FILE_NAME = "log.tsv";
	private static String USERS_PATH = Server.RELATIVE_DATA_PATH + File.separator + "users";
	private static String[] TABLE_COLUMNS = {"user", "timestamp", "clientIP", "action", "description"};

	public static void add(String user, String clientIP, String action, String description) {
		
		String userPath = USERS_PATH + File.separator + user;
		String logPath = userPath + File.separator + LOG_FILE_NAME;
		File f = new File(logPath);
		
		if (!f.exists()) {
			initLog(user, userPath, logPath);
		}
		
		String row = "";
		row += user;
		row += "\t";
		row += LogUtil.makeTimeStamp();
		row += "\t";
		row += clientIP;
		row += "\t";
		row += action;
		row += "\t";
		row += description;
		row += "\n";
		LogUtil.appendData(logPath, row);
	}
	
	private static void initLog(String user, String userPath, String logPath) {

		// Make new user directory if needed
		File directory = new File(userPath);
		if (!directory.exists()) directory.mkdirs();

		// Add an empty table with column headers
		String log = "";
		for(int i=0; i<TABLE_COLUMNS.length; i++) {
			log += TABLE_COLUMNS[i];
			if (i < TABLE_COLUMNS.length-1) log += "\t";
		}
		log += "\n";

		// Save the log to file
		LogUtil.saveData(logPath, log);
	}
}
