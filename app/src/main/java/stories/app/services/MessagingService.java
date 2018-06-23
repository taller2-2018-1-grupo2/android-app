package stories.app.services;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import stories.app.models.Message;
import stories.app.models.responses.ConversationResponse;
import stories.app.models.responses.DirectMessageResponse;
import stories.app.models.responses.ServiceResponse;
import stories.app.utils.Constants;
import stories.app.utils.LocalStorage;

public class MessagingService {

    private Gson gson = new Gson();


    public ServiceResponse<ConversationResponse> getUserMessages(String username) {
        HttpURLConnection client = null;
        try {
            URL url = new URL(Constants.appServerURI + "/direct_message/" + username);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("GET");
            String token = String.format("Bearer %s", LocalStorage.getToken());
            client.setRequestProperty("Authorization", token);
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

            ConversationResponse messages = gson.fromJson(result, ConversationResponse.class);

            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.SUCCESS, messages);
        } catch (Exception exception) {
            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.ERROR);
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }
    }

    public ServiceResponse<DirectMessageResponse> getConversationMessages(String username1, String username2) {
        HttpURLConnection client = null;
        try {
            URL url = new URL(Constants.appServerURI + "/direct_message/conversation/" + username1 + "/" + username2);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("GET");
            String token = String.format("Bearer %s", LocalStorage.getToken());
            client.setRequestProperty("Authorization", token);
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

            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.SUCCESS, response);
        } catch (Exception exception) {
            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.ERROR);
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }
    }

    public ServiceResponse<Message> sendMessage(Message message) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(Constants.appServerURI + "/direct_message");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("Content-Type", "application/json");
            client.setRequestProperty("Accept", "application/json");
            String token = String.format("Bearer %s", LocalStorage.getToken());
            client.setRequestProperty("Authorization", token);

            String requestBody = gson.toJson(message);

            OutputStream outputStream = client.getOutputStream();
            outputStream.write(requestBody.getBytes("UTF-8"));
            outputStream.close();

            client.connect();

            if (200 <= client.getResponseCode() && client.getResponseCode() <= 299) {
                return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.SUCCESS, message);
            }
            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.ERROR);
        } catch (Exception exception) {
            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.ERROR);
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }
    }
}
