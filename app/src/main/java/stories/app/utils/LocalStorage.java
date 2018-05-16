package stories.app.utils;

import stories.app.models.User;

public class LocalStorage {

    private static User user;

    public static void setUser(User user) {
        LocalStorage.user = user;
    }

    public static User getUser() {
        return LocalStorage.user;
    }
}
