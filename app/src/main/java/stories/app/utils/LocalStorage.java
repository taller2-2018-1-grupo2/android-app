package stories.app.utils;

public class LocalStorage {

    private static String userID;

    public static void setUserID(String newUserID) {
        userID = newUserID;
    }

    public static String getUserID() {
        return userID;
    }
}
