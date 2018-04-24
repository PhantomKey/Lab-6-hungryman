package com.thehungryguy.data;

import com.google.gson.JsonObject;

public class Geolocation {
	private float latitude;
	private float longitude;

	public Geolocation(float latitude, float longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	static Geolocation parseFromApi(JsonObject geolocationJson) {
		float lat = geolocationJson.get("lat").getAsFloat();
		float lng = geolocationJson.get("lng").getAsFloat();
		return new Geolocation(lat, lng);
	}

	public float getLatitude() {
		return latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	@Override
	public String toString() {
		return String.format("(%f, %f)", latitude, longitude);
	}
}
