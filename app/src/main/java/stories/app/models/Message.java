package stories.app.models;

public class Message {

    public String _id;
    public String from_username;
    public String to_username;
    public String message;
    public long timestamp;

    @Override
    public String toString() {
        return "Message{" +
                "_id=" + _id + '\'' +
                ", from_username='" + from_username + '\'' +
                ", to_username=" + to_username +
                ", message=" + message +
                ", timestamp=" + timestamp +
                '}';
    }
}
