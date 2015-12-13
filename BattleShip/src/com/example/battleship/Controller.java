package com.example.battleship;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.example.battleship.players.AIPlayer;
import com.example.battleship.players.LocalConsolePlayer;
import com.example.battleship.players.LocalGUIPlayer;
import com.example.battleship.players.Player;

public class Controller implements Runnable {

	private Display disp;
	private Player player1;
	private Player player2;
		
	public Controller(Display disp, Player player1, Player player2) {
		this.disp = disp;
		this.player1 = player1;
		this.player2 = player2;
		this.player1.setEnemy(player2);
		this.player2.setEnemy(player1);
	}
	
	public static void main(String[] args) throws Exception {
		int[] shipCount = {0, 1, 0, 0};
		int zoneSize = 5;
		Display disp = new Display();
		LocalGUIPlayer player1 = new LocalGUIPlayer(disp, "player1", zoneSize, 0, shipCount);
		AIPlayer player2 = new AIPlayer("player2", zoneSize, 0, shipCount);
		
		Controller c = new Controller(disp, player1, player2);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Player current = player1;
				while (true) {
					while (current.shot(current.getShip())) {
						player1.getDisp().asyncExec(player1);
						if (current.getEnemy().isGameOver()) {
							String winner = current.getName();
							player1.getDisp().syncExec(new Runnable() {
								
								@Override
								public void run() {
									player1.run();
									MessageBox message = new MessageBox(player1.getDisp().getActiveShell());
									message.setMessage(winner + " win!");
									message.setText(winner + "win");
									message.open();
									player1.dispose();
								}
							});
							return;
						}
					} 
					current = current.getEnemy();
					player1.getDisp().asyncExec(player1);
				}
				
			}
		}).start();
		
		player1.start();
	}

	@Override
	public void run() {
		
	}

}
