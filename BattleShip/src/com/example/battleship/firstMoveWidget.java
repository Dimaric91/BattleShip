package com.example.battleship;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.example.battleship.ships.Ship;

public class firstMoveWidget {
	
	private Display disp;
	private Shell shell;
	private int cellSize;
	private GameZone fields;
	private List<Ship> ships;
	private Canvas ourZone;
	
	public firstMoveWidget(Display disp) {
		this.disp = disp;
		this.shell = createShell();
	}
	
	private Shell createShell() {
		Shell shell = new Shell(disp, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("BattleShip -> firstMove");
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);
		layout.horizontalSpacing = 8;
		
		Canvas fieldZone = new Canvas(shell, SWT.BORDER);
		fieldZone.setLayoutData(new GridData(cellSize * fields.getSize(), cellSize * fields.getSize()));
		Group shipGroup = new Group(shell, SWT.NONE);
		shipGroup.setLayoutData(new GridData(cellSize * fields.getSize(), cellSize * fields.getSize()));
		shipGroup.setLayout(new GridLayout(1, false));
		
		int[] counts = new int[5];
		Arrays.fill(counts, 0);
		for (Ship s : ships) {
			counts[s.getSize() - 1]++;
		}
		
		for (int i = 4; i > 0; i--) {
			if (counts[i] > 0) {
				Label shipLabel = new Label(shipGroup, SWT.BORDER);
				shipLabel.setBackground(disp.getSystemColor(SWT.COLOR_GRAY));
				shipLabel.setLayoutData(new GridData(cellSize * (i + 1), cellSize));
				shipLabel.setText("X" + counts[i]);
				shipLabel.setDragDetect(true);
			}
		}

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
		
	}

}
