package directions;

import com.google.gson.Gson;

public class User {
	
	private String name;
	private String email;
	private String admin;
	private String addressTo;
	private String addressFrom;
	private String departureName;
	private String transportation;
	private boolean needRide;
	
	private static Gson gson = new Gson();
	
	public User() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}

	public String getAddressTo() {
		return addressTo;
	}

	public void setAddressTo(String addressTo) {
		this.addressTo = addressTo;
	}

	public String getAddressFrom() {
		return addressFrom;
	}

	public void setAddressFrom(String addressFrom) {
		this.addressFrom = addressFrom;
	}

	public String getDepartureName() {
		return departureName;
	}

	public void setDepartureName(String departureName) {
		this.departureName = departureName;
	}

	public String getTransportation() {
		return transportation;
	}

	public void setTransportation(String transportation) {
		this.transportation = transportation;
	}

	public boolean getNeedRide() {
		return needRide;
	}

	public void setNeedRide(boolean b) {
		this.needRide = b;
	}

	public String toJson() {
		return gson.toJson(this);
	}
	
}
