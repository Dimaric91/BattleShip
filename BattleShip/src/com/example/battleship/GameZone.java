package com.example.battleship;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.example.battleship.Field;
import com.example.battleship.exception.FieldNotFoundException;

public class GameZone implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	
	public Field getField(int x, int y) throws FieldNotFoundException {
		if(x < 0 || x >= fields.length || y < 0 || y >= fields.length) {
			throw new FieldNotFoundException("Field on " + x + ", " + y + " not found");
		} 
		return fields[x][y];
	}
	
	public Field getField(Field start, Direction direction) throws FieldNotFoundException {
		int x = start.getX() + direction.getValue() / 2;
		int y = start.getY() + direction.getValue() % 2;
		return getField(x, y);
	}
	
	public boolean isMove(List<Field> shipField, List<Field> neighborField) {
		for (Field field : shipField) {
			if (field.getState(false) != FieldState.EMPTY_STATE) {
				return false;
			}
		}
		for (Field field : neighborField) {
			if(field.getState(false).isShip()) {
				return false;
			}	
		}
		return true;
	}
	
	public boolean isMove(Field start, int size, Direction direction) {
		try {
			List<Field> lst = getFields(start, size, direction);
			return isMove(lst, getNeighbors(lst));
		} catch (FieldNotFoundException e) {
			return false;
		}
	}
	
	public List<Field> getFields(Field head, int size, Direction direction) throws FieldNotFoundException {
		ArrayList<Field> lst = new ArrayList<>();
		Field start = head;
		lst.add(start);
		for (int i = 0; i < size - 1; i++) {
			start = getField(start, direction);
			lst.add(start);
		}
		return lst;
	}
	
	public List<Field> getNeighbors(List<Field> shipField) {
		HashSet<Field> ret = new HashSet<>();
		for (Field field : shipField) {
			for (int i = field.getX() - 1; i <= field.getX() + 1; i++) {
				for (int j = field.getY() - 1; j <= field.getY() + 1; j++) {
						try {
							ret.add(getField(i, j));
						} catch (FieldNotFoundException e) {
						}
				}
			}
							
		}
		ret.removeAll(shipField);
		return new ArrayList<>(ret);
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
				result.append(fields[i][j].forEnemy() + " ");
			}
			result.append("\n");
		}
		return result.toString();
	}
}
