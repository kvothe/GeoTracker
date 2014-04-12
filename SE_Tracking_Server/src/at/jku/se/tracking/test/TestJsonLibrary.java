package at.jku.se.tracking.test;

import java.rmi.MarshalException;

import at.jku.se.tracking.messages.MsgLogin;
import at.jku.se.tracking.messages.MsgRegister;
import at.jku.se.tracking.messages.serialization.AMessage;
import at.jku.se.tracking.messages.serialization.InvalidMessageException;
import at.jku.se.tracking.messages.serialization.MarshallingService;

public class TestJsonLibrary {

	public static void main(String[] args) {
		//testMsgLogin();
		testMsgRegistration();

	}

	private static void testMsgLogin() {
		MsgLogin login = new MsgLogin("user", "pwd");
		String json = MarshallingService.toJSON(login);
		System.out.println(json);
		try {
			AMessage m = MarshallingService.fromJSON(json);
			json = MarshallingService.toJSON(m);
			System.out.println(json);
		} catch (InvalidMessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void testMsgRegistration() {
		MsgRegister registration = new MsgRegister("user", "pwd", false);
		String json = MarshallingService.toJSON(registration);
		System.out.println(json);
		try {
			AMessage m = MarshallingService.fromJSON(json);
			json = MarshallingService.toJSON(m);
			System.out.println(json);
		} catch (InvalidMessageException e) {
			e.printStackTrace();
		}

	}

}
