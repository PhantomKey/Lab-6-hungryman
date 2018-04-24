package com.thehungryguy.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Restaurant {
	private String placeId;
	private String name;
	private Geolocation location;
	private String photoReference;
	private double rating;
	private String vicinity;

	public Restaurant(String placeId, String name, Geolocation location, String photoReference, double rating,
			String vicinity) {
		this.placeId = placeId;
		this.name = name;
		this.location = location;
		this.photoReference = photoReference;
		this.rating = rating;
		this.vicinity = vicinity;
	}

	static Restaurant parseFromApi(JsonObject resultJson) {
		String placeId = resultJson.get("place_id").getAsString();

		String name = resultJson.get("name").getAsString();

		Geolocation location = Geolocation
				.parseFromApi(resultJson.getAsJsonObject("geometry").getAsJsonObject("location"));

		JsonArray photosJson = resultJson.getAsJsonArray("photos");
		String photoReference = null;
		if (photosJson != null && photosJson.size() > 0) {
			photoReference = photosJson.get(0).getAsJsonObject().get("photo_reference").getAsString();
		}

		JsonElement ratingJson = resultJson.get("rating");
		double rating = ratingJson != null ? ratingJson.getAsDouble() : -1;

		JsonElement vicinityJson = resultJson.get("vicinity");
		String vicinity = vicinityJson != null ? vicinityJson.getAsString() : "";

		return new Restaurant(placeId, name, location, photoReference, rating, vicinity);
	}

	public String getPlaceId() {
		return placeId;
	}

	public String getName() {
		return name;
	}

	public Geolocation getLocation() {
		return location;
	}

	public String getPhotoReference() {
		return photoReference;
	}

	public double getRating() {
		return rating;
	}

	public String getVicinity() {
		return vicinity;
	}

	@Override
	public String toString() {
		return String.format("%s %s Rating=%f Vicinity=%s", name, location, rating, vicinity);
	}
}
