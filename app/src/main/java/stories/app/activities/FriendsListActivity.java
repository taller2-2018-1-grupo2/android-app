package stories.app.activities;

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
import java.util.HashMap;

import stories.app.R;
import stories.app.adapters.UsersRecyclerViewAdapter;
import stories.app.services.FriendsService;
import stories.app.models.responses.ServiceResponse;
import stories.app.utils.LocalStorage;

public class FriendsListActivity extends AppCompatActivity implements UsersRecyclerViewAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private UsersRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<HashMap<String,String>> dataset = new ArrayList<HashMap<String,String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friends_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Amigos");
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // set up the RecyclerView
        recyclerView = findViewById(R.id.users_recycler_view);
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerViewAdapter = new UsersRecyclerViewAdapter(this, dataset, "friends");
        recyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);

        new GetFriendsTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onSendMessageClick(View v, int position) {
    }

    protected class GetFriendsTask extends AsyncTask<String, Void, ServiceResponse<ArrayList<HashMap<String,String>>>> {
        private FriendsService friendsService = new FriendsService();

        protected void onPreExecute() {
        }

        protected ServiceResponse<ArrayList<HashMap<String,String>>> doInBackground(String... params) {
            return friendsService.getFriends(
                    LocalStorage.getUser().id
            );
        }

        protected void onPostExecute(ServiceResponse<ArrayList<HashMap<String,String>>> response) {
            ServiceResponse.ServiceStatusCode statusCode = response.getStatusCode();
            if (statusCode == ServiceResponse.ServiceStatusCode.SUCCESS) {
                ArrayList<HashMap<String,String>> result = response.getServiceResponse();
                dataset.clear();
                for (int i = 0; i < result.size(); i++) {
                    dataset.add(result.get(i));
                }
                recyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

}

