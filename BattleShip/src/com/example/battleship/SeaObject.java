package com.example.battleship;

import java.util.List;
import com.example.battleship.Field;
import com.example.battleship.ships.Ship;

public abstract class SeaObject {
	protected List<Field> fields;
	
	public SeaObject() {
	}
	
	public SeaObject(List<Field> fields) {
		setFields(fields);
	}
	
	public List<Field> getFields() {
		return fields;
	}
	
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
	public abstract void shotOnObject(Ship ship);
	
	public abstract boolean isMove(GameZone zone, List<Field> fields);
}
