package com.example.battleship.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class BattleShipMessage {
	
	public BattleShipMessage() {
	}
	
	public abstract void read(InputStream in) throws IOException;
	
	public abstract void write(OutputStream out) throws IOException;
}
