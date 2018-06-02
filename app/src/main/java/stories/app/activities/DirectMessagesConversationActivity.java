package stories.app.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import java.util.ArrayList;

import stories.app.R;
import stories.app.adapters.ConversationMessagesAdapter;
import stories.app.models.Message;
import stories.app.services.MessagingService;
import stories.app.utils.LocalStorage;

public class DirectMessagesConversationActivity extends AppCompatActivity {
    private MessagingService messagingService;
    private String friendUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_direct_messages_conversation);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        this.messagingService = new MessagingService();

        Intent myIntent = getIntent();
        friendUsername = myIntent.getStringExtra("friendUsername");

        setTitle(friendUsername);

        // Retrieve all visible messages to the user
        new FetchConversationTask().execute(LocalStorage.getUser().username, friendUsername);
    }

    protected class FetchConversationTask extends AsyncTask<String, Void, ArrayList<Message>> {
        protected void onPreExecute() {
        }

        protected ArrayList<Message> doInBackground(String... params) {
            return messagingService.getConversationMessages(params[0], params[1]).direct_messages;
        }

        protected void onPostExecute(ArrayList<Message> result) {
            if(result != null) {
                ListView messageList = (ListView) findViewById(R.id.messages_list_view);
                messageList.setAdapter(new ConversationMessagesAdapter(DirectMessagesConversationActivity.this, result));
            }
        }
    }
}
