package com.thehungryguy.main;

import java.util.List;

import com.thehungryguy.data.DataRequestException;
import com.thehungryguy.data.DataRequester;
import com.thehungryguy.data.Geolocation;
import com.thehungryguy.data.Restaurant;
import com.thehungryguy.ui.DetailPage;
import com.thehungryguy.ui.WelcomePage;

import javafx.application.Application;
import javafx.application.Platform;
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

				// TODO Auto-generated method stub
				welcomePage.getErrorLabel().setText(null);
				Thread Req = new Thread(new Runnable() {
					public void run() {
						welcomePage.getLoaderBar().startAnimation();
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								try {
									// welcomePage.getLoaderBar().startAnimation();
									welcomePage.getSearchButton().setDisable(true);
									welcomePage.getQueryTextField().setDisable(true);
									Geolocation data = dataRequester
											.getLocationOfPlace(welcomePage.getQueryTextField().getText());
									dataRequester.searchRestaurantsAroundLocation(data, 500);
									welcomePage.getLoaderBar().stopAnimation();
									DetailPage appDetail = new DetailPage(welcomePage.getQueryTextField().getText());
									primaryStage.setScene(new Scene(appDetail, 800, 600));

									appDetail.getGoBackButton().setOnAction(new EventHandler<ActionEvent>() {

										@Override
										public void handle(ActionEvent arg0) {
											primaryStage.setScene(welcomeScene);
											welcomePage.getQueryTextField().setText(null);
											welcomePage.getSearchButton().setDisable(false);
											welcomePage.getQueryTextField().setDisable(false);
										}

									});

									try {
										Thread.sleep(10);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} catch (DataRequestException e) {
									welcomePage.getErrorLabel().setText(e.getMessage());
									welcomePage.getLoaderBar().stopAnimation();
									welcomePage.getSearchButton().setDisable(false);
									welcomePage.getQueryTextField().setDisable(false);
								}

							}

						});

					}
				});
				// starting the thread
				Req.run();

				/*
				 * if(Req.getState() == Thread.State.TERMINATED) { System.out.println("int");
				 * welcomePage.getLoaderBar().stopAnimation(); }
				 */

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
