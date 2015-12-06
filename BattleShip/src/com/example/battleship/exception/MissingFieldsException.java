package com.example.battleship.exception;

public class MissingFieldsException extends BattleShipException {

	public MissingFieldsException() {
		super();
	}
	
	public MissingFieldsException(String message) {
		super(message);
	}

}
