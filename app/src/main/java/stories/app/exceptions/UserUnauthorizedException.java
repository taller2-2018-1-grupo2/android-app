package stories.app.exceptions;

public class UserUnauthorizedException extends Exception{
    public UserUnauthorizedException(String message) {
        super(message);
    }
}
