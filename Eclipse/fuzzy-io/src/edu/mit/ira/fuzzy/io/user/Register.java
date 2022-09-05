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
	
	private static String USERS_PATH = Server.RELATIVE_DATA_PATH + File.separator + "users" + File.separator;
	private static String REGISTER_PATH = USERS_PATH + "register.tsv";
	private static String PERMA_REGISTER_PATH = USERS_PATH + "perma_register.tsv";
	private static String DEACTIVATED_PATH = USERS_PATH + "deactivated.tsv";
	
	// Registry Entry row is of form: {userID, email}
	private static int REGISTER_COLUMNS = 2;
	
	/**
	 * Check if a user ID is already registered and active (ignores case)
	 * @param userID
	 * @return true if registered AND not deactivate; otherwise false
	 */
	public static boolean isActive(String userID) {
		if (getEntryBy(userID, 0) != null) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Check if a given User ID has been explicitly deactivated (ignores case)
	 * @param userID
	 * @return true if inactive
	 */
	public static boolean isDeactivated(String userID) {
		String[] deactivated = getDeactivatedUsers();
		for (String deactivatedUserID : deactivated) {
			if (RegisterUtil.ignoreCaseEquals(deactivatedUserID, userID)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get email associated with this user; returns null if not found
	 * @param userID
	 * @return email as string; null if not found
	 */
	public static String getEmail(String userID) {
		return getEntryBy(userID, 0)[1];
	}
	
	/**
	 * Get active user associated with this email; returns null if not found
	 * @param userID
	 * @return email as string; null if not found
	 */
	public static String getUser(String email) {
		return getEntryBy(email, 1)[0];
	}
	
	/**
	 * Check if a email is already registered
	 * @param userID
	 * @return true if registered
	 */
	public static boolean isActiveEmail(String email) {
		return !isUniqueEmail(email);
	}
	
	/**
	 * Initialize main register with permanent users
	 */
	public static void init() {
		if (getEntries().length == 0) {
			String[] permaEntries = getPermaEntries();
			for (String row : permaEntries) {
				String[] permaEntry = row.split("\t");
				if (permaEntry.length == REGISTER_COLUMNS) {
					addEntry(permaEntry[0], permaEntry[1]);
				}
			}
		}
	}
	
	/**
	 * Add email address to the registry and assigns them a new user id. If email address is already registered, prior registration is not overridden
	 * @param userID
	 * @param email
	 * @return userID assigned to this email; otherwise null
	 */
	public static String makeUser(String email, UserType type) {

		// Check for duplicate email address
		if (!RegisterUtil.isValidEmail(email)) {
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
			
			String code = RegisterUtil.makeCode();
			userID = prefix + code;
			valid = isUniqueUser(userID);
		}
		
		// Add new user
		addEntry(userID, email);
		return userID;
	}
	
	/**
	 * Write user to register of deactivated users
	 * @param userID
	 * @return true if successful; false if user is already deactivated
	 */
	public static boolean deactivateUser(String userID) {
		
		// Can't deactivate the user if it's already deactivated
		if (!isDeactivated(userID)) {
			
			// Make new Entry
			String row = "";
			if (getDeactivatedUsers().length > 0) row += "\n";
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
	 * Add a new user-email pair to the register, checking for duplicates
	 * @return true if successful; false if failure
	 */
	private static boolean addEntry(String userID, String email) {
		
		// Make new Entry
		String row = "";
		if (getEntries().length > 0) row += "\n";
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
	 * Check if the candidate email is unique (ignores case)
	 * @param userID
	 * @return true if unique (no duplicates found)
	 */
	private static boolean isUniqueEmail(String email) {
		
		if(getEntryBy(email, 1) != null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Check if the candidate username is unique (ignores case)
	 * @param userID
	 * @return true if unique (no duplicates found)
	 */
	private static boolean isUniqueUser(String userID) {
		if(getEntryBy(userID, 0) != null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Get first Register Entry associated with a particular column and value
	 * @param value
	 * @param index
	 * @return entry if found, null if not found
	 */
	private static String[] getEntryBy(String value, int index) {
		String[] entries = getEntries();
		for (String row : entries) {
			String[] entry = row.split("\t");
			if (entry.length == REGISTER_COLUMNS && index < REGISTER_COLUMNS) {
				if (RegisterUtil.ignoreCaseEquals(entry[index], value)) {
					
					// Getting entry by email
					if (index == 1) {
						
						// ignore existing emails associated with deactivated User IDs
						if (!isDeactivated(entry[0])) {
							return entry;
						}
						
					// Getting Entry by User ID
					} else {
						return entry;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Return a list of registered entities
	 * @return empty list if there was an error creating/reading registration file
	 */
	private static String[] getEntries() {
		return RegisterUtil.getRows(REGISTER_PATH);
	}
	
	/**
	 * Return a list of permanently registered entities
	 * @return empty list if there was an error creating/reading registration file
	 */
	private static String[] getPermaEntries() {
		return RegisterUtil.getRows(PERMA_REGISTER_PATH);
	}
	
	/**
	 * Return a list of registered entities
	 * @return empty list if there was an error creating/reading registration file
	 */
	private static String[] getDeactivatedUsers() {
		return RegisterUtil.getRows(DEACTIVATED_PATH);
	}
}
