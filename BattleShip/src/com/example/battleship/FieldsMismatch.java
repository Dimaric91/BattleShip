package com.example.battleship;

public class FieldsMismatch extends BattleShipException {
	public FieldsMismatch() {
		super();
	}
	
	public FieldsMismatch(String message) {
		super(message);
	}
}
