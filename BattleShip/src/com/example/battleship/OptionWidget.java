package com.example.battleship;

import org.eclipse.swt.SWT;
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
	
	public OptionWidget(Display disp) {
		this.disp = disp;
		this.shell = createShell(disp);
	}

	private Shell createShell(Display disp) {
		Shell shell = new Shell(disp, SWT.DIALOG_TRIM | SWT.RESIZE);
		shell.setText("BattleShip -> Option");
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);
		layout.horizontalSpacing = 8;
		
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
		GridData gd = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		randomButton.setLayoutData(gd);
		
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
		destroyerLabel.setText("Количество крейсеров = ");
		destroyerLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		
		destroyerCount = new Spinner(countGroup, SWT.READ_ONLY);
		destroyerCount.setMinimum(0);
		destroyerCount.setMaximum(8);
		destroyerCount.setIncrement(1);
		destroyerCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		
		mineUseButton = new Button(countGroup, SWT.CHECK);
		mineUseButton.setText("Использовать мины?");
		gd = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		mineUseButton.setLayoutData(gd);
		
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
		dispose();	
	}
	
	public void dispose() {
		disp.dispose();
	}
	
	public static void main(String[] args) {
		OptionWidget opt = new OptionWidget(new Display());
		opt.start();
	}
}
