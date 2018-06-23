package stories.app.services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import stories.app.utils.Constants;
import stories.app.utils.LocalStorage;

public class ChatInstanceIDService extends FirebaseInstanceIdService {

    public static String FIREBASE_TOKEN = FirebaseInstanceId.getInstance().getToken();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        FIREBASE_TOKEN = refreshedToken;

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    public void sendRegistrationToServer(String newFirebaseToken) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(Constants.appServerURI + "/users/firebase/" + LocalStorage.getUser().id);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("PUT");
            client.setRequestProperty("Content-Type", "application/json");
            client.setRequestProperty("Accept", "application/json");

            OutputStream outputStream = client.getOutputStream();
            outputStream.write(newFirebaseToken.getBytes("UTF-8"));
            outputStream.close();

            client.connect();

        } catch (Exception exception) {
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }
    }
}
