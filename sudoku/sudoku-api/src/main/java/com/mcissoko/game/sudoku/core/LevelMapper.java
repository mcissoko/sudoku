package com.mcissoko.game.sudoku.core;

import java.util.HashMap;
import java.util.Map;

import com.mcissoko.game.sudoku.core.enumeration.SudokuLevelEnum;

public class LevelMapper {

	private Map<Integer, Integer> mapper;

	public LevelMapper(SudokuLevelEnum level) {
		mapper = new HashMap<>();
		switch (level) {
		case EASY:
			mapper.put(1, 4);
			mapper.put(2, 5);
			mapper.put(3, 4);
			mapper.put(4, 3);
			mapper.put(5, 5);
			mapper.put(6, 4);
			mapper.put(7, 4);
			mapper.put(8, 3);
			mapper.put(9, 6);
			
			break;
		case MEDIUM:
			mapper.put(1, 5);
			mapper.put(2, 3);
			mapper.put(3, 3);
			mapper.put(4, 4);
			mapper.put(5, 2);
			mapper.put(6, 5);
			mapper.put(7, 4);
			mapper.put(8, 3);
			mapper.put(9, 5);
			
			break;
		case HARD:
			mapper.put(1, 4);
			mapper.put(2, 4);
			mapper.put(3, 2);
			mapper.put(4, 1);
			mapper.put(5, 2);
			mapper.put(6, 5);
			mapper.put(7, 4);
			mapper.put(8, 3);
			mapper.put(9, 5);
			
			break;
		case EXPERT:
			mapper.put(1, 1);
			mapper.put(2, 0);
			mapper.put(3, 3);
			mapper.put(4, 4);
			mapper.put(5, 1);
			mapper.put(6, 2);
			mapper.put(7, 2);
			mapper.put(8, 3);
			mapper.put(9, 4);
			
			break;

		default:
			break;
		}
		
	}

	public Map<Integer, Integer> getMapper() {
		return mapper;
	}

	
	
	
}
