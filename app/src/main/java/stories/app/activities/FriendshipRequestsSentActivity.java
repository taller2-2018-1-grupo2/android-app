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

import java.util.ArrayList;

import stories.app.R;
import stories.app.adapters.FriendshipRequestsRecyclerViewAdapter;
import stories.app.services.FriendshipRequestsService;
import stories.app.utils.LocalStorage;

public class FriendshipRequestsSentActivity extends AppCompatActivity implements FriendshipRequestsRecyclerViewAdapter.ItemClickListener{

    private RecyclerView recyclerView;
    private FriendshipRequestsRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<String> dataset = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friendship_requests_sent);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // set up the RecyclerView
        recyclerView = findViewById(R.id.friendship_requests_sent_recycler_view);
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerViewAdapter = new FriendshipRequestsRecyclerViewAdapter(this, dataset);
        recyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);

        new GetFriendshipRequestsSent().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friendship_requests, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search_friends) {
            Intent navigationIntent = new Intent(FriendshipRequestsSentActivity.this, FriendshipRequestsActivity.class);
            startActivity(navigationIntent);
            return(true);
        } else if (item.getItemId() == R.id.friendship_requests_received) {
            Intent navigationIntent = new Intent(FriendshipRequestsSentActivity.this, FriendshipRequestsReceivedActivity.class);
            startActivity(navigationIntent);
            return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    protected class GetFriendshipRequestsSent extends AsyncTask<String, Void, ArrayList<String>> {
        private FriendshipRequestsService friendshipRequestsService = new FriendshipRequestsService();

        protected void onPreExecute() {
        }

        protected ArrayList<String> doInBackground(String... params) {
            return friendshipRequestsService.getFriendshipRequestsSent(
                    LocalStorage.getUsername()
            );
        }

        protected void onPostExecute(ArrayList<String> result) {
            dataset.clear();
            for (int i = 0; i < result.size(); i++) {
                dataset.add(result.get(i));
            }
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }
}