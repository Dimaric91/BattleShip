package com.example.battleship.exception;

public class InvalidFieldSizeException extends BattleShipException{
	
	private int shipArea;
	
	public InvalidFieldSizeException(int shipArea) {
		super();
		this.shipArea = shipArea;
	}
	
	public int getShipArea() {
		return shipArea;
	}
}
