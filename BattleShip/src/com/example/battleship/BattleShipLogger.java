package com.example.battleship;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class BattleShipLogger extends OutputStream {

	private Display disp;
	private Text textLog;
	
	public BattleShipLogger(Display disp, Text textLog) {
		this.disp = disp;
		this.textLog = textLog;
	}
	
	@Override
	public void write(int b) throws IOException {
		disp.asyncExec(new Runnable() {
			
			@Override
			public void run() {
				textLog.append(String.valueOf((char)b));
			}
		});
	}

}
