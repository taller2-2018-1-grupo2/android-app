package stories.app.services;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import stories.app.models.Message;
import stories.app.models.responses.DirectMessageResponse;
import stories.app.utils.Constants;

public class MessagingService {

    private Gson gson = new Gson();


    public DirectMessageResponse getUserMessages(String username) {
        HttpURLConnection client = null;
        try {
            URL url = new URL(Constants.appServerURI + "/direct_message/" + username);
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

            DirectMessageResponse messages = gson.fromJson(result, DirectMessageResponse.class);

            return messages;
        } catch (Exception exception) {
            return null;
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }
    }

    public DirectMessageResponse getConversationMessages(String username1, String username2) {
        HttpURLConnection client = null;
        try {
            URL url = new URL(Constants.appServerURI + "/direct_message/conversation/" + username1 + "/" + username2);
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

            DirectMessageResponse response = gson.fromJson(result, DirectMessageResponse.class);

            return response;
        } catch (Exception exception) {
            return null;
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }
    }

    public Message sendMessage(Message message) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(Constants.appServerURI + "/direct_message");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("Content-Type", "application/json");
            client.setRequestProperty("Accept", "application/json");

            String requestBody = gson.toJson(message);

            OutputStream outputStream = client.getOutputStream();
            outputStream.write(requestBody.getBytes("UTF-8"));
            outputStream.close();

            client.connect();

            if (200 <= client.getResponseCode() && client.getResponseCode() <= 299) {
                return message;
            }
            return null;
        } catch (Exception exception) {
            return null;
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }
    }


}
