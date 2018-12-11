package com.mcissoko.game.sudoku.core;

public enum PositionIndexEnum {

	INDEX_1(1),
	INDEX_2(2),
	INDEX_3(3),
	INDEX_4(4),
	INDEX_5(5),
	INDEX_6(6),
	INDEX_7(7),
	INDEX_8(8),
	INDEX_9(9);
	
	int index;
	
	private PositionIndexEnum(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
	
	public static PositionIndexEnum fromIndex(int index){
		switch (index) {
		case 1:
			return INDEX_1;
		case 2:
			return INDEX_2;
		case 3:
			return INDEX_3;
		case 4:
			return INDEX_4;
		case 5:
			return INDEX_5;
		case 6:
			return INDEX_6;
		case 7:
			return INDEX_7;
		case 8:
			return INDEX_8;
		
		default:
			return INDEX_9;
		}
		
	}
}
