package stories.app.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

import stories.app.R;
import stories.app.adapters.QuickStoriesAdapter;
import stories.app.adapters.StoriesAdapter;
import stories.app.models.Story;
import stories.app.models.responses.ServiceResponse;
import stories.app.services.ChatInstanceIDService;
import stories.app.services.StoryService;
import stories.app.utils.Dates;
import stories.app.utils.LocalStorage;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton refreshButton = (ImageButton) findViewById(R.id.refreshStories);
        refreshButton.setOnClickListener(new RefreshButtonOnClickHandler());

        // Retrieve all stories visibles to the user
        new GetStoriesVisiblesToUserTask().execute(LocalStorage.getUser().id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Intent navigationIntent;

        switch (itemId) {
            case R.id.story_menu:
                navigationIntent = new Intent(HomeActivity.this, CreateStoryActivity.class);
                startActivity(navigationIntent);
                return true;
            case R.id.profile_menu:
                navigationIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                navigationIntent.putExtra("username", LocalStorage.getUser().username);
                startActivity(navigationIntent);
                return true;
            case R.id.friendship_requests:
                navigationIntent = new Intent(HomeActivity.this, FriendshipRequestsActivity.class);
                startActivity(navigationIntent);
                return true;
            case R.id.direct_messages:
                navigationIntent = new Intent(HomeActivity.this, DirectMessagesActivity.class);
                startActivity(navigationIntent);
                return true;
            case R.id.stories_map:
                navigationIntent = new Intent(HomeActivity.this, StoriesMapActivity.class);
                startActivity(navigationIntent);
                return true;
            case R.id.logout:
                // borrar firebase token de la DB del appServer
                // sin firebase token, las notificaciones no llegan
                ChatInstanceIDService.sendRegistrationToServer("");
                LocalStorage.setUser(null);

                navigationIntent = new Intent(HomeActivity.this, LogInActivity.class);
                startActivity(navigationIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected class GetStoriesVisiblesToUserTask extends AsyncTask<String, Void, ServiceResponse<ArrayList<Story>>> {
        private StoryService storyService = new StoryService();
        private Snackbar snackbar;

        public GetStoriesVisiblesToUserTask() {
            this.snackbar = Snackbar.make(findViewById(R.id.activity_home_layout), "Cargando las historias...", Snackbar.LENGTH_INDEFINITE);
        }

        protected void onPreExecute() {
            // Show snackbar until all stories have been loaded
            this.snackbar.show();
        }

        protected ServiceResponse<ArrayList<Story>> doInBackground(String... params) {
            return storyService.getStoriesVisiblesToUser(params[0]);
        }

        protected void onPostExecute(ServiceResponse<ArrayList<Story>> response) {

            // Hide the snackbar
            this.snackbar.dismiss();

            ServiceResponse.ServiceStatusCode statusCode = response.getStatusCode();

            if (statusCode == ServiceResponse.ServiceStatusCode.UNAUTHORIZED) {
                Intent navigationIntent = new Intent(HomeActivity.this, LogInActivity.class);
                startActivity(navigationIntent);
                return;
            }

            ArrayList<Story> result = response.getServiceResponse();

            if (statusCode == ServiceResponse.ServiceStatusCode.SUCCESS && result != null) {

                // Split result between regular stories and quick stories
                ArrayList<Story> regularStories = new ArrayList<Story>();
                ArrayList<Story> quickStories = new ArrayList<Story>();

                for (int i = 0; i < result.size(); i++) {
                    Story story = result.get(i);
                    if (story.isQuickStory) {
                        // Display only 10 fresh stories (<4hs)
                        if (quickStories.size() < 10 && Dates.isFresh(story.timestamp)) {
                            quickStories.add(story);
                        }
                    } else {
                        regularStories.add(story);
                    }
                }

                // Display regular stories
                RecyclerView storiesList = (RecyclerView) findViewById(R.id.storiesList);
                storiesList.setAdapter(new StoriesAdapter(HomeActivity.this, regularStories));

                // Display quick stories
                RecyclerView quickStoriesList = (RecyclerView) findViewById(R.id.quickStoriesList);
                quickStoriesList.setAdapter(new QuickStoriesAdapter(HomeActivity.this, quickStories));
            }
        }
    }

    protected class RefreshButtonOnClickHandler implements View.OnClickListener {
        public void onClick(View v) {
            // Retrieve all stories visibles to the user
            new GetStoriesVisiblesToUserTask().execute(LocalStorage.getUser().id);
        }
    }
}
