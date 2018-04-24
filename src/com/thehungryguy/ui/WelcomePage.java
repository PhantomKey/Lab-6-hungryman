package com.thehungryguy.ui;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;

public class WelcomePage extends VBox {
	private TextField queryTextField;
	private Button searchButton;
	private Label errorLabel;
	private LoaderBar loaderBar;

	public WelcomePage() {
		setPadding(new Insets(10));
		setAlignment(Pos.CENTER);
		setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
		Stop[] stops = new Stop[] { new Stop(0, ApplicationColors.primaryColor),
				new Stop(1, ApplicationColors.secondaryColor) };
		LinearGradient borderPaint = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
		setBorder(new Border(
				new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(10))));

		Label titleLabel = new Label("Hungry?");
		titleLabel.setFont(new Font(60));

		Label subtitleLabel = new Label("Enter your current location!");
		subtitleLabel.setFont(new Font(20));

		queryTextField = new TextField();
		queryTextField.setFont(new Font(20));
		queryTextField.setMaxWidth(500);
		queryTextField.setAlignment(Pos.CENTER);

		Label hintLabel = new Label("e.g. Siam Paragon, Chamchuri Square, Chula Engineering");
		hintLabel.setFont(new Font(13));
		hintLabel.setTextFill(Color.GRAY);
 
		searchButton = new Button("Search");
		searchButton.setFont(new Font(20));
		searchButton.setPadding(new Insets(10, 50, 10, 50));
		VBox.setMargin(searchButton, new Insets(10, 0, 10, 0));

		errorLabel = new Label("");
		errorLabel.setFont(new Font(15));
		errorLabel.setTextFill(Color.rgb(255, 68, 55));
		
		loaderBar = new LoaderBar();

		// When a user presses ENTER key while focusing a text field,
		// activate the search button.
		queryTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					searchButton.fire();
				}
			};
		});

		getChildren().addAll(titleLabel, subtitleLabel, queryTextField, hintLabel, searchButton, errorLabel, loaderBar);
	}

	public TextField getQueryTextField() {
		return queryTextField;
	}

	public Button getSearchButton() {
		return searchButton;
	}

	public Label getErrorLabel() {
		return errorLabel;
	}
	
	public LoaderBar getLoaderBar() {
		return loaderBar;
	}
}
