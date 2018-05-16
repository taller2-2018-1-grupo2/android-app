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
import stories.app.utils.LocalStorage;

public class AuthenticationService {

    private String URL = Constants.appServerURI;

    public boolean loginUser(String username, String password) {
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

            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            String result = sb.toString();

            JSONObject jsonObject = new JSONObject(result);
            String userID = jsonObject.getString("user_id");

            LocalStorage.setUserID(userID);
            LocalStorage.setUsername(username);

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

    public boolean signinUser(String username, String email, String password, String firstName, String lastName) {
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

            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            String result = sb.toString();

            JSONObject jsonObject = new JSONObject(result);
            String userJSON = jsonObject.getString("user");

            jsonObject = new JSONObject(userJSON);
            String userID = jsonObject.getString("user_id");

            LocalStorage.setUserID(userID);

            String saveUsername = jsonObject.getString("username");

            LocalStorage.setUsername(saveUsername);

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
