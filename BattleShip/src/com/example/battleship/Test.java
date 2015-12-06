package com.example.battleship;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		for (Direction dir : Direction.values()) {
			System.out.println(dir + "<->" + dir.getOpposite());
			
		}
	}

}
