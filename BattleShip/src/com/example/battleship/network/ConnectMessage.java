package com.example.battleship.network;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ConnectMessage extends BattleShipMessage {

	private String username;
	
	public ConnectMessage() {
	}
	
	public ConnectMessage(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeUTF(username);
		
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		username = in.readUTF();
	}
}
