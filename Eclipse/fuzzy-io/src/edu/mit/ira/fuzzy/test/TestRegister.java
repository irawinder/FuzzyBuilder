package edu.mit.ira.fuzzy.test;

import edu.mit.ira.fuzzy.io.user.Register;
import edu.mit.ira.fuzzy.io.user.UserType;

public class TestRegister {
	
	public static void main(String[] args) throws Exception {
		
//		for (int i=0; i<1000; i++) {
//			Register.makeUser(i + "@email.com", UserType.ADMIN);
//		}
		
		Register.makeUser("ztest1@email.com", UserType.STUDY);
		Register.makeUser("ztest2@email.com", UserType.STUDY);
		Register.makeUser("ztest3@email.com", UserType.STUDY);
		
		Register.isActive("squidejqmnk");
		Register.isActive("squidEJQMnK");
		Register.isActive("squidejqmnr");
		Register.emailExists("jiw@email.com");
	}
}