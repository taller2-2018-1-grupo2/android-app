package stories.app.utils;

public class LocalStorage {

    private static String userID;
    private static String username;

    public static void setUserID(String newUserID) {
        userID = newUserID;
    }

    public static String getUserID() {
        return userID;
    }

    public static void setUsername(String newUsername) {
        username = newUsername;
    }

    public static String getUsername() {
        return username;
    }
}
