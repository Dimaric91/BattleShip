package com.example.battleship.widgets;


import java.util.Properties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.example.battleship.Controller;

public class HelloWidget extends Dialog {
	
	private Shell shell;
	
	private Button localButton;
	private Button optionsButton;
	
	private Properties optProperty;
	
	public HelloWidget(Shell parent) {
		super(parent, SWT.DIALOG_TRIM);
		this.shell = new Shell(parent, getStyle());
		createContent(this.shell);
	}

	private void createContent(final Shell shell) {
		if (Controller.icon == null) {
			Controller.icon = new Image(getParent().getDisplay(), getClass().getResourceAsStream("icon.png"));
		}
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 13;
		shell.setLayout(new GridLayout(1, false));
		shell.setText(Controller.rb.getString("gameName"));
		shell.setImage(Controller.icon);
		
		FormToolkit tool = new FormToolkit(getParent().getDisplay());
		Form form = tool.createForm(shell);
		form.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		localButton = tool.createButton(form.getBody(), Controller.rb.getString("newGame"), SWT.PUSH);
		optionsButton = tool.createButton(form.getBody(), Controller.rb.getString("options"), SWT.PUSH);
		
		form.getBody().setLayout(layout);
		localButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
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
		
		optionsButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				startOptionWidget();
			}
		});
		
		shell.pack();
	}
	
	private void startOptionWidget() {
		OptionWidget option = new OptionWidget(shell);
		if(option.open()) {
			optProperty = option.getOptions();
		}
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
	
	public boolean isSetOption() {
		return optProperty != null;
	}
	
	public Properties getOptions() {
		return optProperty;
	}
}
