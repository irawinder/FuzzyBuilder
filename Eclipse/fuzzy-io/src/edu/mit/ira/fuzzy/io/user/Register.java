package edu.mit.ira.fuzzy.io.user;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;

import edu.mit.ira.fuzzy.io.Server;

/**
 * Utility class for registering Users
 * @author ira
 *
 */
public class Register {
	
	// Registry Entry row is of form:
	// userID	email	type	status
	
	private static int REGISTER_COLUMNS = 2;
	private static String REGISTER_PATH = Server.RELATIVE_DATA_PATH + File.separator + "users" + File.separator + "register.tsv";
	private static String DEACTIVATED_PATH = Server.RELATIVE_DATA_PATH + File.separator + "users" + File.separator + "deactivated.tsv";
	private static File REGISTER_FILE = new File(REGISTER_PATH);
	private static File DEACTIVATED_FILE = new File(DEACTIVATED_PATH);
	
	public static int CODE_LENGTH = 6;
	
	// Purposely excluded the following, since they can be confused with each other:
	// '1', 'I', 'L', '0', 'O' 
	private static char[] CODE_CHARS = { 
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 
		'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 
		'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', 
		'3', '4', '5', '6', '7', '8', '9'
	};
	
	/**
	 * Get email associated with this user; returns null if not found
	 * @param userID
	 * @return email as string; null if not found
	 */
	public static String email(String userID) {
		return entryBy(userID, 0)[1];
	}
	
	/**
	 * Get active user associated with this email; returns null if not found
	 * @param userID
	 * @return email as string; null if not found
	 */
	public static String user(String email) {
		return entryBy(email, 1)[0];
	}
	
