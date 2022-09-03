package edu.mit.ira.fuzzy.io.user;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.mit.ira.fuzzy.io.Server;

/**
 * Utility class for registering Users
 * @author ira
 *
 */
public class Register {
	
	private static String USERS_PATH = Server.RELATIVE_DATA_PATH + File.separator + "users";
	private static String REGISTER_PATH = USERS_PATH + File.separator + "register.tsv";
	
	private static File USERS_DIR = new File(USERS_PATH);
	private static File REGISTER_FILE = new File(REGISTER_PATH);
	
	public static String[] USER_PREFIXES = {"zebra", "cobra", "panda", "squid"};
	public static int PREFIX_LENGTH = 5;
	public static int CODE_LENGTH = 6;
	private static char[] CODE_CHAR = { // '1', 'I', 'L', '0' and 'O' excluded on purpose, since they can be confused with each other
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 
		'2', '3', '4', '5', '6', '7', '8', '9'
	};

	/**
	 * Add email address to the registry and assigns them a new user id. If email address is already registered, prior registration is not overridden
	 * @param userID
	 * @param email
	 * @return true if new userID assigned to this email; otherwise false
	 */
	public boolean makeUser(String email) {
		
		// Assertively check that registration file exists
		if (this.create()) {
			System.out.println("New registration file created");
		};
				
		// Check for duplicate email address
		for (String row : this.entries()) {
			String[] entry = row.split("\t");
			if (entry.length > 1) {
				if (email.equals(entry[1])) {
					System.out.println("This email has already been registered");
					return false;
				}
			}
		}
		
		// Make a new, unique user name
		String userID = "foo"; // TODO
		
		// Add to registry
		return this.addEntry(userID, email);
	}
	
	private String generateUserID() {
		
		String[] entries = this.entries();
		
		
		
		return "";
	}

	/**
	 * Add a new user-email pair to the register, checking for duplicates
	 * @return true if successful; false if failure
	 */
	private boolean addEntry(String userID, String email) {
		
		String[] entries = this.entries();
		
		// Check for duplicates
		for (String row : entries) {
			String[] entry = row.split("\t");
			
			if (userID.equals(entry[0])) {
				System.out.println("This userID has already been registered");
				return false;
			}
			
			if (entry.length > 1) {
				if (email.equals(entry[1])) {
					System.out.println("This email has already been registered");
					return false;
				}
			}
		}
		
		// Make new Entry
		String row = "";
		if (entries.length > 0) row += "\n";
		row += userID + "\t" + email;

		// Write user-email pair to registration
		try {
			FileWriter myWriter = new FileWriter(REGISTER_PATH);
			myWriter.write(row);
			myWriter.close();
		} catch (IOException e) {
			System.out.println("Error: Could not write to register file");
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * Return a list of registered entities
	 * @return empty list if there was an error creating/reading registration file
	 */
	private String[] entries() {
		
		// Check that register exists
		if (REGISTER_FILE.exists() && REGISTER_FILE.isFile()) {
			try {
				return Files.readString(Path.of(REGISTER_PATH)).split("\n");
			} catch (IOException e) {
				System.out.println("The user registration file is currupted");
				e.printStackTrace();
				return new String[0];
			}
		} else {
			System.out.println("The user registration file does not exist");
			return new String[0];
		}
	}
	
	/**
	 * Create Registration File, if it does not already exist
	 * @return returns true if created; returns false if already exists
	 */
	private boolean create() {
		
		// Assert User Directory Exists
		if (!USERS_DIR.exists()) {
			USERS_DIR.mkdirs();
		}

		// Assert Register File Exists
		if (!REGISTER_FILE.exists()) {
			try {
				if (REGISTER_FILE.createNewFile()) {
					return true;
				}
			} catch (IOException e) {
				System.out.println("Error: could not create new registration file");
				e.printStackTrace();
			}
		}
		return false;
	}
}
