package stories.app.services;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import stories.app.models.User;
import stories.app.utils.Constants;
import stories.app.utils.LocalStorage;

public class AuthenticationService extends BaseService {

    private String URL = Constants.appServerURI;

    public User loginUser(String username, String password) {
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

            JSONObject result = this.getResponseResult(client);
            User user = User.fromJsonObject(result);
            user.username = username;

            // Save the user information
            LocalStorage.setUser(user);

            // Temporary until we find a solution to the code above, which doesn't work for username.
            LocalStorage.setUsername(username);

            return user;
        } catch(Exception exception) {
            return null;
        } finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

    public User signinUser(User user, String password) {
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

            JSONObject result = this.getResponseResult(client);
            User signedInUser = User.fromJsonObject(result.getJSONObject("user"));

            // TODO: merge both user and signedInUser

            // Save the user information
            LocalStorage.setUser(signedInUser);

            // Same as in Login method. Inconsistencies with code above.
            JSONObject jsonUser = result.getJSONObject("user");
            LocalStorage.setUsername(jsonUser.getString("username"));

            return signedInUser;
        } catch(Exception exception) {
            return null;
        } finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

    public User fbLoginUser(String username, String name, String pictureURL, String email, String firebaseToken) {
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

            JSONObject result = this.getResponseResult(client);
            User user = User.fromJsonObject(result.getJSONObject("user"));

            // TODO: merge both user and signedInUser

            // Save the user information
            LocalStorage.setUser(user);

            // Same as in Login method. Inconsistencies with code above.
            JSONObject jsonUser = result.getJSONObject("user");
            LocalStorage.setUsername(jsonUser.getString("username"));

            return user;
        } catch(Exception exception) {
            return null;
        } finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }
}
