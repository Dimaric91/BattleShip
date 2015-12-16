package com.example.battleship.players;

import java.util.Properties;

import com.example.battleship.ships.Ship;

public class NetworkPlayer extends Player {

	public NetworkPlayer(String username, Properties property) {
		super(username, property);
	}
	
	@Override
	public boolean shot(Ship ship) {
		return false;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void move(Ship ship) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Ship getShip() {
		// TODO Auto-generated method stub
		return null;
	}

}
