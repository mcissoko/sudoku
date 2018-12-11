package com.mcissoko.game.sudoku.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mcissoko.game.sudoku.client.Sudoku;
import com.mcissoko.game.sudoku.core.enumeration.GroupIndexEnum;
import com.mcissoko.game.sudoku.core.enumeration.StateBoxEnum;
import com.mcissoko.game.sudoku.core.enumeration.SudokuLevelEnum;

public class SudokuImpl implements Serializable, Sudoku {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Logger log = LoggerFactory.getLogger(SudokuImpl.class);

	private Grid grid;
	protected Date startDate;
	protected Date endDate;
	protected long duration;
	protected ResolutionState state;

	private AtomicBoolean canceled;

	private SudokuLevelEnum level;
	
	SudokuImpl() {
		this.init();
	}


	@Override
	public IPromise resolveAsyn(Grid grid, IMonitor mon) {
		this.canceled.set(false);
		this.grid = grid;
		if (mon == null) {
			mon = fakeMonitor();
		}
		CompletableFuture<Grid> promise = this.resolving(mon);

		this.canceled = new AtomicBoolean(false);
		return new PromiseImpl(promise, this);
	}
	
	@Override
	public IPromise newGridAsync(SudokuLevelEnum level) {
		if(level == null) {
			this.level = SudokuLevelEnum.DEFAULT;
		}else {
			this.level = level;
		}
		init();
		if(SudokuLevelEnum.DEFAULT == level){
			return new PromiseImpl(CompletableFuture.supplyAsync(() -> this.grid), this );
		}
		CompletableFuture<Grid> promiseSolution = resolving(fakeMonitor());

		CompletableFuture<Grid> result = promiseSolution.thenApply(solution -> this.buildNewGrid(solution));
		this.canceled = new AtomicBoolean(false);
		return new PromiseImpl(result, this);
	}
	
	@Override
	public Grid resolve(Grid grid, IMonitor mon) {
		this.canceled.set(false);
		this.grid = grid;
		if (mon == null) {
			mon = fakeMonitor();
		}
		
		return this.executeResolve(mon);
	}
	
	@Override
	public Grid newGrid(SudokuLevelEnum level) {
		if(level == null) {
			this.level = SudokuLevelEnum.DEFAULT;
		}else {
			this.level = level;
		}
		init();
		
		if(SudokuLevelEnum.DEFAULT == level){
			return this.grid;
		}
		Grid solution = this.executeResolve(fakeMonitor());
		return this.buildNewGrid(solution);
	}
	
	@Override
	public Grid getGrid() {
		return this.grid;
	}


	protected void cancel() {
		this.canceled.set(true);
	}

	/*====================== private methods =======================*/
	
	private IMonitor fakeMonitor() {
		return new IMonitor() {
			@Override
			public void erase(Box caze) {
			}
			
			@Override
			public void display(Box caze) {
			}
		};
	}

	private Grid executeResolve(IMonitor monitor) {

		startDate = new Date();

		res(monitor);
		duration = endDate.getTime() - startDate.getTime();
		System.out.println("Start date: " + startDate);
		System.out.println("End date:   " + endDate);
		System.out.println("Durée: " + duration);
		if (state == ResolutionState.SOLVED) {
			return this.grid;
		}
		return null;

	}

	private CompletableFuture<Grid> resolving(IMonitor mon) {
		CompletableFuture<Grid> promise = CompletableFuture.supplyAsync(() -> executeResolve(mon));

		return promise;
	}

	private void init() {
		this.grid = new Grid();
		this.duration = 0;
		this.canceled = new AtomicBoolean(false);
		this.startDate = null;
		this.endDate = null;
		this.state = ResolutionState.INITIAL;
	}

