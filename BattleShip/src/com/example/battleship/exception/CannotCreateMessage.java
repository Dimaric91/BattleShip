package com.example.battleship.exception;

@SuppressWarnings("serial")
public class CannotCreateMessage extends BattleShipException {
	
	public CannotCreateMessage() {
	}
	
	public CannotCreateMessage(String msg) {
		super(msg);
	}
}
