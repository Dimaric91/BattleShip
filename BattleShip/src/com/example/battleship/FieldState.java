package com.example.battleship;

public enum FieldState {
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
}
