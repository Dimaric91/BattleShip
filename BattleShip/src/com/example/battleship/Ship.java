package com.example.battleship;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.example.battleship.exception.FieldNotFoundException;
import com.example.battleship.exception.MissingFieldsException;
import com.example.battleship.exception.ShipIsHittedException;

public class Ship extends SeaObject {
	private static int nextId = 1;
	
	protected int id;
	protected int size;
	protected ShipState state = ShipState.ALIVE_STATE;
	private List<Field> neighboirs;
	private Direction direction;
	
	public Ship() {
		id = nextId++;
	}
	
	public Ship(int size) {
		this();
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
			throw new MissingFieldsException(fields.size() + " != " + size);
		}
		
		if (!isMove(zone, fields)) {
			throw new MissingFieldsException("Fields is busy");
		} 
		
		freeFields();
		for (Field field : fields) {
			field.setObj(this);
		}
		
		this.setFields(fields);
		this.neighboirs = zone.getNeighbors(fields);
	}
	
	public void freeFields() throws ShipIsHittedException {
		if (state != ShipState.ALIVE_STATE) {
			throw new ShipIsHittedException();
		}
		if (this.fields != null) {
			for (Field field : this.fields) {
				field.removeObj();
			}
		}
		fields = null;
		neighboirs = null;
	}
	
	public void move(GameZone zone, Field head, Direction direction) throws FieldNotFoundException, MissingFieldsException, ShipIsHittedException {
		List<Field> lst = zone.getFields(head, size, direction);
		this.move(zone, lst);
		this.direction = direction;
	}
	
	public void destroy() {
		if (state == ShipState.DEAD_STATE) {
			return;
		}
		state = ShipState.DEAD_STATE;
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
			state = ShipState.HIT_STATE;
		}
	}

	public ShipState getState() {
		return state;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		id = in.readInt();
		size = in.readInt();
		state = (ShipState) in.readObject();
		fields = new LinkedList<>();
		for (int i = 0 ; i < size; i++) {
			fields.add((Field) in.readObject());
		}
		direction = (Direction) in.readObject();
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(id);
		out.writeInt(size);
		out.writeObject(state);
		for (Field f : fields) {
			out.writeObject(f);
		}
		out.writeObject(direction);
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		return id == ((Ship)obj).getId();
	}
	
	@Override
	public int hashCode() {
		return id;
	}
}
