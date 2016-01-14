package com.example.battleship;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public enum Direction implements Externalizable {
	UP 		(-2) {
		@Override
		public Direction getOpposite() {
			return Direction.DOWN;
		}

		@Override
		public Direction getSquare() {
			return Direction.LEFT;
		}
	}, 
	DOWN 	(2) {
		@Override
		public Direction getOpposite() {
			return Direction.UP;
		}

		@Override
		public Direction getSquare() {
			return Direction.RIGHT;
		}
	}, 
	LEFT 	(-1) {
		@Override
		public Direction getOpposite() {
			return Direction.RIGHT;
		}

		@Override
		public Direction getSquare() {
			return Direction.UP;
		}
	}, 
	RIGHT 	(1) {
		@Override
		public Direction getOpposite() {
			return Direction.LEFT;
		}

		@Override
		public Direction getSquare() {
			return Direction.DOWN;
		}
	};
	
	private int value;
	
	public abstract Direction getOpposite();
	public abstract Direction getSquare();
	
	private Direction(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		value = in.readInt();
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(value);
	}
}
