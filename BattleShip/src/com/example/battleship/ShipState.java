package com.example.battleship;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public enum ShipState implements Externalizable {
	ALIVE_STATE(0),
	HIT_STATE(1),
	DEAD_STATE(2);
	
	private int value;
	
	private ShipState(int value) {
		this.value = value;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		value = in.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(value);
	}
}
