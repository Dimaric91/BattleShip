package com.example.battleship.players;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.example.battleship.Direction;
import com.example.battleship.GameZone;
import com.example.battleship.Mine;
import com.example.battleship.exception.FieldNotFoundException;
import com.example.battleship.exception.MissingFieldsException;
import com.example.battleship.exception.ShipIsHittedException;
import com.example.battleship.ships.Aerocarrier;
import com.example.battleship.ships.Battleship;
import com.example.battleship.ships.Cruiser;
import com.example.battleship.ships.Destroyer;
import com.example.battleship.ships.Ship;

public abstract class Player {
	private String name;
	protected GameZone zone;
	protected List<Mine> mines;
	protected List<Ship> ships;
	protected Player enemy;
	
	public Player(String username) throws Exception {
		this(username, 10);
	}
	
	public Player(String username, int zoneSize) throws Exception {
		this.name = username;
		int[] shipCount = {1, 2, 3, 4};
		initialize(zoneSize, 2, shipCount);
	}
	
	public Player(String username, int zoneSize, int mineCount, int[] shipCount) throws Exception {
		this.name = username;
		initialize(zoneSize, mineCount, shipCount);
	}

	protected void initialize(int zoneSize, int mineCount, int[] shipCount) throws Exception {
		zone = new GameZone(zoneSize);
		mines = new LinkedList<>();
		ships = new LinkedList<>();
		
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
		
		for (int i = 0; i < mineCount; i++) {
			mines.add(new Mine());
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
	
	public boolean isGameOver() {
		LinkedList<Ship> lst = new LinkedList<>(ships);
		for (Ship ship : lst) {
			if (ship.getState() == Ship.DEAD_STATE) {
				ships.remove(ship);
			}
		}
		return ships.isEmpty();
	}
	
	public String getName() {
		return name;
	}
	
	protected void RandomMove() {
		Random rnd = new Random();
		for (Ship ship : ships) {
			while (true) {
				try {
					int x = rnd.nextInt(zone.getSize());
					int y = rnd.nextInt(zone.getSize());
					int d = rnd.nextInt(Direction.values().length);
					ship.move(zone, zone.getField(x, y), Direction.values()[d]);
					break;
				} catch (FieldNotFoundException | MissingFieldsException | ShipIsHittedException e) {
					//System.err.println(e.getMessage());
					//System.err.println("Try again");
				}
			}
		}
		
		for (Mine mine : mines) {
			while (true) {
				try {
					int x = rnd.nextInt(zone.getSize());
					int y = rnd.nextInt(zone.getSize());
					mine.move(zone.getField(x, y));
					break;
				} catch (MissingFieldsException | FieldNotFoundException e) {
				}
			}
		}
	}
	
	
	public abstract boolean shot(Ship ship);
	
	public abstract void move(Ship ship) throws FieldNotFoundException, MissingFieldsException, ShipIsHittedException;
	
	public abstract Ship getShip();
}
