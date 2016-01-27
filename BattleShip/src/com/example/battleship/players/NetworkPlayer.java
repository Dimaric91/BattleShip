package com.example.battleship.players;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

import com.example.battleship.Field;
import com.example.battleship.FieldState;
import com.example.battleship.exception.CannotCreateMessage;
import com.example.battleship.exception.FieldNotFoundException;
import com.example.battleship.exception.MissingFieldsException;
import com.example.battleship.exception.ShipIsHittedException;
import com.example.battleship.network.BattleShipMessage;
import com.example.battleship.network.MessageFactory;
import com.example.battleship.network.MoveMessage;
import com.example.battleship.network.ReadyMessage;
import com.example.battleship.network.ShotMessage;
import com.example.battleship.widgets.Controller;
import com.example.battleship.Mine;
import com.example.battleship.Ship;

public class NetworkPlayer extends Player {

	private Socket socket;
	private Thread readThread;
	
	public NetworkPlayer(String username, Socket socket, Properties property) {
		super(username, property);
		this.socket = socket;
		readThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					BattleShipMessage message = MessageFactory.readMessage(socket.getInputStream());
					if (message instanceof ReadyMessage) {
						zone = ((ReadyMessage)message).getZone();
						ships = ((ReadyMessage)message).getShips();
						for (Ship s : ships) {
							try {
								ArrayList<Field> fields = new ArrayList<>();
								for (Field f : s.getFields()) {
									fields.add(zone.getField(f.getX(), f.getY()));
								}
								s.move(zone, fields);
							} catch (FieldNotFoundException | MissingFieldsException | ShipIsHittedException e) {
								e.printStackTrace();
							}
						}
						mines = ((ReadyMessage)message).getMines();
						for (Mine m : mines) {
							try {
								Field f = zone.getField(m.getFields().get(0).getX(), m.getFields().get(0).getY());
								m.move(f);
							} catch (FieldNotFoundException | MissingFieldsException e) {
								e.printStackTrace();
							}
						}
						isReady = true;
					}
				} catch (CannotCreateMessage | IOException e) {
					e.printStackTrace();
				}
			}
		});
		start();
	}
	
	public void start() {
		readThread.start();
	}
	
	@Override
	public boolean shot(Ship ship) {
		boolean ret = false;
		try {
			BattleShipMessage message = MessageFactory.readMessage(socket.getInputStream());
			if (message instanceof ShotMessage) {
				Field target = ((ShotMessage)message).getTargetField();
				if (((ShotMessage)message).getPaddedField() != null) {
					Field padded = ((ShotMessage)message).getPaddedField();
					zone.getField(padded.getX(), padded.getY()).shotOnField(null);
					System.out.println(" " + Controller.rb.getString("paddedOnMine") + " x = " + target.getX() + ", y = " + target.getY() +  
							", " + Controller.rb.getString("lossShip") + " x =" + padded.getX() + ", y = " + padded.getY());
				} else {
					System.out.println(" " + Controller.rb.getString("shotOn") + " x = " + target.getX() + ", y = " + target.getY());
				}
				ret = enemy.shotOnField(target.getX(), target.getY(), null);
			}
			if (message instanceof MoveMessage) {
				Ship movedShip = ((MoveMessage)message).getShip();
				ArrayList<Field> fields = new ArrayList<>();
				for (Field f : movedShip.getFields()) {
					fields.add(zone.getField(f.getX(), f.getY()));
				}
				System.out.println(" " + Controller.rb.getString("movedShip"));
				ships.get(ships.indexOf(movedShip)).move(zone, fields);
			}
		} catch (CannotCreateMessage | IOException | FieldNotFoundException | MissingFieldsException | ShipIsHittedException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public void sendReady(ReadyMessage message) {
		try {
			MessageFactory.writeMessage(socket.getOutputStream(), message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean shotOnField(int x, int y, Ship ship) throws FieldNotFoundException {
		if (x != -1 && y != -1) {
			Field field = zone.getField(x, y);
			boolean ret =  super.shotOnField(x, y, ship);
			try {
				ShotMessage message = new ShotMessage(field);
				if (field.getState(false) == FieldState.KILLED_MINE_STATE) {
					message.setPaddedField(((Mine)field.getObj()).getPaddedField());
				}
				MessageFactory.writeMessage(socket.getOutputStream(), message);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return ret;
		} else {
			try {
				MessageFactory.writeMessage(socket.getOutputStream(), new MoveMessage(ship));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
	}
	
	public void close() {
		try {
			readThread.interrupt();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
