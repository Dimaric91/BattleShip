package com.example.battleship.exception;

@SuppressWarnings("serial")
public class FieldNotFoundException extends BattleShipException {
	
	public FieldNotFoundException(String message) {
		super(message);
	}
}
