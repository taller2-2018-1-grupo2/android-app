package stories.app.services;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import stories.app.models.responses.ServiceResponse;
import stories.app.utils.Constants;
import stories.app.utils.LocalStorage;

public class ProfileService {

    private String URL = Constants.appServerURI;

    public ServiceResponse<String> getUserData(String username) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(URL + "/users/info/" + username);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("GET");
            String token = String.format("Bearer %s", LocalStorage.getToken());
            client.setRequestProperty("Authorization", token);

            client.connect();

            int statusCode = client.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED){
                return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.UNAUTHORIZED);
            }

            BufferedReader br;

            if (200 <= statusCode && statusCode <= 299) {
                br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(client.getErrorStream()));
            }

            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            String result = sb.toString();

            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.SUCCESS, result);

        } catch(Exception ex) {
            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.ERROR, "");
        }
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

    public ServiceResponse<Boolean> updateUserData(String userID, String name, String email, String profilePic) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(URL + "/users/" + userID);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("PUT");
            client.setRequestProperty("Content-Type", "application/json");
            client.setRequestProperty("Accept", "application/json");
            String token = String.format("Bearer %s", LocalStorage.getToken());
            client.setRequestProperty("Authorization", token);

            JSONObject credentials = new JSONObject();
            credentials.put("name", name);
            credentials.put("email", email);
            credentials.put("profile_pic", profilePic);

            OutputStream outputStream = client.getOutputStream();
            outputStream.write(credentials.toString().getBytes("UTF-8"));
            outputStream.close();

            client.connect();

            int statusCode = client.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED){
                return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.UNAUTHORIZED);
            }

            BufferedReader br;

            if (200 <= statusCode && statusCode <= 299) {
                br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(client.getErrorStream()));
            }

            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            String result = sb.toString();

            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.SUCCESS, result != "");

        } catch(Exception error) {
            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.ERROR, false);
        }
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }
}
