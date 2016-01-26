package com.example.battleship.players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.example.battleship.Controller;
import com.example.battleship.Direction;
import com.example.battleship.Field;
import com.example.battleship.GameZone;
import com.example.battleship.Mine;
import com.example.battleship.exception.FieldNotFoundException;
import com.example.battleship.exception.MissingFieldsException;
import com.example.battleship.exception.RandomException;
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
	
	protected boolean isReady;
	
	public Player(String username, Properties property) {
		this.isReady = false;
		this.name = username;
		initialize(property);
	}

	protected void initialize(Properties property) {
		zone = new GameZone(Integer.parseInt(property.getProperty("fieldSize")));
		mines = new LinkedList<>();
		ships = new LinkedList<>();
		
		for (int i = 0; i < Integer.parseInt(property.getProperty("aerocarierCount")); i++) {
			ships.add(new Aerocarrier(4));
		}
		
		for (int i = 0; i < Integer.parseInt(property.getProperty("battleshipCount")); i++) {
			ships.add(new Battleship(3));
		}
		
		for (int i = 0; i < Integer.parseInt(property.getProperty("cruiserCount")); i++) {
			ships.add(new Cruiser(2));
		}
		
		for (int i = 0; i < Integer.parseInt(property.getProperty("destroyerCount")); i++) {
			ships.add(new Destroyer(1));
		}
		
		for (int i = 0; i < Integer.parseInt(property.getProperty("mineCount")); i++) {
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
				System.out.println(Controller.rb.getString("player") + " " + getName() + " " + Controller.rb.getString("lossShip")+ 
						" x = " + ship.getFields().get(0).getX() + ", y = " + ship.getFields().get(0).getY());
			}
		}
		return ships.isEmpty();
	}
	
	public String getName() {
		return name;
	}
	
	protected void RandomMove() throws RandomException {
		Random rnd = new Random();
		
		List<Field> fieldsPool = new ArrayList<>();
		for (Field[] f : Arrays.asList(zone.getFields())) {
			fieldsPool.addAll(Arrays.asList(f));
		}
		
		for (Ship ship : ships) {
			List<Field> poolForCurrentShip = new ArrayList<>(fieldsPool);
			while (true) {
				Field f = null;
				Direction selectedDir = Direction.UP;
				try {
					f = poolForCurrentShip.get(rnd.nextInt(poolForCurrentShip.size()));
					int d = rnd.nextInt(Direction.values().length);
					selectedDir = Direction.values()[d];
					ship.move(zone, f, selectedDir);
					fieldsPool.removeAll(ship.getFields());
					break;
				} catch (FieldNotFoundException | MissingFieldsException | ShipIsHittedException e) {
					for (Direction d : Direction.values()) {
						try {
							if (d.equals(selectedDir))
								continue;
							ship.move(zone, f, d);
							fieldsPool.removeAll(ship.getFields());
						} catch (FieldNotFoundException | MissingFieldsException | ShipIsHittedException e2) {
						}
					}
					if (ship.getFields() == null) {
						poolForCurrentShip.remove(f);
						if (poolForCurrentShip.isEmpty()) {
							for (Ship s : ships) {
								try {
									s.freeFields();
								} catch (ShipIsHittedException e1) {
								}
							}
							throw new RandomException();
						}
					}
				}
			}
		}
		
		for (Mine mine : mines) {
			while (true) {
				try {
					//int x = rnd.nextInt(zone.getSize());
					//int y = rnd.nextInt(zone.getSize());
					Field f = fieldsPool.get(rnd.nextInt(fieldsPool.size()));
					mine.move(f);
					break;
				} catch (MissingFieldsException e) {
				}
			}
		}
		isReady = true;
	}
	
	
	public abstract boolean shot(Ship ship);
	
	public Ship getShip() {
		return ships.get(new Random().nextInt(ships.size()));
	}
	
	public boolean isReady() {
		return isReady;
	}
	
	public boolean shotOnField(int x, int y, Ship ship) throws FieldNotFoundException {
		if (x != -1 && y != -1)
			return zone.getField(x, y).shotOnField(ship);
		else
			return false;
	}
}
