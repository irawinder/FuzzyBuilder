package edu.mit.ira.fuzzy.test;

import java.util.HashMap;
import java.util.Map;

import edu.mit.ira.fuzzy.server.user.Register;
import edu.mit.ira.fuzzy.server.user.UserType;

// IMPORTANT! Delete 'data/users/register.tsv' 'data/users/deactivated.tsv' before running

public class TestRegister {
	
	public static void main(String[] args) throws Exception {
		
		HashMap<String, Boolean> testMap = new HashMap<String, Boolean>();
		
		// Add permaEntries to register
		Register.init();
		
		// user should already exist
		testMap.put("Test 1", Register.makeUser("foo@bar.com", UserType.ADMIN) == null);
		
		// Make user and get it's email address
		String email = "test@bar.com";
		String userID1 = Register.makeUser(email, UserType.ADMIN);
		testMap.put("Test 2", Register.getEmail(userID1).equals(email));
		
		// Trying to make user with same user address should fail
		String userID2 = Register.makeUser(email, UserType.ADMIN);
		testMap.put("Test 3", userID2 == null);
		
		// deactivate user and confirm
		Register.deactivateUser(userID1);
		testMap.put("Test 4", Register.isDeactivated(userID1));
		
		// Now that userID1 is deactivated, should be able to make a new user with same email address
		String userID3 = Register.makeUser(email, UserType.ADMIN);
		testMap.put("Test 5", Register.isActiveEmail(email));

		// Now that userID1 is deactivated, should be able to make a new user with same email address
		testMap.put("Test 6", Register.getEmail(userID3).equals(email));
		
		for (Map.Entry<String, Boolean> test : testMap.entrySet()) {
			System.out.println(test.getKey() + ": Pass = " + test.getValue());
		}
	}
}