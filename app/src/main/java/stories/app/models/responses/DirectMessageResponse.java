package stories.app.models.responses;

import java.util.ArrayList;

import stories.app.models.Message;

public class DirectMessageResponse {

    public ArrayList<Message> direct_messages;

    public DirectMessageResponse() {
        this.direct_messages = new ArrayList<>();
    }
}
