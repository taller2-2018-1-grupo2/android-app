package stories.app.models;

public class ConversationDMResponse {

    public String _id;
    public String from_username;
    public String to_username;
    public String message;
    public long timestamp;
    public String name;
    public String profile_pic;

    @Override
    public String toString() {
        return "Message{" +
                "_id=" + _id + '\'' +
                ", from_username='" + from_username + '\'' +
                ", to_username=" + to_username +
                ", message=" + message +
                ", timestamp=" + timestamp +
                ", name=" + name +
                ", profile_pic=" + profile_pic +
                '}';
    }
}
