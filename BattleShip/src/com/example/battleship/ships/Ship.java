package com.example.battleship.ships;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.example.battleship.FieldState;
import com.example.battleship.GameZone;
import com.example.battleship.SeaObject;
import com.example.battleship.exception.FieldNotFoundException;
import com.example.battleship.exception.MissingFieldsException;
import com.example.battleship.exception.ShipIsHittedException;
import com.example.battleship.Direction;
import com.example.battleship.Field;

public abstract class Ship extends SeaObject {
	public final static int ALIVE_STATE = 0;
	public final static int HIT_STATE = 1;
	public final static int DEAD_STATE = 2;
	
	protected int size;
	protected int state = ALIVE_STATE;
	private List<Field> neighboirs;
	private Direction direction;
	
	public Ship() {
	}
	
	public Ship(int size) {
		this.size = size;
	}
	
	@Override
	public boolean isMove(GameZone zone, List<Field> fields) {
		LinkedList<Field> newFields = new LinkedList<>(fields);
		LinkedList<Field> newNeighbors = new LinkedList<>(zone.getNeighbors(newFields));
		if (this.fields != null) {
			newFields.removeAll(this.fields);
			newNeighbors.removeAll(this.fields);
			newNeighbors.removeAll(zone.getNeighbors(this.fields));
		}
		return zone.isMove(newFields,newNeighbors);
	}
	
	public void move(GameZone zone, List<Field> fields) throws MissingFieldsException, ShipIsHittedException {
		if (fields.size() != size){
			throw new MissingFieldsException();
		}
		if (state != ALIVE_STATE) {
			throw new ShipIsHittedException(this.toString() + " is hitted");
		}
		
		
		if (!isMove(zone, fields)) {
			throw new MissingFieldsException("Fields is busy");
		} 
		
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
	
	public void move(GameZone zone, Field head, Direction direction) throws FieldNotFoundException, MissingFieldsException, ShipIsHittedException {
		List<Field> lst = zone.getFields(head, size, direction);
		this.move(zone, lst);
		this.direction = direction;
	}
	
	public void destroy() {
		if (state == DEAD_STATE) {
			return;
		}
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
		for (Field field : fields) {
			if (field.getState(false) == FieldState.PADDED_SHIP_STATE) {
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
			if (field.getState(false) == FieldState.SHIP_STATE) {
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
		return sb.toString();
	}

	public int getState() {
		return state;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
}
