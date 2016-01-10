package com.example.battleship.exception;

public class CannotCreateMessage extends BattleShipException {
	
	public CannotCreateMessage() {
	}
	
	public CannotCreateMessage(String msg) {
		super(msg);
	}
}
