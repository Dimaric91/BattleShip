package com.example.battleship.players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.example.battleship.Controller;
import com.example.battleship.Direction;
import com.example.battleship.Field;
import com.example.battleship.FieldState;
import com.example.battleship.exception.FieldNotFoundException;
import com.example.battleship.ships.Ship;

public class AIPlayer extends Player {

	private List<Field> poolOfNext;	
	private Field firstShotField = null;
	private Direction currentDirection;
	private Field currentShot;
	
	public AIPlayer(String name, Properties property) {
		super(name, property);
		RandomMove();
	}

	@Override
	public boolean shot(Ship ship) {
		//TODO Add mine logic
		Random rnd = new Random();
		if (poolOfNext == null) {
			poolOfNext = new ArrayList<>();
			Field[][] tmp = enemy.getZone().getFields();
			for (Field[] fields : tmp) {
				for (Field field : fields) {
					poolOfNext.add(field);
				}
			}
		}
		
		Iterator<Field> it = poolOfNext.iterator();
		while (it.hasNext()) {
			Field f = it.next();
			if (f.getState(false) != FieldState.EMPTY_STATE && f.getState(false) != FieldState.SHIP_STATE &&
					f.getState(false) != FieldState.MINE_STATE) {
				it.remove();
			}
		}
		
		if (firstShotField != null) {
			try {
				currentShot = enemy.getZone().getField(currentShot, currentDirection);
			} catch (FieldNotFoundException e) {
				currentShot = null;
			}
			if (currentShot == null || !poolOfNext.remove(currentShot)) { 
				ArrayList<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));
				Iterator<Direction> dirIt = directions.iterator();
				while(dirIt.hasNext()) {
					currentDirection = dirIt.next();
					try {
						currentShot = enemy.getZone().getField(firstShotField, currentDirection);
					} catch (FieldNotFoundException e) {
						currentShot = null;
					}
					if (poolOfNext.remove(currentShot)) {
						break;
					}
					dirIt.remove();
				}
				if(directions.isEmpty()) {
					firstShotField = null;
					currentShot = null;
				}
			}
			
		}
		if (firstShotField == null || currentShot == null)
			currentShot = poolOfNext.remove(rnd.nextInt(poolOfNext.size()));
		System.out.println(" " + Controller.rb.getString("shotOn") + " x = " + currentShot.getX() + ", y = " + currentShot.getY());
		boolean ret;
		if (ret = currentShot.shotOnField(ship)) {
			if (firstShotField == null) {
				firstShotField = currentShot;
				currentDirection = Direction.values()[rnd.nextInt(Direction.values().length)];
			}
		} else {
			if (firstShotField != null) {
				currentDirection = currentDirection.getOpposite();
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		return ret;
	}

	@Override
	public void move(Ship ship) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Ship getShip() {
		return ships.get(new Random().nextInt(ships.size()));
	}

}
