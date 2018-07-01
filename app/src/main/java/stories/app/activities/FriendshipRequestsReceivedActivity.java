package stories.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import stories.app.R;
import stories.app.adapters.UsersRecyclerViewAdapter;
import stories.app.services.FriendshipRequestsService;
import stories.app.models.responses.ServiceResponse;
import stories.app.utils.LocalStorage;

public class FriendshipRequestsReceivedActivity extends AppCompatActivity implements UsersRecyclerViewAdapter.ItemClickListener{

    private RecyclerView recyclerView;
    private UsersRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<HashMap<String,String>> dataset = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friendship_requests_received);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // set up the RecyclerView
        recyclerView = findViewById(R.id.friendship_requests_received_recycler_view);
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerViewAdapter = new UsersRecyclerViewAdapter(this, dataset, "received");
        recyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);

        new GetFriendshipRequestsReceived().execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onItemClick(View view, int position) {
        AcceptFriendshipRequestTask task = new AcceptFriendshipRequestTask(this);
        task.execute(recyclerViewAdapter.getItem(position).get("username"));
    }

    @Override
    public void onSendMessageClick(View v, int position) {
    }

    @Override
    public void onProfileButtonClick(View v, int position) {
    }

    protected class GetFriendshipRequestsReceived extends AsyncTask<String, Void, ServiceResponse<ArrayList<HashMap<String,String>>>> {
        private FriendshipRequestsService friendshipRequestsService = new FriendshipRequestsService();
        private Snackbar snackbar;

        public GetFriendshipRequestsReceived() {
            this.snackbar = Snackbar.make(findViewById(R.id.friendship_requests_received_layout), "Cargando...", Snackbar.LENGTH_INDEFINITE);
        }

        protected void onPreExecute() {
            this.snackbar.show();
        }

        protected ServiceResponse<ArrayList<HashMap<String,String>>> doInBackground(String... params) {
            return friendshipRequestsService.getFriendshipRequestsReceived(
                    LocalStorage.getUser().username
            );
        }

        protected void onPostExecute(ServiceResponse<ArrayList<HashMap<String,String>>> response) {
            this.snackbar.dismiss();

            ServiceResponse.ServiceStatusCode statusCode = response.getStatusCode();
            if (statusCode == ServiceResponse.ServiceStatusCode.SUCCESS) {
                ArrayList<HashMap<String,String>> result = response.getServiceResponse();
                dataset.clear();
                for (int i = 0; i < result.size(); i++) {
                    dataset.add(result.get(i));
                }
                recyclerViewAdapter.notifyDataSetChanged();
            } else if (statusCode == ServiceResponse.ServiceStatusCode.UNAUTHORIZED) {
                Intent navigationIntent = new Intent(FriendshipRequestsReceivedActivity.this, LogInActivity.class);
                startActivity(navigationIntent);
            }
        }
    }

    protected class AcceptFriendshipRequestTask extends AsyncTask<String, Void, ServiceResponse<String>> {
        private FriendshipRequestsService friendshipRequestsService = new FriendshipRequestsService();
        private Snackbar snackbar;
        private Context context;

        public AcceptFriendshipRequestTask (Context context){
            this.context = context;
            this.snackbar = Snackbar.make(findViewById(R.id.friendship_requests_received_layout), "Realizando operación...", Snackbar.LENGTH_INDEFINITE);
        }

        protected void onPreExecute() {
            this.snackbar.show();
        }

        protected ServiceResponse<String> doInBackground(String... params) {
            return friendshipRequestsService.acceptFriendshipRequest(
                    params[0],
                    LocalStorage.getUser().username
            );
        }

        protected void onPostExecute(ServiceResponse<String> response) {
            this.snackbar.dismiss();

            ServiceResponse.ServiceStatusCode statusCode = response.getStatusCode();

            if (statusCode == ServiceResponse.ServiceStatusCode.UNAUTHORIZED) {
                Intent navigationIntent = new Intent(FriendshipRequestsReceivedActivity.this, LogInActivity.class);
                startActivity(navigationIntent);
                return;
            }

            if (statusCode == ServiceResponse.ServiceStatusCode.SUCCESS) {
                String result = response.getServiceResponse();
                if (result != "") {
                    Toast.makeText(this.context, "Aceptaste una solicitud de amistad de: " + result, Toast.LENGTH_SHORT).show();

                    for (int i = 0; i < dataset.size(); i++) {
                        if (dataset.get(i).get("username") == result) {
                            dataset.remove(i);
                        }
                    }
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
