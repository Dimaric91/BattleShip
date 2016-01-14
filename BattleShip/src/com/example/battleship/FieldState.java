package com.example.battleship;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public enum FieldState implements Externalizable {
	HIDDEN_STATE(0) {
		@Override
		public boolean isShip() {
			return false;
		}
	},
	EMPTY_STATE(1) {
		@Override
		public boolean isShip() {
			return false;
		}
	},
	CHECKED_FIELD_STATE(2) {
		@Override
		public boolean isShip() {
			return false;
		}
	},
	MINE_STATE(3) {
		@Override
		public boolean isShip() {
			return false;
		}
	},
	KILLED_MINE_STATE(4) {
		@Override
		public boolean isShip() {
			return false;
		}
	},
	SHIP_STATE(5) {
		@Override
		public boolean isShip() {
			return true;
		}
	},
	PADDED_SHIP_STATE(6) {
		@Override
		public boolean isShip() {
			return true;
		}
	},
	KILLED_SHIP_STATE(7) {
		@Override
		public boolean isShip() {
			return true;
		}
	};
	
	private int value;
	
	private FieldState(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public abstract boolean isShip();
	
	@Override
	public String toString() {
		return Integer.toString(value);
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
