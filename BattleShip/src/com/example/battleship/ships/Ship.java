package com.example.battleship.ships;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import com.example.battleship.FieldNotFound;
import com.example.battleship.FieldsMismatch;
import com.example.battleship.GameZone;
import com.example.battleship.MissingFields;
import com.example.battleship.SeaObject;
import com.example.battleship.ShipIsHitted;
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
	
	public void move(GameZone zone, List<Field> fields) throws MissingFields {
		if (fields.size() != size){
			throw new MissingFields();
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
	
	public void move(GameZone zone, Field head, int direction) throws FieldNotFound, MissingFields, ShipIsHitted {
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
			throw new ShipIsHitted(this.toString() + " is hitted");
		}
		if (!zone.isMove(freeFields,freeNeighbors)) {
			throw new MissingFields("Fields is busy");
		} 
		this.move(zone, lst);
	}
	
	public void destroy() {
		state = DEAD_STATE;
		for (Field field : fields) {
			field.removeObj();
			field.setState(Field.KILLED_SHIP_STATE);
		}
	}
	
	public Field hitOnMine() {
		LinkedList<Field> aliveFields = new LinkedList<>();
		Collections.copy(aliveFields, fields);
		for (Field field : fields) {
			if (field.getState() == Field.PADDED_SHIP_STATE) {
				aliveFields.remove(field);
			}
		}
		return aliveFields.get(new Random().nextInt(aliveFields.size()));
	}
	
	public int getSize() {
		return size;
	}
	
	@Override
	public void shotOnObject(Field field) {
		boolean isDead = true;
		for (Field field2 : fields) {
			if (field2.getState() != Field.PADDED_SHIP_STATE) {
				isDead = false;
			}
		}
		
		if (isDead) {
			state = DEAD_STATE;
			for (Field field2 : fields) {
				field2.setState(Field.KILLED_SHIP_STATE);
			}
			
			for (Field field2 : neighboirs) {
				field2.setState(Field.CHECKED_FIELD_STATE);
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.getClass().getSimpleName() + " at x = " + fields.get(0).getX()
				+ ", y = " + fields.get(0).getY());
		sb.append("\nneighbors = ");
		for (Field field : neighboirs) {
			sb.append(field);
		}
		return sb.toString();
	}

	public int getState() {
		return state;
	}
	
	public boolean isKilled() {
		return state == DEAD_STATE;
	}
	
}
