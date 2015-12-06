package com.example.battleship.players;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

import com.example.battleship.Mine;
import com.example.battleship.exception.FieldNotFoundException;
import com.example.battleship.exception.MissingFieldsException;
import com.example.battleship.exception.ShipIsHittedException;
import com.example.battleship.ships.Ship;
import com.example.battleship.Field;

public class LocalPlayer extends Player {
	
	
	public LocalPlayer() throws Exception {
		super();
		//TODO Exception O_o ?!!!!
		//System.out.println(zone);
		//firstMove();
		RandomMove();
	}

	public LocalPlayer(int zoneSize, int mineCount, int[] shipCount) throws Exception {
		super(zoneSize, mineCount, shipCount);
		RandomMove();
	}
	
	public static void main(String[] args) throws Exception {
		int[] shipCount = {1, 1, 1, 1};
		int zoneSize = 7;
		Player player1 = new LocalPlayer(zoneSize, 1, shipCount);
		Player player2 = new AIPlayer(zoneSize, 1, shipCount);
		player1.setEnemy(player2);
		player2.setEnemy(player1);
		
		/*
		Scanner scan = new Scanner(System.in);
		while (scan.nextInt() == 1) {
			player2.shot(player2.getShip());
			//System.out.println("AI zone:");
			//System.out.println("========");
			//System.out.println(player2.getZone());
			System.out.println("YOUR zone:");
			System.out.println("========");
			System.out.println(player2.getEnemy().getZone());
			if(player1.isGameOver()) {
				System.out.println("AI WIN!!!");
			}
		}
		*/
		
		 
		Player current = player1;
		
		while (true) {
			do {
				if (current instanceof Player) {
					System.out.println("YOUR zone:");
					System.out.println("========");
					System.out.println(current.getZone());
					System.out.println("AI zone:");
					System.out.println("========");
					System.out.println(current.enemy.getZone().forEnemy());
				}
				if (current.enemy.isGameOver()) {
					System.out.println(current + " WIN!!!!");
					return;
				}
			} while (current.shot(current.getShip()));
			current = current.enemy;
		}
		
		/*while (true) {
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
		for (Ship ship : ships) {
				while (true) {
					try (Scanner scan = new Scanner(System.in)) {
						System.out.println(ship.getClass().getSimpleName() + "; size = " + ship.getSize());
						int x = scan.nextInt();
						int y = scan.nextInt();
						int direction = scan.nextInt();
						ship.move(zone, zone.getField(x, y), direction);
						break;
					} catch (FieldNotFoundException | MissingFieldsException | ShipIsHittedException e) {
						System.err.println(e.getMessage());
						System.err.println("Try again");
					}
				}
				System.out.println(ship.getClass().getSimpleName() + " move success!!");
		}
		System.out.println("Placing mines. Mine count = " + mines.size());
		for (Mine mine : mines) {
			while (true) {
				try (Scanner scan = new Scanner(System.in)){
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
		System.out.print("direction = ");
		int dir = scan.nextInt();
		ship.move(zone, zone.getField(x, y), dir);
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
