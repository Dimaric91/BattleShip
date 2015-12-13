package com.example.battleship.players;

import java.util.Random;
import java.util.Scanner;

import com.example.battleship.Mine;
import com.example.battleship.exception.FieldNotFoundException;
import com.example.battleship.exception.MissingFieldsException;
import com.example.battleship.exception.ShipIsHittedException;
import com.example.battleship.ships.Ship;
import com.example.battleship.Direction;
import com.example.battleship.Field;

public class LocalConsolePlayer extends Player {
	
	
	public LocalConsolePlayer(String name) throws Exception {
		super(name);
		//TODO Exception O_o ?!!!!
		//System.out.println(zone);
		//firstMove();
		RandomMove();
	}

	public LocalConsolePlayer(String name, int zoneSize, int mineCount, int[] shipCount) throws Exception {
		super(name, zoneSize, mineCount, shipCount);
		RandomMove();
		//firstMove();
	}
	
	public static void main(String[] args) throws Exception {
		int[] shipCount = {0, 3, 0, 0};
		int zoneSize = 6;
		Player player1 = new LocalConsolePlayer("player1", zoneSize, 0, shipCount);
		Player player2 = new AIPlayer("player2", zoneSize, 0, shipCount);
		player1.setEnemy(player2);
		player2.setEnemy(player1);
		
		/*
		Scanner scan = new Scanner(System.in);
		do {
			player2.shot(player2.getShip());
			System.out.println("AI zone:");
			System.out.println("========");
			System.out.println(player2.getZone());
			System.out.println("YOUR zone:");
			System.out.println("========");
			System.out.println(player2.getEnemy().getZone());
			if(player1.isGameOver()) {
				System.out.println("AI WIN!!!");
				break;
			}
		} while (scan.nextInt() == 1);
		*/
		
		 
		Player current = player1;
		
		while (true) {
			do {
				if (current instanceof LocalConsolePlayer) {
					System.out.println("YOUR zone:");
					System.out.println("========");
					System.out.println(current.getZone());
					System.out.println("AI zone:");
					System.out.println("========");
					System.out.println(current.enemy.getZone().forEnemy());
				}
				if (current.enemy.isGameOver()) {
					System.out.println(current.getName() + " WIN!!!!");
					System.out.println(player1.getName() + " zone:");
					System.out.println("========");
					System.out.println(player1.getZone());
					System.out.println(player2.getName() + " zone:");
					System.out.println("========");
					System.out.println(player2.getZone());
					return;
				}
			} while (current.shot(current.getShip()));
			current = current.enemy;
		}
		/*
		while (true) {
			try {
				player1.move(player1.getShip());
				System.out.println(player1.getZone());
				break;
			} catch (FieldNotFound | MissingFields | ShipIsHitted e) {
				System.out.println(e.getMessage());
			}		
		}*/
		
		
	}

	private void firstMove() {
		Scanner scan = new Scanner(System.in);
		for (Ship ship : ships) {
				System.out.println("Game Zone:\n" + zone);
				while (true) {
					try {
						move(ship);
						break;
					} catch (FieldNotFoundException | MissingFieldsException | ShipIsHittedException | ArrayIndexOutOfBoundsException e) {
						System.err.println(e.getMessage());
						System.err.println("Try again");
					}
				}
				//System.out.println(ship.getClass().getSimpleName() + " move success!!");
		}
		System.out.println("Placing mines. Mine count = " + mines.size());
		for (Mine mine : mines) {
			System.out.println("Game Zone:\n" + zone);
			while (true) {
				try {
					int x = scan.nextInt();
					int y = scan.nextInt();
					mine.move(zone.getField(x, y));
					break;
				} catch (MissingFieldsException | FieldNotFoundException e) {
					System.err.println(e.getMessage());
					System.err.println("Try again");
				}
			}
		}
	}
	
	@Override
	public boolean shot(Ship ship) {
		Scanner scan = new Scanner(System.in);
		while (true) {
			System.out.print("x = ");
			int x = scan.nextInt();
			System.out.print("y = ");
			int y = scan.nextInt();
			try {
				return enemy.getZone().getField(x, y).shotOnField(ship);
			} catch (FieldNotFoundException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	@Override
	public void move(Ship ship) throws FieldNotFoundException, MissingFieldsException, ShipIsHittedException {
		Scanner scan = new Scanner(System.in);
		System.out.print("x = ");
		int x = scan.nextInt();
		System.out.print("y = ");
		int y = scan.nextInt();
		System.out.println("Input directions:");
		for (Direction d : Direction.values()) {
			System.out.println(d.ordinal() + " - " + d);
		}
		System.out.print("direction = ");
		int d = scan.nextInt();
		ship.move(zone, zone.getField(x, y), Direction.values()[d]);
	}

	
	@Override
	public Ship getShip() {
		for (int i = 0; i < ships.size(); i++) {
			System.out.println(i + ") " + ships.get(i));
		}
		Scanner scan = new Scanner(System.in);
		Ship result;
		while (true) {
			try {
				result = ships.get(scan.nextInt());
				break;
			} catch (IndexOutOfBoundsException ex) {
				System.out.println("wrong input, try again");
			}
		}
		return result;
	}
}
