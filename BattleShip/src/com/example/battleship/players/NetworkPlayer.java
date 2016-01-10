package com.example.battleship.players;

import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

import com.example.battleship.exception.CannotCreateMessage;
import com.example.battleship.network.BattleShipMessage;
import com.example.battleship.network.MessageFactory;
import com.example.battleship.network.ReadyMessage;
import com.example.battleship.ships.Ship;

public class NetworkPlayer extends Player {

	private Socket socket;
	
	public NetworkPlayer(String username, Socket socket, Properties property) {
		super(username, property);
		this.socket = socket;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					BattleShipMessage message = MessageFactory.readMessage(socket.getInputStream());
					if (message instanceof ReadyMessage) {
						zone = ((ReadyMessage)message).getZone();
						ships = ((ReadyMessage)message).getShips();
						mines = ((ReadyMessage)message).getMines();
						isReady = true;
					}
				} catch (CannotCreateMessage | IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	@Override
	public boolean shot(Ship ship) {
		return false;
		
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
	
	public void sendReady(ReadyMessage message) {
		try {
			MessageFactory.writeMessage(socket.getOutputStream(), message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
