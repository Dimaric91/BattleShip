package com.example.battleship.players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.example.battleship.Direction;
import com.example.battleship.Field;
import com.example.battleship.FieldState;
import com.example.battleship.ships.Ship;

public class AIPlayer extends Player {

	private List<Field> listOfNextShots;
	private Field firstShotField = null;
	private Direction direction;
	private Field currentShot;
	
	public AIPlayer() throws Exception {
		super();
	}

	public AIPlayer(int zoneSize, int mineCount, int[] shipCount) throws Exception {
		super(zoneSize, mineCount, shipCount);
		RandomMove();
	}

	@Override
	public boolean shot(Ship ship) {
		//TODO Add mine logic
		Random rnd = new Random();
		if (listOfNextShots == null) {
			listOfNextShots = new ArrayList<>();
			Field[][] tmp = enemy.getZone().getFields();
			for (Field[] fields : tmp) {
				for (Field field : fields) {
					listOfNextShots.add(field);
				}
			}
		}
		
		Iterator<Field> it = listOfNextShots.iterator();
		while (it.hasNext()) {
			Field f = it.next();
			if (f.getState() != FieldState.EMPTY_STATE && f.getState() != FieldState.SHIP_STATE &&
					f.getState() != FieldState.MINE_STATE) {
				it.remove();
			}
		}
		
		if (firstShotField != null) {
			currentShot = enemy.getZone().getField(currentShot, direction);
			if (currentShot == null || !listOfNextShots.remove(currentShot)) { 
				ArrayList<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));
				Iterator<Direction> dirIt = directions.iterator();
				while(dirIt.hasNext()) {
					direction = dirIt.next();
					currentShot = enemy.getZone().getField(firstShotField, direction);
					if (listOfNextShots.remove(currentShot)) {
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
			currentShot = listOfNextShots.remove(rnd.nextInt(listOfNextShots.size()));
		System.out.println("AI shot on x = " + currentShot.getX() + ", y = " + currentShot.getY());
		if (currentShot.shotOnField(ship)) {
			if (firstShotField == null) {
				firstShotField = currentShot;
				direction = Direction.values()[rnd.nextInt(Direction.values().length)];
			}
			return true;
		} else {
			if (firstShotField != null) {
				direction = direction.getOpposite();
			}
			return false;
		}
		
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
