package com.example.battleship;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.example.battleship.Field;

public class GameZone {
	private Field[][] fields;
	
	public GameZone() {
		this(10);
	}
	
	public GameZone(int size) {
		fields = new Field[size][size];
		
		for (int i = 0; i < fields.length; i++) {
			for (int j = 0; j < fields.length; j++) {
				fields[i][j] = new Field(i, j);
			}
		}
	}
	
	public Field[][] getFields() {
		return fields;
	}
	
	public Field getField(int x, int y) throws FieldNotFound {
		if(x < 0 || x >= fields.length || y < 0 || y >= fields.length) {
			throw new FieldNotFound("Field on " + x + ", " + y + " not found");
		} 
		return fields[x][y];
	}
	
	public Field getField(Field start, Direction direction) {
		int x = start.getX() + direction.getValue() / 2;
		int y = start.getY() + direction.getValue() % 2;
		try {
			return getField(x, y);
		} catch (FieldNotFound e) {
			return null;
		}
	}
	
	public boolean isMove(List<Field> shipField, List<Field> neighborField) {
		for (Field field : shipField) {
			if (field.getState() != FieldState.EMPTY_STATE) {
				return false;
			}
		}
		for (Field field : neighborField) {
			if(field.getState().isShip()) {
				return false;
			}	
		}
		return true;
	}
	
	public List<Field> getNeighbors(List<Field> shipField) {
		LinkedList<Field> lst = new LinkedList<>();
		for (Field field : shipField) {
			for (int i = field.getX() - 1; i <= field.getX() + 1; i++) {
				for (int j = field.getY() - 1; j <= field.getY() + 1; j++) {
						try {
							lst.add(getField(i, j));
						} catch (FieldNotFound e) {
						}
				}
			}
							
		}
		for (Field field : shipField) {
			while(lst.remove(field));
		}
		return lst;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("x\\y");
		for (int i = 0; i < fields.length; i++) {
			result.append(i + " ");
		}
		result.append("\n");
		for (int i = 0; i < fields.length + 1; i++) {
			result.append("--");
		}
		result.append("\n");
		for (int i = 0; i < fields.length; i++) {
			result.append(i + " |");
			for (int j = 0; j < fields.length; j++) {
				result.append(fields[i][j] + " ");
			}
			result.append("\n");
		}
		return result.toString();
	}
	
	
	public int getSize() {
		return fields.length;
	}

	public String forEnemy() {
		StringBuilder result = new StringBuilder();
		result.append("x\\y");
		for (int i = 0; i < fields.length; i++) {
			result.append(i + " ");
		}
		result.append("\n");
		for (int i = 0; i < fields.length + 1; i++) {
			result.append("--");
		}
		result.append("\n");
		for (int i = 0; i < fields.length; i++) {
			result.append(i + " |");
			for (int j = 0; j < fields.length; j++) {
				result.append(fields[i][j].toEnemy() + " ");
			}
			result.append("\n");
		}
		return result.toString();
	}
}
