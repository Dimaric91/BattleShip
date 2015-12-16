package com.example.battleship;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class HelloWidget {
	private Display disp;
	private Shell shell;
	
	private Button localButton;
	private Button networkButton;
	private Button optionsButton;
	
	private int optFieldSize;
	private boolean isRandom;
	private int[] optShipCount;
	private int optMineCount;
	
	private boolean isSetOption;
	
	public HelloWidget(Display disp) {
		this.disp = disp;
		this.shell = createShell(this.disp);
		isSetOption = false;
	}

	private Shell createShell(Display disp) {
		Shell shell = new Shell(disp, SWT.DIALOG_TRIM | SWT.RESIZE);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 13;
		shell.setLayout(new GridLayout(1, false));
		//shell.setLayout(layout);
		shell.setText("BattleShip!");
		
		
		FormToolkit tool = new FormToolkit(disp);
		Form form = tool.createForm(shell);
		form.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		localButton = tool.createButton(form.getBody(), "Local Game", SWT.PUSH);
		networkButton = tool.createButton(form.getBody(), "Network Game", SWT.PUSH);
		optionsButton = tool.createButton(form.getBody(), "Options", SWT.PUSH);
		
		form.getBody().setLayout(layout);
		localButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		networkButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		optionsButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		
		localButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					if (!isSetOption) {
						startOptions();
					}
					disposeShell();
				}
			}
		});
		
		networkButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					if (e.button == 1) {
						MessageBox message = new MessageBox(shell);
						message.setText("Mode not support");
						message.setMessage("Network mode not supported");
						message.open();
					}
				}
			}
		});
		
		optionsButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				startOptions();
			}
		});
		
		shell.pack();
		
		return shell;
	}
	
	private void startOptions() {
		OptionWidget option = new OptionWidget(disp);
		option.start();
		optFieldSize = option.getFieldSize();
		optShipCount = option.getShips();
		isRandom = option.isRandom();
		optMineCount = option.getMine();
		isSetOption = true;
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
	
	public int getOptFieldSize() {
		return optFieldSize;
	}
	
	public int getOptMineCount() {
		return optMineCount;
	}
	
	public boolean isRandom() {
		return isRandom;
	}
	
	public int[] getOptShipCount() {
		return optShipCount;
	}
	
	public boolean isSetOption() {
		return isSetOption;
	}
}
