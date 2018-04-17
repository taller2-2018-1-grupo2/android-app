package stories.app.services;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class AuthenticationService {
    public boolean loginUser(String username, String password) {
        HttpURLConnection client = null;

        try {
            URL url = new URL("http://192.168.0.22:8000/api/v1/users/login");
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

    public boolean signinUser(String username, String email, String password) {
        HttpURLConnection client = null;

        try {
            URL url = new URL("http://192.168.0.22:8000/api/v1/users");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("Content-Type", "application/json");
            client.setRequestProperty("Accept", "application/json");

            JSONObject credentials = new JSONObject();

            credentials.put("username", username);
            credentials.put("email", email);
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
