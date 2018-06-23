package stories.app.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import stories.app.R;
import stories.app.adapters.ConversationMessagesAdapter;
import stories.app.adapters.MessagesRecyclerViewAdapter;
import stories.app.adapters.UsersRecyclerViewAdapter;
import stories.app.models.Message;
import stories.app.models.User;
import stories.app.models.UserDMResponse;
import stories.app.models.responses.DirectMessageResponse;
import stories.app.services.ChatInstanceIDService;
import stories.app.services.ChatService;
import stories.app.services.MessagingService;
import stories.app.utils.Base64UtilityClass;
import stories.app.utils.LocalStorage;

public class DirectMessagesConversationActivity extends AppCompatActivity {

    private MessagingService messagingService;
    private String friendUsername;
    private RecyclerView recyclerView;
    private MessagesRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<Message> dataset = new ArrayList<Message>();
    private final Handler handler = new Handler();
    private final int delay = 3000;
    public Runnable fetchConversationMessages;
    private boolean infoAlreadySet = false;

    private LayoutInflater mInflater;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_direct_messages_conversation);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        this.messagingService = new MessagingService();

        Intent myIntent = getIntent();
        friendUsername = myIntent.getStringExtra("friendUsername");

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        mInflater = LayoutInflater.from(this);

        // set up the RecyclerView
        recyclerView = findViewById(R.id.messages_recycler_view);
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerViewAdapter = new MessagesRecyclerViewAdapter(this, dataset);
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
        infoAlreadySet = false;
    }

    protected class SendMessageClickHandler implements View.OnClickListener {
        public void onClick(View v) {
            EditText messageText = findViewById(R.id.message_text);

            Message message = new Message();
            message.from_username = LocalStorage.getUser().username;
            message.to_username = friendUsername;
            message.message = messageText.getText().toString();
            message.timestamp = System.currentTimeMillis();

            new SendMessageTask().execute(message);
        }
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

    protected class FetchConversationTask extends AsyncTask<String, Void, DirectMessageResponse> {
        protected void onPreExecute() {
        }

        protected DirectMessageResponse doInBackground(String... params) {
            return messagingService.getConversationMessages(params[0], params[1]);
        }

        protected void onPostExecute(DirectMessageResponse result) {
            if(result != null) {
                dataset.clear();
                for (int i = 0; i < result.direct_messages.size(); i++) {
                    dataset.add(result.direct_messages.get(i));
                }
                recyclerViewAdapter.notifyDataSetChanged();
                LinearLayoutManager rvLayoutManager = (LinearLayoutManager) recyclerViewLayoutManager;
                rvLayoutManager.setStackFromEnd(true);

                if (!infoAlreadySet) {
                    View mCustomView = mInflater.inflate(R.layout.dm_custom_toolbar, null);

                    UserDMResponse user = result.user;
                    TextView toolbarNameView = mCustomView.findViewById(R.id.toolbar_name);
                    TextView toolbarUsernameView = mCustomView.findViewById(R.id.toolbar_username);
                    CircleImageView toolbarProfilePicView = mCustomView.findViewById(R.id.toolbar_profile_pic);

                    toolbarNameView.setText(user.name);
                    toolbarUsernameView.setText(user.username);

                    if (!user.profile_pic.isEmpty()) {
                        toolbarProfilePicView.setImageBitmap(Base64UtilityClass.toBitmap(user.profile_pic));
                    }

                    mActionBar.setCustomView(mCustomView);
                    mActionBar.setDisplayShowCustomEnabled(true);
                    infoAlreadySet = true;
                }
            }
        }
    }
}
