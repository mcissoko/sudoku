package com.mcissoko.game.view;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import org.apache.commons.lang.StringUtils;

import com.mcissoko.game.MainApp;
import com.mcissoko.game.data.BoxUi;
import com.mcissoko.game.listener.FocusEventListener;
import com.mcissoko.game.listener.InputChangeListener;
import com.mcissoko.game.print.MyPrinter;
import com.mcissoko.game.sudoku.client.Sudoku;
import com.mcissoko.game.sudoku.core.Box;
import com.mcissoko.game.sudoku.core.Grid;
import com.mcissoko.game.sudoku.core.Group;
import com.mcissoko.game.sudoku.core.IMonitor;
import com.mcissoko.game.sudoku.core.IPromise;
import com.mcissoko.game.sudoku.core.IndexBox;
import com.mcissoko.game.sudoku.core.PositionIndexEnum;
import com.mcissoko.game.sudoku.core.Sequence;
import com.mcissoko.game.sudoku.core.SudokuFactory;
import com.mcissoko.game.sudoku.core.enumeration.GroupIndexEnum;
import com.mcissoko.game.sudoku.core.enumeration.InputActionEnum;
import com.mcissoko.game.sudoku.core.enumeration.StateBoxEnum;
import com.mcissoko.game.sudoku.core.enumeration.SudokuLevelEnum;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class RootLayoutController implements IMonitor {

	private MainApp mainApp;
	private Stage primaryStage;
	
	private Sudoku sudoku;
	private Grid solution;
	private Map<String, BoxUi> gridMap;
	
	private final String focusInOthersStyle  = "-fx-background-color: #a9a6d8;";
	private final String focusOutStyle = "-fx-background-color: white;";
	private final String fixedStyle = "-fx-font: bold 26px Arial;";
	private boolean initiating; 
	private final String errorStyle = "-fx-background-color: red;";
	private final String focusInStyle =
	    "-fx-border-color: blue ;"
	    + "-fx-border-width: 4 ; "
	    + "-fx-font: bold 20px Arial;"
	   + "-fx-border-style: segments(5, 5, 5, 5)  line-cap round ;"
	    ;
	
	
	private List<IndexBox> occurIndexList;
	private Deque<Sequence> undoSequences;
	private Deque<Sequence> redoSequences;
	
	private boolean writingSolution;
	
	private List<BoxUi> boxes;

	private Grid grille;
	private MyPrinter myPrinter;
	
	protected IMonitor monitor;
	protected IPromise promise;
	@FXML
    private void handleExit() {
		
        System.exit(0);
    }
	
	private String indexBox(GroupIndexEnum groupIndexEnum, PositionIndexEnum positionIndexEnum) {
		return "g"+ groupIndexEnum.getIndex() + "Box" + positionIndexEnum.getIndex();
	}	

	@FXML private void cancelResolve() {
		//service.cancel();
		//this.sudoku.setGrid(grille);
		this.promise.cancel();
		this.pauseTgl.setSelected(false);
		pauseTgl.setText("PAUSE");
		this.paused = false;

		
		cancelPane.setVisible(false);
		cancelPane.toBack();
		menuPane.setDisable(false);
		unrePane.setDisable(false);
	}
	@FXML private ToggleButton pauseTgl;
	private boolean paused;
	@FXML private void pause() {
		if(pauseTgl.isSelected()) {
			pauseTgl.setText("PLAY");
			this.paused = true;
			System.out.println("paused");
		}else {
			pauseTgl.setText("PAUSE");
			this.paused = false;
			System.out.println("activated");
		}
	}
	
	private void doSolved(Grid grid) {

		this.writingSolution = true;
		for (GroupIndexEnum groupIndexEnum : GroupIndexEnum.values()) {
			Group group = grid.getGroup(groupIndexEnum);

			for (PositionIndexEnum positionIndexEnum : PositionIndexEnum.values()) {
				BoxUi box = gridMap.get(indexBox(groupIndexEnum, positionIndexEnum));
				if (box.isFixed()) {
					continue;
				}
				box.getGroupBox().setText(String.valueOf(group.getBox(positionIndexEnum).getContent()));
			}
		}
		markAsResolved();

		this.writingSolution = false;
	}
	
	@FXML private void resolveCurrentPasAPas() {
		resolve(this.monitor);
	}
	
	private void resolve(IMonitor mon) {
		
		
		System.out.println(grille);
		Grid grilleCopy = grille.copy();
				
		menuPane.setDisable(true);
		unrePane.setDisable(true);
		
		cancelPane.toFront();
		cancelPane.setVisible(true);

		this.promise = sudoku.resolveAsyn(grilleCopy, mon);
		
		this.promise.done(grid -> {
			Platform.runLater(() -> {
				cancelPane.toBack();
				cancelPane.setVisible(false);
				menuPane.setDisable(false);
				unrePane.setDisable(false);
				
				if(grid == null) {
					
					showMessage(AlertType.ERROR, "No solution !");
					return;
				}
				if(this.promise.isCanceled()) {
					return;
				}
				

				doSolved(grid);

			});

		});
		

//		
//		promise.fail(e -> {
//			System.out.println("------------------------" + e);
//			return null;
//		});


	}

	@FXML private void resolveCurrent() {
		resolve(null);
	}
	
	
	@SuppressWarnings("unused")
	private void showMessage (AlertType type, String message) {
		this.showMessage(type, StringUtils.EMPTY, StringUtils.EMPTY, message);
		
	}
	private void showMessage (AlertType type, String title, String message) {
		this.showMessage(type, title, StringUtils.EMPTY, message);
		
	}
	private void showMessage (AlertType type, String title, String header, String message) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.showAndWait().ifPresent(rs -> {
		    if (rs == ButtonType.OK) {
		        System.out.println("Pressed OK.");
		    }
		});
	}
	
	private static final int MAX_SEQUENCE = 100;
	private void registerChange(GroupIndexEnum gIngex, PositionIndexEnum pIndex, Integer newContent, Integer oldContent) {
		if(undoSequences.size() >= MAX_SEQUENCE) {
			do {
				undoSequences.pollLast();				
			}while(undoSequences.size() > MAX_SEQUENCE); 
		}
		undoSequences.addFirst(new Sequence(gIngex, pIndex, newContent, oldContent));
		if(undoBtn.isDisable()) {
			undoBtn.setDisable(false);
		}
	}
	
	@FXML private void undo() {
		if(undoSequences.isEmpty()) {
			undoBtn.setDisable(true);	
			return;
		}
		Sequence seq = undoSequences.pollFirst();
		BoxUi b = gridMap.get(indexBox(seq.getGroupIndex(), seq.getPositionIndex()));
		b.setChangeFromUndo(true);
		Integer content = seq.getOldContent();
		if(content > 0) {
			b.getGroupBox().setText(String.valueOf(content));			
		} else {
			b.getGroupBox().setText(StringUtils.EMPTY);
		}
		undoBtn.setDisable(undoSequences.isEmpty());
		
		// Activate redo
		if(redoBtn.isDisabled()) {
			redoBtn.setDisable(false);	
		}
		redoSequences.addFirst(seq);
		
		b.setChangeFromUndo(false);
		
		b.getGroupBox().setStyle(focusInStyle);
		
		b.getGroupBox().requestFocus();			
		
	}
	
	@FXML private void redo() {
		if(redoSequences.isEmpty()) {
			redoBtn.setDisable(true);	
			return;
		}
		Sequence seq = redoSequences.pollFirst();
		BoxUi b = gridMap.get(indexBox(seq.getGroupIndex(), seq.getPositionIndex()));
		b.setChangeFromRedo(true);
		Integer content = seq.getNewContent();
		if(content > 0) {
			b.getGroupBox().setText(String.valueOf(content));			
		} else {
			b.getGroupBox().setText(StringUtils.EMPTY);
		}
		redoBtn.setDisable(redoSequences.isEmpty());
		b.setChangeFromRedo(false);

		b.getGroupBox().setStyle(focusInStyle);
		
		b.getGroupBox().requestFocus();			
	}
	
	public void excuteValueChanged(String id, InputActionEnum actionEnum, String value, String old) {
		if(this.writingSolution) {
			return;
		}
		BoxUi box = gridMap.get(id);
		IndexBox index = box.getIndexBox();
		Group group = this.grille.getGroup(box.getIndexBox().getGroupIndexEnum());
		Box caze = group.getBox(box.getIndexBox().getPositionIndexEnum());
				
		if(!box.isChangeFromUndo() && !box.isChangeFromRedo() && redoBtn.isDisabled() == false) {
			redoBtn.setDisable(true);
			redoSequences.clear();
		}
		
		if(!box.isChangeFromUndo()) {
			registerChange(caze.getGroup().getIndex(), caze.getPosition().getIndex(), StringUtils.isBlank(value) ? 0 : Integer.parseInt(value), StringUtils.isBlank(old) ? 0 : Integer.parseInt(old));				
		}

		switch (actionEnum) {
		case NEW:
			
			caze.tryContent(Integer.parseInt(value));	
			break;
		case CHANGE:
			
			caze.tryContent(Integer.parseInt(value));
			this.activateOthers();
			break;
			
		default:
			// REMOVE
			//caze.resetContent();
			caze = this.sudoku.resetContent(caze);
			this.activateOthers();
			occurIndexList.clear();
			resolveMenuItem.setDisable(false);
			resolveMenuItem2.setDisable(false);
			
			if(undoBtn.isDisable() && !undoSequences.isEmpty()) {
				undoBtn.setDisable(false);
			}
			return;
		}
		
		List<Box> occur = new ArrayList<>();
		if(!caze.getGroup().isContentUnique(caze, occur)){
			
			for(Box c: occur){
				box = gridMap.get(indexBox(c.getGroup().getIndex(), c.getPosition().getIndex()));
				occurIndexList.add(new IndexBox(c.getGroup().getIndex(), c.getPosition().getIndex()));
				if (box.getIndexBox().equals(index)) {
					box.getGroupBox().setStyle(errorStyle + focusInStyle);
				}else {
					box.getGroupBox().setStyle(errorStyle);					
				}
					
				box.setInError(true);
				
			}
			resolveMenuItem.setDisable(true);
			resolveMenuItem2.setDisable(true);
			
			
			this.deactivateOthers();
			
		}else {
			occurIndexList.clear();
			resolveMenuItem.setDisable(false);
			resolveMenuItem2.setDisable(false);
			
			if(undoBtn.isDisabled()) {
				undoBtn.setDisable(false);
			}
			
			if(this.sudoku.getGrid().isSudoku()){
				markAsResolved();
				
			}else{
				// vérifier si le nombre est resolu
				/*
				List<Box> resolved = this.sudoku.getGrid().checkNumberIsResolved(caze.getContent());
				if(!resolved.isEmpty()){
					this.markAsResolved(resolved);
				}
				//*/
			}
		}
		
	}
	
	private void markAsResolved() {
		this.gridSudoku.setDisable(true);
		this.durationLabel.setVisible(true);
		resolveMenuItem.setDisable(true);
		resolveMenuItem2.setDisable(true);
		eraseMenuItem.setDisable(true);
		redoBtn.setDisable(true); redoBtn.setVisible(false); redoSequences.clear();
		undoBtn.setDisable(true); undoBtn.setVisible(false); undoSequences.clear();
		String str = "Solved in " + convertDuration(this.promise.getDuration());
		Platform.runLater(
				  () -> {
					  this.durationLabel.setText(str);
				    
				  }
				);
		animateBravo();
	}
	
	private String convertDuration(long duration) {
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = duration / daysInMilli;
		duration = duration % daysInMilli;
		
		long elapsedHours = duration / hoursInMilli;
		duration = duration % hoursInMilli;
		
		long elapsedMinutes = duration / minutesInMilli;
		duration = duration % minutesInMilli;
		
		long elapsedSeconds = duration / secondsInMilli;
		
		long elapsedMillis = duration % secondsInMilli;
		
		return String.format(
		    "%d days, %d hours, %d minutes, %d seconds%n, %d millis%n", 
		    elapsedDays,
		    elapsedHours, elapsedMinutes, elapsedSeconds, elapsedMillis);
	}

	private void animateBravo() {
		bravoLabel.setVisible(true);
		durationLabel.setVisible(true);
        fadeTransition.play();
	}
	
	private void stopBravo() {
		bravoLabel.setVisible(false);
		durationLabel.setVisible(false);
        fadeTransition.stop();
	}

