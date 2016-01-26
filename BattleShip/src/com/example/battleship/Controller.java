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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.example.battleship.exception.CannotCreateMessage;
import com.example.battleship.network.BattleShipMessage;
import com.example.battleship.network.ConnectMessage;
import com.example.battleship.network.FailMessage;
import com.example.battleship.network.MessageFactory;
import com.example.battleship.network.OptionMessage;
import com.example.battleship.players.AIPlayer;
import com.example.battleship.players.LocalGUIPlayer;
import com.example.battleship.players.NetworkPlayer;
import com.example.battleship.players.Player;
import com.example.battleship.widgets.HelloWidget;
import com.example.battleship.widgets.WaitWidget;

public class Controller extends Thread {

	public final static ResourceBundle rb = ResourceBundle.getBundle("battleShip");
	
	private LocalGUIPlayer player1;
	private Player player2;
	private Properties properties;
	private String winner;
	
	private ServerSocket serv = null;
	public Controller(Display disp, Properties property) {
		this.properties = property;
		switch(property.getProperty("playerType")) {
			case "local":
				this.player1 = new LocalGUIPlayer(this, disp, property.getProperty("username"), property);
				this.player2 = new AIPlayer("AI player", property);
				break;
			case "bind":
				Socket socket = null;
				try {
					serv = new ServerSocket(Integer.parseInt(property.getProperty("port")));
					WaitWidget wait = new WaitWidget(this, disp, serv);
					wait.start();			
					
					socket = wait.getSocket();
					
					BattleShipMessage message = MessageFactory.readMessage(socket.getInputStream());
					if (message instanceof ConnectMessage) {
						String remoteName = ((ConnectMessage)message).getUsername();
						if (remoteName.equals(property.getProperty("username"))) {
							MessageFactory.writeMessage(socket.getOutputStream(), new FailMessage(Controller.rb.getString("userUsed")));
						} else {
							MessageFactory.writeMessage(socket.getOutputStream(), new OptionMessage(property));
							this.player1 = new LocalGUIPlayer(this, disp, property.getProperty("username"), property);
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
						Shell shell = new Shell(disp);
						MessageBox fail = new MessageBox(shell, SWT.OK);
						fail.setText(Controller.rb.getString("fail"));
						fail.setMessage(host + ":" + String.valueOf(port) + " " + Controller.rb.getString("response") + 
								":\n" + ((FailMessage)msg).getReason());
						fail.open();
						shell.dispose();
					} else if (msg instanceof OptionMessage) {
						Properties prop = ((OptionMessage)msg).getProperty();
						prop.put("isRandom", property.getProperty("isRandom"));
						this.player1 = new LocalGUIPlayer(this, disp, property.getProperty("username"), prop);
						this.player2 = new NetworkPlayer(prop.getProperty("username"), socket, prop);
					} else {
						Shell shell = new Shell(disp);
						MessageBox fail = new MessageBox(shell, SWT.OK);
						fail.setText(Controller.rb.getString("fail"));
						fail.setMessage(Controller.rb.getString("unknownMessage"));
						fail.open();
						shell.dispose();
					}
					
				} catch (SocketTimeoutException e) {
					Shell shell = new Shell(disp);
					MessageBox msg = new MessageBox(shell, SWT.OK);
					msg.setText(Controller.rb.getString("timeout"));
					msg.setMessage(host + ":" + String.valueOf(port) + " " + Controller.rb.getString("noResponse"));
					msg.open();
					shell.dispose();
				} catch (ConnectException e) {
					Shell shell = new Shell(disp);
					MessageBox msg = new MessageBox(shell, SWT.OK);
					msg.setText(Controller.rb.getString("errorConnect"));
					msg.setMessage(host + ":" + String.valueOf(port) + " " + Controller.rb.getString("response") + 
							":\n" + e.getMessage());
					msg.open();
					shell.dispose();
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
						System.exit(0);
					}
				}
				break;
		}
		
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

	public void createLogger() {
		PrintStream pintStream = new PrintStream(new BattleShipLogger(player1.getDisp(), player1.getLogArea(), 512), true);
		System.setOut(pintStream);
	}
	
	@Override
	public void run() {
		Player current = null;
		//TODO Fucking shit
		if (properties.getProperty("playerType").equals("connect")) {
			current = player2;
		} else {
			current = player1;
		}
		try {
			synchronized (player1) {
				player1.wait();
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		createLogger();
		
		if (player2 instanceof NetworkPlayer) {
			((NetworkPlayer) player2).sendReady(player1.getReady());
			System.out.println(Controller.rb.getString("player") + " " + player1.getName() + " " + Controller.rb.getString("ready"));
			while (!player2.isReady()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					return;
				}
			}
			System.out.println(Controller.rb.getString("player") + " " + player2.getName() + " " + Controller.rb.getString("ready"));
		}
			
		player1.redraw(current == player1);
		while (true) {
			System.out.println(Controller.rb.getString("nowTurn") + " " + current.getName() + ":");
			while (current.shot(current.getShip())) {
				player1.redraw(current == player1);
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
						MessageBox message = new MessageBox(player1.getShell());
						message.setMessage(Controller.rb.getString("player") + " " + winner + " " + Controller.rb.getString("win") + "!");
						message.setText(winner + " " + Controller.rb.getString("win"));
						message.open();
						player1.dispose();
					}
				});
				return;
			}
			current = current.getEnemy();
			if(Thread.currentThread().isInterrupted()) {
				return;
			}
			player1.redraw(current == player1);
		}
	}

	public LocalGUIPlayer getPlayer1() {
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

}
