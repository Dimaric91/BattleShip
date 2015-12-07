package com.example.battleship;

import com.example.battleship.players.AIPlayer;
import com.example.battleship.players.LocalGUIPlayer;
import com.example.battleship.players.Player;

public class Controller implements Runnable {

	private Player player1;
	private Player player2;
		
	public Controller(Player player1, Player player2) {
		this.player1 = player1;
		this.player2 = player2;
		this.player1.setEnemy(player2);
		this.player2.setEnemy(player1);
	}
	
	public static void main(String[] args) throws Exception {
		int[] shipCount = {1, 2, 3, 4};
		int zoneSize = 10;
		LocalGUIPlayer player1 = new LocalGUIPlayer("player1", zoneSize, 2, shipCount);
		AIPlayer player2 = new AIPlayer("player2", zoneSize, 2, shipCount);
		
		Controller c = new Controller(player1, player2);
		
	}

	@Override
	public void run() {
		
	}

}
