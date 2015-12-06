package com.example.battleship.exception;

public class FieldsMismatchException extends BattleShipException {
	public FieldsMismatchException() {
		super();
	}
	
	public FieldsMismatchException(String message) {
		super(message);
	}
}
