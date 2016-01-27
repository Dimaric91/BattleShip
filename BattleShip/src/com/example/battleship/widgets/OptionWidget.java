package com.example.battleship.widgets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.example.battleship.exception.InvalidFieldSizeException;

public class OptionWidget extends Dialog {
	private Shell shell;
	
	private Spinner aerocarierCount;
	private Spinner battleshipCount;
	private Spinner cruiserCount;
	private Spinner destroyerCount;
	private Group countGroup;
	private Label aerocarierLabel;
	private Label battleshipLabel;
	private Label cruiserLabel;
	private Label destroyerLabel;
	private Label sizeLabel;
	private Spinner sizeField;
	private Button randomButton;
	private Button mineUseButton;
	private Label mineLabel;
	private Spinner mineCount;
	private Composite buttonGroup;
	private Button saveButton;
	private Button loadButton;
	private Button applyButton;
	private Group userGroup;
	private Label userLabel;
	private Text userText;
	private Group gameGroup;
	private Button localGameButton;
	private Button bindNetworkButton;
	private Button connectNetworkButton;
	private Spinner portNum;
	private Label portLabel;
	private Label hostLabel;
	private Text hostText;
	
	private Properties options;
	
	public OptionWidget(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		this.shell = new Shell(parent, getStyle());
		createContent(this.shell);
		this.options = new Properties();
		//this.disp = disp;
		//this.shell = createShell(disp);
		
	}

	private void createContent(final Shell shell) {
		shell.setText(Controller.rb.getString("gameName") + " -> " + Controller.rb.getString("options"));
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);
		shell.setImage(Controller.icon);
		layout.horizontalSpacing = 8;
		
