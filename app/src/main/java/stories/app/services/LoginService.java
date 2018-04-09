package stories.app.services;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class LoginService {
    public boolean validateUser(String username, String password) {
        HttpURLConnection client = null;
        // see https://developer.android.com/reference/java/net/HttpURLConnection.html
        // and https://www.numetriclabz.com/android-post-and-get-request-using-httpurlconnection/
        try {
            URL url = new URL("http://www.google.com");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("GET");

            client.connect();

            int responseCode = client.getResponseCode();
            return responseCode == 200;

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
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }
}
