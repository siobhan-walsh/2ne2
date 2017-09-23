package database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

public class FirebaseConnector {
	private static final String FIREBASE_URL = "https://newhere-da83c.firebaseio.com/";
	private static final String REST_EXT = ".json";
	private static final String CERTIFICATE = "{\"type\": \"service_account\", \"project_id\": \"newhere-da83c\",\"private_key_id\": \"bda1b1800d275c7514e3e8dd7fb2aa96aa2dd32b\", \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDG/L6i6xtd01W8\\nHJksO61r4Vul5xXibsX20ROksSKo/PyrtTiDycm2kTMiFcknpmWVPEgzcBYaT8Wn\\nbS+wy+q2RQqi4tcg9wMs1B8M86xG3oZZdRw6kbUFGFngk5Jlba2/UhyWFhESsEL8\\nfBp9J8qyu38BH3zARI68o8tVZ8M8Gi6ihI4be7ELOPQFFfKQhpCJrMyxj3JJHICA\\n1b1+pCfaFAqVlOWsgMew59bi9QfKqH/nPtHMX6FUWjs9g99LXSktYs+e0v5u6m0q\\n07LxeKSrgLKHDvQqU9AwZusUFnThsEL7j4c//55UQZV3AAJ2ZhILFFbEmAzqnzZ6\\nKr+dwqcXAgMBAAECggEAYukiV7hXF5njrYhnqSGW8B+Kn1pROgdHFwtRYNV9/ZMX\\ng8CmcXfd8QaW7LP4k+F18CsRVqQ6EGUjwjgXcyHBr019Xn80YVn3dZKRRq5fLbES\\nBiic2g4wdXJnxqaEKC/PJGFL/VzFqsz71yZXQmwDqTljBathpu7NJr6iZXcZCGe4\\nWukjLgp+VpKzR5T8LSnVGQitUnkhFlPm0R/lNtnTiLcb31Kso2Wzn0aoC916zcmC\\nuRDdnL/5Mbj/Eszokad4jd9d09Z0NhAtD/ekkKbzjT5lAYbP86BgnYE2o2tMeABx\\n+U/0dcFgWcboTCx9K8PDpHyRY9y553etsSzxso6YCQKBgQDyrH+IRLsXiBouvXK0\\nmlvHS3BcJn4E289JpGICgQRNBdJEl34Fz6Pa36D7e8KGu/2TqPRQ4CUtrkhIbHSV\\nzNHaiv9bkbOpMgOO8YoVtobu8NGlFu5xo7Guy03+oCX2WPHzwUf1jEUXz2cq70yn\\nBTv63t80DyU6nTtuMQpoQhAeTwKBgQDR6ho1WntVH6vouxx40OwL2ymqvtf8Xj20\\nXitgEBhoawEE8GZMxKcGIzPeWZANrYPx6J0lNMfTRLvxjZNcWaD/q5BWzfVFNeOa\\nFg+AOPSzzGesJbqRDX06tDKbl7Me8B8lJtCXJ3cBSjkvKbgt959h1mlbFO1vNcBA\\nTvvU/adAuQKBgD4tpJ+tvKJS5SKEuBc+VbXCxo9V7YQhLgOYcuRhWlDB5RZeGpQy\\nl7FFC2JkW3taP6bN64utYtlJhovH/jdDu6FbbAJyWq5HelHt4YuesQQYAB9kuMO1\\nRZmbzegn8JImSBhEtcNcpXdsVOrTVbiVxsCIynf0SX2zO78IIbqE8sxRAoGAdmh/\\ncCkh+NpstlMVtHDlNarizhXo78qZM+0Kup1Zp0z0vjx8+EDVlni1AW+z+oMuMn7s\\nZdhn/5x+B7u8rBNC5fV47f4vpGLJiFl5VPLL1cAvmsONgdUCFuMeyPXBbGo2p1ZF\\nw33XezrLFveJlSRhG2c+9snPRTQkxiHHnFWYysECgYEAx43lV+d9PSxV3ohRZI1C\\nr9Hyme6GDaPYgZFamS3WCwjBzOCvJ6tJSitPc4kNJ57Xfs65jIVP/t+K84MfSaV3\\nLYJl3dEOVQ7FAjSNml6bciQ1pc5nuVXeL7x3GhWcQ6bGqXf0t+PpfyGcHmGebrEK\\nQBjoaMbhEpo8U+M0HJ+4ujc=\\n-----END PRIVATE KEY-----\\n\", \"client_email\": \"firebase-adminsdk-2td75@newhere-da83c.iam.gserviceaccount.com\",  \"client_id\": \"100944820891499167009\",  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",  \"token_uri\": \"https://accounts.google.com/o/oauth2/token\", \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\", \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-2td75%40newhere-da83c.iam.gserviceaccount.com\"}";
	private static GoogleCredential scoped;
	
