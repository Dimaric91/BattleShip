package com.example.battleship.players;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.example.battleship.Controller;
import com.example.battleship.Direction;
import com.example.battleship.Field;
import com.example.battleship.Mine;
import com.example.battleship.SeaObject;
import com.example.battleship.Ship;
import com.example.battleship.exception.FieldNotFoundException;
import com.example.battleship.exception.MissingFieldsException;
import com.example.battleship.exception.RandomException;
import com.example.battleship.exception.ShipIsHittedException;
import com.example.battleship.network.ReadyMessage;

public class LocalPlayer extends Player {
	public static final int cellSize = 28;
	
	private class ShipPlaceWidget extends Dialog {

		private Shell shell;
		private HashMap<SeaObject, Label> labels;
		private Canvas fieldZone;
		
		public ShipPlaceWidget(Shell parent) {
			super(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
			this.shell = new Shell(parent, getStyle());
			createContent(this.shell);
		}

		private void createContent(final Shell shell) {
			GridLayout layout = new GridLayout(2, false);
			layout.horizontalSpacing = 8;
			shell.setLayout(layout);
			shell.setImage(Controller.icon);
			shell.setText(Controller.rb.getString("gameName") + " -> " + Controller.rb.getString("firstMove") + "(" 
					+ getName()+  ")");
			
			fieldZone = new Canvas(shell, SWT.BORDER);
			fieldZone.setLayoutData(new GridData(cellSize * (getZone().getSize() + 1) + 1 , cellSize * (getZone().getSize() + 1) + 1));
			Composite shipGroup = new Composite(shell, SWT.NONE);
			shipGroup.setLayout(new GridLayout(1, false));
			
			Composite bottom = new Composite(shell, SWT.NONE);
			bottom.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 2, 1));
			bottom.setLayout(new GridLayout(2,false));
			
			Button bRandom = new Button(bottom, SWT.PUSH);
			bRandom.setText(Controller.rb.getString("randomButton"));
			bRandom.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
			
			Button bOK = new Button(bottom, SWT.PUSH);
			bOK.setText(Controller.rb.getString("startButton"));
			bOK.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
			
			currentColor = disp.getSystemColor(SWT.COLOR_DARK_RED);

			fillShipGroup(shipGroup);
			
