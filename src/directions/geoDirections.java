package directions;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Instant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

public class geoDirections {
	private final String getDirectionsResponse;
	private final String builderResponse;
	private final String responseTimesArePopulatedCorrectly;
	
	public geoDirections()
	{
		getDirectionsResponse = "";
		builderResponse = "";
		responseTimesArePopulatedCorrectly = "";
	}
	
	public static void testGetDirections(String from, String to) throws Exception{

		GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyBQ-A3XwmrhTU2mvK7XXaflOiOEkBf7Rd0").build();
		DirectionsResult result = DirectionsApi.getDirections(context, from, to).await();
		
		
		System.out.println(result.routes[0].summary);
		System.out.println(result.routes[0].overviewPolyline.getEncodedPath());
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		
		JsonElement je = jp.parse(result.routes[0].bounds.northeast.toString()); //need help w/ this part eh -- cna
		String prettyJsonString = gson.toJson(je);
		System.out.println(prettyJsonString);
		
	}
	
	public static void getOptimizedRoute() throws Exception {
		LatLng ln = new LatLng(49.2506488,-123.054083);
		LatLng ln2 = new LatLng(49.3265176,-123.1418956);
		LatLng ln3 = new LatLng(49.3571979,-123.2641182);
		
		LatLng origin = new LatLng(49.2834546,-123.1174435);
		LatLng destination = new LatLng(49.3571979,-123.2641182);
		GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyBQ-A3XwmrhTU2mvK7XXaflOiOEkBf7Rd0").build();
		
		List<LatLng> waypoints = new ArrayList<LatLng>();
		
		waypoints.add(ln);
		waypoints.add(ln2);
		waypoints.add(ln3);
		
		DirectionsResult result = DirectionsApi.newRequest(context)
        .origin(origin)
        .destination(destination)
        .departureTime(Instant.now())
        .waypoints(waypoints.subList(2, waypoints.size()).toArray(new LatLng[0]))
        .optimizeWaypoints(true)
        .mode(TravelMode.DRIVING)
        .await();
		
		System.out.println(result.routes[0].summary);
	}

	public String getGetDirectionsResponse() {
		return getDirectionsResponse;
	}

	public String getBuilderResponse() {
		return builderResponse;
	}

	public String getResponseTimesArePopulatedCorrectly() {
		return responseTimesArePopulatedCorrectly;
	}
	
	public static void main(String [] args)
	{
		try {
			testGetDirections("555 Seymour St, Vancouver, BC", "808 Marine Dr, West Vancouver, BC");
			getOptimizedRoute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