	private Grid buildNewGrid(Grid solution) {

		Grid newGrid = createNewEmptyGrid();

		Map<Integer, Integer> mapper = (new LevelMapper(level)).getMapper();

		List<GroupIndexEnum> groupIndexList = Arrays.asList(GroupIndexEnum.values());
		Collections.shuffle(groupIndexList);
		int i = 1;
		for (GroupIndexEnum groupIndexEnum : groupIndexList) {
			Group group = solution.getGroup(groupIndexEnum);
			// index.setGroupIndexEnum(groupIndexEnum);
			List<PositionIndexEnum> posIndexList = Arrays.asList(PositionIndexEnum.values());
			Collections.shuffle(posIndexList);
			int limit = mapper.get(i);

			for (PositionIndexEnum positionIndexEnum : posIndexList) {

				if (limit > 0) {
					newGrid.getGroup(groupIndexEnum).fixBox(positionIndexEnum,
							group.getBox(positionIndexEnum).getContent());
				} else {
					break;
				}

				limit--;
			}
			i++;
		}
		this.grid = newGrid;
		return newGrid;
	}

	private void res(IMonitor monitor) {
		try {
			canceled = new AtomicBoolean(false);
			if (!this.grid.isValid()) {
				endDate = new Date();
				// pas de solution possible
				log.warn("Pas de solution possible pour cette grille\n{}", this.grid);
				state = ResolutionState.NO_AVAILABLE_SOLUTION;
				return;
			}

			// Date t1 = new Date();
			Box candidate = grid.random();
			log.info("Case depart: " + candidate);
			boolean gridFilled;
			gridFilled = false;
			boolean seach = true;
			ResultMapper result = new ResultMapper(gridFilled, candidate);
			while (!gridFilled && !canceled.get()) {

				result.setGridFilled(gridFilled);
				if (seach) {
					gridFilled = true;
					for (GroupIndexEnum groupIndexEnum : GroupIndexEnum.values()) {
						if (canceled.get()) {
							state = ResolutionState.CANCELED;
							return;
						}
						Group group = grid.getGroup(groupIndexEnum);
						result = group.checkCandidate(result);
						gridFilled = gridFilled & result.isGridFilled();
					}
					candidate = result.getCandidate();
				}

				if (gridFilled) {
					this.endDate = new Date();
					if (grid.isSudoku()) {
						log.info("\n {}", grid);
						log.info("resolu");
						System.out.println("resolu");

						state = ResolutionState.SOLVED;
						return;
					}
					log.info("\n {}", grid);
					log.info("Taille de la sequence", grid.getPlaySequence().size());
					log.info("Echec");
					state = ResolutionState.FAILED_AFTER_FILLED;
					return;
				} else {
					if (candidate.getState() == StateBoxEnum.FILLED || candidate.getState() == StateBoxEnum.FIXED) {
						seach = true;
						continue;
					}
				}

				PlaySequence playSequence = candidate.fill();
				if (playSequence == null) {
					candidate.resetTried();

					log.info("\n {}", grid);
					candidate = grid.restaure();
					if (candidate == null) {
						this.endDate = new Date();
						log.info("Echec");
						log.info("Taille de la sequence", grid.getPlaySequence().size());
						state = ResolutionState.NO_SOLUTION;
						return;
					}
					seach = false;
					gridFilled = false;

					monitor.erase(candidate);

					continue;
				}

				candidate.getGroup().removeCandidate(candidate, playSequence);

				grid.addPlaySequence(playSequence);

				monitor.display(candidate);

				seach = true;
				gridFilled = false;
			}
		} catch (IllegalStateException e) {
			log.error("Exception", e);
			state = ResolutionState.ERROR;
			return;
		}
		state = ResolutionState.INITIAL;
		return;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	private Grid createNewEmptyGrid() {
		return new Grid();
	}

	protected long getDuration() {
		return duration;
	}

	protected boolean isCanceled() {
		return this.canceled.get();
	}


	@Override
	public Box resetContent(Box box) {
		if(box == null) {
			return null;
		}
		box.resetContent();
		return box;
	}

}
