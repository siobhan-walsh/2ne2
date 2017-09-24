package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import database.FirebaseConnector;
import database.Tuple;
import directions.EventUser;
import directions.GeoDirections;
import directions.User;

public class UserController extends HttpServlet{
	private static Gson gson = new Gson();
	private static FirebaseConnector fbc = new FirebaseConnector();
	private static String USER_TABLE = "Users";
	private static String EVENT_USER_TABLE = "EventUsers";
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Tuple keyAndUser = null;
		String command = request.getParameter("cmd");
		PrintWriter o = response.getWriter();
		switch (command) {
		case "login":
			User user = User.fromJson(request.getParameter("userData"));
			boolean success = checkUser(user);
			o.print(success);
			break;
		case "register":
			User newUser = User.fromJson(request.getParameter("userData"));
			boolean registerSuccess = saveUser(newUser);
			EventUser registerEventUser = new EventUser();
			registerEventUser.setEmail(newUser.getEmail());
			String savedId_ = fbc.postData(EVENT_USER_TABLE, registerEventUser.toJson());
			o.print(registerSuccess);
			break;
		case "inviteUser": 
			EventUser invitedUser = new EventUser();
			invitedUser.setStatus(EventUser.UNREGISTERED);
			invitedUser.setEmail(request.getParameter("email"));
			String savedId = fbc.postData(EVENT_USER_TABLE, invitedUser.toJson());
			o.println(savedId);
			break;
		case "getEventUsers":
			String eventUsersJson = getEventUsers(request.getParameter("eventId"));
			o.print(eventUsersJson);
			break;
		case "saveEventUser":
			String eventUserToUpdate = request.getParameter("email");
			keyAndUser = getEventUser(request.getParameter("email"));
			
			EventUser eventUser = (EventUser) keyAndUser.getY();
			eventUser.setAddressFrom(request.getParameter("addressFrom"));
			eventUser.setAddressTo("555 Seymour St, Vancouver, BC");
			eventUser.setArrivalTime(null);
			eventUser.setLeavingTime(null);
			eventUser.setNeedRide(Boolean.parseBoolean(request.getParameter("needRide")));
			eventUser.setAdmin(false);
			eventUser.setEmail(request.getParameter("email"));
			eventUser.setStatus(EventUser.DONE);
			eventUser.setTransportation(request.getParameter("transportation"));
			eventUser.calculateLatLong();
			
			boolean savedExtraData = fbc.putData(EVENT_USER_TABLE, (String) keyAndUser.getX(), eventUser.toJson());
			o.println(savedExtraData);
			break;
		case "getNearbyUsers":
			keyAndUser = getEventUser(request.getParameter("email"));
			EventUser curUser = (EventUser) keyAndUser.getY();
			List<EventUser> nearbyGuests = getEventUserList(getEventUsers(request.getParameter("eventId")));
			List<EventUser> canjoin = GeoDirections.getAvailableGuests(curUser.getOriginCoordinate(), nearbyGuests);
			String nearbyGuestList =  gson.toJson(canjoin);
			o.println(nearbyGuestList);
			break;
		}
		o.flush();
		o.close();
	}
	
	public static String getEventUsers(String eventId) {
		HashMap<String, String> params = new HashMap<>();
		params.put("orderBy", "\"eventId\"");
		params.put("equalTo", "\""+eventId+"\"");
		return fbc.getData(EVENT_USER_TABLE, "", fbc.encodeParams(params));
	}
	
	public static Tuple getEventUser(String email) {
		HashMap<String, String> params = new HashMap<>();
		params.put("orderBy", "\"email\"");
		params.put("equalTo", "\""+email+"\"");
		String userData = fbc.getData(EVENT_USER_TABLE, "", fbc.encodeParams(params));
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = jsonParser.parse(userData).getAsJsonObject();
		String keyStr = "";
		for (String key : jsonObject.keySet()) {
			keyStr = key;
			break;
		}
		
		return new Tuple(keyStr, EventUser.fromJson(jsonObject.get(keyStr).toString()));
	}
	
	public static List<EventUser> getEventUserList(String eventUserJson) {
		List<EventUser> userList = new ArrayList<>();
		Type gsonType = new TypeToken<Map<String,EventUser>>() {}.getType();
		Map<String,EventUser> eventUsers = gson.fromJson(eventUserJson, gsonType);
		for (EventUser user : eventUsers.values()) {
			userList.add(user);
		}
		return userList;
	}

	private static boolean saveUser(User newUser) {
		if (newUser != null && newUser.getEmail() != null && !newUser.getEmail().isEmpty()) {
			return fbc.putData(USER_TABLE, newUser.getEmail().replace(".","_"), newUser.toJson());	
		}
		return false;
	}

	private static boolean checkUser(User user) {
		String email = user.getEmail();
		String userData = fbc.getData(USER_TABLE,email.replace(".","_"),"");
		if (userData != null && user != null && user.getPassword() != null) {
			User checkUser = User.fromJson(userData);
			if (checkUser != null) {
				return user.getPassword().equals(checkUser.getPassword());
			}
		}
		return false;
	}
	
	
	
	public static void main(String[] args) throws IOException {
		EventUser eu = new EventUser();
		eu.setAddressFrom("798 Granville St, Vancouver, BC");
		eu.setAddressTo("555 Seymour St, Vancouver, BC");
		eu.setArrivalTime(new DateTime().toString());
		eu.setLeavingTime(new DateTime().toString());
		eu.setNeedRide(true);
		eu.setAdmin(false);
		eu.setEmail("teste@test.com");
		eu.setStatus(EventUser.UNREGISTERED);
		eu.setTransportation(EventUser.TAXI);
		eu.setEventId("1");
		eu.calculateLatLong();
//		fbc.postData(EVENT_USER_TABLE, eu.toJson());
//		String response = getEventUsers("1");
//		getEventUserList(response);
//		System.out.println(response);
		
	}
}
