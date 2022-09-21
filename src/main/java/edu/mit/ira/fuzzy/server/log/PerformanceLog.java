package edu.mit.ira.fuzzy.server.log;

import java.io.File;

import edu.mit.ira.fuzzy.server.Server;

/** A class for saving user phrases to text file
 * @author Ira Winder
 */
public class PerformanceLog {
	
	private static String LOG_FILE_NAME = "performanceLog.tsv";
	private static String USERS_PATH = Server.RELATIVE_DATA_PATH + File.separator + "users";

	public static void add(String user, String headers, String row) {
		
		String userPath = USERS_PATH + File.separator + user;
		String logPath = userPath + File.separator + LOG_FILE_NAME;
		File f = new File(logPath);
		
		if (!f.exists()) {
			initLog(user, userPath, logPath, headers);
		}
		
		LogUtil.appendData(logPath, user + "\t" + row + "\n");
	}
	
	private static void initLog(String user, String userPath, String logPath, String headers) {

		// Make new user directory if needed
		File directory = new File(userPath);
		if (!directory.exists()) directory.mkdirs();

		// Add an empty table with column headers
		String log = "user" + "\t" + headers + "\n";

		// Save the log to file
		LogUtil.saveData(logPath, log);
	}
}
