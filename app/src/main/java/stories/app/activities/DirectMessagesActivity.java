package stories.app.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.util.ArrayList;

import stories.app.R;
import stories.app.adapters.DirectMessagesAdapter;
import stories.app.adapters.MessageHolder;
import stories.app.models.Message;
import stories.app.services.MessagingService;
import stories.app.utils.LocalStorage;

public class DirectMessagesActivity extends AppCompatActivity {

    private MessagingService messagingService;
    private DirectMessagesAdapter directMessagesAdapter;
    private ListView messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_direct_messages);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        messageList = (ListView) findViewById(R.id.messages_list_view);

        this.messagingService = new MessagingService();

        // Retrieve all visible messages to the user
        new GetUserMessagesTask().execute(LocalStorage.getUser().username);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_direct_messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Intent navigationIntent;
        if (itemId == R.id.item_create_message) {
            navigationIntent = new Intent(DirectMessagesActivity.this, DirectMessagesCreateActivity.class);
            startActivity(navigationIntent);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    protected class GetUserMessagesTask extends AsyncTask<String, Void, ArrayList<Message>> {
        protected void onPreExecute() {
        }

        protected ArrayList<Message> doInBackground(String... params) {
            return messagingService.getUserMessages(params[0]).direct_messages;
        }

        protected void onPostExecute(ArrayList<Message> result) {
            directMessagesAdapter = new DirectMessagesAdapter(DirectMessagesActivity.this, result);
            messageList.setAdapter(directMessagesAdapter);
            messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position, long itemId) {
                    // ItemClicked item = adapter.getItemAtPosition(position);
                    Message message = (Message) messageList.getAdapter().getItem(position);

                    Intent intent = new Intent(DirectMessagesActivity.this, DirectMessagesConversationActivity.class);
                    intent.putExtra("friendUsername", message._id);
                    //based on item add info to intent
                    startActivity(intent);
                }
            });
        }
    }

}
