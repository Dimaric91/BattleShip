package com.example.battleship.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.example.battleship.exception.CannotCreateMessage;

public class MessageFactory {
	private static List<Class<? extends BattleShipMessage>> supportedMessages;
	
	static {
		supportedMessages = new ArrayList<>();
		supportedMessages.add(ConnectMessage.class);  	//code = 0
		supportedMessages.add(FailMessage.class);		//code = 1
		supportedMessages.add(OptionMessage.class);		//code = 2
		supportedMessages.add(ReadyMessage.class);		//code = 3
		supportedMessages.add(ShotMessage.class);		//code = 4
	}
	
	private static BattleShipMessage createMessageInstance(int messageCode) throws CannotCreateMessage {
		try {
			Constructor<? extends BattleShipMessage> constructor = supportedMessages.get(messageCode).getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new CannotCreateMessage();
		}
	}
	
	public static BattleShipMessage readMessage(InputStream in) throws IOException, CannotCreateMessage {
		ObjectInputStream ois = new ObjectInputStream(in);
		int messageCode = ois.readInt();
		BattleShipMessage message = createMessageInstance(messageCode);
		try {
			message.readExternal(ois);
			System.out.println(message.getClass().getName());
		} catch (ClassNotFoundException e) {
			throw new CannotCreateMessage("Class not found");
		}
		return message;
	}
	
	public static void writeMessage(OutputStream out, BattleShipMessage message) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(out);
		int messageCode = supportedMessages.indexOf(message.getClass());
		oos.writeInt(messageCode);
		message.writeExternal(oos);
		oos.flush();
	}
}
