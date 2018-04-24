package com.thehungryguy.ui;

import com.thehungryguy.data.Restaurant;

import javafx.scene.image.Image;

public class SearchResultEntry {
	private Restaurant restaurant;
	private Image image;

	public SearchResultEntry(Restaurant restaurant, Image image) {
		this.restaurant = restaurant;
		this.image = image;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public Image getImage() {
		return image;
	}
}