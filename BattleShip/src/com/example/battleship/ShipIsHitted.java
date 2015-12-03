package com.example.battleship;

public class ShipIsHitted extends BattleShipException {
	public ShipIsHitted() {
	}
	
	public ShipIsHitted(String message) {
		super(message);
	}
}
