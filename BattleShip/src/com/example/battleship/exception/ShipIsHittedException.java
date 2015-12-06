package com.example.battleship.exception;

public class ShipIsHittedException extends BattleShipException {
	public ShipIsHittedException() {
	}
	
	public ShipIsHittedException(String message) {
		super(message);
	}
}
