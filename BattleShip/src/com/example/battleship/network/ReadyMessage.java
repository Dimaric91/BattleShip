package com.example.battleship.network;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import com.example.battleship.GameZone;
import com.example.battleship.Mine;
import com.example.battleship.Ship;

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
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(zone);
		out.writeInt(ships.size());
		for (Ship s : ships) {
			out.writeObject(s);
		}
		if (mines != null) { 
			out.writeInt(mines.size());
			for (Mine m : mines) {
				out.writeObject(m);
			}
		} else {
			out.writeInt(0);
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		zone = (GameZone) in.readObject();
		int shipCount = in.readInt();
		ships = new ArrayList<>();
		for (int i = 0; i < shipCount; i++) {
			ships.add((Ship) in.readObject());
		}
		int mineCount = in.readInt();
		mines = new ArrayList<>();
		for (int i = 0; i < mineCount; i++) {
			mines.add((Mine) in.readObject());			
		}
	}

}
