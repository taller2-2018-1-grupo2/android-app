package stories.app.services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import stories.app.models.Story;
import stories.app.models.responses.ServiceResponse;
import stories.app.utils.Constants;
import stories.app.utils.LocalStorage;

public class StoryService extends BaseService {

    public ServiceResponse<ArrayList<Story>> getStoriesVisiblesToUser(String userId) {

        HttpURLConnection client = null;

        try {
            URL url = new URL(Constants.appServerURI + "/stories?user_id=" + userId);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("GET");
            String token = String.format("Bearer %s", LocalStorage.getToken());
            client.setRequestProperty("Authorization", token);

            client.connect();

            int statusCode = client.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED){
                return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.UNAUTHORIZED);
            }

            JSONObject result = this.getResponseResult(client);
            JSONArray storiesJson = result.getJSONArray("stories");
            ArrayList<Story> stories = new ArrayList<>();

            for (int i = 0; i < storiesJson.length(); i++) {
                JSONObject storyJson = storiesJson.getJSONObject(i);
                stories.add(Story.fromJsonObject(storyJson));
            }

            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.SUCCESS, stories);
        } catch (Exception exception) {
            return null;
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }
    }

    public ServiceResponse<Story> createStory(Story story) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(Constants.appServerURI + "/stories");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("Content-Type", "application/json");
            client.setRequestProperty("Accept", "application/json");
            String token = String.format("Bearer %s", LocalStorage.getToken());
            client.setRequestProperty("Authorization", token);

            JSONObject requestBody = Story.toJsonObject(story);

            OutputStream outputStream = client.getOutputStream();
            outputStream.write(requestBody.toString().getBytes("UTF-8"));
            outputStream.close();

            client.connect();

            int statusCode = client.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED){
                return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.UNAUTHORIZED);
            }

            JSONObject result = this.getResponseResult(client);

            if (result == null) {
                return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.ERROR);
            }

            Story newStory = Story.fromJsonObject(result);
            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.SUCCESS, newStory);
        } catch (Exception exception) {
            return null;
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }
    }

    public ServiceResponse<String> deleteStory(String storyID) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(Constants.appServerURI + "/stories/" + storyID);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("DELETE");
            String token = String.format("Bearer %s", LocalStorage.getToken());
            client.setRequestProperty("Authorization", token);

            client.connect();

            String response;

            int status = client.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                JSONObject result = this.getResponseResult(client);
                JSONObject story = result.getJSONObject("story");
                response = story.getString("story_id");
                return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.SUCCESS, response);
            } else if (status == HttpURLConnection.HTTP_UNAUTHORIZED){
                return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.UNAUTHORIZED);
            } else {
                return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.ERROR);
            }

        } catch(Exception error) {
            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.ERROR);
        }
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }
}
