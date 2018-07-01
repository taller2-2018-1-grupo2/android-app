package stories.app.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import stories.app.R;
import stories.app.models.Message;
import stories.app.utils.LocalStorage;

public class ChatCreateActivity extends AppCompatActivity {
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_create);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        dbRef = FirebaseDatabase.getInstance().getReference();

        final Button sendMessageButton = this.findViewById(R.id.sendChatMessageButton);
        sendMessageButton.setOnClickListener(new ChatCreateActivity.SendMessageHandler());
        sendMessageButton.setEnabled(false);

        final EditText destination = findViewById(R.id.chatDestination);
        final EditText messageText = findViewById(R.id.chatText);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sendMessageButton.setEnabled(
                    destination.getText().length() != 0 && messageText.getText().length() != 0
                );
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };

        destination.addTextChangedListener(textWatcher);
        messageText.addTextChangedListener(textWatcher);
    }

    protected class SendMessageHandler implements View.OnClickListener {
        public void onClick(View v) {
            EditText destination = findViewById(R.id.chatDestination);
            EditText messageText = findViewById(R.id.chatText);

            Message message = new Message();
            message.to_username = destination.getText().toString();
            message.message = messageText.getText().toString();
            message.from_username = LocalStorage.getUser().username;
            message.timestamp = System.currentTimeMillis();

            new ChatCreateActivity.SendMessageTask().execute(message);
        }
    }

    protected class SendMessageTask extends AsyncTask<Message, Void, Message> {

        protected void onPreExecute() {
            Button sendMessageButton = findViewById(R.id.sendChatMessageButton);
            sendMessageButton.setEnabled(false);
        }

        protected Message doInBackground(Message... params) {
            dbRef.push().setValue(params[0]);
            return params[0];
        }

        protected void onPostExecute(Message result) {
            Button sendMessageButton = findViewById(R.id.sendChatMessageButton);
            sendMessageButton.setEnabled(true);

            if (result != null) {
                // Navigate to Home page
                Intent navigationIntent = new Intent(ChatCreateActivity.this, ChatActivity.class);
                startActivity(navigationIntent);
            }
        }
    }
}
