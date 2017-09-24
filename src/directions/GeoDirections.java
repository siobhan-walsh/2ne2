package directions;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

public class GeoDirections {
	
	private String addressFrom;
	private LatLng originCoordinate;
	private String addressTo;
	private LatLng destCoordinate;
	
	private List<LatLng> coordinatesOfTrip;
	
	private Double minutesOfTrip;
	private Double kilometersOfTrip;
	
	private DirectionsResult directionsResponse;
	
	private static final GeoApiContext context;
	
	static {
		context = new GeoApiContext.Builder().apiKey("AIzaSyBQ-A3XwmrhTU2mvK7XXaflOiOEkBf7Rd0").build();	
	}
	
	//instantiate geodirection and also save coordinates of first and last points
	public GeoDirections(String addressFrom, String addressTo) {
		this.addressFrom = addressFrom;
		this.addressTo = addressTo;
		
		directionsResponse = getDirections(this.addressFrom, this.addressTo);
		
		calculateDistanceAndDurationOfTrip(directionsResponse);	
		if (!coordinatesOfTrip.isEmpty()) {
			originCoordinate = coordinatesOfTrip.get(0);
			destCoordinate = coordinatesOfTrip.get(coordinatesOfTrip.size()-1);
		}
	}
	
	public void calculateDistanceAndDurationOfTrip(DirectionsResult result) {
		Long distanceInM = 0l;
		Long timeInSeconds = 0l;
		
		if (result != null && result.routes != null && result.routes.length > 0) {
			DirectionsRoute route = result.routes[0];
			if (coordinatesOfTrip == null) {
				coordinatesOfTrip = new ArrayList<LatLng>();
			}
			
			for (int i = 0; i < route.legs.length; i++) {
				DirectionsLeg leg = route.legs[i];
				if (i == 0) {
					coordinatesOfTrip.add(leg.startLocation);
				} 
				coordinatesOfTrip.add(leg.endLocation);
				distanceInM += leg.distance.inMeters;
				timeInSeconds += leg.duration.inSeconds;
			}
		}
		this.kilometersOfTrip = (distanceInM / 1000d);
		this.minutesOfTrip = (timeInSeconds / 60d);
		
	}
	
	public static DirectionsResult getDirections(String from, String to) {
		DirectionsResult result = null;
		try {
			result = DirectionsApi.getDirections(context, from, to).await();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void getOptimizedRoute(LatLng origin, LatLng destination, List<LatLng> extraWayPoints, DateTime time) throws Exception {		

		Instant departTime = Instant.now();
		if (time != null) {
			departTime = time.toInstant();
		}
		
		DirectionsResult result = DirectionsApi.newRequest(context)
        .origin(origin)
        .destination(destination)
        .departureTime(departTime)
        .waypoints(extraWayPoints.toArray(new LatLng[extraWayPoints.size()]))
        .optimizeWaypoints(true)
        .mode(TravelMode.DRIVING)
        .await();
		
		System.out.println(result.routes[0].summary);
	}
	
	
	public static void main(String [] args)
	{
		try {
			GeoDirections g = new GeoDirections("555 Seymour St, Vancouver, BC", "808 Marine Dr, West Vancouver, BC");
//			testGetDirections("555 Seymour St, Vancouver, BC", "808 Marine Dr, West Vancouver, BC");
//			
//			LatLng origin = new LatLng(49.2834546,-123.1174435);
//			LatLng destination = new LatLng(49.3571979,-123.2641182);
//			
//			List<LatLng> waypoints = new ArrayList<LatLng>();
//
//			LatLng ln = new LatLng(49.2506488,-123.054083);
//			LatLng ln2 = new LatLng(49.3265176,-123.1418956);
//			LatLng ln3 = new LatLng(49.3571979,-123.2641182);
//			
//			waypoints.add(ln);
//			waypoints.add(ln2);
//			waypoints.add(ln3);
//			
//			getOptimizedRoute(origin, destination, waypoints, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
