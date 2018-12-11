package com.mcissoko.play.sudoku.test;

import java.util.concurrent.CompletableFuture;

import com.mcissoko.game.sudoku.client.Sudoku;
import com.mcissoko.game.sudoku.core.Grid;
import com.mcissoko.game.sudoku.core.Group;
import com.mcissoko.game.sudoku.core.IPromise;
import com.mcissoko.game.sudoku.core.PositionIndexEnum;
import com.mcissoko.game.sudoku.core.SudokuFactory;
import com.mcissoko.game.sudoku.core.enumeration.GroupIndexEnum;
import com.mcissoko.game.sudoku.core.enumeration.SudokuLevelEnum;

public class SudokuTest2 {

	public static void main(String[] args) {
		test6();
	}

	@SuppressWarnings("unused")
	private static void test8() {
		String name = "bbb";
//		CompletableFuture.supplyAsync(() -> {
//		      if (name == null) {
//		          throw new RuntimeException("Computation error!");
//		      }
//		      return "Hello, " + name;
//		  }).exceptionally((overcooked) -> {
//		       
//		  }).thenAccept(x -> System.out.println(x));
//		 
	}

	private static void test7() {

		CompletableFuture.supplyAsync(() -> 5)
		.thenApply(i -> i * 3)
		.thenAccept(j -> System.out.println("The result is " + j))
		.thenRun(() -> System.out.println("Finished."));
	}
	static Grid grid = null;

	private static void test6() {
		
		Sudoku sudoku = SudokuFactory.create();
		 IPromise promise = sudoku.newGridAsync(SudokuLevelEnum.EASY);
				promise.done(g -> { 
			grid = g;
			System.out.println("new grid \n"+g);
			
		});

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		promise = sudoku.resolveAsyn(grid, null);
		promise.done(done -> {
			System.out.println("solved :\n"+done);
		});
		

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static void test5(){}
	
}
