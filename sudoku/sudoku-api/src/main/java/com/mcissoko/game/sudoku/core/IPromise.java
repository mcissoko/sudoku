package com.mcissoko.game.sudoku.core;

import java.util.function.Consumer;
import java.util.function.Function;

public interface IPromise {
	
	
	void done(Consumer<Grid> action);

	void cancel();
	
	void fail(Function<Throwable, ? extends Grid> fn);
	
	boolean isCanceled();
		
	long getDuration();

}
