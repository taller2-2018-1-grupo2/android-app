package stories.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import stories.app.R;
import stories.app.adapters.MessageHolder;
import stories.app.models.Message;
import stories.app.services.ChatService;

public class ChatActivity extends AppCompatActivity {

    private ChatService chatService;
    private FirebaseRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        this.chatService = new ChatService();

        RecyclerView recycler = (RecyclerView) findViewById(R.id.chat_recycler_view);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapter =
            new FirebaseRecyclerAdapter<Message, MessageHolder>(Message.class, R.layout.message_item, MessageHolder.class, chatService.messagesDB) {
                @Override
                public void populateViewHolder(MessageHolder predViewHolder, Message message, int position) {
                    predViewHolder.setMessageUsername(message);
                    predViewHolder.setMessage(message);
                    predViewHolder.setTimestamp(message);
                }
            };
        recycler.setAdapter(mAdapter);
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
            navigationIntent = new Intent(ChatActivity.this, ChatCreateActivity.class);
            startActivity(navigationIntent);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
}