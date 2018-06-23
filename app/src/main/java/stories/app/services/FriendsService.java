package stories.app.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import stories.app.models.responses.ServiceResponse;
import stories.app.utils.Constants;
import stories.app.utils.LocalStorage;

public class FriendsService {

    private String URL = Constants.appServerURI;

    public ServiceResponse<ArrayList<HashMap<String, String>>> getFriends(String userID) {
        HttpURLConnection client = null;

        try {
            java.net.URL url = new URL(URL + "/users/friends/" + userID);
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

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("friends");

            ArrayList<HashMap<String,String>> friends = new ArrayList<HashMap<String,String>>();

            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject itemObject = jsonArray.getJSONObject(i);
                HashMap<String,String> map = new HashMap<>();
                map.put("username", itemObject.getString("username"));
                map.put("profilePic", itemObject.getString("profile_pic"));
                map.put("name", itemObject.getString("name"));
                // Pulling items from the array
                friends.add(map);
            }

            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.SUCCESS, friends);

        } catch(MalformedURLException error) {
            //Handles an incorrectly entered URL
            ArrayList<HashMap<String,String>> result = new ArrayList<>();
            HashMap<String,String> map = new HashMap<>();
            map.put("error", "MalformedURLException");
            result.add(map);
            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.ERROR, result);
        }
        catch(SocketTimeoutException error) {
            //Handles URL access timeout.
            ArrayList<HashMap<String,String>> result = new ArrayList<>();
            HashMap<String,String> map = new HashMap<>();
            map.put("error", "SocketTimeoutException");
            result.add(map);
            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.ERROR, result);
        }
        catch (IOException error) {
            //Handles input and output errors
            ArrayList<HashMap<String,String>> result = new ArrayList<>();
            HashMap<String,String> map = new HashMap<>();
            map.put("error", "IOException");
            result.add(map);
            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.ERROR, result);
        }
        catch (JSONException error) {
            //Handles input and output errors
            ArrayList<HashMap<String,String>> result = new ArrayList<>();
            HashMap<String,String> map = new HashMap<>();
            map.put("error", "JSONException");
            result.add(map);
            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.ERROR, result);
        }
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }
}
