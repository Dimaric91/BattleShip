package com.example.battleship.players;

import java.util.Properties;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.example.battleship.Field;
import com.example.battleship.exception.FieldNotFoundException;
import com.example.battleship.exception.MissingFieldsException;
import com.example.battleship.exception.ShipIsHittedException;
import com.example.battleship.ships.Ship;

public class LocalGUIPlayer extends Player implements Runnable{

	private Display disp;
	private Shell shell;
	private Canvas ourZone;
	private Canvas enemyZone;
	
	private int shotX = -1;
	private int shotY = -1;
	private final int cellSize = 28;
	
	public LocalGUIPlayer(Display disp, String username, Properties property) {
		super(username, property);
		RandomMove();
		this.disp = disp;
		//this.shell = createShell(this.disp);
	}

	public void redraw() {
		ourZone.redraw();
		enemyZone.redraw();
	}
	
	private Shell createShell(Display disp) {
		Shell shell = new Shell(disp, SWT.DIALOG_TRIM);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 8;
		shell.setLayout(layout);
		shell.setText("BattleShip! ");
		
		Group ourGroup = new Group(shell, SWT.NONE);
		ourGroup.setText(getName() + " ships");
		ourGroup.setLayout(new GridLayout());
		Group enemyGroup = new Group(shell, SWT.NONE);
		enemyGroup.setText(getEnemy().getName() + " ships");
		enemyGroup.setLayout(new GridLayout());
		
		ourZone = new Canvas(ourGroup, SWT.BORDER);
		ourZone.setLayoutData(new GridData(cellSize * getZone().getSize(), cellSize * getZone().getSize()));
		enemyZone = new Canvas(enemyGroup, SWT.BORDER);
		enemyZone.setLayoutData(new GridData(cellSize * getZone().getSize(), cellSize * getZone().getSize()));
		
		enemyZone.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				//can.redraw();
				if (e.button == 1) {
					shotX = e.x / cellSize;
					shotY = e.y / cellSize;
				}
			}
		});
		
		enemyZone.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				paintFields(e, getEnemy().getZone().getFields(), true);
			}
		});
		
		ourZone.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				paintFields(e, getZone().getFields(), false);
			}
		});
		shell.pack();
		return shell;
	}
	
	public void start() {
		shell = createShell(disp);
		shell.open();
		while (!shell.isDisposed()) {
			if (!disp.readAndDispatch()) {
				disp.sleep();
			}
		}
		dispose();	
	}
	
	@Override
	public void run() {
		redraw();
	}
	
	public void dispose() {
		disp.dispose();
	}

	public Display getDisp() {
		return disp;
	}
	
	/*public static void main(String[] args) throws Exception {
		int[] shipCount = {1, 2, 3, 4};
		int zoneSize = 10;
		LocalGUIPlayer player1 = new LocalGUIPlayer("player1", zoneSize, 0, shipCount);
		AIPlayer player2 = new AIPlayer("player2", zoneSize, 0, shipCount);
		player1.setEnemy(player2);
		player2.setEnemy(player1);
		
		player1.run();
	}*/

	private void drawRectangle(GC gc, Color color, int x, int y) {
		gc.setBackground(color);
		gc.fillRectangle(x * cellSize, y * cellSize, cellSize, cellSize);
		gc.setForeground(disp.getSystemColor(SWT.COLOR_BLACK));
		gc.drawRectangle(x * cellSize, y * cellSize, cellSize, cellSize);
	}
	
	private void paintFields(PaintEvent e, Field[][] fields, boolean isEnemy) {
		e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
		for (int i = 0; i < fields.length; i++) {
			for (int j = 0; j < fields.length; j++) {
				switch (fields[i][j].getState(isEnemy)) {
				case HIDDEN_STATE:
				case EMPTY_STATE:
					e.gc.drawRectangle(i * cellSize, j * cellSize, cellSize, cellSize);
					break;
				case CHECKED_FIELD_STATE:
					drawRectangle(e.gc, e.display.getSystemColor(SWT.COLOR_BLUE), i, j);
					break;
				case MINE_STATE:
					drawRectangle(e.gc, e.display.getSystemColor(SWT.COLOR_YELLOW), i, j);
					break;
				case KILLED_MINE_STATE:
					drawRectangle(e.gc, e.display.getSystemColor(SWT.COLOR_DARK_YELLOW), i, j);
					break;
				case SHIP_STATE:
					drawRectangle(e.gc, e.display.getSystemColor(SWT.COLOR_GRAY), i, j);
					break;
				case PADDED_SHIP_STATE:
					drawRectangle(e.gc, e.display.getSystemColor(SWT.COLOR_RED), i, j);
					break;
				case KILLED_SHIP_STATE:
					drawRectangle(e.gc, e.display.getSystemColor(SWT.COLOR_DARK_RED), i, j);
					break;
				default:
					break;
				}
			}
		}
		
	}

	@Override
	public boolean shot(Ship ship) {
		while (true) {
			boolean ret;
			while(shotX == -1 && shotY == -1) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return false;
				}
			}
		
			try {
				ret = enemy.getZone().getField(shotX, shotY).shotOnField(ship);
				return ret;
			} catch (FieldNotFoundException e) {
			} finally {
				shotX = -1;
				shotY = -1;
			}
		}
	}

	@Override
	public void move(Ship ship) throws FieldNotFoundException, MissingFieldsException, ShipIsHittedException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Ship getShip() {
		return ships.get(new Random().nextInt(ships.size()));
	}
}
