package com.example.battleship;

import java.util.ArrayList;
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
}
