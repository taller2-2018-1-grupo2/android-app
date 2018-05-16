package stories.app.models;

public class User {
    public String id;
    public String firstName;
    public String lastName;
    public String email;
    public String token;

    public User(String id, String firstName, String lastName, String email, String token) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.token = token;
    }
}
