package com.example.battleship.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.example.battleship.exception.CannotCreateMessage;

public class MessageFactory {
	private static List<Class<? extends BattleShipMessage>> supportedMessages;
	
	static {
		supportedMessages = new ArrayList<>();
		supportedMessages.add(ConnectMessage.class);  	//0
		supportedMessages.add(FailMessage.class);		//1
		supportedMessages.add(OptionMessage.class);		//2
		supportedMessages.add(ReadyMessage.class);		//3
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
		DataInputStream dis = new DataInputStream(in);
		int messageCode = dis.readInt();
		BattleShipMessage message = createMessageInstance(messageCode);
		message.read(in);
		return message;
	}
	
	public static void writeMessage(OutputStream out, BattleShipMessage message) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);
		int messageCode = supportedMessages.indexOf(message.getClass());
		dos.writeInt(messageCode);
		message.write(out);
		dos.flush();
	}
}
