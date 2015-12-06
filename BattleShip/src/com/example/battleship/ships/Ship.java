package com.example.battleship.ships;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.example.battleship.FieldState;
import com.example.battleship.GameZone;
import com.example.battleship.SeaObject;
import com.example.battleship.exception.FieldNotFoundException;
import com.example.battleship.exception.MissingFieldsException;
import com.example.battleship.exception.ShipIsHittedException;
import com.example.battleship.Field;

public abstract class Ship extends SeaObject {
	public final static int ALIVE_STATE = 0;
	public final static int HIT_STATE = 1;
	public final static int DEAD_STATE = 2;
	
	public final static int DIRECTION_UP = -2;
	public final static int DIRECTION_RIGHT = 1;
	public final static int DIRECTION_DOWN = 2;
	public final static int DIRECTION_LEFT = -1;
	
	protected int size;
	protected int state = ALIVE_STATE;
	//protected int direction = DIRECTION_UP;
	private List<Field> neighboirs;
	
	public Ship() {
	}
	
	public Ship(int size) {
		this.size = size;
	}
	
	public void move(GameZone zone, List<Field> fields) throws MissingFieldsException {
		if (fields.size() != size){
			throw new MissingFieldsException();
		}
		// TODO Fields in line????
		if (this.fields != null) {
			for (Field field : this.fields) {
				field.removeObj();
			}
		}
		for (Field field : fields) {
			field.setObj(this);
		}
		this.setFields(fields);
		this.neighboirs = zone.getNeighbors(fields);
	}
	
	public void move(GameZone zone, Field head, int direction) throws FieldNotFoundException, MissingFieldsException, ShipIsHittedException {
		ArrayList<Field> lst = new ArrayList<>();
		for (int i = 0, x = head.getX(), y = head.getY(); i < size; i++, x += direction / 2, 
				y += direction % 2) {
			lst.add(zone.getField(x, y));
		}
		LinkedList<Field> freeFields = new LinkedList<>(lst);
		LinkedList<Field> freeNeighbors = new LinkedList<>(zone.getNeighbors(freeFields));
		if (this.fields != null) {
			for (Field field : this.fields) {
				freeFields.remove(field);
				freeNeighbors.remove(field);
			}
			for (Field field : zone.getNeighbors(this.fields)) {
				freeNeighbors.remove(field);
			}
		}
		if (state != ALIVE_STATE) {
			throw new ShipIsHittedException(this.toString() + " is hitted");
		}
		if (!zone.isMove(freeFields,freeNeighbors)) {
			throw new MissingFieldsException("Fields is busy");
		} 
		this.move(zone, lst);
	}
	
	public void destroy() {
		state = DEAD_STATE;
		for (Field field2 : fields) {
			field2.setState(FieldState.KILLED_SHIP_STATE);
		}
		
		for (Field field2 : neighboirs) {
			field2.setState(FieldState.CHECKED_FIELD_STATE);
		}
	}
	
	public Field getAliveField() {
		LinkedList<Field> aliveFields = new LinkedList<>(fields);
		//Collections.copy(aliveFields, fields);
		for (Field field : fields) {
			if (field.getState() == FieldState.PADDED_SHIP_STATE) {
				aliveFields.remove(field);
			}
		}
		return aliveFields.get(new Random().nextInt(aliveFields.size()));
	}
	
	public int getSize() {
		return size;
	}
	
	private boolean isDead() {
		for (Field field : fields) {
			if (field.getState() != FieldState.PADDED_SHIP_STATE) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void shotOnObject(Ship ship) {
		if (isDead()) {
			destroy();
		} else {
			state = HIT_STATE;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.getClass().getSimpleName() + " at x = " + fields.get(0).getX()
				+ ", y = " + fields.get(0).getY());
		//sb.append("\nneighbors = ");
		//for (Field field : neighboirs) {
		//	sb.append(field);
		//}
		return sb.toString();
	}

	public int getState() {
		return state;
	}
	
	public boolean isKilled() {
		return state == DEAD_STATE;
	}
	
}
