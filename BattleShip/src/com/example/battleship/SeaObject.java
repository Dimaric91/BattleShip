package com.example.battleship;

import java.io.Externalizable;
import java.io.Serializable;
import java.util.List;
import com.example.battleship.Field;
import com.example.battleship.ships.Ship;

public abstract class SeaObject implements Externalizable {
	
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