	/**
	 * Check if a user ID is already registered
	 * @param userID
	 * @return true if registered
	 */
	public static boolean active(String userID) {
		if (entryBy(userID, 0) != null) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Write user to register of deactivated users
	 * @param userID
	 * @return true if successful
	 */
	public static boolean deactivate(String userID) {
		
		// Can't deactivate the user if it's already deactivated
		if (!deactivated(userID)) {
			
			// Make new Entry
			String row = "";
			if (deactivated().length > 0) row += "\n";
			row += userID;
	
			// Write user to register of deactivated users
			byte data[] = row.getBytes();
			Path p = Paths.get(DEACTIVATED_PATH);
			try (OutputStream out = new BufferedOutputStream(
					Files.newOutputStream(p, StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
				out.write(data, 0, data.length);
			} catch (IOException x) {
				System.out.println("Error: Could not write to inactive file");
				System.err.println(x);
			}
	
			return true;
		}
			
		return false;
	}
	
	/**
	 * Checks if user has a prefix associated with the given index
	 * @param userID
	 * @param index
	 * @return true if match
	 */
	public static boolean hasPrefix(String userID, UserPrefixStudy p) {
		String prefix = p.lc();
		if (userID.length() > prefix.length()) {
			String prefixUser = userID.substring(0, prefix.length());
			return ignoreCaseEquals(prefix, prefixUser);
		} else {
			return false;
		}
	}
	
	/**
	 * Checks if user has a prefix associated with the given index
	 * @param userID
	 * @param index
	 * @return true if match
	 */
	public static boolean hasPrefix(String userID, UserPrefixAdmin p) {
		String prefix = p.lc();
		if (userID.length() > prefix.length()) {
			String prefixUser = userID.substring(0, prefix.length());
			return ignoreCaseEquals(prefix, prefixUser);
		} else {
			return false;
		}
	}
	
	/**
	 * Check if a email is already registered
	 * @param userID
	 * @return true if registered
	 */
	public static boolean emailExists(String email) {
		return !isUniqueEmail(email);
	}
	
	/**
	 * Add email address to the registry and assigns them a new user id. If email address is already registered, prior registration is not overridden
	 * @param userID
	 * @param email
	 * @return userID assigned to this email; otherwise null
	 */
	public static String makeUser(String email, UserType type) {

		// Check for duplicate email address
		if (!isValidEmail(email)) {
			return null;
		}

		// Check for duplicate email address
		if (!isUniqueEmail(email)) {
			System.out.println(email + " is already in use");
			return null;
		}
		
		Random r = new Random();
		String userID = null;
		
		boolean valid = false;
		int attempts = 0;
		
		while(userID == null || !valid) {
			
			if(attempts++ > 10) {
				System.out.println("Failed to find unique User ID after 10 tries");
				return null;
			}
			
			// Find the prefix
			String prefix;
			if (type == UserType.ADMIN) {
				
				// always equals 'squid'
				prefix = UserPrefixAdmin.SQUID.lc();
				
			} else if (type == UserType.STUDY) {
				
				// 1 : 1 : 1 split between 'zebra', 'cobra', and 'panda'
				int prefixIndex = r.nextInt(UserPrefixStudy.values().length);
				prefix = UserPrefixStudy.values()[prefixIndex].lc();
				
			} else {
				
				System.out.println(type + " is not a valid user type");
				return null;
			}
			
			String code = makeCode();
			userID = prefix + code;
			valid = isUniqueUser(userID);
		}
		
		// Add new user
		addEntry(userID, email);
		return userID;
	}
	
	/**
	 * Check if the candidate username is unique (ignores case)
	 * @param userID
	 * @return true if unique (no duplicates found)
	 */
	private static boolean isUniqueUser(String userID) {
		if(entryBy(userID, 0) != null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Check if the candidate email is unique (ignores case)
	 * @param userID
	 * @return true if unique (no duplicates found)
	 */
	private static boolean isUniqueEmail(String email) {
		
		if(entryBy(email, 1) != null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if email is of format "email@domain.name"
	 * @param email
	 * @return true if valid
	 */
	private static boolean isValidEmail(String email) {
		String temp = email.replace(".", ":");
		String[] array = temp.split("@");
		if (array.length == 2) {
			if (array[1].split(":").length == 2) {
				return true;
			}
		}
		System.out.println(email + " is not a valid email address");
		return false;
	}
	
	/**
	 * Make a CODE for a user ID
	 * @return
	 */
	private static String makeCode() {
		Random r = new Random();
		String code = "";
		for (int i=0; i<CODE_LENGTH; i++) {
			int charIndex = r.nextInt(CODE_CHARS.length);
			code += CODE_CHARS[charIndex];
		}
		return code;
	}

	/**
	 * Add a new user-email pair to the register, checking for duplicates
	 * @return true if successful; false if failure
	 */
	private static boolean addEntry(String userID, String email) {
		
		// Make new Entry
		String row = "";
		if (entries().length > 0) row += "\n";
		row += userID + "\t" + email;

		// Write user-email pair to registration
		byte data[] = row.getBytes();
		Path p = Paths.get(REGISTER_PATH);
		try (OutputStream out = new BufferedOutputStream(
				Files.newOutputStream(p, StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
			out.write(data, 0, data.length);
		} catch (IOException x) {
			System.out.println("Error: Could not write to register file");
			System.err.println(x);
		}

		return true;
	}

	/**
	 * Return a list of registered entities
	 * @return empty list if there was an error creating/reading registration file
	 */
	private static String[] entries() {
		
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
			return new String[0];
		}
	}
	
	/**
	 * Get first Register Entry  associate with a particular column and value
	 * @param value
	 * @param index
	 * @return entry if found, null if not found
	 */
	private static String[] entryBy(String value, int index) {
		String[] entries = entries();
		for (String row : entries) {
			String[] entry = row.split("\t");
			if (entry.length == REGISTER_COLUMNS && index < REGISTER_COLUMNS) {
				if (ignoreCaseEquals(entry[index], value)) {
					// don't match emails with deactivated accounts
					if (index == 1) {
						if (!deactivated(entry[0])) {
							return entry;
						}
					} else {
						return entry;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Check if a given User ID has been deactivated (ignores case)
	 * @param userID
	 * @return true if inactive
	 */
	public static boolean deactivated(String userID) {
		String[] deactivated = deactivated();
		for (String deactivatedUserID : deactivated) {
			if (ignoreCaseEquals(deactivatedUserID, userID)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return a list of registered entities
	 * @return empty list if there was an error creating/reading registration file
	 */
	private static String[] deactivated() {
		
		// Check that register exists
		if (DEACTIVATED_FILE.exists() && DEACTIVATED_FILE.isFile()) {
			try {
				return Files.readString(Path.of(DEACTIVATED_PATH)).split("\n");
			} catch (IOException e) {
				System.out.println("The inactive user file is currupted");
				e.printStackTrace();
				return new String[0];
			}
		} else {
			return new String[0];
		}
	}
	
	/**
	 * Check if two strings are equal, ignoring case
	 * @param str1
	 * @param str2
	 * @return true if equal, ignoring case
	 */
	private static boolean ignoreCaseEquals(String str1, String str2) {
		String str1_lc = str1.toLowerCase();
		String str2_lc = str2.toLowerCase();
		return str1_lc.equals(str2_lc);
	}
}
