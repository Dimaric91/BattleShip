package com.example.battleship.players;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
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

import com.example.battleship.Controller;
import com.example.battleship.Direction;
import com.example.battleship.Field;
import com.example.battleship.Mine;
import com.example.battleship.SeaObject;
import com.example.battleship.exception.FieldNotFoundException;
import com.example.battleship.exception.MissingFieldsException;
import com.example.battleship.exception.ShipIsHittedException;
import com.example.battleship.network.ReadyMessage;
import com.example.battleship.ships.Ship;

public class LocalGUIPlayer extends Player implements Runnable{

	private Controller controller;
	
	private Display disp;
	private Shell shell;
	private Canvas ourZone;
	private Canvas enemyZone;
	
	private int shotX = -1;
	private int shotY = -1;
	private final int cellSize = 28;
	
	private SeaObject selectedObject;
	private Direction selectedDirection = Direction.RIGHT;
	private List<Field> selectedFields;
	//private boolean isReady = false;
	private Color currentColor;

	private boolean isMove = false;
	
	public LocalGUIPlayer(Controller c, Display disp, String username, Properties property) {
		super(username, property);
		this.disp = disp;
		this.controller = c;
		if (Boolean.valueOf(property.getProperty("isRandom"))) {
			RandomMove();
		} else {
			firstMove();
		}
	}

	public void redraw() {
		ourZone.redraw();
		enemyZone.redraw();
	}
	
	public ReadyMessage getReady() {
		return new ReadyMessage(zone, ships, mines);
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
		
		ourZone.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1 && e.count == 1) {
					int x = e.x / cellSize;
					int y = e.y / cellSize;
					try {
						if (selectedObject != null) {
							try {
								if (!selectedFields.containsAll(selectedObject.getFields())) {
									
									if (selectedObject instanceof Ship) {
										((Ship)selectedObject).move(zone, zone.getField(x, y), selectedDirection);
									} else {
										((Mine)selectedObject).move(zone.getField(x, y));
									}
									isMove = true;
								}
							} catch (MissingFieldsException | ShipIsHittedException e1) {
							} finally {
								selectedObject = null;
								selectedFields = null;
								ourZone.redraw();
							}
						} else {
							Field f = getZone().getField(x, y);
							if (f.getObj() instanceof Ship) {
								selectedObject = f.getObj();
								if (((Ship)selectedObject).getState() != Ship.ALIVE_STATE) {
									MessageBox message = new MessageBox(shell);
									message.setMessage("ship is hitted");
									message.open();
									selectedObject = null;
									return;
								}
								selectedDirection = ((Ship)selectedObject).getDirection();
								selectedFields = selectedObject.getFields();
								currentColor = disp.getSystemColor(SWT.COLOR_DARK_GRAY);
							}
							if (f.getObj() instanceof Mine) {
								MessageBox message = new MessageBox(shell);
								message.setMessage("In this phase mine doen't move");
								message.open();
								return;
							}
						}
					} catch (FieldNotFoundException e1) {
					}
				}
				if (e.button == 3) {
					selectedObject = null;
					selectedFields = null;
				}
				ourZone.redraw();
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
						selectedObject = f.getObj();
						selectedDirection = ((Ship)selectedObject).getDirection();
						try {
							((Ship)selectedObject).move(zone, f, selectedDirection.getSquare());
						} catch (FieldNotFoundException | MissingFieldsException ex) {
							for (Direction d : Direction.values()) {
								try {
									if (d.equals(selectedDirection))
										continue;
									((Ship)selectedObject).move(zone, f, d);
								} catch (FieldNotFoundException | MissingFieldsException | ShipIsHittedException e2) {
								}
							}
						} catch (ShipIsHittedException ex) {
							MessageBox message = new MessageBox(shell);
							message.setMessage("ship is hitted");
							message.open();
						}
						selectedObject = null;
						selectedFields = null;							
					}
					
					ourZone.redraw();
				}
			}
		});
		
		ourZone.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {
				if (selectedObject != null) {
					int x = e.x / cellSize;
					int y = e.y / cellSize;
					try {
						Field field = getZone().getField(x, y);
						List<Field> temp = null;
						if (selectedObject instanceof Ship) {
							temp = zone.getFields(field, ((Ship)selectedObject).getSize(), selectedDirection);
						} else {
							temp = zone.getFields(field, 1, null);
						}
						if (!temp.containsAll(selectedFields)) {
							selectedFields = temp;
							ourZone.redraw();
						}
					} catch (FieldNotFoundException e1) {
					}
				}	
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
					drawRectangle(e.gc, new Color(disp, new RGB(176, 224, 230)), i, j); //Powder Blue
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
		if (selectedObject != null) {
			e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_GREEN));
			if (selectedObject.getFields() != null) {
				for (Field f : selectedObject.getFields()) {
					e.gc.drawRectangle(f.getX() * cellSize, f.getY() * cellSize, cellSize, cellSize);
				}
			}
		}
		if (selectedFields != null) {
			if (selectedObject.isMove(zone, selectedFields)) {
				currentColor = disp.getSystemColor(SWT.COLOR_DARK_GREEN);
			} else {
				currentColor = disp.getSystemColor(SWT.COLOR_DARK_RED);
			}
			for (Field f : selectedFields) {
				drawRectangle(e.gc, currentColor, f.getX(), f.getY());
			}
		}
		
	}
	
	public void firstMove() {
		dialogMove();
	}
	
