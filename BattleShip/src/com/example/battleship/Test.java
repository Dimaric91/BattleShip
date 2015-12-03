package com.example.battleship;

import java.util.EnumMap;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Direction d = Direction.UP;
		
		for (Direction dir : Direction.values()) {
			System.out.println(dir + "<->" + dir.getOpposite());
			
		}
	}

}
