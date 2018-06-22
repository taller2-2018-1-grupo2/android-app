package stories.app.models.responses;

import java.util.ArrayList;

import stories.app.models.ConversationDMResponse;
import stories.app.models.Message;
import stories.app.models.UserDMResponse;

public class ConversationResponse {

    public ArrayList<ConversationDMResponse> direct_messages;

    public ConversationResponse() {
        this.direct_messages = new ArrayList<>();
    }
}
