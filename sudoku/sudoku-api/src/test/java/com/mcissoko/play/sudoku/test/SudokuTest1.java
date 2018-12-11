package com.mcissoko.play.sudoku.test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;

import com.mcissoko.game.sudoku.client.Sudoku;
import com.mcissoko.game.sudoku.core.Grid;
import com.mcissoko.game.sudoku.core.Group;
import com.mcissoko.game.sudoku.core.IPromise;
import com.mcissoko.game.sudoku.core.PositionIndexEnum;
import com.mcissoko.game.sudoku.core.SudokuFactory;
import com.mcissoko.game.sudoku.core.SudokuImpl;
import com.mcissoko.game.sudoku.core.enumeration.GroupIndexEnum;
import com.mcissoko.game.sudoku.core.enumeration.SudokuLevelEnum;

public class SudokuTest1 {

	public static void main(String[] args) {
		test3();
	}
	
	
	public static void test3(){
		Deque<String> queue = new ArrayDeque<>();
		queue.addFirst("Un");
		queue.addFirst("deux");
		queue.addFirst("trois");
		queue.addFirst("quatre");
		
		System.out.println(queue.pop());
		System.out.println(queue);
		
	}
	public static void test2(){
		List<Integer> numbers = new ArrayList<>();
		for(int i = 1; i <= 9; i++){
			numbers.add(i);
		}
		Random randomizer = new Random();
		while(true){
			
			if(numbers.isEmpty()){
				return;
			}
			Integer random = numbers.get(randomizer.nextInt(numbers.size()));
			numbers.remove(random);
			System.out.println(random);
		}
		
	}
	
	

}
