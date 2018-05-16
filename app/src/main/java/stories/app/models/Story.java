package stories.app.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class Story {
    public String id;
    public String userId;
    public String title;
    public String description;
    public Boolean isQuickStory;
    public String visibility;
    public String fileUrl;
    public String location;
    public String timestamp;

    public static Story fromJsonObject(JSONObject storyJson) throws JSONException {
        Story story = new Story();
        story.id = storyJson.getString("id");
        story.userId = storyJson.getString("user_id");
        story.title = storyJson.getString("title");
        story.description = storyJson.getString("description");
        story.isQuickStory = storyJson.getBoolean("is_quick_story");
        story.visibility = storyJson.getString("visibility");
        story.fileUrl = storyJson.getString("file_url");
        story.location = storyJson.getString("location");
        story.timestamp = storyJson.getString("timestamp");

        return story;
    }

    public static JSONObject toJsonObject(Story story) throws JSONException {
        JSONObject storyJson = new JSONObject();
        storyJson.put("id", story.id);
        storyJson.put("user_id", story.userId);
        storyJson.put("title", story.title);
        storyJson.put("description", story.description);
        storyJson.put("is_quick_story", story.isQuickStory);
        storyJson.put("visibility", story.visibility);
        storyJson.put("file_url", story.fileUrl);
        storyJson.put("location", story.location);
        storyJson.put("timestamp", story.timestamp);

        return storyJson;
    }
}
