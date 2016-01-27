package com.example.battleship.widgets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.example.battleship.Controller;

public class WaitWidget extends Dialog {
	private Shell shell;
	private ServerSocket serv;
	private Socket socket;
	private boolean isConnect;
	
	public WaitWidget(Shell parent, ServerSocket serv) {
		super(parent, SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		this.shell = new Shell(parent, getStyle());
		createContent(this.shell);
		this.serv = serv;
		this.isConnect = false;
		
	}

	private void createContent(final Shell shell) {
		shell.setText(Controller.rb.getString("gameName") + " -> " + Controller.rb.getString("wait"));
		
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);
		shell.setImage(Controller.icon);
		
		Label info = new Label(shell, SWT.NONE);
		info.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		info.setText(Controller.rb.getString("waitingPlayer"));
		
		
		Button bCancel = new Button(shell, SWT.PUSH);
		bCancel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		bCancel.setText(Controller.rb.getString("cancel"));
		
		bCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					try {
						serv.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					disposeShell();
				}
			}
		});
		shell.pack();
	}
	
	public boolean open() {
		Display disp = getParent().getDisplay();
		shell.open();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					socket = serv.accept();
					isConnect = true;
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}).start();
		while (!shell.isDisposed() && !isConnect) {
			if (!disp.readAndDispatch()) {
				disp.sleep();
			}
		}
		disposeShell();
		return isConnect;
	}

	public void disposeShell() {
		shell.dispose();
	}
	
	public Socket getSocket() {
		return socket;
	}
}


