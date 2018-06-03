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

import stories.app.utils.Constants;

public class FriendsService {

    private String URL = Constants.appServerURI;

    public ArrayList<Pair<String, String>> getFriends(String userID) {
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

            ArrayList<Pair<String,String>> friends = new ArrayList<Pair<String,String>>();

            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject itemObject = jsonArray.getJSONObject(i);
                // Pulling items from the array
                friends.add(Pair.create(itemObject.getString("username"), itemObject.getString("profile_pic")));
            }

            return friends;

        } catch(MalformedURLException error) {
            //Handles an incorrectly entered URL
            ArrayList<Pair<String,String>> result = new ArrayList<Pair<String,String>>();
            result.add(Pair.create("MalformedURLException",""));
            return result;
        }
        catch(SocketTimeoutException error) {
            //Handles URL access timeout.
            ArrayList<Pair<String,String>> result = new ArrayList<Pair<String,String>>();
            result.add(Pair.create("SocketTimeoutException",""));
            return result;
        }
        catch (IOException error) {
            //Handles input and output errors
            ArrayList<Pair<String,String>> result = new ArrayList<Pair<String,String>>();
            result.add(Pair.create("IOException",""));
            return result;
        }
        catch (JSONException error) {
            //Handles input and output errors
            ArrayList<Pair<String,String>> result = new ArrayList<Pair<String,String>>();
            result.add(Pair.create("JSONException",""));
            return result;
        }
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }
}
