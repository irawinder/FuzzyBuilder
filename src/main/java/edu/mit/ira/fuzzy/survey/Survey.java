package edu.mit.ira.fuzzy.survey;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import edu.mit.ira.fuzzy.server.Server;
import edu.mit.ira.fuzzy.server.user.RegisterUtil;

public class Survey {

	private static final String USERS_PATH = Server.RELATIVE_DATA_PATH + File.separator + "users";
	private static final String SURVEYS_FOLDER = "surveys";
	private static final String ENTRY_SURVEY = "entry.json";
	private static final String EXIT_SURVEY = "exit.json";

	/**
	 * Parse data for survey response, saving it to user folder
	 * @param data
	 * @return true if success; false if failure
	 */
	public static boolean save(String user, SurveyType type, String data) {
		
		String surveysPath = surveysPath(user);
		String surveyPath = surveyPath(user, type);
		
		File directory = new File(surveysPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		
		byte bytes[] = data.getBytes();
	    Path p = Paths.get(surveyPath);
	    try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(p))) {
	      out.write(bytes, 0, bytes.length);
	      return true;
	    } catch (IOException x) {
	      System.err.println(x);
	      return false;
	    }
	}
	
	/**
	 * Check if a survey of a given type has already been saved by this user
	 * @param user
	 * @param type
	 * @return true if already saved
	 */
	public static boolean exists(String user, SurveyType type) {
		String path = surveyPath(user, type);
		if (path != null) {
			File f = new File(path);
			if (f.exists() && f.isFile()) {
				return true;
			}
		} else {
			System.out.println(type + " is not a valid SurveyType");
		}
		return false;
	}
	
	/**
	 * Build the path to the user's survey folder
	 * @param user
	 * @return path to survey folder
	 */
	private static String surveysPath(String user) {
		return USERS_PATH + File.separator + RegisterUtil.formalCase(user) + File.separator + SURVEYS_FOLDER;
	}
	
	private static String surveyPath(String user, SurveyType type) {
		switch(type) {
			case ENTRY:
				return surveysPath(user) + File.separator + ENTRY_SURVEY;
			case EXIT:
				return surveysPath(user) + File.separator + EXIT_SURVEY;
			default:
				return null;
		}
	}
}
