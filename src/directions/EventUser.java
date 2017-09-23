package directions;

import java.util.Date;

//Description of Users in the context of setting up an event
public class EventUser {
	
	//transportation options
	public static final String PUBLIC_TRANSPORT = "public transit";
	public static final String DRIVING = "driving";
	public static final String TAXI = "taxi";
	public static final String OTHER = "other";
	
	//Email is tied to user table
	private String email;
	private String admin;
	private String addressFrom;
	private Date leavingTime;
	private String addressTo;
	private Date arrivalTime;
	
	private String transportation;
	private boolean needRide;
		
}
