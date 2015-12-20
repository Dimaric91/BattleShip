package com.example.battleship;

import java.util.Properties;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.example.battleship.players.AIPlayer;
import com.example.battleship.players.LocalGUIPlayer;
import com.example.battleship.players.Player;

public class Controller extends Thread {

	private Display disp;
	private LocalGUIPlayer player1;
	private Player player2;
		
	public Controller(Display disp, Properties property) {
		this.disp = disp;
		this.player1 = new LocalGUIPlayer(this, disp, property.getProperty("username"), property);
		this.player2 = new AIPlayer("AI player", property);
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

		Controller c = new Controller(disp, hello.getOptions());
		c.start();
		
		c.getPlayer1().start();
		disp.dispose();
		c.exit();
	}

	
	
	@Override
	public void run() {
		Player current = player1;
		while (true) {
			while (current.shot(current.getShip())) {
				if (current == player2) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
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

	public LocalGUIPlayer getPlayer1() {
		return this.player1;
	}
	
	public void exit() {
		if (isAlive()) {
			interrupt();
		}
		else {
			System.exit(1);
		}
	}

}
