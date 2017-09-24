package directions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;
import com.google.maps.model.TransitRoutingPreference;
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
	
	public String getAddressFrom() {
		return addressFrom;
	}

	public void setAddressFrom(String addressFrom) {
		this.addressFrom = addressFrom;
	}

	public LatLng getOriginCoordinate() {
		return originCoordinate;
	}

	public void setOriginCoordinate(LatLng originCoordinate) {
		this.originCoordinate = originCoordinate;
	}

	public String getAddressTo() {
		return addressTo;
	}

	public void setAddressTo(String addressTo) {
		this.addressTo = addressTo;
	}

	public LatLng getDestCoordinate() {
		return destCoordinate;
	}

	public void setDestCoordinate(LatLng destCoordinate) {
		this.destCoordinate = destCoordinate;
	}

	public List<LatLng> getCoordinatesOfTrip() {
		return coordinatesOfTrip;
	}

	public void setCoordinatesOfTrip(List<LatLng> coordinatesOfTrip) {
		this.coordinatesOfTrip = coordinatesOfTrip;
	}

	public Double getMinutesOfTrip() {
		return minutesOfTrip;
	}

	public void setMinutesOfTrip(Double minutesOfTrip) {
		this.minutesOfTrip = minutesOfTrip;
	}

	public Double getKilometersOfTrip() {
		return kilometersOfTrip;
	}

	public void setKilometersOfTrip(Double kilometersOfTrip) {
		this.kilometersOfTrip = kilometersOfTrip;
	}

	public DirectionsResult getDirectionsResponse() {
		return directionsResponse;
	}

	public void setDirectionsResponse(DirectionsResult directionsResponse) {
		this.directionsResponse = directionsResponse;
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
	
	private List<Long> getCost(Long kilometers, Long duration){
		List<Long> list = new ArrayList<>();
		
		list.add(getEvoCost(duration));
		list.add(getTaxiCost(kilometers));
		list.add(getUberCost(kilometers)); //assuming uber is available in ur area
		
		return list;
	}
	
	private Long getUberCost(Long kilometers){
		Long miles = (long) (0.621 * kilometers); //based on non busy times
		Long costPerMile = (long) (0.97 * miles);
		
		return costPerMile;
	}
	
	private Long getTaxiCost(Long kilometers){
		Double startFare = 3.50; //starter fare
		Long costPerKilometer = (long) (1.85 * kilometers + startFare);
		
		return costPerKilometer;
	}
	
	private Long getEvoCost(Long duration){
		Double perMinute = 0.41;
		Long costPerMinute = (long) (perMinute * duration);
		
		return costPerMinute;
		
	}
	
	private static boolean getListOfBuses(LatLng origin, LatLng destination){
		GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyBQ-A3XwmrhTU2mvK7XXaflOiOEkBf7Rd0").build();
		boolean busAvailable = false;
		try {
			DirectionsResult result = DirectionsApi.newRequest(context)
			        .origin(origin)
			        .destination(destination)
			        .departureTime(Instant.now())
			        .optimizeWaypoints(true)
			        .mode(TravelMode.TRANSIT)
			        .transitRoutingPreference(TransitRoutingPreference.FEWER_TRANSFERS)
			        .await();
			if (result.routes.length > 0)
			{
				busAvailable = true;
			}

		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return busAvailable;
	
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
			
			LatLng ln2 = new LatLng(49.3265176,-123.1418956);
			LatLng ln3 = new LatLng(49.3571979,-123.2641182);
			
			LatLng sanfran = new LatLng(37.726087,-122.446342);
			getListOfBuses(ln2,sanfran);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
