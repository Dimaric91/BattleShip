package com.example.battleship.players;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.example.battleship.Direction;
import com.example.battleship.Field;
import com.example.battleship.Mine;
import com.example.battleship.SeaObject;
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
	
	private Ship selectedShip;
	private Direction selectedDirection = Direction.RIGHT;
	private List<Field> selectedFields;
	private Color currentColor;
	
	public LocalGUIPlayer(Display disp, String username, Properties property) {
		super(username, property);
		this.disp = disp;
		if (Boolean.getBoolean(property.getProperty("isRandom"))) {
			RandomMove();
		} else {
			firstMove();
		}
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
		if (selectedShip != null) {
			e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_GREEN));
			if (selectedShip.getFields() != null) {
				for (Field f : selectedShip.getFields()) {
					e.gc.drawRectangle(f.getX() * cellSize, f.getY() * cellSize, cellSize, cellSize);
				}
			}
		}
		if (selectedFields != null) {
			for (Field f : selectedFields) {
				drawRectangle(e.gc, currentColor, f.getX(), f.getY());
			}
		}
		
	}
	
	public void firstMove() {
		dialogMove();
	}
	
	public void dialogMove() {
		Shell shell2 = new Shell(disp, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 8;
		shell2.setLayout(layout);
		shell2.setText("BattlwShip -> FirstMove");
		
		Canvas fieldZone = new Canvas(shell2, SWT.BORDER);
		fieldZone.setLayoutData(new GridData(cellSize * getZone().getSize(), cellSize * getZone().getSize()));
		Group shipGroup = new Group(shell2, SWT.NONE);
		//shipGroup.setLayoutData(new GridData(cellSize * getZone().getSize(), cellSize * getZone().getSize()));
		shipGroup.setLayout(new GridLayout(2, false));

		Composite bottom = new Composite(shell2, SWT.NONE);
		bottom.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 2, 1));
		bottom.setLayout(new GridLayout(2,false));
		
		Button bRandom = new Button(bottom, SWT.PUSH);
		bRandom.setText("Random");
		bRandom.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		Button bOK = new Button(bottom, SWT.PUSH);
		bOK.setText("Start");
		bOK.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		currentColor = disp.getSystemColor(SWT.COLOR_DARK_RED);
		
		HashMap<SeaObject, Label> labels = new HashMap<>();
		
		for (Ship s : ships) {
			Label shipLabel = new Label(shipGroup, SWT.NONE);
			shipLabel.setBackground(disp.getSystemColor(SWT.COLOR_GRAY));
			shipLabel.setLayoutData(new GridData(cellSize * s.getSize(), cellSize));
			labels.put(s, shipLabel);
			shipLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					shipLabel.setBackground(disp.getSystemColor(SWT.COLOR_YELLOW));
					selectedShip = s;
					selectedDirection = Direction.DOWN;
					try {
						selectedFields = zone.getFields(zone.getField(0, 0), s.getSize(), selectedDirection);
					} catch (FieldNotFoundException ex) {
					}
				}
			});
		}
		
		for (Mine m : mines) {
			Label mineLabel = new Label(shipGroup, SWT.NONE);
			mineLabel.setBackground(disp.getSystemColor(SWT.COLOR_BLACK));
			mineLabel.setLayoutData(new GridData(cellSize, cellSize));
			labels.put(m, mineLabel);
			mineLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					mineLabel.setBackground(disp.getSystemColor(SWT.COLOR_BLUE));
				}
			});
		}
		
		bRandom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				RandomMove();
				for (Label l : labels.values()) {
					l.dispose();
				}
				labels.clear();
				fieldZone.redraw();
			}
		});
		
		bOK.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				shell2.dispose();
			}
		});
		
		fieldZone.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				paintFields(e, getZone().getFields(), false);
			}
		});
		
		fieldZone.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1 && e.count == 1) {
					int x = e.x / cellSize;
					int y = e.y / cellSize;
					try {
						if (selectedShip != null) {
							try {
								selectedShip.move(zone, zone.getField(x, y), selectedDirection);
								Label l = labels.remove(selectedShip);
								if (l != null) {
									l.dispose();
								}
								selectedShip = null;
								selectedFields = null;
								fieldZone.redraw();
								
							} catch (MissingFieldsException | ShipIsHittedException e1) {
							}
						} else {
							Field f = getZone().getField(x, y);
							if (f.getObj() instanceof Ship) {
								selectedShip = (Ship) f.getObj();
								selectedDirection = selectedShip.getDirection();
								selectedFields = selectedShip.getFields();
								currentColor = disp.getSystemColor(SWT.COLOR_DARK_GRAY);
							}
						}
					} catch (FieldNotFoundException e1) {
					}
				}
				if (e.button == 3) {
					selectedShip = null;
					selectedFields = null;
				}
				fieldZone.redraw();
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (e.button == 1) {
					Field f;
					try {
						int x = e.x / cellSize;
						int y = e.y / cellSize;
						f = getZone().getField(x, y);
					} catch (FieldNotFoundException ex){
						return;
					}
					if (f.getObj() instanceof Ship) {
						selectedShip = (Ship) f.getObj();
						selectedDirection = selectedShip.getDirection();
						try {
							selectedShip.move(zone, f, selectedDirection.getSquare());
						} catch (FieldNotFoundException | MissingFieldsException ex) {
							for (Direction d : Direction.values()) {
								try {
									if (d.equals(selectedDirection))
										continue;
									selectedShip.move(zone, f, d);
								} catch (FieldNotFoundException | MissingFieldsException | ShipIsHittedException e2) {
								}
							}
						} catch (ShipIsHittedException ex) {
							MessageBox message = new MessageBox(shell2);
							message.setMessage("ship is hitted");
							message.open();
						}
						selectedShip = null;
						selectedFields = null;							
					}
					
					fieldZone.redraw();
				}
			}
		});
		
		fieldZone.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {
				if (selectedShip != null) {
					int x = e.x / cellSize;
					int y = e.y / cellSize;
					try {
						Field field = getZone().getField(x, y);
						List<Field> temp = zone.getFields(field, selectedShip.getSize(), selectedDirection);
						if (!temp.containsAll(selectedFields)) {
							if (selectedShip.isMove(zone, temp)) {
								currentColor = disp.getSystemColor(SWT.COLOR_DARK_GREEN);
							} else {
								currentColor = disp.getSystemColor(SWT.COLOR_DARK_RED);
							}
							fieldZone.redraw();
							selectedFields = temp;
						}
					} catch (FieldNotFoundException e1) {
					}
				}	
			}
		});
		
		shell2.pack();
		shell2.open();
		while (!shell2.isDisposed()) {
			if (!disp.readAndDispatch()) {
				disp.sleep();
			}
		}
		shell2.dispose();	
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
