package com.example.battleship;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import com.example.battleship.Field;
import com.example.battleship.exception.MissingFieldsException;
import com.example.battleship.ships.Ship;

public class Mine extends SeaObject {

	private boolean isDead;
	private Field paddedField;
	
	public Mine() {
	}
	
	public Mine(List<Field> fields) {
		super(fields);
		isDead = false;
	}
	
	@Override
	public void shotOnObject(Ship ship) {
		if (!isDead) {
			isDead = true;
			if (ship != null) {
				paddedField = ship.getAliveField();
				paddedField.shotOnField(null);
			}
		}
	}
	
	public Field getPaddedField() {
		return paddedField;
	}
	
	public void move(Field field) throws MissingFieldsException {
		ArrayList<Field> lst = new ArrayList<>();
		lst.add(field);
		if (!isMove(null, lst)) {
			throw new MissingFieldsException("Field is busy");
		}
		if (this.fields != null) {
			Field f = this.fields.remove(0);
			f.removeObj();	
		}
		field.setObj(this);
		this.fields = lst;
	}
	
	public boolean isMove(GameZone zon, List<Field> fields) {
		return fields.get(0).getState(false) == FieldState.EMPTY_STATE || this.fields != null && this.fields.get(0) == fields.get(0);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(fields.get(0));
		out.writeBoolean(isDead);
		
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		fields = new ArrayList<>();
		fields.add((Field) in.readObject());
		isDead = in.readBoolean();
	}
	
}