		gameGroup = new Group(shell, SWT.NONE);
		gameGroup.setText(Controller.rb.getString("gameType"));
		gameGroup.setLayout(new GridLayout(2, true));
		gameGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		localGameButton = new Button(gameGroup, SWT.RADIO);
		localGameButton.setText(Controller.rb.getString("gameWithComputer"));
		localGameButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		
		bindNetworkButton = new Button(gameGroup, SWT.RADIO);
		bindNetworkButton.setText(Controller.rb.getString("gameNetworkBind"));
		bindNetworkButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		
		connectNetworkButton = new Button(gameGroup, SWT.RADIO);
		connectNetworkButton.setText(Controller.rb.getString("gameNetworkConnect"));
		connectNetworkButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		
		localGameButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				portLabel.setEnabled(false);
				portNum.setEnabled(false);
				hostLabel.setEnabled(false);
				hostText.setEnabled(false);
				options.setProperty("playerType", "local");
				countGroup.setVisible(true);
			}
		});
		
		bindNetworkButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				portLabel.setEnabled(true);
				portNum.setEnabled(true);
				hostLabel.setEnabled(false);
				hostText.setEnabled(false);
				options.setProperty("playerType", "bind");
				countGroup.setVisible(true);
			}
		});
		
		connectNetworkButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				portLabel.setEnabled(true);
				portNum.setEnabled(true);
				hostLabel.setEnabled(true);
				hostText.setEnabled(true);
				options.setProperty("playerType", "connect");
				countGroup.setVisible(false);
			}
			
			
		});
		
		portLabel = new Label(gameGroup, SWT.NONE);
		portLabel.setText(Controller.rb.getString("port") + ":");
		portLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		portLabel.setEnabled(false);
		
		portNum = new Spinner(gameGroup, SWT.NONE);
		portNum.setMaximum(20000);
		portNum.setMinimum(10001);
		portNum.setSelection(10001);
		portNum.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		portNum.setEnabled(false);
		
		hostLabel = new Label(gameGroup, SWT.NONE);
		hostLabel.setText(Controller.rb.getString("host") + ":");
		hostLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		hostLabel.setEnabled(false);
		
		hostText = new Text(gameGroup, SWT.BORDER);
		hostText.setText("localhost");
		hostText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		hostText.setEnabled(false);
		
		userGroup = new Group(shell, SWT.NONE);
		userGroup.setText(Controller.rb.getString("userOptions"));
		userGroup.setLayout(new GridLayout(2, true));
		userGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		userLabel = new Label(userGroup, SWT.NONE);
		userLabel.setText(Controller.rb.getString("username") + ":");
		userLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		
		userText = new Text(userGroup, SWT.BORDER);
		userText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		userText.setText(System.getProperty("user.name"));
		
		randomButton = new Button(userGroup, SWT.CHECK);
		randomButton.setText(Controller.rb.getString("isRandom"));
		randomButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2 ,1));
		
		randomButton.setSelection(true);
		
		countGroup = new Group(shell, SWT.NONE);
		countGroup.setLayout(new GridLayout(2, false));
		countGroup.setText(Controller.rb.getString("gameOptions"));
		countGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		sizeLabel = new Label(countGroup, SWT.NONE);
		sizeLabel.setText(Controller.rb.getString("fieldSize") + " = ");
		sizeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		sizeField = new Spinner(countGroup, SWT.NONE);
		sizeField.setMinimum(0);
		sizeField.setMaximum(40);
		sizeField.setSelection(10);
		sizeField.setIncrement(1);
		sizeField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		aerocarierLabel = new Label(countGroup, SWT.NONE);
		aerocarierLabel.setText(Controller.rb.getString("aerocarierCount") + " = ");
		aerocarierLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		aerocarierCount = new Spinner(countGroup, SWT.READ_ONLY);
		aerocarierCount.setMinimum(0);
		aerocarierCount.setMaximum(5);
		aerocarierCount.setIncrement(1);
		aerocarierCount.setSelection(1);
		aerocarierCount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		battleshipLabel = new Label(countGroup, SWT.NONE);
		battleshipLabel.setText(Controller.rb.getString("battleshipCount") + " = ");
		battleshipLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		battleshipCount = new Spinner(countGroup, SWT.READ_ONLY);
		battleshipCount.setMinimum(0);
		battleshipCount.setMaximum(6);
		battleshipCount.setIncrement(1);
		battleshipCount.setSelection(2);
		battleshipCount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		cruiserLabel = new Label(countGroup, SWT.NONE);
		cruiserLabel.setText(Controller.rb.getString("cruiserCount") + " = ");
		cruiserLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		cruiserCount = new Spinner(countGroup, SWT.READ_ONLY);
		cruiserCount.setMinimum(0);
		cruiserCount.setMaximum(7);
		cruiserCount.setIncrement(1);
		cruiserCount.setSelection(3);
		cruiserCount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		destroyerLabel = new Label(countGroup, SWT.NONE);
		destroyerLabel.setText(Controller.rb.getString("destroyerCount") + " = ");
		destroyerLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		destroyerCount = new Spinner(countGroup, SWT.READ_ONLY);
		destroyerCount.setMinimum(0);
		destroyerCount.setMaximum(8);
		destroyerCount.setIncrement(1);
		destroyerCount.setSelection(4);
		destroyerCount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		mineUseButton = new Button(countGroup, SWT.CHECK);
		mineUseButton.setText(Controller.rb.getString("isMine"));
		mineUseButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2 , 1));
		
		mineUseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mineLabel.setEnabled(mineUseButton.getSelection());
				mineCount.setEnabled(mineUseButton.getSelection());	
			}
		});
		
		mineUseButton.setSelection(true);
		
		mineLabel = new Label(countGroup, SWT.NONE);
		mineLabel.setText(Controller.rb.getString("mineCount") + " = ");
		mineLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		mineCount = new Spinner(countGroup, SWT.READ_ONLY);
		mineCount.setMinimum(0);
		mineCount.setMaximum(5);
		mineCount.setIncrement(1);
		mineCount.setSelection(2);
		mineCount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		buttonGroup = new Composite(shell,  SWT.NONE);
		buttonGroup.setLayout(new GridLayout(3, false));
		buttonGroup.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false));
		
		saveButton = new Button(buttonGroup, SWT.PUSH);
		saveButton.setText(Controller.rb.getString("saveSettings"));
		saveButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		loadButton = new Button(buttonGroup, SWT.PUSH);
		loadButton.setText(Controller.rb.getString("loadSettings"));
		loadButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		applyButton = new Button(buttonGroup, SWT.PUSH);
		applyButton.setText(Controller.rb.getString("applySettings"));
		applyButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		applyButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					options = readOptions();
					try {
						checkOption();
					} catch (InvalidFieldSizeException e1) {
						MessageBox msg = new MessageBox(shell, SWT.OK);
						msg.setText(Controller.rb.getString("invalidFieldSize"));
						msg.setMessage(Controller.rb.getString("invalidFieldSizeMessage"));	
						msg.open();
						sizeField.setSelection((int)Math.sqrt(e1.getShipArea()) + 1);
						return;
					}
					disposeShell();
				}
			}
		});
		
		saveButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				Properties property = readOptions();
				FileDialog fd = new FileDialog(shell, SWT.SAVE);
				fd.setFilterNames(new String[] {"XML files"});
				fd.setFilterExtensions(new String[] {"*.xml"});
				fd.open();
				if (fd.getFileName().equals(""))
					return;
				try {
					File file = FileSystems.getDefault().getPath(fd.getFilterPath(), fd.getFileName()).toFile();
					FileOutputStream fos = new FileOutputStream(file);
					property.storeToXML(fos, null);
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		loadButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				Properties property = new Properties();
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setFilterNames(new String[] {"XML files"});
				fd.setFilterExtensions(new String[] {"*.xml"});
				fd.open();
				if (fd.getFileName().equals(""))
					return;
				try {
					File file = FileSystems.getDefault().getPath(fd.getFilterPath(), fd.getFileName()).toFile();
					FileInputStream fis = new FileInputStream(file);
					property.loadFromXML(fis);
					fis.close();
					redraw(property);
				} catch (InvalidPropertiesFormatException e1) {
					MessageBox msg = new MessageBox(shell, SWT.OK);
					msg.setText(Controller.rb.getString("invalidOptionFile"));
					msg.setMessage(Controller.rb.getString("invalidOptionFileMessage"));	
					msg.open();
				} catch (FileNotFoundException e1) {
					MessageBox msg = new MessageBox(shell, SWT.OK);
					msg.setText(Controller.rb.getString("fileNotFound"));
					msg.setMessage(Controller.rb.getString("fileNotFoundMessage"));	
					msg.open();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		shell.pack();
	}

	
	public boolean open() {
		Display disp = getParent().getDisplay();
		shell.open();
		while (!shell.isDisposed()) {
			if (!disp.readAndDispatch()) {
				disp.sleep();
			}
		}
		disposeShell();	
		return isSetOption();
	}
	
	public void disposeShell() {
		shell.dispose();
	}
	
	private Properties readOptions() {
		Properties property = new Properties();
		property.setProperty("playerType", options.getProperty("playerType"));
		if (portLabel.getEnabled()) {
			property.setProperty("port", portNum.getText());
		}
		if (hostLabel.getEnabled()) {
			property.setProperty("host", hostText.getText());
		}
		
		property.setProperty("username", userText.getText());
		property.setProperty("fieldSize", Integer.toString(sizeField.getSelection()));
		property.setProperty("isRandom", Boolean.toString(randomButton.getSelection()));
		
		property.setProperty("aerocarierCount", Integer.toString(aerocarierCount.getSelection()));
		property.setProperty("battleshipCount", Integer.toString(battleshipCount.getSelection()));
		property.setProperty("cruiserCount", Integer.toString(cruiserCount.getSelection()));
		property.setProperty("destroyerCount", Integer.toString(destroyerCount.getSelection()));
		
		if (mineUseButton.getSelection()) {
			property.setProperty("mineCount", Integer.toString(mineCount.getSelection()));
		} else {
			property.setProperty("mineCount", "0");
		}
		return property;
	}
	
	public void checkOption() throws InvalidFieldSizeException {
		int shipArea = 0;
		shipArea += 10 * aerocarierCount.getSelection();
		shipArea += 8 * battleshipCount.getSelection();
		shipArea += 6 * cruiserCount.getSelection();
		shipArea += 2 * destroyerCount.getSelection();
		if (shipArea > sizeField.getSelection() * sizeField.getSelection()) {
			throw new InvalidFieldSizeException(shipArea);
		}
	}
	
	public void redraw(Properties property) {
		if (property.containsKey("port")) {
			portLabel.setEnabled(true);
			portNum.setEnabled(true);
			portNum.setSelection(Integer.parseInt(property.getProperty("port")));
		}
		if (property.containsKey("host")) {
			hostLabel.setEnabled(true);
			hostText.setEnabled(true);
			hostText.setText(property.getProperty("host"));
		}
		userText.setText(property.getProperty("username"));
		sizeField.setSelection(Integer.parseInt(property.getProperty("fieldSize")));
		randomButton.setSelection(Boolean.parseBoolean(property.getProperty("isRandom")));
		
		aerocarierCount.setSelection(Integer.parseInt(property.getProperty("aerocarierCount")));
		battleshipCount.setSelection(Integer.parseInt(property.getProperty("battleshipCount")));
		cruiserCount.setSelection(Integer.parseInt(property.getProperty("cruiserCount")));
		destroyerCount.setSelection(Integer.parseInt(property.getProperty("destroyerCount")));
		
		mineCount.setSelection(Integer.parseInt(property.getProperty("mineCount")));
		if (mineCount.getSelection() > 0) {
			mineUseButton.setSelection(true);
			mineLabel.setEnabled(true);
			mineCount.setEnabled(true);
		} else {
			mineUseButton.setSelection(false);
			mineLabel.setEnabled(false);
			mineCount.setEnabled(false);
		}
	}
	
	public boolean isSetOption() {
		return options.size() > 1;
	}
	
	public Properties getOptions() {
		return options;
	}
}
