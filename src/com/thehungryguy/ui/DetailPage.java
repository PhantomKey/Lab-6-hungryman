package com.thehungryguy.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class DetailPage extends BorderPane {
	private Button goBackButton;
	private ObservableList<SearchResultEntry> searchResultEntries;

	public DetailPage(String searchQuery) {
		setPadding(new Insets(10));
		setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
		Stop[] stops = new Stop[] {
				new Stop(0, ApplicationColors.primaryColor),
				new Stop(1, ApplicationColors.secondaryColor) };
		LinearGradient topBorderPaint = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
		setBorder(new Border(new BorderStroke(topBorderPaint, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
				new BorderWidths(10, 0, 0, 0))));
 
		// Top bar

		goBackButton = new Button("Go Back");
		goBackButton.setFont(new Font(16));

		Label searchQueryLabel = new Label("Restaurants around \"" + searchQuery + "\"");
		searchQueryLabel.setFont(new Font(16));

		HBox topBar = new HBox(15);
		topBar.setAlignment(Pos.CENTER_LEFT);
		topBar.getChildren().addAll(goBackButton, searchQueryLabel);
		BorderPane.setMargin(topBar, new Insets(0, 0, 10, 0));

		setTop(topBar);

		// Content

		searchResultEntries = FXCollections.observableArrayList();

		ListView<SearchResultEntry> searchListView = new ListView<>(searchResultEntries);
		searchListView.setFixedCellSize(80);
		searchListView.setCellFactory(new Callback<ListView<SearchResultEntry>, ListCell<SearchResultEntry>>() {
			@Override
			public ListCell<SearchResultEntry> call(ListView<SearchResultEntry> param) {
				return new SearchEntryCell();
			}
		});

		setCenter(searchListView);
	}

	public void addSearchResultEntry(SearchResultEntry newSearchResultEntry) {
		searchResultEntries.add(newSearchResultEntry);
	}

	public Button getGoBackButton() {
		return goBackButton;
	}
}
