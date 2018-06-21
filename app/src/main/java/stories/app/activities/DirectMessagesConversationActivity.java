package stories.app.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import stories.app.R;
import stories.app.adapters.ConversationMessagesAdapter;
import stories.app.adapters.MessagesRecyclerViewAdapter;
import stories.app.adapters.UsersRecyclerViewAdapter;
import stories.app.models.Message;
import stories.app.models.User;
import stories.app.services.ChatInstanceIDService;
import stories.app.services.ChatService;
import stories.app.services.MessagingService;
import stories.app.utils.LocalStorage;

public class DirectMessagesConversationActivity extends AppCompatActivity implements MessagesRecyclerViewAdapter.ItemClickListener{

    private MessagingService messagingService;
    private String friendUsername;
    private RecyclerView recyclerView;
    private MessagesRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<Message> dataset = new ArrayList<Message>();
    private final Handler handler = new Handler();
    private final int delay = 3000;
    public Runnable fetchConversationMessages;

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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbarTitle = (TextView) this.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(friendUsername);

        // set up the RecyclerView
        recyclerView = findViewById(R.id.messages_recycler_view);
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerViewAdapter = new MessagesRecyclerViewAdapter(this, dataset, "chat");
        recyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);

        // Retrieve all visible messages to the user
        new FetchConversationTask().execute(LocalStorage.getUser().username, friendUsername);

        fetchConversationMessages = new Runnable() {
            public void run(){
                // Retrieve all visible messages to the user
                new FetchConversationTask().execute(LocalStorage.getUser().username, friendUsername);
                handler.postDelayed(this, delay);
            }
        };

        handler.postDelayed(fetchConversationMessages, delay);

        Button sendMessageButton = this.findViewById(R.id.send_message_button);
        sendMessageButton.setOnClickListener(new SendMessageClickHandler());
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(fetchConversationMessages);
    }

    protected class SendMessageClickHandler implements View.OnClickListener {
        public void onClick(View v) {
            EditText messageText = findViewById(R.id.message_text);

            Message message = new Message();
            message.from_username = LocalStorage.getUsername();
            message.to_username = friendUsername;
            message.message = messageText.getText().toString();
            message.timestamp = System.currentTimeMillis();

            new SendMessageTask().execute(message);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
    }

    protected class SendMessageTask extends AsyncTask<Message, Void, Message> {
        private MessagingService messagingService = new MessagingService();

        protected void onPreExecute() {
            Button sendMessageButton = findViewById(R.id.send_message_button);
            sendMessageButton.setEnabled(false);
        }

        protected Message doInBackground(Message... params) {
            return messagingService.sendMessage(params[0]);
        }

        protected void onPostExecute(Message result) {
            Button sendMessageButton = findViewById(R.id.send_message_button);
            sendMessageButton.setEnabled(true);

            TextView messageText = findViewById(R.id.message_text);
            messageText.setText("");

            new FetchConversationTask().execute(LocalStorage.getUser().username, friendUsername);
        }
    }

    protected class FetchConversationTask extends AsyncTask<String, Void, ArrayList<Message>> {
        protected void onPreExecute() {
        }

        protected ArrayList<Message> doInBackground(String... params) {
            return messagingService.getConversationMessages(params[0], params[1]).direct_messages;
        }

        protected void onPostExecute(ArrayList<Message> result) {
            if(result != null) {
                dataset.clear();
                for (int i = 0; i < result.size(); i++) {
                    dataset.add(result.get(i));
                }
                recyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }
}
