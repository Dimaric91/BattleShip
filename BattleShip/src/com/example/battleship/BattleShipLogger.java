package com.example.battleship;

import java.io.IOException;
import java.io.OutputStream;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class BattleShipLogger extends OutputStream {

	private Display disp;
	private Text textLog;
	private byte[] buffer;
	private int ptr;
	
	public BattleShipLogger(Display disp, Text textLog, int capacity) {
		this.buffer = new byte[capacity];
		this.ptr = 0;
		this.disp = disp;
		this.textLog = textLog;
	}
	
	@Override
	public void write(int b) throws IOException {
		buffer[ptr++] = (byte)b;
	}
	
	@Override
	public void flush() throws IOException {
		disp.syncExec(new Runnable() {
			
			@Override
			public void run() {
				String str = new String(buffer, 0, ptr);
				textLog.append(str);
				ptr = 0;
			}
		});
		super.flush();
	}

}
