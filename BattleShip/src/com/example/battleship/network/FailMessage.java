package com.example.battleship.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;

public class FailMessage extends BattleShipMessage {

	private String reason;
	
	public FailMessage() {
	}
	
	public FailMessage(String reason) {
		this.reason = reason;
	}
	
//	@Override
//	public void read(InputStream in) throws IOException {
//		DataInputStream dis = new DataInputStream(in);
//		reason = dis.readUTF();
//	}
//
//	@Override
//	public void write(OutputStream out) throws IOException {
//		DataOutputStream dos = new DataOutputStream(out);
//		dos.writeUTF(reason);
//	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeUTF(reason);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		reason = in.readUTF();
		
	}

}
