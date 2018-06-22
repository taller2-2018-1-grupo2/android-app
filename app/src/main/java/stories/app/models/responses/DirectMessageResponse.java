package stories.app.models.responses;

import java.util.ArrayList;

import stories.app.models.Message;
import stories.app.models.User;
import stories.app.models.UserDMResponse;

public class DirectMessageResponse {

    public ArrayList<Message> direct_messages;
    public UserDMResponse user;

    public DirectMessageResponse() {
        this.direct_messages = new ArrayList<>();
        this.user = new UserDMResponse();
    }
}
