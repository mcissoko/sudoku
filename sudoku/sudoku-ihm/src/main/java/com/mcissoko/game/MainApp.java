package com.mcissoko.game;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mcissoko.game.view.RootLayoutController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private AnchorPane rootLayout;
    private AnchorPane loginLayout;
        
    /**
     * The data as an observable list of Persons.
     */
    Logger log = LoggerFactory.getLogger(MainApp.class);    
    
    @Override
    public void start(Stage primaryStage) {
    	log.info("===================== START =======================");
    	
    	
    	//Thread.setDefaultUncaughtExceptionHandler(ApplicationExceptionHandler::showError);
    	 Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
             System.out.println("Handler caught exception: "+throwable);
             throwable.printStackTrace();
         });
    	
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Signal Manager");
        
        // Set the application icon.
        //this.primaryStage.getIcons().add(new Image(MainApp.class.getResource("../../../images/icon.png").toExternalForm()));
        this.primaryStage.getIcons().add(new Image(MainApp.class.getResource("images/icon.png").toExternalForm()));


        initRootLayout();
        
    }

    public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			this.primaryStage.setTitle("Sudoku");
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			loginLayout = (AnchorPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(loginLayout);
			//scene.getStylesheets().add(MainApp.class.getResource("../../../styles/styles.css").toExternalForm());
			scene.getStylesheets().add(MainApp.class.getResource("styles/styles.css").toExternalForm());

//			String css = (MainApp.class.getResource("view/style.css")).toExternalForm();
//			scene.getStylesheets().add(css);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);

			// Give the controller access to the main app.
			RootLayoutController controller = loader.getController();
			controller.setMainApp(this);
			controller.init();
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
	public static void main(String[] args) {
        launch(args);
    }
}