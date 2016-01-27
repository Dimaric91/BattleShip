package com.example.battleship.network;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.example.battleship.Ship;

public class MoveMessage extends BattleShipMessage {

	private Ship ship;
	
	public MoveMessage() {
	}
	
	public MoveMessage(Ship ship) {
		this.ship = ship;
	}
	
	public Ship getShip() {
		return ship;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(ship);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		ship = (Ship) in.readObject();
	}

}
