package stories.app.utils;

import stories.app.models.User;

public class LocalStorage {

    private static User user;
    private static String username;

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
}