//	private Label InitLabel(Group shipGroup, int size) {
//		
//	}
	
	public void dialogMove() {
		Shell shell2 = new Shell(disp, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 8;
		shell2.setLayout(layout);
		shell2.setText("BattlwShip -> FirstMove");
		
		Canvas fieldZone = new Canvas(shell2, SWT.BORDER);
		fieldZone.setLayoutData(new GridData(cellSize * getZone().getSize(), cellSize * getZone().getSize()));
		Composite shipGroup = new Composite(shell2, SWT.NONE);
		//shipGroup.setLayoutData(new GridData(cellSize * getZone().getSize(), cellSize * getZone().getSize()));
		shipGroup.setLayout(new GridLayout(1, false));
		//shipGroup.setText("Ships");

		Group ship4 = null;
		Group ship3 = null;
		Group ship2 = null;
		Group ship1 = null;
		Group mine = null;
		
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
			Label shipLabel = null;
			
			switch (s.getSize()) {
			case 1:
				if (ship1 == null) {
					ship1 = new Group(shipGroup, SWT.NONE);
					ship1.setText("Destroyers");
					ship1.setLayout(new GridLayout(2, false));
				}
				shipLabel = new Label(ship1, SWT.NONE);
				shipLabel.setImage(new Image(disp, "resources\\destroyer.png"));
				break;
			case 2:
				if (ship2 == null) {
					ship2 = new Group(shipGroup, SWT.NONE);
					ship2.setText("Cruisers");
					ship2.setLayout(new GridLayout(2, false));
				}
				shipLabel = new Label(ship2, SWT.NONE);
				shipLabel.setImage(new Image(disp, "resources\\cruiser.png"));
				break;
			case 3:
				if (ship3 == null) {
					ship3 = new Group(shipGroup, SWT.NONE);
					ship3.setText("Battleships");
					ship3.setLayout(new GridLayout(2, false));
				}
				shipLabel = new Label(ship3, SWT.NONE);
				shipLabel.setImage(new Image(disp, "resources\\battleship.png"));
				break;
			case 4:
				if (ship4 == null) {
					ship4 = new Group(shipGroup, SWT.NONE);
					ship4.setText("Aerocariers");
					ship4.setLayout(new GridLayout(2, false));
				}
				shipLabel = new Label(ship4, SWT.NONE);
				shipLabel.setImage(new Image(disp, "resources\\aerocarier.png"));
				break;
			default:
				break;
			}
			
			shipLabel.setBackground(disp.getSystemColor(SWT.COLOR_TRANSPARENT));
			shipLabel.setLayoutData(new GridData(cellSize * s.getSize() + 2, cellSize + 2));
			labels.put(s, shipLabel);
			shipLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (selectedObject == null) {
						//shipLabel.setBackground(disp.getSystemColor(SWT.COLOR_DARK_GREEN));
						((Label)e.getSource()).setBackground(disp.getSystemColor(SWT.COLOR_DARK_GREEN));
						selectedObject = s;
						selectedDirection = Direction.DOWN;
						try {
							selectedFields = zone.getFields(zone.getField(0, 0), s.getSize(), selectedDirection);
						} catch (FieldNotFoundException ex) {
						}
					} else {
						Label l = labels.get(selectedObject);
						l.setBackground(disp.getSystemColor(SWT.COLOR_TRANSPARENT));
						if (l == ((Label)e.getSource())) {
							selectedObject = null;
							selectedFields = null;
							
						} else {
							((Label)e.getSource()).setBackground(disp.getSystemColor(SWT.COLOR_DARK_GREEN));
							selectedObject = s;
							selectedDirection = Direction.DOWN;
							try {
								selectedFields = zone.getFields(zone.getField(0, 0), s.getSize(), selectedDirection);
							} catch (FieldNotFoundException ex) {
							}
						}
					}
					fieldZone.redraw();
				}
			});
		}
		
		for (Mine m : mines) {
			if (mine == null) {
				mine = new Group(shipGroup, SWT.NONE);
				mine.setText("Mines");
				mine.setLayout(new GridLayout(2, false));
			}
			Label mineLabel = new Label(mine, SWT.NONE);
			mineLabel.setBackground(disp.getSystemColor(SWT.COLOR_BLACK));
			mineLabel.setLayoutData(new GridData(cellSize, cellSize));
			labels.put(m, mineLabel);
			mineLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (selectedObject == null) {
						mineLabel.setBackground(disp.getSystemColor(SWT.COLOR_BLUE));
						selectedObject= m;
						try {
							selectedFields = zone.getFields(zone.getField(0, 0), 1, null);
						} catch (FieldNotFoundException e1) {
						}
					} else {
						Label l = labels.get(selectedObject);
						l.setBackground(disp.getSystemColor(SWT.COLOR_BLACK));
						if (l == mineLabel) {
							selectedObject = null;
							selectedFields = null;
							
						} else {
							mineLabel.setBackground(disp.getSystemColor(SWT.COLOR_BLUE));
							selectedObject = m;
							try {
								selectedFields = zone.getFields(zone.getField(0, 0), 1, null);
							} catch (FieldNotFoundException ex) {
							}
						}
					}
					fieldZone.redraw();
				}
			});
		}
		
		shell2.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if(!isReady) {
					controller.exit();
				}
			}
		});
		
		bRandom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				RandomMove();
				for (Label l : labels.values()) {
					l.dispose();
				}
				selectedObject = null;
				selectedFields = null;
				labels.clear();
				fieldZone.redraw();
			}
		});
		
		bOK.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				for (Ship s : ships) {
					if (s.getFields() == null) {
						MessageBox message = new MessageBox(shell2);
						message.setMessage("Not all ships placed");
						message.open();
						return;
					}
				}
				for (Mine m : mines) {
					if (m.getFields() == null) {
						MessageBox message = new MessageBox(shell2);
						message.setMessage("Not all mines placed");
						message.open();
						return;
					}
				}
				isReady = true;
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
						if (selectedObject != null) {
							try {
								if (selectedObject instanceof Ship) {
									((Ship)selectedObject).move(zone, zone.getField(x, y), selectedDirection);
								} else {
									((Mine)selectedObject).move(zone.getField(x, y));
								}
								Label l = labels.remove(selectedObject);
								if (l != null) {
									l.dispose();
								}
								selectedObject = null;
								selectedFields = null;
								fieldZone.redraw();
								
							} catch (MissingFieldsException | ShipIsHittedException e1) {
							}
						} else {
							Field f = getZone().getField(x, y);
							if (f.getObj() instanceof Ship) {
								selectedObject = f.getObj();
								selectedDirection = ((Ship)selectedObject).getDirection();
								selectedFields = selectedObject.getFields();
								currentColor = disp.getSystemColor(SWT.COLOR_DARK_GRAY);
							}
							if (f.getObj() instanceof Mine) {
								selectedObject = f.getObj();
								selectedFields = selectedObject.getFields();
								currentColor = disp.getSystemColor(SWT.COLOR_GREEN);
							}
						}
					} catch (FieldNotFoundException e1) {
					}
				}
				if (e.button == 3) {
					selectedObject = null;
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
						selectedObject = f.getObj();
						selectedDirection = ((Ship)selectedObject).getDirection();
						try {
							((Ship)selectedObject).move(zone, f, selectedDirection.getSquare());
						} catch (FieldNotFoundException | MissingFieldsException ex) {
							for (Direction d : Direction.values()) {
								try {
									if (d.equals(selectedDirection))
										continue;
									((Ship)selectedObject).move(zone, f, d);
								} catch (FieldNotFoundException | MissingFieldsException | ShipIsHittedException e2) {
								}
							}
						} catch (ShipIsHittedException ex) {
							MessageBox message = new MessageBox(shell2);
							message.setMessage("ship is hitted");
							message.open();
						}
						selectedObject = null;
						selectedFields = null;							
					}
					
					fieldZone.redraw();
				}
			}
		});
		
		fieldZone.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {
				if (selectedObject != null) {
					int x = e.x / cellSize;
					int y = e.y / cellSize;
					try {
						Field field = getZone().getField(x, y);
						List<Field> temp = null;
						if (selectedObject instanceof Ship) {
							temp = zone.getFields(field, ((Ship)selectedObject).getSize(), selectedDirection);
						} else {
							temp = zone.getFields(field, 1, null);
						}
						if (!temp.containsAll(selectedFields)) {
							selectedFields = temp;
							fieldZone.redraw();
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
	
	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	@Override
	public boolean shot(Ship ship) {
		while (true) {
			isMove = false;
			while(shotX == -1 && shotY == -1) {
				try {
					Thread.sleep(1000);
					if (isMove) {
						return false;
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return false;
				}
			}
		
			try {
				return enemy.shotOnField(shotX, shotY, ship);
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
