package com.thehungryguy.main;

import com.thehungryguy.data.DataRequestException;
import com.thehungryguy.data.DataRequester;
import com.thehungryguy.data.Geolocation;
import com.thehungryguy.ui.WelcomePage;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	private DataRequester dataRequester;

	public Main() throws Exception {
		dataRequester = DataRequester.createUsingSecretKeysFile();
	}

	@Override
	public void start(Stage primaryStage) {
		WelcomePage welcomePage = new WelcomePage();
		Scene welcomeScene = new Scene(welcomePage, 800, 600);
		
		welcomePage.getSearchButton().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				Thread loadBar = new Thread(new Runnable(){
					 public void run(){ 
						 System.out.println("1");
						 welcomePage.getLoaderBar().startAnimation(); 
					 }}); 
				
				// TODO Auto-generated method stub
				Thread Req = new Thread(new Runnable(){
				 public void run(){ 
					 try {
						welcomePage.getSearchButton().setDisable(true);
						Geolocation find =  dataRequester.getLocationOfPlace(welcomePage.getQueryTextField().getText());
						dataRequester.searchRestaurantsAroundLocation(find, 500);
						welcomePage.getLoaderBar().stopAnimation();
						loadBar.interrupt();
					} catch (DataRequestException e) {
						// TODO Auto-generated catch block
						welcomePage.getErrorLabel().setText(e.getMessage());
						loadBar.interrupt();
						welcomePage.getLoaderBar().stopAnimation();
						welcomePage.getSearchButton().setDisable(false);
						
						 
					}
			 
				 }}); 
			    // starting the thread
				loadBar.setDaemon(true);
				loadBar.run();
				try {
					loadBar.join(8000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Req.run();
				
				/*if(Req.getState() == Thread.State.TERMINATED) {
					System.out.println("int");
					welcomePage.getLoaderBar().stopAnimation();
					}*/
				

			};
		});

		primaryStage.setTitle("The Hungry Guy");
		primaryStage.setScene(welcomeScene);
		primaryStage.setMinWidth(450);
		primaryStage.setMinHeight(400);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
