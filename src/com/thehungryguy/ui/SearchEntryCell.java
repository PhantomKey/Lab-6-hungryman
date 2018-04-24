package com.thehungryguy.ui;

import com.thehungryguy.data.Restaurant;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

class SearchEntryCell extends ListCell<SearchResultEntry> {
	@Override
	protected void updateItem(SearchResultEntry item, boolean empty) {
		super.updateItem(item, empty);
		if (item == null) {
			return;
		}

		ImageView imageView = new ImageView(item.getImage());
		Restaurant restaurant = item.getRestaurant();
		Label labelName = new Label(restaurant.getName());
		Label labelAddress = new Label("@" + restaurant.getVicinity());
		String ratingString = restaurant.getRating() >= 0 ? Double.toString(restaurant.getRating()) : "N/A";
		Label labelRating = new Label("Rating: " + ratingString);
		HBox cell = createSearchEntryCell(imageView, labelName, labelAddress, labelRating);

		setGraphic(cell);
	}

	protected HBox createSearchEntryCell(ImageView imageView, Label labelName, Label labelAddress,
			Label labelRating) {
		// Set All Pane
		BorderPane imagePane = new BorderPane();
		imagePane.setMaxSize(100, 80);
		imagePane.setMinSize(100, 80);
		imagePane.setPrefSize(100, 80);

		VBox labelPane = new VBox();
		labelPane.setBackground(new Background(new BackgroundFill(ApplicationColors.searchColor, null, null)));
		labelPane.setAlignment(Pos.CENTER_LEFT);
		labelPane.setPrefSize(500, 80);

		VBox ratingPane = new VBox();
		ratingPane.setBackground(new Background(new BackgroundFill(ApplicationColors.primaryColor, null, null)));
		ratingPane.setAlignment(Pos.CENTER);
		ratingPane.setMaxSize(80, 20);

		HBox cell = new HBox();
		cell.setBackground(new Background(new BackgroundFill(ApplicationColors.searchColor, null, null)));
		cell.setSpacing(40);
		cell.setAlignment(Pos.CENTER_LEFT);

		// Set properties of Image
		imageView.setPreserveRatio(true);
		imageView.fitWidthProperty().bind(imagePane.widthProperty());
		imageView.setFitHeight(70);

		// Set properties of label
		labelName.setFont(Font.font(null, FontWeight.BOLD, 15));
		labelName.setTextFill(Color.gray(0.2));

		labelAddress.setTextFill(Color.GRAY);
		labelAddress.setWrapText(true);

		labelRating.setFont(Font.font(null, FontWeight.BOLD, 12));
		labelRating.setTextFill(Color.WHITE);

		// Set All Children
		imagePane.setCenter(imageView);
		ratingPane.getChildren().add(labelRating);
		labelPane.getChildren().addAll(ratingPane, labelName, labelAddress);
		cell.setPadding(new Insets(0, 0, 0, 100));
		cell.getChildren().addAll(imagePane, labelPane);

		return cell;
	}
}