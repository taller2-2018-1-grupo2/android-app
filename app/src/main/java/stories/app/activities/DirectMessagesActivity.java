package stories.app.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import stories.app.R;
import stories.app.adapters.ConversationsRecyclerViewAdapter;
import stories.app.models.ConversationDMResponse;
import stories.app.models.responses.ConversationResponse;
import stories.app.models.responses.ServiceResponse;
import stories.app.services.MessagingService;
import stories.app.utils.LocalStorage;

public class DirectMessagesActivity extends AppCompatActivity implements ConversationsRecyclerViewAdapter.ItemClickListener{

    private MessagingService messagingService;
    private RecyclerView recyclerView;
    private ConversationsRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<ConversationDMResponse> dataset = new ArrayList<ConversationDMResponse>();
    private final Handler handler = new Handler();
    private final int delay = 3000;
    public Runnable fetchConversations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_direct_messages);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.messagingService = new MessagingService();

        // set up the RecyclerView
        recyclerView = findViewById(R.id.conversations_recycler_view);
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerViewAdapter = new ConversationsRecyclerViewAdapter(this, dataset);
        recyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);

        // Retrieve all visible messages to the user
        new GetUserMessagesTask().execute(LocalStorage.getUser().username);

        fetchConversations = new Runnable() {
            public void run(){
                // Retrieve all visible messages to the user
                new GetUserMessagesTask().execute(LocalStorage.getUser().username);
                handler.postDelayed(this, delay);
            }
        };

        handler.postDelayed(fetchConversations, delay);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(fetchConversations);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(DirectMessagesActivity.this, DirectMessagesConversationActivity.class);
        intent.putExtra("friendUsername", recyclerViewAdapter.getItem(position)._id);
        //based on item add info to intent
        startActivity(intent);
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
        } else if (itemId == R.id.user_search) {
            navigationIntent = new Intent(DirectMessagesActivity.this, FriendshipRequestsActivity.class);
            startActivity(navigationIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected class GetUserMessagesTask extends AsyncTask<String, Void, ServiceResponse<ConversationResponse>> {

        protected ServiceResponse<ConversationResponse> doInBackground(String... params) {
            return messagingService.getUserMessages(params[0]);
        }

        protected void onPostExecute(ServiceResponse<ConversationResponse> response) {

            ServiceResponse.ServiceStatusCode statusCode = response.getStatusCode();
            if (statusCode == ServiceResponse.ServiceStatusCode.SUCCESS) {
                ArrayList<ConversationDMResponse> result = response.getServiceResponse().direct_messages;
                if (result != null) {
                    dataset.clear();
                    for (int i = 0; i < result.size(); i++) {
                        dataset.add(result.get(i));
                    }
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            } else if (statusCode == ServiceResponse.ServiceStatusCode.UNAUTHORIZED) {
                Intent navigationIntent = new Intent(DirectMessagesActivity.this, LogInActivity.class);
                startActivity(navigationIntent);
            }
        }
    }

}
