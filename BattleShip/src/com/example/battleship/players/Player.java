package com.example.battleship.players;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.example.battleship.FieldNotFound;
import com.example.battleship.GameZone;
import com.example.battleship.Mine;
import com.example.battleship.MissingFields;
import com.example.battleship.ShipIsHitted;
import com.example.battleship.ships.Aerocarrier;
import com.example.battleship.ships.Battleship;
import com.example.battleship.ships.Cruiser;
import com.example.battleship.ships.Destroyer;
import com.example.battleship.ships.Ship;

public abstract class Player {
	protected GameZone zone;
	protected List<Mine> mines;
	protected List<Ship> ships;
	protected Player enemy;
	
	public Player() throws Exception {
		this(10);
	}
	
	public Player(int zoneSize) throws Exception {
		int[] shipCount = {1, 2, 3, 4};
		initialize(zoneSize, 2, shipCount);
	}
	
	public Player(int zoneSize, int mineCount, int[] shipCount) throws Exception {
		initialize(zoneSize, mineCount, shipCount);
	}

	protected void initialize(int zoneSize, int mineCount, int[] shipCount) throws Exception {
		zone = new GameZone(zoneSize);
		mines = new LinkedList<>();
		ships = new LinkedList<>();
		for (int i = 0; i < mineCount; i++) {
			mines.add(new Mine());
		}
		
		if ( shipCount.length != 4 ) {
			throw new Exception("Invalid shipCount");
		}
		
		for (int i = 0; i < shipCount[0]; i++) {
			ships.add(new Aerocarrier(4));
		}
		
		for (int i = 0; i < shipCount[1]; i++) {
			ships.add(new Battleship(3));
		}
		
		for (int i = 0; i < shipCount[2]; i++) {
			ships.add(new Cruiser(2));
		}
		
		for (int i = 0; i < shipCount[3]; i++) {
			ships.add(new Destroyer(1));
		}
		
	}
	
	public void setEnemy(Player enemy) {
		this.enemy = enemy;
	}
	
	public Player getEnemy() {
		return enemy;
	}
	
	public GameZone getZone() {
		return zone;
	}
	
	public abstract boolean shot(Ship ship);
	
	public abstract void move(Ship ship) throws FieldNotFound, MissingFields, ShipIsHitted;
	
	protected void RandomMove() {
		int[] direction = {Ship.DIRECTION_UP, Ship.DIRECTION_RIGHT, Ship.DIRECTION_DOWN, Ship.DIRECTION_LEFT};
		Random rnd = new Random();
		for (Ship ship : ships) {
			while (true) {
				try {
					//System.out.println(ship.getClass().getSimpleName() + "; size = " + ship.getSize());
					int x = rnd.nextInt(zone.getSize());
					int y = rnd.nextInt(zone.getSize());
					int d = rnd.nextInt(direction.length);
					ship.move(zone, zone.getField(x, y), direction[d]);
					break;
				} catch (FieldNotFound | MissingFields | ShipIsHitted e) {
					//System.err.println(e.getMessage());
					//System.err.println("Try again");
				}
			}
			//System.out.println(ship.getClass().getSimpleName() + " move success!!");
		}
	}
	
	public abstract Ship getShip();

	public boolean isGameOver() {
		LinkedList<Ship> lst = new LinkedList<>(ships);
		for (Ship ship : lst) {
			if (ship.getState() == Ship.DEAD_STATE) {
				ships.remove(ship);
			}
		}
		return ships.isEmpty();
	}
	
}
