package com.example.battleship;

import java.util.LinkedList;
import java.util.List;

import com.example.battleship.Field;
import com.example.battleship.exception.MissingFieldsException;
import com.example.battleship.ships.Ship;

public class Mine extends SeaObject {

	private boolean isDead = false;
	
	public Mine() {
	}
	
	public Mine(List<Field> fields) {
		super(fields);
	}
	
	@Override
	public void shotOnObject(Ship ship) {
		if (!isDead) {
			isDead = true;
			ship.getAliveField().shotOnField(null);
		}
	}
	
	public void move(Field field) throws MissingFieldsException {
		if (!isMove(field)) {
			throw new MissingFieldsException("Field is busy");
		}
		if (fields == null) {
			fields = new LinkedList<>();
		} else {
			Field f = fields.remove(0);
			f.removeObj();	
		}
		field.setObj(this);
		fields.add(field);
	}
	
	public boolean isMove(Field field) {
		return field.getState(false) == FieldState.EMPTY_STATE || fields != null && fields.get(0) == field;
	}
}
