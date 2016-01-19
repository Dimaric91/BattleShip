package com.example.battleship.widgets;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.example.battleship.Controller;
import com.example.battleship.exception.InvalidFieldSizeException;

public class OptionWidget {
	private Display disp;
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
	private Spinner sizeCount;
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
	
	public OptionWidget(Display disp) {
		this.disp = disp;
		this.shell = createShell(disp);
		this.options = new Properties();
	}

	private Shell createShell(Display disp) {
		Shell shell = new Shell(disp, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText(Controller.rb.getString("gameName") + " -> " + Controller.rb.getString("options"));
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);
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
		
		sizeCount = new Spinner(countGroup, SWT.NONE);
		sizeCount.setMinimum(0);
		sizeCount.setMaximum(40);
		sizeCount.setSelection(10);
		sizeCount.setIncrement(1);
		sizeCount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
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
		
		mineUseButton.setSelection(false);
		
		mineLabel = new Label(countGroup, SWT.NONE);
		mineLabel.setText(Controller.rb.getString("mineCount") + " = ");
		mineLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		mineLabel.setEnabled(false);
		
		mineCount = new Spinner(countGroup, SWT.READ_ONLY);
		mineCount.setMinimum(0);
		mineCount.setMaximum(5);
		mineCount.setIncrement(1);
		mineCount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		mineCount.setEnabled(false);
		
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
					readOptions();
					try {
						checkOption();
					} catch (InvalidFieldSizeException e1) {
						MessageBox msg = new MessageBox(shell, SWT.OK);
						msg.setText(Controller.rb.getString("invalidFieldSize"));
						msg.setMessage(Controller.rb.getString("invalidFieldSizeMessage"));	
						msg.open();
						sizeCount.setSelection((int)Math.sqrt(e1.getShipArea()) + 1);
						return;
					}
					disposeShell();
				}
			}
		});
		
		saveButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Store on properties
			}
		});
		
		loadButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				// TODO loadFromProperties
			}
		});
		
		shell.pack();
		return shell;
	}
	
	public void start() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!disp.readAndDispatch()) {
				disp.sleep();
			}
		}
		disposeShell();	
	}
	
	public void dispose() {
		disp.dispose();
	}
	
	public void disposeShell() {
		shell.dispose();
	}
	
	private void readOptions() {
		if (portLabel.getEnabled()) {
			options.setProperty("port", portNum.getText());
		}
		if (hostLabel.getEnabled()) {
			options.setProperty("host", hostText.getText());
		}
		
		options.setProperty("username", userText.getText());
		options.setProperty("fieldSize", Integer.toString(sizeCount.getSelection()));
		options.setProperty("isRandom", Boolean.toString(randomButton.getSelection()));
		
		options.setProperty("aerocarierCount", Integer.toString(aerocarierCount.getSelection()));
		options.setProperty("battleshipCount", Integer.toString(battleshipCount.getSelection()));
		options.setProperty("cruiserCount", Integer.toString(cruiserCount.getSelection()));
		options.setProperty("destroyerCount", Integer.toString(destroyerCount.getSelection()));
		
		if (mineUseButton.getSelection()) {
			options.setProperty("mineCount", Integer.toString(mineCount.getSelection()));
		} else {
			options.setProperty("mineCount", "0");
		}
	}
	
	public void checkOption() throws InvalidFieldSizeException {
		int shipArea = 0;
		shipArea += 10 * aerocarierCount.getSelection();
		shipArea += 8 * battleshipCount.getSelection();
		shipArea += 6 * cruiserCount.getSelection();
		shipArea += 2 * destroyerCount.getSelection();
		if (shipArea > sizeCount.getSelection() * sizeCount.getSelection()) {
			throw new InvalidFieldSizeException(shipArea);
		}
	}
	
	public Properties getOptions() {
		return options;
	}
}
