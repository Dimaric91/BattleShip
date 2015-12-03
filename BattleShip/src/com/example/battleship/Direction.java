package com.example.battleship;

public enum Direction {
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
}
