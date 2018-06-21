package stories.app.utils;

import stories.app.models.User;

public class LocalStorage {

    private static User user;
    private static String username;
    private static String token;

    public static void setUser(User user) {
        LocalStorage.user = user;
    }

    public static User getUser() {
        return LocalStorage.user;
    }

    public static void setUsername(String newUsername) {
        username = newUsername;
    }

    public static String getUsername() {
        return username;
    }

    public static void setToken(String token) { LocalStorage.token = token; }
    
    public static String getToken() { return token; }
}
