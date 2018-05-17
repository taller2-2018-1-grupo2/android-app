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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import stories.app.R;
import stories.app.adapters.UsersRecyclerViewAdapter;
import stories.app.services.FriendshipRequestsService;
import stories.app.services.ProfileService;
import stories.app.utils.LocalStorage;

public class FriendshipRequestsActivity extends AppCompatActivity implements UsersRecyclerViewAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private UsersRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<String> dataset = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friendship_requests);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        SearchView searchView = this.findViewById(R.id.friends_search_bar);
        searchView.setOnQueryTextListener(new SearchQueryHandler());

        // set up the RecyclerView
        recyclerView = findViewById(R.id.users_recycler_view);
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerViewAdapter = new UsersRecyclerViewAdapter(this, dataset);
        recyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friendship_requests, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.friendship_requests_sent) {
            Intent navigationIntent = new Intent(FriendshipRequestsActivity.this, FriendshipRequestsSentActivity.class);
            startActivity(navigationIntent);
            return(true);
        } else if (item.getItemId() == R.id.friendship_requests_received) {
            Intent navigationIntent = new Intent(FriendshipRequestsActivity.this, FriendshipRequestsReceivedActivity.class);
            startActivity(navigationIntent);
            return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

    protected class SearchQueryHandler implements SearchView.OnQueryTextListener {
        public boolean onQueryTextChange(String s) {
            return true;
        }

        public boolean onQueryTextSubmit(String s){
            SearchView searchView = findViewById(R.id.friends_search_bar);

            new GetUsersTask().execute(searchView.getQuery().toString());

            searchView.clearFocus();

            return true;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        new CreateFriendshipRequestTask().execute(recyclerViewAdapter.getItem(position));

        Toast.makeText(this, "Enviaste una solicitud de amistad a: " + recyclerViewAdapter.getItem(position), Toast.LENGTH_SHORT).show();
    }

    protected class GetUsersTask extends AsyncTask<String, Void, ArrayList<String>> {
        private FriendshipRequestsService friendshipRequestsService = new FriendshipRequestsService();

        protected void onPreExecute() {
        }

        protected ArrayList<String> doInBackground(String... params) {
            return friendshipRequestsService.getUsers(
                    params[0],
                    LocalStorage.getUser().id
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

    protected class CreateFriendshipRequestTask extends AsyncTask<String, Void, Boolean> {
        private FriendshipRequestsService friendshipRequestsService = new FriendshipRequestsService();

        protected void onPreExecute() {
        }

        protected Boolean doInBackground(String... params) {
            return friendshipRequestsService.createFriendshipRequest(
                    LocalStorage.getUsername(),
                    params[0]
            );
        }

        protected void onPostExecute(Boolean result) {

        }
    }
}
