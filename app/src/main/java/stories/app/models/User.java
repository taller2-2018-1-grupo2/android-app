package stories.app.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    public String id;
    public String username;
    public String firstName;
    public String lastName;
    public String email;
    public String token;

    public static User fromJsonObject(JSONObject userJson) throws JSONException {
        User user = new User();
        user.id = userJson.getString("user_id");
        user.username = userJson.has("username") ? userJson.getString("username") : "";
        user.firstName = userJson.has("first_name") ? userJson.getString("first_name") : "";
        user.lastName = userJson.has("last_name") ? userJson.getString("last_name") : "";
        user.email = userJson.has("email") ? userJson.getString("email") : "";
        user.token = userJson.has("token") ? userJson.getJSONObject("token").getString("token") : "";

        return user;
    }

    public static JSONObject toJsonObject(User user) throws JSONException {
        JSONObject userJson = new JSONObject();
        userJson.put("user_id", user.id);
        userJson.put("username", user.username);
        userJson.put("first_name", user.firstName);
        userJson.put("last_name", user.lastName);
        userJson.put("email", user.email);

        return userJson;
    }
}
