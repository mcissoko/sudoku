package com.mcissoko.game.sudoku.core;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class PromiseImpl implements IPromise {

	private CompletableFuture<Grid> promise;
	private SudokuImpl sudoku;
	private boolean execute;

	
	public PromiseImpl(CompletableFuture<Grid> promise, SudokuImpl sudoku) {
		super();
		this.promise = promise;
		this.sudoku = sudoku;
		this.execute = true;
	}
	
	public PromiseImpl() {
		super();
		this.promise = null;
		this.sudoku = null;
		this.execute = false;
	}

	@Override
	public void done(Consumer<Grid> action) {
		
		if(!execute) {
			return;
		}
		promise.thenAccept(action);
		
	}

	@Override
	public void cancel() {
		if(!execute) {
			return;
		}
		this.sudoku.cancel();
		promise.cancel(true);
	}

	@Override
	public void fail(Function<Throwable, ? extends Grid> fn) {
		if(promise != null) {
			return;
		}
		promise.exceptionally(fn);
	}

	@Override
	public boolean isCanceled() {
		if(!execute) {
			return false;
		}
		return this.sudoku.isCanceled();
	}

	@Override
	public long getDuration() {
		if(!execute) {
			return 0;
		}
		return sudoku.getDuration();
	}
	
	
	
}
