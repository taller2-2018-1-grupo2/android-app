package stories.app.models;

public class UserDMResponse {

    public String user_id;
    public String username;
    public String name;
    public String email;
    public String profile_pic;

    @Override
    public String toString() {
        return "Message{" +
                "user_id=" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", name=" + name +
                ", email=" + email +
                ", profile_pic=" + profile_pic +
                '}';
    }
}
