package com.example.battleship.network;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class FailMessage extends BattleShipMessage {

	private String reason;
	
	public FailMessage() {
	}
	
	public FailMessage(String reason) {
		this.reason = reason;
	}
	
	public String getReason() {
		return reason;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeUTF(reason);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		reason = in.readUTF();
		
	}

}
