package stories.app.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import stories.app.R;
import stories.app.models.Message;
import stories.app.models.responses.ServiceResponse;
import stories.app.services.MessagingService;
import stories.app.utils.LocalStorage;

public class DirectMessagesCreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_direct_messages_create);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Button sendMessageButton = this.findViewById(R.id.sendMessageButton);
        sendMessageButton.setOnClickListener(new DirectMessagesCreateActivity.SendMessageHandler());
        sendMessageButton.setEnabled(false);

        final EditText destination = findViewById(R.id.createMessageDestination);
        final EditText messageText = findViewById(R.id.createMessageText);

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    protected class SendMessageHandler implements View.OnClickListener {
        public void onClick(View v){
            EditText destination = findViewById(R.id.createMessageDestination);
            EditText messageText = findViewById(R.id.createMessageText);

            Message message = new Message();
            message.from_username = LocalStorage.getUser().username;
            message.to_username = destination.getText().toString();
            message.message = messageText.getText().toString();
            message.timestamp = System.currentTimeMillis();

            new DirectMessagesCreateActivity.SendMessageTask().execute(message);
        }
    }

    protected class SendMessageTask extends AsyncTask<Message, Void, ServiceResponse<Message>> {
        private MessagingService messagingService = new MessagingService();

        protected void onPreExecute() {
            Button sendMessageButton = findViewById(R.id.sendMessageButton);
            sendMessageButton.setEnabled(false);
        }

        protected ServiceResponse<Message> doInBackground(Message... params) {
            return messagingService.sendMessage(params[0]);
        }

        protected void onPostExecute(ServiceResponse<Message> response) {
            Button sendMessageButton = findViewById(R.id.sendMessageButton);
            sendMessageButton.setEnabled(true);

            TextView sendMessageResult = findViewById(R.id.sendMessageResult);

            ServiceResponse.ServiceStatusCode statusCode = response.getStatusCode();
            if (statusCode == ServiceResponse.ServiceStatusCode.SUCCESS){
                Message result = response.getServiceResponse();
                Intent navigationIntent = new Intent(DirectMessagesCreateActivity.this, DirectMessagesConversationActivity.class);
                navigationIntent.putExtra("friendUsername", result.to_username);
                startActivity(navigationIntent);
            }
            else if (statusCode == ServiceResponse.ServiceStatusCode.UNAUTHORIZED) {
                Intent navigationIntent = new Intent(DirectMessagesCreateActivity.this, LogInActivity.class);
                startActivity(navigationIntent);
            }
            else{
                sendMessageResult.setText(R.string.error_invalid_message);
            }
        }
    }
}
