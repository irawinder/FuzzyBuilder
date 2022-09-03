package edu.mit.ira.fuzzy.test;

import edu.mit.ira.fuzzy.io.user.Register;

public class TestRegister {
	
	public static void main(String[] args) throws Exception {
		
//		for (int i=0; i<1000; i++) {
//			Register.makeUser(i + "@email.com", "admin");
//		}
		
		Register.makeUser("ztest1@email.com", "study");
		Register.makeUser("ztest2@email.com", "study");
		Register.makeUser("ztest3@email.com", "study");
		
		Register.userExists("squidejqmnk");
		Register.userExists("squidEJQMnK");
		Register.userExists("squidejqmnr");
		Register.emailExists("jiw@email.com");
	}
}