package com.example.battleship;

import java.lang.reflect.Array;
import java.util.Arrays;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

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
	private Group sizeFieldGroup;
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
	
	private Properties options;
	private Group userGroup;
	private Label userLabel;
	private Text userText;
	
	public OptionWidget(Display disp) {
		this.disp = disp;
		this.shell = createShell(disp);
		this.options = new Properties();
	}

	private Shell createShell(Display disp) {
		Shell shell = new Shell(disp, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("BattleShip -> Option");
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);
		layout.horizontalSpacing = 8;
		
		userGroup = new Group(shell, SWT.NONE);
		userGroup.setText("Имя пользователя");
		userGroup.setLayout(new GridLayout(2, false));
		userGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		userLabel = new Label(userGroup, SWT.NONE);
		userLabel.setText("Имя пользователя = ");
		userLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		userText = new Text(userGroup, SWT.BORDER);
		userText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		
		sizeFieldGroup = new Group(shell, SWT.NONE);
		sizeFieldGroup.setLayout(new GridLayout(2, false));
		sizeFieldGroup.setText("Размер поля");
		sizeFieldGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		sizeLabel = new Label(sizeFieldGroup, SWT.NONE);
		sizeLabel.setText("Размер поля = ");
		sizeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		sizeCount = new Spinner(sizeFieldGroup, SWT.NONE);
		sizeCount.setMinimum(0);
		sizeCount.setMaximum(40);
		sizeCount.setIncrement(1);
		sizeCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
	
		randomButton = new Button(sizeFieldGroup, SWT.CHECK);
		randomButton.setText("Случайное расположение кораблей?");
		randomButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2 ,1));
		
		randomButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				randomButton.setSelection(true);
			}
		});
		
		randomButton.setSelection(true);
		
		countGroup = new Group(shell, SWT.NONE);
		countGroup.setLayout(new GridLayout(2, false));
		countGroup.setText("Объекты");
		countGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		aerocarierLabel = new Label(countGroup, SWT.NONE);
		aerocarierLabel.setText("Количество авианосцев = ");
		aerocarierLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		aerocarierCount = new Spinner(countGroup, SWT.READ_ONLY);
		aerocarierCount.setMinimum(0);
		aerocarierCount.setMaximum(5);
		aerocarierCount.setIncrement(1);
		aerocarierCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		
		battleshipLabel = new Label(countGroup, SWT.NONE);
		battleshipLabel.setText("Количество линейных кораблей = ");
		battleshipLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		battleshipCount = new Spinner(countGroup, SWT.READ_ONLY);
		battleshipCount.setMinimum(0);
		battleshipCount.setMaximum(6);
		battleshipCount.setIncrement(1);
		battleshipCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		
		cruiserLabel = new Label(countGroup, SWT.NONE);
		cruiserLabel.setText("Количество крейсеров = ");
		cruiserLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		cruiserCount = new Spinner(countGroup, SWT.READ_ONLY);
		cruiserCount.setMinimum(0);
		cruiserCount.setMaximum(7);
		cruiserCount.setIncrement(1);
		cruiserCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		
		destroyerLabel = new Label(countGroup, SWT.NONE);
		destroyerLabel.setText("Количество эсминцев = ");
		destroyerLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		destroyerCount = new Spinner(countGroup, SWT.READ_ONLY);
		destroyerCount.setMinimum(0);
		destroyerCount.setMaximum(8);
		destroyerCount.setIncrement(1);
		destroyerCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		
		mineUseButton = new Button(countGroup, SWT.CHECK);
		mineUseButton.setText("Использовать мины?");
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
		mineLabel.setText("Количество мин = ");
		mineLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		mineCount = new Spinner(countGroup, SWT.READ_ONLY);
		mineCount.setMinimum(0);
		mineCount.setMaximum(5);
		mineCount.setIncrement(1);
		mineCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		
		buttonGroup = new Composite(shell,  SWT.NONE);
		buttonGroup.setLayout(new GridLayout(3, false));
		buttonGroup.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false));
		
		saveButton = new Button(buttonGroup, SWT.PUSH);
		saveButton.setText("Save Settings");
		saveButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		loadButton = new Button(buttonGroup, SWT.PUSH);
		loadButton.setText("Load Settings");
		loadButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		applyButton = new Button(buttonGroup, SWT.PUSH);
		applyButton.setText("Apply");
		applyButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		applyButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					readOptions();
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
	
	public static void main(String[] args) {
		OptionWidget opt = new OptionWidget(new Display());
		opt.start();
	}
	
	private void readOptions() {
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
	
	public void validateOptions() {
		if (options.isEmpty()) {
			return;
		}
		
		
	}
	
	public Properties getOptions() {
		return options;
	}
}
