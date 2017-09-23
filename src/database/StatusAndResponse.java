package database;

public class StatusAndResponse {
	private Integer status;
	private String response;
	
	public StatusAndResponse(Integer status, String response) {
		this.status = status;
		this.response =  response;
	}
	
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
}
