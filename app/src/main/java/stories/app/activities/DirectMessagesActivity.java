package stories.app.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import java.util.ArrayList;

import stories.app.R;
import stories.app.adapters.DirectMessagesAdapter;
import stories.app.adapters.StoriesAdapter;
import stories.app.models.Message;
import stories.app.models.Story;
import stories.app.services.MessagingService;
import stories.app.utils.LocalStorage;

public class DirectMessagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_direct_messages);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Retrieve all stories visibles to the user
        new GetUserMessagesTask().execute(LocalStorage.getUser().id);
    }

    protected class GetUserMessagesTask extends AsyncTask<String, Void, ArrayList<Message>> {
        private MessagingService messagingService = new MessagingService();

        protected void onPreExecute() {
        }

        protected ArrayList<Message> doInBackground(String... params) {
            return messagingService.getUserMessages(params[0]);
        }

        protected void onPostExecute(ArrayList<Message> result) {

            ListView messageList = (ListView) findViewById(R.id.messages_list_view);
            messageList.setAdapter(new DirectMessagesAdapter(DirectMessagesActivity.this, result));
        }
    }

}
