package com.example.battleship;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class OptionWidget {
	private Display disp;
	private Shell shell;
	
	public OptionWidget(Display disp) {
		this.disp = disp;
		this.shell = createShell(disp);
	}

	private Shell createShell(Display disp) {
		Shell shell = new Shell(disp, SWT.DIALOG_TRIM | SWT.RESIZE);
		GridLayout layout = new GridLayout(1, false);
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
