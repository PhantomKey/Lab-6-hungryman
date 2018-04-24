package com.thehungryguy.data;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import javafx.scene.image.Image;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataRequester {
	private static final String SECRET_KEY_FILENAME = "secret.json";
	private static final String GOOGLE_PLACES_API_KEY_FIELD_NAME = "googlePlacesApiKey";
	private static final String GOOGLE_MAPS_GEOCODING_API_KEY_FIELD_NAME = "googleMapsGeocodingApiKey";
	private static final String IMAGE_CACHE_FOLDER_NAME = "cache/img";
	private static final String SEARCH_CACHE_FOLDER_NAME = "cache/search";

	private String googlePlacesApiKey;
	private String googleMapsGeocodingApiKey;
	private OkHttpClient client;
	private JsonParser parser;

	private DataRequester(String googlePlacesApiKey, String googleMapsGeocodingApiKey) {
		this.googlePlacesApiKey = googlePlacesApiKey;
		this.googleMapsGeocodingApiKey = googleMapsGeocodingApiKey;
		client = new OkHttpClient();
		parser = new JsonParser();
	}

	public static DataRequester createUsingSecretKeysFile()
			throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		FileReader secretKeysFileReader = new FileReader(SECRET_KEY_FILENAME);
		JsonObject secretFileJson = (JsonObject) new JsonParser().parse(secretKeysFileReader);

		String googlePlacesApiKey = readKeyFromJson(GOOGLE_PLACES_API_KEY_FIELD_NAME, secretFileJson);
		String googleMapsGeocodingApiKey = readKeyFromJson(GOOGLE_MAPS_GEOCODING_API_KEY_FIELD_NAME, secretFileJson);

		return new DataRequester(googlePlacesApiKey, googleMapsGeocodingApiKey);
	}

	private static String readKeyFromJson(String fieldName, JsonObject secretFileJson) {
		JsonElement keyJson = secretFileJson.get(fieldName);

		if (keyJson == null) {
			String exceptionMessage = String.format(
					"There is no \"%s\" field in secret keys file \"%s\". Did you misspell the field's name?",
					fieldName,
					SECRET_KEY_FILENAME);

			throw new RuntimeException(exceptionMessage);
		}

		String key = keyJson.getAsString().trim();

		if (key.isEmpty()) {
			String exceptionMessage = String.format(
					"Field \"%s\" in secret keys file \"%s\" is empty. Do you forget to put the secret key?",
					fieldName,
					SECRET_KEY_FILENAME);

			throw new RuntimeException(exceptionMessage);
		}

		return key;
	}

	public Geolocation getLocationOfPlace(String placeName) throws DataRequestException {
		if (placeName == null) {
			throw new IllegalArgumentException("Query string cannot be null.");
		}

		if (placeName.trim().isEmpty()) {
			throw new DataRequestException("Please enter the name of a place.");
		}

		System.out.println("Getting geolocation of the place \"" + placeName + "\".");

		try {
			// Artificially slow down the location getting in case if the internet is too
			// fast. :)
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		HttpUrl url = new HttpUrl.Builder()
				.scheme("https")
				.host("maps.googleapis.com")
				.addPathSegments("maps/api/geocode/json")
				.addQueryParameter("key", googleMapsGeocodingApiKey)
				.addQueryParameter("address", placeName)
				.addQueryParameter("region", "th").build();

		Request request = new Request.Builder().url(url).get().build();

		try (Response response = client.newCall(request).execute()) {
			JsonObject bodyJson = (JsonObject) parser.parse(response.body().charStream());
			checkStatusCode(bodyJson);

			JsonArray results = bodyJson.getAsJsonArray("results");
			if (results.size() == 0) {
				throw new DataRequestException(
						"Cannot find a place with name \"" + placeName + "\". Please try using another place.");
			}

			JsonObject firstPlace = results.get(0).getAsJsonObject();
			Geolocation location = Geolocation
					.parseFromApi(firstPlace.getAsJsonObject("geometry").getAsJsonObject("location"));

			System.out.println("Complete getting location.");

			return location;
		} catch (IOException e) {
			throw new DataRequestException("Cannot request for a location of a place.", e);
		}
	}

	public List<Restaurant> searchRestaurantsAroundLocation(Geolocation location, float radius)
			throws DataRequestException {
		if (location == null) {
			throw new IllegalArgumentException("Location cannot be null.");
		}

		if (radius < 10) {
			throw new IllegalArgumentException("Radius must be larger than 10 meters.");
		}

		if (radius > 50000) {
			throw new IllegalArgumentException("Radius must be smaller than 50000 meters.");
		}

		System.out.println("Searching for restaurants around location " + location + " with radius " + radius + ".");

		// First, try reading the search result from cache file first.

		String cacheEntryName = location.getLatitude() + "-" + location.getLongitude() + "-" + radius;
		File cacheFile = new File(SEARCH_CACHE_FOLDER_NAME + "/" + cacheEntryName);
		if (cacheFile.exists()) {
			System.out.println(
					"Using search result from cache for location " + location + " with radius " + radius + ".");
			try {
				CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
				InputStreamReader cacheFileReader = new InputStreamReader(new FileInputStream(cacheFile), decoder);
				JsonObject bodyJson = (JsonObject) parser.parse(cacheFileReader);
				cacheFileReader.close();
				return convertToRestaurants(bodyJson);
			} catch (IOException e) {
				System.out.println(
						"Reading cache for search result " + location + " with radius " + radius + " failed.");
				e.printStackTrace();
			}
		}

		// Requesting the actual API as a fallback.

		HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
				.scheme("https")
				.host("maps.googleapis.com")
				.addPathSegments("maps/api/place/nearbysearch/json")
				.addQueryParameter("key", googlePlacesApiKey)
				.addQueryParameter("region", "th")
				.addQueryParameter("location", location.getLatitude() + "," + location.getLongitude())
				.addQueryParameter("radius", Float.toString(radius))
				.addQueryParameter("type", "restaurant")
				.addQueryParameter("language", "th");

		Request request = new Request.Builder().url(urlBuilder.build()).get().build();

		try (Response response = client.newCall(request).execute()) {
			String bodyString = response.body().string();
			JsonObject bodyJson = (JsonObject) parser.parse(bodyString);

			// Writing the search result into a cache.

			OutputStreamWriter cacheFileWriter = null;
			try {
				cacheFile.getParentFile().mkdirs();
				CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
				cacheFileWriter = new OutputStreamWriter(new FileOutputStream(cacheFile), encoder);
				cacheFileWriter.write(bodyString);
			} catch (IOException e) {
				System.out.println(
						"Writing cache for search result " + location + " with radius " + radius + " failed.");
				e.printStackTrace();
			} finally {
				if (cacheFileWriter != null) {
					cacheFileWriter.close();
				}
			}

			System.out.println(
					"Complete searching for restaurants around location " + location + " with radius " + radius + ".");

			return convertToRestaurants(bodyJson);
		} catch (IOException e) {
			throw new DataRequestException("Cannot request for restaurants.", e);
		}
	}

	private List<Restaurant> convertToRestaurants(JsonObject bodyJson) throws DataRequestException {
		checkStatusCode(bodyJson);

		List<Restaurant> restaurants = new ArrayList<>();
		for (JsonElement resultJson : bodyJson.getAsJsonArray("results")) {
			restaurants.add(Restaurant.parseFromApi((JsonObject) resultJson));
		}

		return restaurants;
	}

	public Image getPhotoFromReference(String photoReference) throws DataRequestException {
		if (photoReference == null) {
			throw new IllegalArgumentException("Photo reference cannot be null.");
		}

		System.out.println("Loading photo \"" + photoReference + "\".");

		try {
			// Artificially slow down the photo loading. :)
			Thread.sleep(1000 + Math.round(Math.random() * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// First, try reading the image from cache file first.

		File cacheFile = new File(IMAGE_CACHE_FOLDER_NAME + "/" + photoReference);
		if (cacheFile.exists()) {
			System.out.println("Using image from cache for photo \"" + photoReference + "\".");
			try {
				FileInputStream cacheFileInputStream = new FileInputStream(cacheFile);
				Image cachedImage = new Image(cacheFileInputStream);
				cacheFileInputStream.close();
				return cachedImage;
			} catch (IOException e) {
				System.out.println("Reading cache for photo \"" + photoReference + "\" failed.");
				e.printStackTrace();
			}
		}

		// Requesting the actual API as a fallback.

		HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
				.scheme("https")
				.host("maps.googleapis.com")
				.addPathSegments("maps/api/place/photo")
				.addQueryParameter("key", googlePlacesApiKey)
				.addQueryParameter("maxheight", "160")
				.addQueryParameter("photoreference", photoReference);

		Request request = new Request.Builder().url(urlBuilder.build()).get().build();

		try (Response response = client.newCall(request).execute()) {
			if (response.code() == 400) {
				JsonObject bodyJson = (JsonObject) parser.parse(response.body().charStream());
				String errorMessage = "The API returns non-OK result";

				if (bodyJson.has("error_message")) {
					errorMessage += " with addition error message \"" + bodyJson.get("error_message").getAsString()
							+ "\"";
				}

				errorMessage += ".";
				throw new DataRequestException(errorMessage);
			}

			byte[] imageBytes = response.body().bytes();

			// Writing the image into a cache.

			FileOutputStream cacheFileOutputStream = null;
			try {
				cacheFile.getParentFile().mkdirs();
				cacheFileOutputStream = new FileOutputStream(cacheFile);
				cacheFileOutputStream.write(imageBytes);
			} catch (IOException e) {
				System.out.println("Writing cache for photo \"" + photoReference + "\" failed.");
				e.printStackTrace();
			} finally {
				if (cacheFileOutputStream != null) {
					cacheFileOutputStream.close();
				}
			}

			System.out.println("Complete loading photo \"" + photoReference + "\".");

			return new Image(new ByteArrayInputStream(imageBytes));
		} catch (IOException e) {
			throw new DataRequestException("Cannot request for a place photo.", e);
		}
	}

	private void checkStatusCode(JsonObject bodyJson) throws DataRequestException {
		String status = bodyJson.get("status").getAsString();

		if (status.equals("ZERO_RESULTS")) {
			throw new DataRequestException("Search result returns empty.");
		}

		if (!status.equals("OK")) {
			String errorMessage = "The API returns non-OK result: \"" + status + "\"";

			if (bodyJson.has("error_message")) {
				errorMessage += " with addition error message \"" + bodyJson.get("error_message").getAsString() + "\"";
			}

			errorMessage += ". Please see https://google-developers.appspot.com/places/web-service/search#PlaceSearchStatusCodes for more information.";
			throw new DataRequestException(errorMessage);
		}
	}
}
