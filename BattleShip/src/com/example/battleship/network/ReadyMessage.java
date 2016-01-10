package com.example.battleship.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.example.battleship.GameZone;
import com.example.battleship.Mine;
import com.example.battleship.ships.Ship;

public class ReadyMessage extends BattleShipMessage {

	private GameZone zone;
	private List<Ship> ships;
	private List<Mine> mines;
	
	public ReadyMessage() {
	}
	
	public void setMines(List<Mine> mines) {
		this.mines = mines;
	}
	
	public void setShips(List<Ship> ships) {
		this.ships = ships;
	}
	
	public void setZone(GameZone zone) {
		this.zone = zone;
	}
	
	public List<Mine> getMines() {
		return mines;
	}
	
	public List<Ship> getShips() {
		return ships;
	}
	
	public GameZone getZone() {
		return zone;
	}
	
	public ReadyMessage(GameZone zone, List<Ship> ships, List<Mine> mines) {
		setZone(zone);
		setMines(mines);
		setShips(ships);
	}
	
	@Override
	public void read(InputStream in) throws IOException {
		ObjectInputStream ois = new ObjectInputStream(in);
		try {
			zone = (GameZone) ois.readObject();
			int shipCount = ois.readInt();
			ships = new ArrayList<>();
			for (int i = 0; i < shipCount; i++) {
				ships.add((Ship) ois.readObject());
			}
			int mineCount = ois.readInt();
			mines = new ArrayList<>();
			for (int i = 0; i < mineCount; i++) {
				mines.add((Mine) ois.readObject());			
			}
		} catch (ClassNotFoundException e) {
			throw new IOException("Class not found", e);
		}
	}

	@Override
	public void write(OutputStream out) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(zone);
		oos.writeInt(ships.size());
		for (Ship s : ships) {
			oos.writeObject(s);
		}
		if (mines != null) { 
			oos.writeInt(mines.size());
			for (Mine m : mines) {
				oos.writeObject(m);
			}
		} else {
			oos.writeInt(0);
		}
	}

}
