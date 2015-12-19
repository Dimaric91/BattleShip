package com.example.battleship;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.example.battleship.ships.Ship;

public interface RemotePlayer extends Remote {
	Field getField(int x, int y) throws RemoteException;
	void setField(Field field) throws RemoteException;
	void setShip(Ship ship) throws RemoteException;
	void moveComplete() throws RemoteException;
	void shotComplite() throws RemoteException;
}
