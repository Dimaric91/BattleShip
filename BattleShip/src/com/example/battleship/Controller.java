package com.example.battleship;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.example.battleship.exception.CannotCreateMessage;
import com.example.battleship.exception.RandomException;
import com.example.battleship.network.BattleShipMessage;
import com.example.battleship.network.ConnectMessage;
import com.example.battleship.network.FailMessage;
import com.example.battleship.network.MessageFactory;
import com.example.battleship.network.OptionMessage;
import com.example.battleship.players.AIPlayer;
import com.example.battleship.players.LocalPlayer;
import com.example.battleship.players.NetworkPlayer;
import com.example.battleship.players.Player;
import com.example.battleship.widgets.HelloWidget;
import com.example.battleship.widgets.WaitWidget;

public class Controller extends Thread {

	public final static ResourceBundle rb = ResourceBundle.getBundle("battleShip");
	public static Image icon;
	
	private LocalPlayer player1;
	private Player player2;
	private Properties properties;
	private String winner;
	
	private ServerSocket serv = null;
	private Shell shell;
	
	public Controller(Shell shell, Properties property) {
		this.properties = property;
		this.shell = shell;
		switch(property.getProperty("playerType")) {
			case "local":
				this.player1 = new LocalPlayer(this, shell.getDisplay(), property.getProperty("username"), property);
			try {
				this.player2 = new AIPlayer("AI player", property);
			} catch (RandomException e1) {
				printMessage("AI player:" + Controller.rb.getString("randomException"), 
						"AI player:" + Controller.rb.getString("randomException"));
			}
				break;
			case "bind":
				Socket socket = null;
				try {
					serv = new ServerSocket(Integer.parseInt(property.getProperty("port")));
					WaitWidget wait = new WaitWidget(shell, serv);
					if (!wait.open()) {
						break;
					}
					
					socket = wait.getSocket();
					
					BattleShipMessage message = MessageFactory.readMessage(socket.getInputStream());
					if (message instanceof ConnectMessage) {
						String remoteName = ((ConnectMessage)message).getUsername();
						if (remoteName.equals(property.getProperty("username"))) {
							MessageFactory.writeMessage(socket.getOutputStream(), new FailMessage(Controller.rb.getString("userUsed")));
						} else {
							MessageFactory.writeMessage(socket.getOutputStream(), new OptionMessage(property));
							this.player1 = new LocalPlayer(this, shell.getDisplay(), property.getProperty("username"), property);
							this.player2 = new NetworkPlayer(remoteName, socket, property);
						}
					} else {
						MessageFactory.writeMessage(socket.getOutputStream(), new FailMessage(Controller.rb.getString("wrongMessage")));
					}
				} catch (NumberFormatException | IOException | CannotCreateMessage e) {
					e.printStackTrace();
				} finally {
					if (this.player1 == null || this.player2 == null) {
						if (socket != null) {
							try {
								socket.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						System.exit(0);
					}
				}
				break;
			case "connect":
				socket = null;
				String host = property.getProperty("host");
				int port = Integer.parseInt(property.getProperty("port"));
				try {
					socket = new Socket();
					socket.connect(new InetSocketAddress(host, port), 10000);
					
					MessageFactory.writeMessage(socket.getOutputStream(), new ConnectMessage(property.getProperty("username")));
					BattleShipMessage msg = MessageFactory.readMessage(socket.getInputStream());
					
					if (msg instanceof FailMessage) {
						printMessage(Controller.rb.getString("fail"), host + ":" + String.valueOf(port) + " " + 
								Controller.rb.getString("response") +":\n" + ((FailMessage)msg).getReason());
					} else if (msg instanceof OptionMessage) {
						Properties prop = ((OptionMessage)msg).getProperty();
						prop.put("isRandom", property.getProperty("isRandom"));
						this.player1 = new LocalPlayer(this, shell.getDisplay(), property.getProperty("username"), prop);
						this.player2 = new NetworkPlayer(prop.getProperty("username"), socket, prop);
					} else {
						printMessage(Controller.rb.getString("fail"), Controller.rb.getString("unknownMessage"));
					}
					
				} catch (SocketTimeoutException e) {
					printMessage(Controller.rb.getString("timeout"), host + ":" + String.valueOf(port) + " " + 
							Controller.rb.getString("noResponse"));
				} catch (ConnectException e) {
					printMessage(Controller.rb.getString("errorConnect"), host + ":" + String.valueOf(port) + " " + 
							Controller.rb.getString("response") + ":\n" + e.getMessage());
				}catch (NumberFormatException | IOException | CannotCreateMessage e) {
					e.printStackTrace();
				} finally {
					if (this.player1 == null || this.player2 == null) {
						if (socket != null) {
							try {
								socket.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
				break;
		}
		if (this.player1 == null || this.player2 == null) {
			System.exit(0);
		}
		this.player1.setEnemy(player2);
		this.player2.setEnemy(player1);
	}

	public void createLogger() {
		PrintStream pintStream = new PrintStream(new BattleShipLogger(player1.getDisp(), player1.getLogArea(), 512), true);
		System.setOut(pintStream);
	}
	
	public void printMessage(String title, String message) {
		MessageBox msg = new MessageBox(shell, SWT.OK);
		msg.setText(title);
		msg.setMessage(message);
		msg.open();
	}
	
	@Override
	public void run() {
		Player current = null;

		if (properties.getProperty("playerType").equals("connect")) {
			current = player2;
		} else {
			current = player1;
		}
		
		try {
			synchronized (player1) {
				sleep(1000);
				player1.wait();
			}
		} catch (InterruptedException e1) {
			return;
		}
		createLogger();
		
		while(!player1.isReady()) {
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				return;
			}
		}
		
		if (player2 instanceof NetworkPlayer) {
			((NetworkPlayer) player2).sendReady(player1.getReady());
			System.out.println(Controller.rb.getString("player") + " " + player1.getName() + " " + Controller.rb.getString("ready"));
			while (!player2.isReady()) {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					return;
				}
			}
			System.out.println(Controller.rb.getString("player") + " " + player2.getName() + " " + Controller.rb.getString("ready"));
		}
		
		while (true) {
			player1.setCurrent(current);
			player1.redraw();
			System.out.println(Controller.rb.getString("nowTurn") + " " + current.getName() + ":");
			while (current.action(current.getShip())) {
				player1.redraw();
				if (current.getEnemy().isGameOver()) {
					winner = current.getName();
					break;
				}
			}
			if (current.isGameOver()) {
				winner = current.getEnemy().getName();
			}
			if (winner != null) {
				player1.getDisp().syncExec(new Runnable() {
					
					@Override
					public void run() {
						printMessage(winner + " " + Controller.rb.getString("win"), Controller.rb.getString("player") 
								+ " " + winner + " " + Controller.rb.getString("win") + "!");
						System.out.println(Controller.rb.getString("player") + " " + winner + " "
								 + Controller.rb.getString("win") + "!");
						player1.gameOver();
					}
				});
				return;
			}
			current = current.getEnemy();
			if(Thread.currentThread().isInterrupted()) {
				return;
			}
		}
	}

	public LocalPlayer getPlayer1() {
		return this.player1;
	}
	
	public void exit() {
		if (serv != null) {
			try {
				serv.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (isAlive()) {
			interrupt();
		} else {
			System.exit(1);
		}
	}
	
	public static void main(String[] args) throws Exception {
		Display disp = new Display();
		Shell shell = new Shell(disp);
		
		HelloWidget hello = new HelloWidget(shell);
		
		if(!hello.open()) {
			return;
		}

		Controller c = new Controller(shell, hello.getOptions());
		c.start();
		
		c.getPlayer1().start();
		c.exit();
		disp.dispose();
	}

}
