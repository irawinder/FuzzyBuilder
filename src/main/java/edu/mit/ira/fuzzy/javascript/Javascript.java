package edu.mit.ira.fuzzy.javascript;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.mit.ira.fuzzy.server.Server;

public class Javascript {
	
	private static String JAVASCRIPT_PATH = Server.RELATIVE_DATA_PATH + File.separator + "scripts" + File.separator + "js";
	
	/**
	 * Get the text file at a known location
	 * @param fileName
	 * @return
	 */
	public static String load(String fileName) {
		String filePath = JAVASCRIPT_PATH + File.separator + fileName;
		File f = new File(filePath);
		if (f.exists() && f.isFile()) {
			try {
				return Files.readString(Path.of(filePath));
			} catch (IOException e) {
				System.out.println("The file at " + filePath + " is currupted");
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}
}