//	private void markAsResolved(List<Box> resolved){
//		for(Box c: resolved){
//			Box box = grilleMap.get(indexBox(c.getGroup().getIndex(), c.getPosition().getIndex()));
//			box.getGroupBox().setStyle(resovedStyle);
//		}
//	}
	
	private void deactivateOthers(){
		if (occurIndexList.isEmpty()){
			return;
		}
		for(BoxUi b: this.gridMap.values()){
			Box c = this.sudoku.getGrid().getGroup(b.getIndexBox().getGroupIndexEnum()).getBox(b.getIndexBox().getPositionIndexEnum());
			if(c.getState() != StateBoxEnum.FIXED && !occurIndexList.contains(b.getIndexBox())){
				b.getGroupBox().setDisable(true);
			}
			
		}
	}
	
	private void activateOthers(){
		if (occurIndexList.isEmpty()){
			return;
		}
		for(BoxUi b: this.gridMap.values()){
			Box c = this.sudoku.getGrid().getGroup(b.getIndexBox().getGroupIndexEnum()).getBox(b.getIndexBox().getPositionIndexEnum());
			if(c.getState() != StateBoxEnum.FIXED && !occurIndexList.contains(b.getIndexBox())){
				b.getGroupBox().setDisable(false);
			}
			
		}
		
		for(IndexBox ib: this.occurIndexList){
			BoxUi box = gridMap.get(this.indexBox(ib.getGroupIndexEnum(), ib.getPositionIndexEnum()));
			box.setInError(false);
			if(box.getGroupBox().isFocused()) {
				box.getGroupBox().setStyle(focusInStyle);				
			}else {
				box.getGroupBox().setStyle(focusInOthersStyle);
			}
		}
		
	}
	//private Box cazePrev;
	
	public void executeEventEntered(String id){
		this.setInitiating(false);

		BoxUi box = gridMap.get(id);
		
		
		//box.getGroupBox().selectRange(0, 1);
		Group group = sudoku.getGrid().getGroup(box.getIndexBox().getGroupIndexEnum());
		Box caze = group.getBox(box.getIndexBox().getPositionIndexEnum()); 
		
//		boxes.stream().filter(b -> !b.isInError()).forEach(b -> b.getGroupBox().setStyle(focusOutStyle));
		for(BoxUi b: boxes){
			if(! b.isInError()){								
				b.getGroupBox().setStyle(focusOutStyle);
			}			
			
		}
			
		boxes.clear();
		/////////////////////////////////////////
		
		for(PositionIndexEnum index: PositionIndexEnum.values()){
			BoxUi b = gridMap.get(indexBox(group.getIndex(), index));
			if(! b.isInError()){
				b.getGroupBox().setStyle(focusInOthersStyle);								
			}
			
			boxes.add(b);
		}
		
		
		//--------------------------------------------------------- line
		for(GroupIndexEnum gie: group.getGroupLine()){
			Group g = sudoku.getGrid().getGroup(gie);
			for(PositionIndexEnum index: PositionIndexEnum.values()){
				Box c = g.getBox(index);
				if(caze.getPosition().getCoordinate().getLine() == c.getPosition().getCoordinate().getLine()){
					BoxUi b = gridMap.get(indexBox(gie, index));
					if(! b.isInError()){								
						b.getGroupBox().setStyle(focusInOthersStyle);	
					}
					
					boxes.add(b);
				}
			}
		}
		//--------------------------------------------------------- column
		for(GroupIndexEnum gie: group.getGroupColumn()){
			Group g = sudoku.getGrid().getGroup(gie);
			for(PositionIndexEnum index: PositionIndexEnum.values()){
				Box c = g.getBox(index);
				if(caze.getPosition().getCoordinate().getColumn() == c.getPosition().getCoordinate().getColumn()){
					BoxUi b = gridMap.get(indexBox(gie, index));
					if(! b.isInError()){									
						b.getGroupBox().setStyle(focusInOthersStyle);
					}				
					
					boxes.add(b);
				}
			}
		}
		if(box.isInError()) {
			box.getGroupBox().setStyle(focusInStyle + errorStyle);
		}else {
			box.getGroupBox().setStyle(focusInStyle);			
		}
	}
	
	public boolean isInitiating() {
		return initiating;
	}
	public void setInitiating(boolean initiating) {
		this.initiating = initiating;
	}
	
	

	private void initGrille(SudokuLevelEnum level){
		//sudoku = new SudokuImpl();
		redoBtn.setDisable(true); redoBtn.setVisible(true); redoSequences.clear();
		undoBtn.setDisable(true); undoBtn.setVisible(true); undoSequences.clear();
		
		printMenuItem.setDisable(false);
		//printSolutionMenuItem.setDisable(false);
//		this.sudoku = SudokuFactory.create();
		
		System.out.println(level);
		//resolveMenuItem.setDisable(true);
		promise = sudoku.newGridAsync(level);
		promise.done(done -> {
			this.grille = done;
			this.setInitiating(true);
			for (GroupIndexEnum groupIndexEnum : GroupIndexEnum.values()) {
				Group group = this.grille.getGroup(groupIndexEnum);

				for (PositionIndexEnum positionIndexEnum : PositionIndexEnum.values()) {
					Box box = group.getBox(positionIndexEnum);
					if (box.getState() == StateBoxEnum.FIXED) {
						BoxUi boxUi = gridMap.get(indexBox(groupIndexEnum, positionIndexEnum));
						if(boxUi == null) {
							continue;
						}

						this.grille.getGroup(groupIndexEnum).fixBox(positionIndexEnum, group.getBox(positionIndexEnum).getContent());

						boxUi.getGroupBox().setText(String.valueOf(group.getBox(positionIndexEnum).getContent()));
						boxUi.getGroupBox().setDisable(true);
						boxUi.getGroupBox().setStyle(fixedStyle);
						boxUi.setFixed(true);
					}

				}
			}
			this.setInitiating(false);
			resolveMenuItem.setDisable(false);
			resolveMenuItem2.setDisable(false);

		});


		
	}
	
	@FXML private void printGrid(){
		printJobStatusLabel.setVisible(true);
		
		//gridSudoku.setStyle("-fx-background-color: white;");
		//boxes.forEach(b -> b.getGroupBox().setStyle("-fx-background-color: white;-fx-text-inner-color: red;"));
			
		myPrinter.print(gridSudoku);
		
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(javafx.util.Duration.seconds(10), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				printJobStatusLabel.setVisible(false);
			}
		}));
		timeline.play();
	}
	
	@FXML private void printSolution(){
		
	}
	
	@FXML private void erase(){
		for(BoxUi b: gridMap.values()) {
			if(b.isFixed() == false && StringUtils.isNotBlank(b.getGroupBox().getText())) {
				b.getGroupBox().setText(StringUtils.EMPTY);
			}
		}
		undoSequences.clear();
		undoBtn.setDisable(true);
		
		redoSequences.clear();
		redoBtn.setDisable(true);
		
		activateOthers();
	}
		
	@FXML private void choiceEasy(){
		choice(SudokuLevelEnum.EASY);
	}
	@FXML private void choiceMedium(){
		choice(SudokuLevelEnum.MEDIUM);
	}
	@FXML private void choiceHard(){
		choice(SudokuLevelEnum.HARD);
	}
	@FXML private void choiceExpert(){
		choice(SudokuLevelEnum.EXPERT);
	}
	@FXML private void choiceDefault(){
		choice(SudokuLevelEnum.DEFAULT);
	}
	private void choice(SudokuLevelEnum level) {
		resolveMenuItem.setDisable(false);
		resolveMenuItem2.setDisable(false);
		eraseMenuItem.setDisable(false);
		initGrille(level);
		
		choicePane.setVisible(false);
		gridSudoku.setFocusTraversable(false);
		gridSudoku.setVisible(true);
		
	}
	
	@FXML private void newGame(){
		
		stopBravo();
		choicePane.setVisible(true);
		gridSudoku.setVisible(false);
		printJobStatusLabel.setVisible(false);
		
		this.initiating = true;
		for(GroupIndexEnum groupIndexEnum: GroupIndexEnum.values()){
			for(PositionIndexEnum positionIndexEnum: PositionIndexEnum.values()){
				BoxUi box = gridMap.get(indexBox(groupIndexEnum, positionIndexEnum));
				box.getGroupBox().setText(StringUtils.EMPTY);
				box.getGroupBox().setDisable(false);
				box.getGroupBox().setStyle(focusOutStyle);
				box.setFixed(false);
			}
		}
		
		this.gridSudoku.setDisable(false);
		// TODO 
		//this.gridSudoku.setStyle("-fx-background-color: white");
		resolveMenuItem.setDisable(true);
		resolveMenuItem2.setDisable(true);
		this.initiating = false;
	}
	
	
	@SuppressWarnings("unchecked")
	public void init(){
		this.monitor = this;
		this.sudoku = SudokuFactory.create();
		this.gridMap = new HashMap<>();
		boxes = new ArrayList<>();
		occurIndexList = new ArrayList<>();
		undoSequences = new ArrayDeque<>();
		redoSequences = new ArrayDeque<>();
		
		myPrinter = new MyPrinter();
		myPrinter.setPrintJobStatusLabel(printJobStatusLabel);
		
		gridMap.put(indexBox(GroupIndexEnum.INDEX_1, PositionIndexEnum.INDEX_1), new BoxUi(g1Box1, g1Candidates1Label, new IndexBox(GroupIndexEnum.INDEX_1, PositionIndexEnum.INDEX_1)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_1, PositionIndexEnum.INDEX_2), new BoxUi(g1Box2, g1Candidates2Label, new IndexBox(GroupIndexEnum.INDEX_1, PositionIndexEnum.INDEX_2)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_1, PositionIndexEnum.INDEX_3), new BoxUi(g1Box3, g1Candidates3Label, new IndexBox(GroupIndexEnum.INDEX_1, PositionIndexEnum.INDEX_3)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_1, PositionIndexEnum.INDEX_4), new BoxUi(g1Box4, g1Candidates4Label, new IndexBox(GroupIndexEnum.INDEX_1, PositionIndexEnum.INDEX_4)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_1, PositionIndexEnum.INDEX_5), new BoxUi(g1Box5, g1Candidates5Label, new IndexBox(GroupIndexEnum.INDEX_1, PositionIndexEnum.INDEX_5)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_1, PositionIndexEnum.INDEX_6), new BoxUi(g1Box6, g1Candidates6Label, new IndexBox(GroupIndexEnum.INDEX_1, PositionIndexEnum.INDEX_6)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_1, PositionIndexEnum.INDEX_7), new BoxUi(g1Box7, g1Candidates7Label, new IndexBox(GroupIndexEnum.INDEX_1, PositionIndexEnum.INDEX_7)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_1, PositionIndexEnum.INDEX_8), new BoxUi(g1Box8, g1Candidates8Label, new IndexBox(GroupIndexEnum.INDEX_1, PositionIndexEnum.INDEX_8)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_1, PositionIndexEnum.INDEX_9), new BoxUi(g1Box9, g1Candidates9Label, new IndexBox(GroupIndexEnum.INDEX_1, PositionIndexEnum.INDEX_9)));
		
		gridMap.put(indexBox(GroupIndexEnum.INDEX_2, PositionIndexEnum.INDEX_1), new BoxUi(g2Box1, g2Candidates1Label, new IndexBox(GroupIndexEnum.INDEX_2, PositionIndexEnum.INDEX_1)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_2, PositionIndexEnum.INDEX_2), new BoxUi(g2Box2, g2Candidates2Label, new IndexBox(GroupIndexEnum.INDEX_2, PositionIndexEnum.INDEX_2)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_2, PositionIndexEnum.INDEX_3), new BoxUi(g2Box3, g2Candidates3Label, new IndexBox(GroupIndexEnum.INDEX_2, PositionIndexEnum.INDEX_3)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_2, PositionIndexEnum.INDEX_4), new BoxUi(g2Box4, g2Candidates4Label, new IndexBox(GroupIndexEnum.INDEX_2, PositionIndexEnum.INDEX_4)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_2, PositionIndexEnum.INDEX_5), new BoxUi(g2Box5, g2Candidates5Label, new IndexBox(GroupIndexEnum.INDEX_2, PositionIndexEnum.INDEX_5)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_2, PositionIndexEnum.INDEX_6), new BoxUi(g2Box6, g2Candidates6Label, new IndexBox(GroupIndexEnum.INDEX_2, PositionIndexEnum.INDEX_6)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_2, PositionIndexEnum.INDEX_7), new BoxUi(g2Box7, g2Candidates7Label, new IndexBox(GroupIndexEnum.INDEX_2, PositionIndexEnum.INDEX_7)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_2, PositionIndexEnum.INDEX_8), new BoxUi(g2Box8, g2Candidates8Label, new IndexBox(GroupIndexEnum.INDEX_2, PositionIndexEnum.INDEX_8)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_2, PositionIndexEnum.INDEX_9), new BoxUi(g2Box9, g2Candidates9Label, new IndexBox(GroupIndexEnum.INDEX_2, PositionIndexEnum.INDEX_9)));
		
		gridMap.put(indexBox(GroupIndexEnum.INDEX_3, PositionIndexEnum.INDEX_1), new BoxUi(g3Box1, g3Candidates1Label, new IndexBox(GroupIndexEnum.INDEX_3, PositionIndexEnum.INDEX_1)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_3, PositionIndexEnum.INDEX_2), new BoxUi(g3Box2, g3Candidates2Label, new IndexBox(GroupIndexEnum.INDEX_3, PositionIndexEnum.INDEX_2)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_3, PositionIndexEnum.INDEX_3), new BoxUi(g3Box3, g3Candidates3Label, new IndexBox(GroupIndexEnum.INDEX_3, PositionIndexEnum.INDEX_3)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_3, PositionIndexEnum.INDEX_4), new BoxUi(g3Box4, g3Candidates4Label, new IndexBox(GroupIndexEnum.INDEX_3, PositionIndexEnum.INDEX_4)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_3, PositionIndexEnum.INDEX_5), new BoxUi(g3Box5, g3Candidates5Label, new IndexBox(GroupIndexEnum.INDEX_3, PositionIndexEnum.INDEX_5)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_3, PositionIndexEnum.INDEX_6), new BoxUi(g3Box6, g3Candidates6Label, new IndexBox(GroupIndexEnum.INDEX_3, PositionIndexEnum.INDEX_6)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_3, PositionIndexEnum.INDEX_7), new BoxUi(g3Box7, g3Candidates7Label, new IndexBox(GroupIndexEnum.INDEX_3, PositionIndexEnum.INDEX_7)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_3, PositionIndexEnum.INDEX_8), new BoxUi(g3Box8, g3Candidates8Label, new IndexBox(GroupIndexEnum.INDEX_3, PositionIndexEnum.INDEX_8)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_3, PositionIndexEnum.INDEX_9), new BoxUi(g3Box9, g3Candidates9Label, new IndexBox(GroupIndexEnum.INDEX_3, PositionIndexEnum.INDEX_9)));
		
		gridMap.put(indexBox(GroupIndexEnum.INDEX_4, PositionIndexEnum.INDEX_1), new BoxUi(g4Box1, g4Candidates1Label, new IndexBox(GroupIndexEnum.INDEX_4, PositionIndexEnum.INDEX_1)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_4, PositionIndexEnum.INDEX_2), new BoxUi(g4Box2, g4Candidates2Label, new IndexBox(GroupIndexEnum.INDEX_4, PositionIndexEnum.INDEX_2)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_4, PositionIndexEnum.INDEX_3), new BoxUi(g4Box3, g4Candidates3Label, new IndexBox(GroupIndexEnum.INDEX_4, PositionIndexEnum.INDEX_3)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_4, PositionIndexEnum.INDEX_4), new BoxUi(g4Box4, g4Candidates4Label, new IndexBox(GroupIndexEnum.INDEX_4, PositionIndexEnum.INDEX_4)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_4, PositionIndexEnum.INDEX_5), new BoxUi(g4Box5, g4Candidates5Label, new IndexBox(GroupIndexEnum.INDEX_4, PositionIndexEnum.INDEX_5)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_4, PositionIndexEnum.INDEX_6), new BoxUi(g4Box6, g4Candidates6Label, new IndexBox(GroupIndexEnum.INDEX_4, PositionIndexEnum.INDEX_6)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_4, PositionIndexEnum.INDEX_7), new BoxUi(g4Box7, g4Candidates7Label, new IndexBox(GroupIndexEnum.INDEX_4, PositionIndexEnum.INDEX_7)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_4, PositionIndexEnum.INDEX_8), new BoxUi(g4Box8, g4Candidates8Label, new IndexBox(GroupIndexEnum.INDEX_4, PositionIndexEnum.INDEX_8)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_4, PositionIndexEnum.INDEX_9), new BoxUi(g4Box9, g4Candidates9Label, new IndexBox(GroupIndexEnum.INDEX_4, PositionIndexEnum.INDEX_9)));
		
		gridMap.put(indexBox(GroupIndexEnum.INDEX_5, PositionIndexEnum.INDEX_1), new BoxUi(g5Box1, g5Candidates1Label, new IndexBox(GroupIndexEnum.INDEX_5, PositionIndexEnum.INDEX_1)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_5, PositionIndexEnum.INDEX_2), new BoxUi(g5Box2, g5Candidates2Label, new IndexBox(GroupIndexEnum.INDEX_5, PositionIndexEnum.INDEX_2)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_5, PositionIndexEnum.INDEX_3), new BoxUi(g5Box3, g5Candidates3Label, new IndexBox(GroupIndexEnum.INDEX_5, PositionIndexEnum.INDEX_3)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_5, PositionIndexEnum.INDEX_4), new BoxUi(g5Box4, g5Candidates4Label, new IndexBox(GroupIndexEnum.INDEX_5, PositionIndexEnum.INDEX_4)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_5, PositionIndexEnum.INDEX_5), new BoxUi(g5Box5, g5Candidates5Label, new IndexBox(GroupIndexEnum.INDEX_5, PositionIndexEnum.INDEX_5)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_5, PositionIndexEnum.INDEX_6), new BoxUi(g5Box6, g5Candidates6Label, new IndexBox(GroupIndexEnum.INDEX_5, PositionIndexEnum.INDEX_6)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_5, PositionIndexEnum.INDEX_7), new BoxUi(g5Box7, g5Candidates7Label, new IndexBox(GroupIndexEnum.INDEX_5, PositionIndexEnum.INDEX_7)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_5, PositionIndexEnum.INDEX_8), new BoxUi(g5Box8, g5Candidates8Label, new IndexBox(GroupIndexEnum.INDEX_5, PositionIndexEnum.INDEX_8)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_5, PositionIndexEnum.INDEX_9), new BoxUi(g5Box9, g5Candidates9Label, new IndexBox(GroupIndexEnum.INDEX_5, PositionIndexEnum.INDEX_9)));
		
		gridMap.put(indexBox(GroupIndexEnum.INDEX_6, PositionIndexEnum.INDEX_1), new BoxUi(g6Box1, g6Candidates1Label, new IndexBox(GroupIndexEnum.INDEX_6, PositionIndexEnum.INDEX_1)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_6, PositionIndexEnum.INDEX_2), new BoxUi(g6Box2, g6Candidates2Label, new IndexBox(GroupIndexEnum.INDEX_6, PositionIndexEnum.INDEX_2)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_6, PositionIndexEnum.INDEX_3), new BoxUi(g6Box3, g6Candidates3Label, new IndexBox(GroupIndexEnum.INDEX_6, PositionIndexEnum.INDEX_3)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_6, PositionIndexEnum.INDEX_4), new BoxUi(g6Box4, g6Candidates4Label, new IndexBox(GroupIndexEnum.INDEX_6, PositionIndexEnum.INDEX_4)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_6, PositionIndexEnum.INDEX_5), new BoxUi(g6Box5, g6Candidates5Label, new IndexBox(GroupIndexEnum.INDEX_6, PositionIndexEnum.INDEX_5)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_6, PositionIndexEnum.INDEX_6), new BoxUi(g6Box6, g6Candidates6Label, new IndexBox(GroupIndexEnum.INDEX_6, PositionIndexEnum.INDEX_6)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_6, PositionIndexEnum.INDEX_7), new BoxUi(g6Box7, g6Candidates7Label, new IndexBox(GroupIndexEnum.INDEX_6, PositionIndexEnum.INDEX_7)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_6, PositionIndexEnum.INDEX_8), new BoxUi(g6Box8, g6Candidates8Label, new IndexBox(GroupIndexEnum.INDEX_6, PositionIndexEnum.INDEX_8)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_6, PositionIndexEnum.INDEX_9), new BoxUi(g6Box9, g6Candidates9Label, new IndexBox(GroupIndexEnum.INDEX_6, PositionIndexEnum.INDEX_9)));
		
		gridMap.put(indexBox(GroupIndexEnum.INDEX_7, PositionIndexEnum.INDEX_1), new BoxUi(g7Box1, g7Candidates1Label, new IndexBox(GroupIndexEnum.INDEX_7, PositionIndexEnum.INDEX_1)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_7, PositionIndexEnum.INDEX_2), new BoxUi(g7Box2, g7Candidates2Label, new IndexBox(GroupIndexEnum.INDEX_7, PositionIndexEnum.INDEX_2)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_7, PositionIndexEnum.INDEX_3), new BoxUi(g7Box3, g7Candidates3Label, new IndexBox(GroupIndexEnum.INDEX_7, PositionIndexEnum.INDEX_3)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_7, PositionIndexEnum.INDEX_4), new BoxUi(g7Box4, g7Candidates4Label, new IndexBox(GroupIndexEnum.INDEX_7, PositionIndexEnum.INDEX_4)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_7, PositionIndexEnum.INDEX_5), new BoxUi(g7Box5, g7Candidates5Label, new IndexBox(GroupIndexEnum.INDEX_7, PositionIndexEnum.INDEX_5)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_7, PositionIndexEnum.INDEX_6), new BoxUi(g7Box6, g7Candidates6Label, new IndexBox(GroupIndexEnum.INDEX_7, PositionIndexEnum.INDEX_6)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_7, PositionIndexEnum.INDEX_7), new BoxUi(g7Box7, g7Candidates7Label, new IndexBox(GroupIndexEnum.INDEX_7, PositionIndexEnum.INDEX_7)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_7, PositionIndexEnum.INDEX_8), new BoxUi(g7Box8, g7Candidates8Label, new IndexBox(GroupIndexEnum.INDEX_7, PositionIndexEnum.INDEX_8)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_7, PositionIndexEnum.INDEX_9), new BoxUi(g7Box9, g7Candidates9Label, new IndexBox(GroupIndexEnum.INDEX_7, PositionIndexEnum.INDEX_9)));
		
		gridMap.put(indexBox(GroupIndexEnum.INDEX_8, PositionIndexEnum.INDEX_1), new BoxUi(g8Box1, g8Candidates1Label, new IndexBox(GroupIndexEnum.INDEX_8, PositionIndexEnum.INDEX_1)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_8, PositionIndexEnum.INDEX_2), new BoxUi(g8Box2, g8Candidates2Label, new IndexBox(GroupIndexEnum.INDEX_8, PositionIndexEnum.INDEX_2)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_8, PositionIndexEnum.INDEX_3), new BoxUi(g8Box3, g8Candidates3Label, new IndexBox(GroupIndexEnum.INDEX_8, PositionIndexEnum.INDEX_3)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_8, PositionIndexEnum.INDEX_4), new BoxUi(g8Box4, g8Candidates4Label, new IndexBox(GroupIndexEnum.INDEX_8, PositionIndexEnum.INDEX_4)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_8, PositionIndexEnum.INDEX_5), new BoxUi(g8Box5, g8Candidates5Label, new IndexBox(GroupIndexEnum.INDEX_8, PositionIndexEnum.INDEX_5)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_8, PositionIndexEnum.INDEX_6), new BoxUi(g8Box6, g8Candidates6Label, new IndexBox(GroupIndexEnum.INDEX_8, PositionIndexEnum.INDEX_6)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_8, PositionIndexEnum.INDEX_7), new BoxUi(g8Box7, g8Candidates7Label, new IndexBox(GroupIndexEnum.INDEX_8, PositionIndexEnum.INDEX_7)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_8, PositionIndexEnum.INDEX_8), new BoxUi(g8Box8, g8Candidates8Label, new IndexBox(GroupIndexEnum.INDEX_8, PositionIndexEnum.INDEX_8)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_8, PositionIndexEnum.INDEX_9), new BoxUi(g8Box9, g8Candidates9Label, new IndexBox(GroupIndexEnum.INDEX_8, PositionIndexEnum.INDEX_9)));
		
		gridMap.put(indexBox(GroupIndexEnum.INDEX_9, PositionIndexEnum.INDEX_1), new BoxUi(g9Box1, g9Candidates1Label, new IndexBox(GroupIndexEnum.INDEX_9, PositionIndexEnum.INDEX_1)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_9, PositionIndexEnum.INDEX_2), new BoxUi(g9Box2, g9Candidates2Label, new IndexBox(GroupIndexEnum.INDEX_9, PositionIndexEnum.INDEX_2)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_9, PositionIndexEnum.INDEX_3), new BoxUi(g9Box3, g9Candidates3Label, new IndexBox(GroupIndexEnum.INDEX_9, PositionIndexEnum.INDEX_3)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_9, PositionIndexEnum.INDEX_4), new BoxUi(g9Box4, g9Candidates4Label, new IndexBox(GroupIndexEnum.INDEX_9, PositionIndexEnum.INDEX_4)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_9, PositionIndexEnum.INDEX_5), new BoxUi(g9Box5, g9Candidates5Label, new IndexBox(GroupIndexEnum.INDEX_9, PositionIndexEnum.INDEX_5)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_9, PositionIndexEnum.INDEX_6), new BoxUi(g9Box6, g9Candidates6Label, new IndexBox(GroupIndexEnum.INDEX_9, PositionIndexEnum.INDEX_6)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_9, PositionIndexEnum.INDEX_7), new BoxUi(g9Box7, g9Candidates7Label, new IndexBox(GroupIndexEnum.INDEX_9, PositionIndexEnum.INDEX_7)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_9, PositionIndexEnum.INDEX_8), new BoxUi(g9Box8, g9Candidates8Label, new IndexBox(GroupIndexEnum.INDEX_9, PositionIndexEnum.INDEX_8)));
		gridMap.put(indexBox(GroupIndexEnum.INDEX_9, PositionIndexEnum.INDEX_9), new BoxUi(g9Box9, g9Candidates9Label, new IndexBox(GroupIndexEnum.INDEX_9, PositionIndexEnum.INDEX_9)));
		
		FocusEventListener focusListen = new FocusEventListener(this);
		InputChangeListener inputListen = new InputChangeListener(this);
		for(GroupIndexEnum groupIndexEnum: GroupIndexEnum.values()){
			for(PositionIndexEnum positionIndexEnum: PositionIndexEnum.values()){
				BoxUi boxUi = gridMap.get(indexBox(groupIndexEnum, positionIndexEnum));
				
				boxUi.getGroupBox().focusedProperty().addListener(focusListen);
				boxUi.getGroupBox().setTextFormatter(new TextFormatter<String>(filter));
				
				boxUi.getGroupBox().textProperty().addListener(inputListen);
				boxUi.getGroupBox().addEventFilter(KeyEvent.KEY_PRESSED,
		                event -> System.out.println("Pressed: " + event.getText()));
			}
		}
		
		// Init "Bravo !" text animation ==================================================================
		this.fadeTransition = new FadeTransition(javafx.util.Duration.seconds(2), bravoLabel);
        fadeTransition.setFromValue(8.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(Animation.INDEFINITE);
	}
   
	UnaryOperator<TextFormatter.Change> filter = c -> {
	    String proposedText = c.getControlNewText();
	    if (proposedText.matches("[1-9]{0,1}")) {
	        return c ;
	    } else {
	        return null ;
	    }
	};
	
	@FXML private MenuItem printMenuItem;
	@FXML private MenuItem printSolutionMenuItem;
	@FXML private Label printJobStatusLabel;
	
	
	@FXML private Label durationLabel; 
	@FXML private Pane menuPane;
	@FXML private Pane unrePane;
	@FXML private Pane cancelPane;
	
	@FXML private Button undoBtn;
	@FXML private Button redoBtn;
	
	private FadeTransition fadeTransition;
	
	@FXML private Label bravoLabel; 
	
	@FXML private MenuItem resolveMenuItem;
	@FXML private MenuItem resolveMenuItem2;
	@FXML private MenuItem eraseMenuItem;
	@FXML private GridPane gridSudoku;
	@FXML private Pane choicePane;
	
	@FXML private TextField g1Box1;
	@FXML private Label g1Candidates1Label; 
	@FXML private TextField g1Box2;
	@FXML private Label g1Candidates2Label; 
	@FXML private TextField g1Box3;
	@FXML private Label g1Candidates3Label; 
	@FXML private TextField g1Box4;
	@FXML private Label g1Candidates4Label; 
	@FXML private TextField g1Box5;
	@FXML private Label g1Candidates5Label; 
	@FXML private TextField g1Box6;
	@FXML private Label g1Candidates6Label; 
	@FXML private TextField g1Box7;
	@FXML private Label g1Candidates7Label; 
	@FXML private TextField g1Box8;
	@FXML private Label g1Candidates8Label; 
	@FXML private TextField g1Box9;
	@FXML private Label g1Candidates9Label; 

	@FXML private TextField g2Box1;
	@FXML private Label g2Candidates1Label; 
	@FXML private TextField g2Box2;
	@FXML private Label g2Candidates2Label; 
	@FXML private TextField g2Box3;
	@FXML private Label g2Candidates3Label; 
	@FXML private TextField g2Box4;
	@FXML private Label g2Candidates4Label; 
	@FXML private TextField g2Box5;
	@FXML private Label g2Candidates5Label; 
	@FXML private TextField g2Box6;
	@FXML private Label g2Candidates6Label; 
	@FXML private TextField g2Box7;
	@FXML private Label g2Candidates7Label; 
	@FXML private TextField g2Box8;
	@FXML private Label g2Candidates8Label; 
	@FXML private TextField g2Box9;
	@FXML private Label g2Candidates9Label; 
	
	@FXML private TextField g3Box1;
	@FXML private Label g3Candidates1Label; 
	@FXML private TextField g3Box2;
	@FXML private Label g3Candidates2Label; 
	@FXML private TextField g3Box3;
	@FXML private Label g3Candidates3Label; 
	@FXML private TextField g3Box4;
	@FXML private Label g3Candidates4Label; 
	@FXML private TextField g3Box5;
	@FXML private Label g3Candidates5Label; 
	@FXML private TextField g3Box6;
	@FXML private Label g3Candidates6Label; 
	@FXML private TextField g3Box7;
	@FXML private Label g3Candidates7Label; 
	@FXML private TextField g3Box8;
	@FXML private Label g3Candidates8Label; 
	@FXML private TextField g3Box9;
	@FXML private Label g3Candidates9Label; 
	
	@FXML private TextField g4Box1;
	@FXML private Label g4Candidates1Label; 
	@FXML private TextField g4Box2;
	@FXML private Label g4Candidates2Label; 
	@FXML private TextField g4Box3;
	@FXML private Label g4Candidates3Label; 
	@FXML private TextField g4Box4;
	@FXML private Label g4Candidates4Label; 
	@FXML private TextField g4Box5;
	@FXML private Label g4Candidates5Label; 
	@FXML private TextField g4Box6;
	@FXML private Label g4Candidates6Label; 
	@FXML private TextField g4Box7;
	@FXML private Label g4Candidates7Label; 
	@FXML private TextField g4Box8;
	@FXML private Label g4Candidates8Label; 
	@FXML private TextField g4Box9;
	@FXML private Label g4Candidates9Label; 
	
	@FXML private TextField g5Box1;
	@FXML private Label g5Candidates1Label; 
	@FXML private TextField g5Box2;
	@FXML private Label g5Candidates2Label; 
	@FXML private TextField g5Box3;
	@FXML private Label g5Candidates3Label; 
	@FXML private TextField g5Box4;
	@FXML private Label g5Candidates4Label; 
	@FXML private TextField g5Box5;
	@FXML private Label g5Candidates5Label; 
	@FXML private TextField g5Box6;
	@FXML private Label g5Candidates6Label; 
	@FXML private TextField g5Box7;
	@FXML private Label g5Candidates7Label; 
	@FXML private TextField g5Box8;
	@FXML private Label g5Candidates8Label; 
	@FXML private TextField g5Box9;
	@FXML private Label g5Candidates9Label; 
	
	@FXML private TextField g6Box1;
	@FXML private Label g6Candidates1Label; 
	@FXML private TextField g6Box2;
	@FXML private Label g6Candidates2Label; 
	@FXML private TextField g6Box3;
	@FXML private Label g6Candidates3Label; 
	@FXML private TextField g6Box4;
	@FXML private Label g6Candidates4Label; 
	@FXML private TextField g6Box5;
	@FXML private Label g6Candidates5Label; 
	@FXML private TextField g6Box6;
	@FXML private Label g6Candidates6Label; 
	@FXML private TextField g6Box7;
	@FXML private Label g6Candidates7Label; 
	@FXML private TextField g6Box8;
	@FXML private Label g6Candidates8Label; 
	@FXML private TextField g6Box9;
	@FXML private Label g6Candidates9Label; 
	
	@FXML private TextField g7Box1;
	@FXML private Label g7Candidates1Label; 
	@FXML private TextField g7Box2;
	@FXML private Label g7Candidates2Label; 
	@FXML private TextField g7Box3;
	@FXML private Label g7Candidates3Label; 
	@FXML private TextField g7Box4;
	@FXML private Label g7Candidates4Label; 
	@FXML private TextField g7Box5;
	@FXML private Label g7Candidates5Label; 
	@FXML private TextField g7Box6;
	@FXML private Label g7Candidates6Label; 
	@FXML private TextField g7Box7;
	@FXML private Label g7Candidates7Label; 
	@FXML private TextField g7Box8;
	@FXML private Label g7Candidates8Label; 
	@FXML private TextField g7Box9;
	@FXML private Label g7Candidates9Label; 
	
	@FXML private TextField g8Box1;
	@FXML private Label g8Candidates1Label; 
	@FXML private TextField g8Box2;
	@FXML private Label g8Candidates2Label; 
	@FXML private TextField g8Box3;
	@FXML private Label g8Candidates3Label; 
	@FXML private TextField g8Box4;
	@FXML private Label g8Candidates4Label; 
	@FXML private TextField g8Box5;
	@FXML private Label g8Candidates5Label; 
	@FXML private TextField g8Box6;
	@FXML private Label g8Candidates6Label; 
	@FXML private TextField g8Box7;
	@FXML private Label g8Candidates7Label; 
	@FXML private TextField g8Box8;
	@FXML private Label g8Candidates8Label; 
	@FXML private TextField g8Box9;
	@FXML private Label g8Candidates9Label; 
	
	@FXML private TextField g9Box1;
	@FXML private Label g9Candidates1Label; 
	@FXML private TextField g9Box2;
	@FXML private Label g9Candidates2Label; 
	@FXML private TextField g9Box3;
	@FXML private Label g9Candidates3Label; 
	@FXML private TextField g9Box4;
	@FXML private Label g9Candidates4Label; 
	@FXML private TextField g9Box5;
	@FXML private Label g9Candidates5Label; 
	@FXML private TextField g9Box6;
	@FXML private Label g9Candidates6Label; 
	@FXML private TextField g9Box7;
	@FXML private Label g9Candidates7Label; 
	@FXML private TextField g9Box8;
	@FXML private Label g9Candidates8Label; 
	@FXML private TextField g9Box9;
	@FXML private Label g9Candidates9Label; 
	/*
	//*/
	
	public MainApp getMainApp() {
		return mainApp;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;		
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
		
	}

	@Override
	public void display(Box caze) {
		
		try {
			System.out.println("display " + caze);
			while(this.paused) {
				System.out.println("en pause");
				Thread.sleep(1000);
				//this.paused = this.promise.isCanceled();
			}
			BoxUi box = gridMap.get(indexBox(caze.getGroup().getIndex(), caze.getPosition().getIndex()));
			if(box != null) {
				box.getGroupBox().setText(caze.getContent() + "");
			}
			Thread.sleep(1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void erase(Box caze) {
		try {
			System.out.println("erase " + caze);
			while(this.paused) {
				System.out.println("en pause");
				Thread.sleep(1000);
			}
			System.out.println(caze);
			BoxUi box = gridMap.get(indexBox(caze.getGroup().getIndex(), caze.getPosition().getIndex()));
			if(box != null) {
				box.getGroupBox().setText("");
			}
			Thread.sleep(1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}