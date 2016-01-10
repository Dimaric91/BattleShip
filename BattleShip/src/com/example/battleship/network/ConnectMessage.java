package com.example.battleship.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.net.DatagramPacket;

public class ConnectMessage extends BattleShipMessage {

	private String username;
	
	public ConnectMessage() {
	}
	
	public ConnectMessage(String username) {
		this.username = username;
	}

	@Override
	public void read(InputStream in) throws IOException {
		DataInputStream dis = new DataInputStream(in);
		username = dis.readUTF();
	}

	@Override
	public void write(OutputStream out) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);
		dos.writeUTF(username);
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
}
