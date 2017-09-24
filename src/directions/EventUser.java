package directions;

import com.google.gson.Gson;

//Description of Users in the context of setting up an event
public class EventUser {
	private static Gson gson = new Gson();
	
	//transportation options
	public static final String PUBLIC_TRANSPORT = "public transit";
	public static final String DRIVING = "driving";
	public static final String TAXI = "taxi";
	public static final String OTHER = "other";
	
	//status options
	public static final String UNREGISTERED = "UNREGISTERED";
	public static final String DONE = "DONE";
	
	//Email is tied to user table
	private String eventId;
	private String status;
	private String email;
	private boolean admin;
	private String addressFrom;
	private String leavingTime;
	private String addressTo;
	private String arrivalTime;
	
	private String transportation;
	private boolean needRide;
	
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean getAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public String getAddressFrom() {
		return addressFrom;
	}
	public void setAddressFrom(String addressFrom) {
		this.addressFrom = addressFrom;
	}
	public String getLeavingTime() {
		return leavingTime;
	}
	public void setLeavingTime(String leavingTime) {
		this.leavingTime = leavingTime;
	}
	public String getAddressTo() {
		return addressTo;
	}
	public void setAddressTo(String addressTo) {
		this.addressTo = addressTo;
	}
	public String getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public String getTransportation() {
		return transportation;
	}
	public void setTransportation(String transportation) {
		this.transportation = transportation;
	}
	public boolean isNeedRide() {
		return needRide;
	}
	public void setNeedRide(boolean needRide) {
		this.needRide = needRide;
	}
	
	public String toJson() {
		return gson.toJson(this);
	}
	
	public static EventUser fromJson(String json) {
		return gson.fromJson(json, EventUser.class);
	}
		
}
