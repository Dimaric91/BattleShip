package com.example.battleship.exception;

@SuppressWarnings("serial")
public class ShipIsHittedException extends BattleShipException {
	public ShipIsHittedException() {
	}
	
	public ShipIsHittedException(String message) {
		super(message);
	}
}
