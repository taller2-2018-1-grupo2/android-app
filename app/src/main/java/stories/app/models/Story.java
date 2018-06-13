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
    public String uploadedFile;
    public String uploadedFilename;
    public String username;
    public String name;
    public String profilePic;

    //outgoing and incoming stories are different, consider refactor
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
        story.username = storyJson.has("username") ? storyJson.getString("username") : "";
        story.name = storyJson.has("name") ? storyJson.getString("name") : "";
        story.profilePic = storyJson.has("profile_pic") ? storyJson.getString("profile_pic") : "";

        return story;
    }

    //outgoing and incoming stories are different, consider refactor
    public static JSONObject toJsonObject(Story story) throws JSONException {
        JSONObject storyJson = new JSONObject();
        storyJson.put("id", story.id);
        storyJson.put("user_id", story.userId);
        storyJson.put("title", story.title);
        storyJson.put("description", story.description);
        storyJson.put("uploaded_file", story.uploadedFile);
        storyJson.put("uploaded_filename", story.uploadedFilename);
        storyJson.put("is_quick_story", story.isQuickStory);
        storyJson.put("visibility", story.visibility);
        storyJson.put("location", story.location);
        storyJson.put("timestamp", story.timestamp);

        return storyJson;
    }
}
