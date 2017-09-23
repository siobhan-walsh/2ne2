package directions;

import com.google.gson.Gson;

public class User {
	private static Gson gson = new Gson();
	
	private String email;
	private String password;
	private String name; //Optional

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toJson() {
		return gson.toJson(this);
	}
	
	public static User fromJson(String json) {
		return gson.fromJson(json, User.class);
	}

}
