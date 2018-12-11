package com.mcissoko.game.sudoku.core.enumeration;

import java.util.HashMap;
import java.util.Map;

public enum SudokuLevelEnum {

	EASY(0, "Easy"),
	MEDIUM(1, "Medium"),
	HARD(2, "Hard"),
	EXPERT(3, "Expert"),
	DEFAULT(-1, "Free");
	
	private final Integer id;
	private final String label;
	
	private static final Map<String, SudokuLevelEnum> map;
	static {
		map = new HashMap<String, SudokuLevelEnum>();
		map.put(EASY.getLabel(), EASY);
		map.put(MEDIUM.getLabel(), MEDIUM);
		map.put(HARD.getLabel(), HARD);
		map.put(EXPERT.getLabel(), EXPERT);
	}
	private SudokuLevelEnum(Integer id, String label) {
		this.id = id;
		this.label = label;
	}

	public Integer getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}
	
	private SudokuLevelEnum fromLabel(String label){
		return map.get(label);
	}
	
}