			bRandom.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					try {
						RandomMove();
					} catch (RandomException e1) {
						MessageBox message = new MessageBox(shell);
						message.setMessage(Controller.rb.getString("randomException"));
						message.open();
						for (Label l : labels.values()) {
							l.setVisible(true);
						}
						return;
					}
					for (Label l : labels.values()) {
						l.setVisible(false);
					}
					selectedObject = null;
					selectedFields = null;
					fieldZone.redraw();
				}
			});
			
			bOK.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					for (Ship s : ships) {
						if (s.getFields() == null) {
							MessageBox message = new MessageBox(shell);
							message.setMessage(Controller.rb.getString("notAllShips"));
							message.open();
							return;
						}
					}
					for (Mine m : mines) {
						if (m.getFields() == null) {
							MessageBox message = new MessageBox(shell);
							message.setMessage(Controller.rb.getString("notAllMines"));
							message.open();
							return;
						}
					}
					isReady = true;
					shell.dispose();
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
						int x = e.x / cellSize - 1;
						int y = e.y / cellSize - 1;
						try {
							if (selectedObject != null) {
								try {
									if (selectedObject instanceof Ship) {
										((Ship)selectedObject).move(zone, zone.getField(x, y), selectedDirection);
									} else {
										((Mine)selectedObject).move(zone.getField(x, y));
									}
									Label l = labels.get(selectedObject);
									if (l != null) {
										l.setVisible(false);
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
							int x = e.x / cellSize - 1;
							int y = e.y / cellSize - 1;
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
								message.setMessage(Controller.rb.getString("shipIsHitted"));
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
						int x = e.x / cellSize - 1;
						int y = e.y / cellSize - 1;
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
			
			shell.setFocus();
			shell.pack();
		}
		
		private void fillShipGroup(Composite shipGroup) {
			Group ship4 = null;
			Group ship3 = null;
			Group ship2 = null;
			Group ship1 = null;
			Group mine = null;
			
			labels = new HashMap<>();
			for (Ship s : ships) {
				Label shipLabel = null;
				
				switch (s.getSize()) {
				case 1:
					if (ship1 == null) {
						ship1 = new Group(shipGroup, SWT.NONE);
						ship1.setText(Controller.rb.getString("destroyers"));
						ship1.setLayout(new GridLayout(2, false));
					}
					shipLabel = new Label(ship1, SWT.NONE);
					shipLabel.setImage(new Image(disp, getClass().getResourceAsStream("destroyer.png")));
					break;
				case 2:
					if (ship2 == null) {
						ship2 = new Group(shipGroup, SWT.NONE);
						ship2.setText(Controller.rb.getString("cruisers"));
						ship2.setLayout(new GridLayout(2, false));
					}
					shipLabel = new Label(ship2, SWT.NONE);
					;
					shipLabel.setImage(new Image(disp, getClass().getResourceAsStream("cruiser.png")));
					break;
				case 3:
					if (ship3 == null) {
						ship3 = new Group(shipGroup, SWT.NONE);
						ship3.setText(Controller.rb.getString("battleships"));
						ship3.setLayout(new GridLayout(2, false));
					}
					shipLabel = new Label(ship3, SWT.NONE);
					shipLabel.setImage(new Image(disp, getClass().getResourceAsStream("battleship.png")));
					break;
				case 4:
					if (ship4 == null) {
						ship4 = new Group(shipGroup, SWT.NONE);
						ship4.setText(Controller.rb.getString("aerocariers"));
						ship4.setLayout(new GridLayout(2, false));
					}
					shipLabel = new Label(ship4, SWT.NONE);
					shipLabel.setImage(new Image(disp, getClass().getResourceAsStream("aerocarier.png")));
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
					mine.setText(Controller.rb.getString("mines"));
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
		}

		public boolean open() {
			shell.open();
			while (!shell.isDisposed()) {
				if (!disp.readAndDispatch()) {
					disp.sleep();
				}
			}
			shell.dispose();
			return isReady;
		}
		
	}
	
	private Controller controller;
	private boolean isLocal;
	
	private Display disp;
	private Shell shell;
	private Canvas ourZone;
	private Canvas enemyZone;
	private Text textLog;
	
	private int shotX = -1;
	private int shotY = -1;
	private boolean isMove;
	
	private SeaObject selectedObject;
	private Direction selectedDirection = Direction.RIGHT;
	private List<Field> selectedFields;
	private Color currentColor;
	private Ship movedShip;
	
	public LocalPlayer(Controller c, Display disp, String username, Properties property) {
		super(username, property);
		this.isMove = true;
		this.selectedDirection = Direction.RIGHT;
		this.disp = disp;
		this.controller = c;
		
		if (Boolean.valueOf(property.getProperty("isRandom"))) {
			try {
				RandomMove();
			} catch (RandomException e) {
				Shell tempShell = new Shell(disp);
				MessageBox message = new MessageBox(tempShell);
				message.setMessage(username + ":" + Controller.rb.getString("randomException"));
				message.open();
				tempShell.dispose();
				firstMove();
			}
		} else {
			firstMove();
		}
	}

	public void redraw() {
		disp.syncExec(new Runnable() {
			
			@Override
			public void run() {
				ourZone.redraw();
				enemyZone.redraw();
			}
		});
		
	}
	
	public ReadyMessage getReady() {
		return new ReadyMessage(zone, ships, mines);
	}
	
	public Text getLogArea() {
		return textLog;
	}
	
	private Shell createShell(Display disp) {
		Shell shell = new Shell(disp, SWT.DIALOG_TRIM);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 8;
		shell.setLayout(layout);
		shell.setText(Controller.rb.getString("gameName"));
		shell.setImage(Controller.icon);
		
		Group ourGroup = new Group(shell, SWT.NONE);
		ourGroup.setText(getName() + " " + Controller.rb.getString("ships"));
		ourGroup.setLayout(new GridLayout());
		Group enemyGroup = new Group(shell, SWT.NONE);
		enemyGroup.setText(getEnemy().getName() + " " + Controller.rb.getString("ships"));
		enemyGroup.setLayout(new GridLayout());
		
		ourZone = new Canvas(ourGroup, SWT.BORDER);
		ourZone.setLayoutData(new GridData(cellSize * (getZone().getSize() + 1) + 1 , cellSize * (getZone().getSize() + 1) + 1));
		enemyZone = new Canvas(enemyGroup, SWT.BORDER);
		enemyZone.setLayoutData(new GridData(cellSize * (getZone().getSize() + 1) + 1, cellSize * (getZone().getSize() + 1) + 1));
		textLog = new Text(shell, SWT.MULTI | SWT.V_SCROLL | SWT.READ_ONLY | SWT.BORDER);
		textLog.setBackground(disp.getSystemColor(SWT.COLOR_WHITE));
		textLog.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		textLog.setText("\n\n\n\n\n");
		
		enemyZone.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					if (shotX == -1 && shotY == -1) {
						shotX = e.x / cellSize - 1;
						shotY = e.y / cellSize - 1;
					}
				}
			}
		});
		
		enemyZone.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				paintFields(e, getEnemy().getZone().getFields(), true);
				if (isLocal) {
					e.gc.setForeground(disp.getSystemColor(SWT.COLOR_RED));
					e.gc.drawRectangle(cellSize, cellSize, getZone().getSize() * cellSize, getZone().getSize() * cellSize);
				} 
			}
		});
		
		ourZone.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				paintFields(e, getZone().getFields(), false);
				if (!isLocal) {
					e.gc.setForeground(disp.getSystemColor(SWT.COLOR_RED));
					e.gc.drawRectangle(cellSize, cellSize, getZone().getSize() * cellSize, getZone().getSize() * cellSize);
				} 
			}
		});
		
		ourZone.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				if (isMove)
					return;
				if (e.button == 1 && e.count == 1) {
					int x = e.x / cellSize - 1;
					int y = e.y / cellSize - 1;
					try {
						if (selectedObject != null) {
							try {
								if (!selectedFields.containsAll(selectedObject.getFields())) {
									
									if (selectedObject instanceof Ship) {
										((Ship)selectedObject).move(zone, zone.getField(x, y), selectedDirection);
									} 
									isMove = true;
									movedShip = (Ship) selectedObject;
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
									message.setMessage(Controller.rb.getString("shipIsHitted"));
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
								message.setMessage(Controller.rb.getString("errorMoveMine"));
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
					if (isMove)
						return;
					Field f;
					try {
						int x = e.x / cellSize - 1;
						int y = e.y / cellSize - 1;
						f = getZone().getField(x, y);
					} catch (FieldNotFoundException ex){
						return;
					}
					if (f.getObj() instanceof Ship) {
						selectedObject = f.getObj();
						selectedDirection = ((Ship)selectedObject).getDirection();
						selectedFields = ((Ship)selectedObject).getFields();
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
							message.setMessage(Controller.rb.getString("shipIsHitted"));
							message.open();
						}
						if (!selectedFields.containsAll(selectedObject.getFields())) {
							isMove = true;
							movedShip = (Ship) selectedObject;
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
					int x = e.x / cellSize - 1;
					int y = e.y / cellSize - 1;
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
		textLog.setText("");
		synchronized (this) {
			notifyAll();
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!disp.readAndDispatch()) {
				disp.sleep();
			}
		}
		dispose();	
	}
	
	public void dispose() {
		disp.dispose();
	}

	public Display getDisp() {
		return disp;
	}
	
	public Shell getShell() {
		return shell;
	}
	
	private void fillRectangle(GC gc, Color color, int x, int y) {
		gc.setBackground(color);
		gc.fillRectangle(x * cellSize, y * cellSize, cellSize, cellSize);
		gc.setForeground(disp.getSystemColor(SWT.COLOR_BLACK));
		gc.drawRectangle(x * cellSize, y * cellSize, cellSize, cellSize);
	}
	
	private void paintFields(PaintEvent e, Field[][] fields, boolean isEnemy) {
		Font font = new Font(disp, new FontData("TimesNewRoman", cellSize/2 - 3, SWT.NORMAL));
		e.gc.setFont(font);
		e.gc.drawText("Y\\X", font.getFontData()[0].getHeight()/2, font.getFontData()[0].getHeight()/2, true);
		font.dispose();
		font = new Font(disp, new FontData("TimesNewRoman", cellSize/2, SWT.NORMAL));
		e.gc.setFont(font);
		int shiftText = font.getFontData()[0].getHeight()/2 - 2;
		for (int i = 0; i < fields.length; i++) {
			e.gc.drawText(String.valueOf(i), shiftText, (i + 1) * cellSize + shiftText, true);
		}
		e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
		for (int i = 0; i < fields.length; i++) {
			e.gc.drawText(String.valueOf(i), (i + 1) * cellSize + shiftText , shiftText, true);
			for (int j = 0; j < fields.length; j++) {
				switch (fields[i][j].getState(isEnemy)) {
				case HIDDEN_STATE:
				case EMPTY_STATE:
					e.gc.drawRectangle((i + 1) * cellSize, (j + 1) * cellSize, cellSize, cellSize);
					break;
				case CHECKED_FIELD_STATE:
					fillRectangle(e.gc, new Color(disp, new RGB(176, 224, 230)), i + 1, j + 1); //Powder Blue
					break;
				case MINE_STATE:
					fillRectangle(e.gc, e.display.getSystemColor(SWT.COLOR_YELLOW), i + 1, j + 1);
					break;
				case KILLED_MINE_STATE:
					fillRectangle(e.gc, e.display.getSystemColor(SWT.COLOR_DARK_YELLOW), i + 1, j + 1);
					break;
				case SHIP_STATE:
					fillRectangle(e.gc, e.display.getSystemColor(SWT.COLOR_GRAY), i + 1, j + 1);
					break;
				case PADDED_SHIP_STATE:
					fillRectangle(e.gc, e.display.getSystemColor(SWT.COLOR_RED), i + 1, j + 1);
					break;
				case KILLED_SHIP_STATE:
					fillRectangle(e.gc, e.display.getSystemColor(SWT.COLOR_DARK_RED), i + 1, j + 1);
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
					e.gc.drawRectangle((f.getX() + 1) * cellSize, (f.getY() + 1) * cellSize, cellSize, cellSize);
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
				fillRectangle(e.gc, currentColor, (f.getX() + 1), (f.getY() + 1));
			}
		}
		
	}
	
	public void firstMove() {
		Shell tempShell = new Shell(disp);
		ShipPlaceWidget widget = new ShipPlaceWidget(tempShell);
		if (!widget.open()) {
			controller.exit();
		}
		tempShell.dispose();
	}
	
	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	@Override
	public boolean action(Ship ship) {
		while (true) {
			isMove = false;
			shotX = -1;
			shotY = -1;
			while(shotX == -1 && shotY == -1) {
				try {
					Thread.sleep(1000);
					if (isMove) {
						enemy.shotOnField(shotX, shotY, movedShip);
						System.out.println(" " + Controller.rb.getString("movedShip") + " to x = " + movedShip.getFields().get(0).getX() + 
								", y = " + movedShip.getFields().get(0).getY());
						movedShip = null;
						return false;
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return false;
				} catch (FieldNotFoundException e) {
					e.printStackTrace();
				}
			}
		
			try {
				System.out.println(" " + Controller.rb.getString("shotOn") + " x = " + shotX + ", y = " + shotY);
				return enemy.shotOnField(shotX, shotY, ship);
			} catch (FieldNotFoundException e) {
			} finally {
				isMove = true;
			}
		}
	}

	public void setCurrent(Player current) {
		isLocal = current == this;
	}
}