	static {
		try{
			GoogleCredential googleCred =  GoogleCredential.fromStream(new ByteArrayInputStream(CERTIFICATE.getBytes(StandardCharsets.UTF_8.name())));
			
			scoped = googleCred.createScoped(
				    Arrays.asList(
				      "https://www.googleapis.com/auth/firebase.database",
				      "https://www.googleapis.com/auth/userinfo.email"
				    )
				);
		} catch (Exception e) {
			e.printStackTrace();
			scoped = null;
		}
	}
	
	//Returns whether put was successful or not
	public boolean putData(String tableName, String key, String jsonData) {
		String url = FIREBASE_URL + tableName + "/" + key + REST_EXT + authenticationToken();
		HttpPut putRequest = new HttpPut(url);
		StringEntity entity;
		try {
			entity = new StringEntity(jsonData);
			putRequest.setEntity(entity);
			StatusAndResponse sReponse = this.makeRequest(putRequest);
			if (sReponse.getStatus() == 200) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean patchData(String tableName, String jsonData) {
		String url = FIREBASE_URL + tableName + REST_EXT + authenticationToken();
		HttpPatch patchRequest = new HttpPatch(url);
		StringEntity entity;
		try {
			entity = new StringEntity(jsonData);
			patchRequest.setEntity(entity);
			StatusAndResponse sReponse = this.makeRequest(patchRequest);
			if (sReponse.getStatus() == 200) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//Returns a json with the key that was generated for the new data
	// e.g. {"name":"-JSOpn9ZC54A4P4RoqVa"}
	public String postData(String tableName, String jsonData) {
		String url = FIREBASE_URL + tableName + REST_EXT + authenticationToken();
		HttpPost postRequest = new HttpPost(url);
		StringEntity entity;
		try {
			entity = new StringEntity(jsonData);
			postRequest.setEntity(entity);
			StatusAndResponse sReponse = this.makeRequest(postRequest);
			if (sReponse.getStatus() == 200) {
				return sReponse.getResponse();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean deleteData(String tableName, String key) {
		String url = FIREBASE_URL + tableName + "/" + key + REST_EXT + authenticationToken();
		HttpDelete deleteRequest = new HttpDelete(url);
		try {
			StatusAndResponse sReponse = this.makeRequest(deleteRequest);
			if (sReponse.getStatus() == 200) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String getData(String tableName, String key, String query) {
		String url = FIREBASE_URL + tableName + "/" + key + REST_EXT + authenticationToken() + query;
		HttpGet getRequest = new HttpGet(url);
		try {
			StatusAndResponse sReponse = this.makeRequest(getRequest);
			if (sReponse.getStatus() == 200) {
				return sReponse.getResponse();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private StatusAndResponse makeRequest(HttpRequestBase request) {
		StatusAndResponse response = null;

		if( request == null ) {
			return response;
		}
		
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			HttpResponse httpResponse = httpClient.execute(request);
			Integer statusCode = httpResponse.getStatusLine().getStatusCode();
			InputStream is = httpResponse.getEntity().getContent();
			
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) != -1) {
			    result.write(buffer, 0, length);
			}
			String responseStr = result.toString("UTF-8");
			response = new StatusAndResponse(statusCode, responseStr);
		} catch (IOException e) {
			e.printStackTrace();
		} 		
		
		return response;
	}
	
	private String authenticationToken() {
		try {
			scoped.refreshToken();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String token = scoped.getAccessToken();
		return "?access_token="+token;
	}
	
	public static void main(String[] args) throws IOException {
	}
	
	
}
