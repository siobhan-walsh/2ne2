package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.FirebaseConnector;
import directions.User;

public class UserController extends HttpServlet{
	private static FirebaseConnector fbc = new FirebaseConnector();
	private static String USER_TABLE = "Users";
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String command = request.getParameter("cmd");
		switch (command) {
		case "login":
			User user = User.fromJson(request.getParameter("userData"));
			checkUser(user);
			break;
		case "register":
			User newUser = User.fromJson(request.getParameter("userData"));
			saveUser(newUser);
			break;
		}
				
		PrintWriter o = response.getWriter();
		o.flush();
		o.close();
	}
	
	private static boolean saveUser(User newUser) {
		return fbc.putData(USER_TABLE, newUser.getEmail().replace(".","_"), newUser.toJson());	
	}

	private static boolean checkUser(User user) {
		String email = user.getEmail();
		String userData = fbc.getData(USER_TABLE,email.replace(".","_"),"");
		if (userData != null) {
			User checkUser = User.fromJson(userData);
			return user.getPassword().equals(checkUser.getPassword());
		}
		return false;
	}
	
	public static void main(String[] args) throws IOException {
		String command = "login";
		switch (command) {
		case "login":
//			User user = User.fromJson(request.getParameter("userData"));
			User user = new User();
			user.setEmail("cchan@agreementexpress.com");
			user.setName("Test");
			user.setPassword("password");
			boolean passwordMatch = checkUser(user);
			break;
		case "register":
//			User newUser = User.fromJson(request.getParameter("userData"));
			User newUser = new User();
			newUser.setEmail("cchan@agreementexpress.com");
			newUser.setName("Test");
			newUser.setPassword("password");
			saveUser(newUser);
			break;
		}
	}
}
