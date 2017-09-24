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

import database.FirebaseConnector;
import directions.EventUser;
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
			saveUser(newUser);
			break;
		case "getEventUsers":
			String eventUsersJson = getEventUsers(request.getParameter("eventId"));
			o.print(eventUsersJson);
			break;
		case "saveEventUser":
			EventUser eventUser = new EventUser();
//			eventUser.setAddressFrom(addressFrom);
//			eventUser.setAddressTo(addressTo);
			eventUser.calculateLatLong();
			break;
		}

		o.flush();
		o.close();
	}
	
	private static String getEventUsers(String eventId) {
		HashMap<String, String> params = new HashMap<>();
		params.put("orderBy", "\"eventId\"");
		params.put("equalTo", "\""+eventId+"\"");
		return fbc.getData(EVENT_USER_TABLE, "", fbc.encodeParams(params));
	}
	
	private static List<EventUser> getEventUserList(String eventUserJson) {
		List<EventUser> userList = new ArrayList<>();
		Type gsonType = new TypeToken<Map<String,EventUser>>() {}.getType();
		Map<String,EventUser> eventUsers = gson.fromJson(eventUserJson, gsonType);
		for (EventUser user : eventUsers.values()) {
			userList.add(user);
		}
		return userList;
	}

	private static boolean saveUser(User newUser) {
		return fbc.putData(USER_TABLE, newUser.getEmail().replace(".","_"), newUser.toJson());	
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
		String response = getEventUsers("1");
		getEventUserList(response);
		System.out.println(response);
	}
}
