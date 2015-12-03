package com.example.battleship;

import java.util.List;

import com.example.battleship.Field;

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
	
	protected void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
	public abstract void shotOnObject(Field field);
}
