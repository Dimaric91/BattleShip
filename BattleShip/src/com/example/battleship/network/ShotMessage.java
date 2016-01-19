package com.example.battleship.network;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import com.example.battleship.Field;

public class ShotMessage extends BattleShipMessage {

	private Field targetField;
	private Field paddedField;
	
	public ShotMessage() {
	}
	
	public ShotMessage(Field field) {
		this.targetField = field;
	}
	
	public void setPaddedField(Field paddedField) {
		this.paddedField = paddedField;
	}
	
	public Field getPaddedField() {
		return paddedField;
	}
	
	public Field getTargetField() {
		return targetField;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(targetField);
		if (paddedField != null) {
			out.writeBoolean(true);
			out.writeObject(paddedField);
		} else {
			out.writeBoolean(false);
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		targetField = (Field) in.readObject();
		if (in.readBoolean()) {
			paddedField = (Field) in.readObject();
		}
		
	}

}
