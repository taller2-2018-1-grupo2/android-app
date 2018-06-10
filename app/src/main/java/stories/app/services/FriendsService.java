package stories.app.services;

import android.util.Pair;

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

import stories.app.utils.Constants;

public class FriendsService {

    private String URL = Constants.appServerURI;

    public ArrayList<HashMap<String, String>> getFriends(String userID) {
        HttpURLConnection client = null;

        try {
            java.net.URL url = new URL(URL + "/users/friends/" + userID);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("GET");

            client.connect();

            BufferedReader br;

            if (200 <= client.getResponseCode() && client.getResponseCode() <= 299) {
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

            return friends;

        } catch(MalformedURLException error) {
            //Handles an incorrectly entered URL
            ArrayList<HashMap<String,String>> result = new ArrayList<>();
            HashMap<String,String> map = new HashMap<>();
            map.put("error", "MalformedURLException");
            result.add(map);
            return result;
        }
        catch(SocketTimeoutException error) {
            //Handles URL access timeout.
            ArrayList<HashMap<String,String>> result = new ArrayList<>();
            HashMap<String,String> map = new HashMap<>();
            map.put("error", "SocketTimeoutException");
            result.add(map);
            return result;
        }
        catch (IOException error) {
            //Handles input and output errors
            ArrayList<HashMap<String,String>> result = new ArrayList<>();
            HashMap<String,String> map = new HashMap<>();
            map.put("error", "IOException");
            result.add(map);
            return result;
        }
        catch (JSONException error) {
            //Handles input and output errors
            ArrayList<HashMap<String,String>> result = new ArrayList<>();
            HashMap<String,String> map = new HashMap<>();
            map.put("error", "JSONException");
            result.add(map);
            return result;
        }
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }
}
