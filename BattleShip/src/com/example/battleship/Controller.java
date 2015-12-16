package com.example.battleship;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.example.battleship.players.AIPlayer;
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
		Display disp = new Display();
		HelloWidget hello = new HelloWidget(disp);
		hello.start();
		
		if(!hello.isSetOption()) {
			return;
		}

		LocalGUIPlayer player1 = new LocalGUIPlayer(disp, hello.getOptions().getProperty("username"), hello.getOptions());
		AIPlayer player2 = new AIPlayer("AI player", hello.getOptions());
		
		Controller c = new Controller(disp, player1, player2);
		
		Thread game = new Thread(new Runnable() {
			
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
									message.setMessage("Player " + winner + " win!");
									message.setText(winner + " win");
									message.open();
									player1.dispose();
								}
							});
							return;
						}
					} 
					current = current.getEnemy();
					if(Thread.currentThread().isInterrupted()) {
						return;
					}
					player1.getDisp().asyncExec(player1);
				}
				
			}
		});
		game.start();
		
		player1.start();
		disp.dispose();
		game.interrupt();
	}

	@Override
	public void run() {
		
	}

}
