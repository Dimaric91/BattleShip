package com.example.battleship;

public class MissingFields extends BattleShipException {

	public MissingFields() {
		super();
	}
	
	public MissingFields(String message) {
		super(message);
	}

}
