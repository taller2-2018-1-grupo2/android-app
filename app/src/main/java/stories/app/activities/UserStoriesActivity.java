package stories.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class UserStoriesActivity extends AppCompatActivity {
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_stories);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent myIntent = getIntent();
        username = myIntent.getStringExtra("username");

        // Retrieve all stories visibles to the user
        GetStoriesFromUserTask task = new GetStoriesFromUserTask(this);
        task.execute(username, LocalStorage.getUser().id);
    }

    protected class GetStoriesFromUserTask extends AsyncTask<String, Void, ServiceResponse<ArrayList<Story>>> {
        private StoryService storyService = new StoryService();
        private Context mContext;

        public GetStoriesFromUserTask(Context context) {
            this.mContext = context;
        }

        protected void onPreExecute() {
        }

        protected ServiceResponse<ArrayList<Story>> doInBackground(String... params) {
            return storyService.getStoriesFromUser(params[0], params[1]);
        }

        protected void onPostExecute(ServiceResponse<ArrayList<Story>> response) {

            ServiceResponse.ServiceStatusCode statusCode = response.getStatusCode();

            if (statusCode == ServiceResponse.ServiceStatusCode.SUCCESS) {
                ArrayList<Story> result = response.getServiceResponse();

                // Split result between regular stories and quick stories
                ArrayList<Story> regularStories = new ArrayList<Story>();

                if (result != null) {
                    for (int i = 0; i < result.size(); i++) {
                        Story story = result.get(i);
                        if (!story.isQuickStory) {
                            regularStories.add(story);
                        }
                    }
                }
                // Display regular stories
                RecyclerView storiesList = (RecyclerView) findViewById(R.id.storiesList);
                storiesList.setAdapter(new StoriesAdapter(this.mContext, regularStories));

            } else if (statusCode == ServiceResponse.ServiceStatusCode.UNAUTHORIZED) {
                Intent navigationIntent = new Intent(UserStoriesActivity.this, LogInActivity.class);
                startActivity(navigationIntent);
            }
        }
    }
}
