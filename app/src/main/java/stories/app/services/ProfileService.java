package stories.app.services;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import stories.app.utils.Constants;

public class ProfileService {

    private String URL = Constants.appServerURI;

    public String getUserData(String userID) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(URL + "/users/" + userID);
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

            return result;

        } catch(MalformedURLException error) {
            //Handles an incorrectly entered URL
            return "";
        }
        catch(SocketTimeoutException error) {
            //Handles URL access timeout.
            return "";
        }
        catch (IOException error) {
            //Handles input and output errors
            return "";
        }
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

    public Boolean updateUserData(String userID, String name, String email, String profilePic) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(URL + "/users/" + userID);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("PUT");
            client.setRequestProperty("Content-Type", "application/json");
            client.setRequestProperty("Accept", "application/json");

            JSONObject credentials = new JSONObject();
            credentials.put("name", name);
            credentials.put("email", email);
            credentials.put("profile_pic", profilePic);

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

            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            String result = sb.toString();

            return result != "";

        } catch(MalformedURLException error) {
            //Handles an incorrectly entered URL
            return false;
        }
        catch(SocketTimeoutException error) {
            //Handles URL access timeout.
            return false;
        }
        catch (IOException error) {
            //Handles input and output errors
            return false;
        }
        catch (JSONException error) {
            return false;
        }
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }
}
