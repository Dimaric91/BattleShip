package com.example.battleship;


import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class HelloWidget {
	private Display disp;
	private Shell shell;
	
	private Button localButton;
	//private Button networkButton;
	private Button optionsButton;
	
	private Properties optProperty;
	public HelloWidget(Display disp) {
		this.disp = disp;
		this.shell = createShell(this.disp);
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
		localButton = tool.createButton(form.getBody(), "New Game", SWT.PUSH);
		//networkButton = tool.createButton(form.getBody(), "Network Game", SWT.PUSH);
		optionsButton = tool.createButton(form.getBody(), "Options", SWT.PUSH);
		
		form.getBody().setLayout(layout);
		localButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		//networkButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		optionsButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		
		localButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					if (!isSetOption()) {
						startOptionWidget();
					}
					if (isSetOption()) {
						disposeShell();
					}
				}
			}
		});
		
//		networkButton.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseDown(MouseEvent e) {
//				
//			}
//		});
		
		optionsButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				startOptionWidget();
			}
		});
		
		shell.pack();
		
		return shell;
	}
	
	private void startOptionWidget() {
		OptionWidget option = new OptionWidget(disp);
		option.start();
		optProperty = option.getOptions();
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
	
	public boolean isSetOption() {
		return (optProperty != null) && !optProperty.isEmpty();
	}
	
	public Properties getOptions() {
		return optProperty;
	}
}
