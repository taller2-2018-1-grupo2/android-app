package stories.app.services;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import stories.app.models.User;
import stories.app.models.responses.ServiceResponse;
import stories.app.utils.Constants;
import stories.app.utils.LocalStorage;

public class AuthenticationService extends BaseService {

    private String URL = Constants.appServerURI;

    public ServiceResponse<User> loginUser(String username, String password) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(URL + "/users/login");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("Content-Type", "application/json");
            client.setRequestProperty("Accept", "application/json");

            JSONObject credentials = new JSONObject();
            credentials.put("username",username);
            credentials.put("password", password);

            OutputStream outputStream = client.getOutputStream();
            outputStream.write(credentials.toString().getBytes("UTF-8"));
            outputStream.close();

            client.connect();

            // Save the user information
            JSONObject result = this.getResponseResult(client);
            User userResult = User.fromJsonObject(result.getJSONObject("user"));
            LocalStorage.setUser(userResult);

            // Saving token for future sends
            LocalStorage.setToken(result.getJSONObject("token").getString("token"));

            // enviar firebase token al appServer para guardar nuevamente en DB (borrado en sign out)
            // sin firebase token, las notificaciones no llegan
            ChatInstanceIDService.sendRegistrationToServer(ChatInstanceIDService.FIREBASE_TOKEN);

            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.SUCCESS, userResult);
        } catch(Exception exception) {
            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.ERROR);
        } finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

    public ServiceResponse<User> signinUser(User user, String password) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(URL + "/users");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("Content-Type", "application/json");
            client.setRequestProperty("Accept", "application/json");

            JSONObject requestBody = User.toJsonObject(user);
            requestBody.put("password", password);

            OutputStream outputStream = client.getOutputStream();
            outputStream.write(requestBody.toString().getBytes("UTF-8"));
            outputStream.close();

            client.connect();

            // Save the user information
            JSONObject result = this.getResponseResult(client);
            User userResult = User.fromJsonObject(result.getJSONObject("user"));
            LocalStorage.setUser(userResult);

            // Saving token for future sends
            LocalStorage.setToken(result.getJSONObject("token").getString("token"));

            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.SUCCESS, userResult);
        } catch(Exception exception) {
            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.ERROR);
        } finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

    public ServiceResponse<User> fbLoginUser(String username, String name, String pictureURL, String email, String firebaseToken) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(URL + "/users/fb_login");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("Content-Type", "application/json");
            client.setRequestProperty("Accept", "application/json");

            JSONObject credentials = new JSONObject();
            credentials.put("username",username);
            credentials.put("name", name);
            credentials.put("profile_pic", pictureURL);
            credentials.put("email", email);
            credentials.put("firebase_token", firebaseToken);

            OutputStream outputStream = client.getOutputStream();
            outputStream.write(credentials.toString().getBytes("UTF-8"));
            outputStream.close();

            client.connect();

            // Save the user information
            JSONObject result = this.getResponseResult(client);
            User userResult = User.fromJsonObject(result.getJSONObject("user"));
            LocalStorage.setUser(userResult);

            // Saving token for future sends
            LocalStorage.setToken(result.getJSONObject("token").getString("token"));

            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.SUCCESS, userResult);
        } catch(Exception exception) {
            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.ERROR);
        } finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }
}
