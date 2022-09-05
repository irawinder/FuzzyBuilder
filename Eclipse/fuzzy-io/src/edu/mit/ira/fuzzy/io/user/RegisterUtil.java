package edu.mit.ira.fuzzy.io.user;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class RegisterUtil {
	
	private static int CODE_LENGTH = 6;
	
	// Purposely excluded the following, since they can be confused with each other:
	// '1', 'I', 'L', '0', 'O' 
	private static char[] CODE_CHARS = { 
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 
		'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 
		'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', 
		'3', '4', '5', '6', '7', '8', '9'
	};
	
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
	 * Parse the study prefix of a userID
	 * @param userID
	 * @return prefix as String; null if fails to find prefix
	 */
	public static UserPrefixStudy parseStudyPrefix(String userID) {
		if (userID.length() > RegisterUtil.CODE_LENGTH) {
			String prefix = userID.substring(0, userID.length() - RegisterUtil.CODE_LENGTH).toUpperCase();
			return UserPrefixStudy.valueOf(prefix);
		} else {
			return null;
		}
	}
	
	/**
	 * Parse the study prefix of a userID
	 * @param userID
	 * @return prefix as String; null if fails to find prefix
	 */
	public static UserPrefixAdmin parseAdminPrefix(String userID) {
		if (userID.length() > RegisterUtil.CODE_LENGTH) {
			String prefix = userID.substring(0, userID.length() - RegisterUtil.CODE_LENGTH).toUpperCase();
			return UserPrefixAdmin.valueOf(prefix);
		} else {
			return null;
		}
	}
	
	/**
	 * Check if two strings are equal, ignoring case
	 * @param str1
	 * @param str2
	 * @return true if equal, ignoring case
	 */
	public static boolean ignoreCaseEquals(String str1, String str2) {
		if (str1 == null || str2 == null) {
			System.out.println("Error: Cannot compare null Strings");
			return false;
		}
		String str1_lc = str1.toLowerCase();
		String str2_lc = str2.toLowerCase();
		return str1_lc.equals(str2_lc);
	}
	
	/**
	 * Return a list of rows in a text file as String array
	 * @return array of rows as Strings; otherwise empty list if there was an error creating/reading on file
	 */
	public static String[] getRows(String filePath) {
		
		// Check that register exists
		File f = new File(filePath);
		if (f.exists() && f.isFile()) {
			try {
				return Files.readString(Path.of(filePath)).split("\n");
			} catch (IOException e) {
				System.out.println("The file at " + filePath + " is currupted");
				e.printStackTrace();
				return new String[0];
			}
		} else {
			return new String[0];
		}
	}
	
	/**
	 * Checks if email is of format "email@domain.name"
	 * @param email
	 * @return true if valid
	 */
	public static boolean isValidEmail(String email) {
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
	public static String makeCode() {
		Random r = new Random();
		String code = "";
		for (int i=0; i<CODE_LENGTH; i++) {
			int charIndex = r.nextInt(CODE_CHARS.length);
			code += CODE_CHARS[charIndex];
		}
		return code;
	}
}
