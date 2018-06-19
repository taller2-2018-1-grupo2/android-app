package stories.app.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import stories.app.models.Story;
import stories.app.utils.Constants;
import stories.app.utils.LocalStorage;

public class StoryService extends BaseService {

    public ArrayList<Story> getStoriesVisiblesToUser(String userId) {

        HttpURLConnection client = null;

        try {
            URL url = new URL(Constants.appServerURI + "/stories?user_id=" + userId);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("GET");

            client.connect();

            JSONObject result = this.getResponseResult(client);
            JSONArray storiesJson = result.getJSONArray("stories");
            ArrayList<Story> stories = new ArrayList<>();

            for (int i = 0; i < storiesJson.length(); i++) {
                JSONObject storyJson = storiesJson.getJSONObject(i);
                stories.add(Story.fromJsonObject(storyJson));
            }

            return stories;
        } catch (Exception exception) {
            return null;
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }
    }

    public Story createStory(Story story) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(Constants.appServerURI + "/stories");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("Content-Type", "application/json");
            client.setRequestProperty("Accept", "application/json");

            JSONObject requestBody = Story.toJsonObject(story);

            OutputStream outputStream = client.getOutputStream();
            outputStream.write(requestBody.toString().getBytes("UTF-8"));
            outputStream.close();

            client.connect();

            JSONObject result = this.getResponseResult(client);

            if (result == null) {
                return null;
            }

            Story newStory = Story.fromJsonObject(result);
            return newStory;
        } catch (Exception exception) {
            return null;
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }
    }

    public String deleteStory(String storyID) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(Constants.appServerURI + "/stories/" + storyID);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("DELETE");

            client.connect();

            String response;

            int status = client.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                JSONObject result = this.getResponseResult(client);
                JSONObject story = result.getJSONObject("story");
                response = story.getString("story_id");
            } else {
                return null;
            }

            return response;

        } catch(MalformedURLException error) {
            //Handles an incorrectly entered URL
            return null;
        }
        catch(SocketTimeoutException error) {
            //Handles URL access timeout.
            return null;
        }
        catch (IOException error) {
            //Handles input and output errors
            return null;
        }
        catch (JSONException error) {
            //Handles JSON errors
            return null;
        }
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }
}
