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

public class AuthenticationService {

    private String URL = Constants.appServerURI;

    public Boolean loginUser(String username, String password) {
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

            BufferedReader br;

            if (200 <= client.getResponseCode() && client.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(client.getErrorStream()));
            }

            JSONObject result = this.getResponseResult(br);

            // Save the user's id and token
            User user = new User(
                result.getString("user_id"),
                null,
               null,
                null,
                result.getJSONObject("token").getString("token")
            );
            LocalStorage.setUser(user);
            return true;
        } catch(Exception exception) {
            return null;
        } finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

    public Boolean signinUser(String username, String email, String password, String firstName, String lastName) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(URL + "/users");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("Content-Type", "application/json");
            client.setRequestProperty("Accept", "application/json");

            JSONObject credentials = new JSONObject();
            credentials.put("username", username);
            credentials.put("email", email);
            credentials.put("password", password);
            credentials.put("first_name", firstName);
            credentials.put("last_name", lastName);

            OutputStream outputStream = client.getOutputStream();
            outputStream.write(credentials.toString().getBytes("UTF-8"));
            outputStream.close();

            client.connect();

            BufferedReader br;

            if (200 <= client.getResponseCode() && client.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(client.getErrorStream()));
            }

            JSONObject result = this.getResponseResult(br);

            // Save the user information (but not the token)
            JSONObject userObject = result.getJSONObject("user");
            User user = new User(
                userObject.getString("user_id"),
                userObject.getString("first_name"),
                userObject.getString("last_name"),
                userObject.getString("email"),
                null
            );
            LocalStorage.setUser(user);
            return true;
        } catch(Exception exception) {
            return null;
        } finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

    private JSONObject getResponseResult(BufferedReader br) throws IOException, JSONException {
        // Convert result to string
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }

        String stringResult = sb.toString();

        // Convert result string to JSON object
        JSONObject result = new JSONObject(stringResult);

        if (result.has("error")) {
            return null;
        }

        return result;
    }
}
