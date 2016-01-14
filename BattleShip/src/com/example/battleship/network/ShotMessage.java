package com.example.battleship.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

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
	public void read(InputStream in) throws IOException {
		ObjectInputStream ois = new ObjectInputStream(in);
		try {
			targetField = (Field) ois.readObject();
			if (ois.readBoolean()) {
				paddedField = (Field) ois.readObject();
			}
		} catch (ClassNotFoundException e) {
			throw new IOException("Class not found", e);
		}
	}

	@Override
	public void write(OutputStream out) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(targetField);
		if (paddedField != null) {
			oos.writeBoolean(true);
			oos.writeObject(paddedField);
		} else {
			oos.writeBoolean(false);
		}
	}

}
