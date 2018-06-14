package stories.app.activities;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import stories.app.R;
import stories.app.adapters.QuickStoriesAdapter;
import stories.app.adapters.StoriesAdapter;
import stories.app.models.Story;
import stories.app.services.StoryService;
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

        if (getIntent().getExtras() != null) {
            String from_username = getIntent().getExtras().getString("from_username");
            String to_username = getIntent().getExtras().getString("to_username");
        }
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
//            case R.id.chat:
//                navigationIntent = new Intent(HomeActivity.this, ChatActivity.class);
//                startActivity(navigationIntent);
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected class GetStoriesVisiblesToUserTask extends AsyncTask<String, Void, ArrayList<Story>> {
        private StoryService storyService = new StoryService();

        protected void onPreExecute() {
        }

        protected ArrayList<Story> doInBackground(String... params) {
            return storyService.getStoriesVisiblesToUser(params[0]);
        }

        protected void onPostExecute(ArrayList<Story> result) {

            // Split result between regular stories and quick stories
            ArrayList<Story> regularStories = new ArrayList<Story>();
            ArrayList<Story> quickStories = new ArrayList<Story>();

            if (result != null) {
                for (int i = 0; i < result.size(); i++) {
                    Story story = result.get(i);
                    if (story.isQuickStory) {
                        quickStories.add(story);
                    } else {
                        regularStories.add(story);
                    }
                }
            }

            // Display regular stories
            ListView storiesList = (ListView) findViewById(R.id.storiesList);
            storiesList.setAdapter(new StoriesAdapter(HomeActivity.this, regularStories));

            // Display quick stories
            RecyclerView quickStoriesList = (RecyclerView) findViewById(R.id.quickStoriesList);
            quickStoriesList.setAdapter(new QuickStoriesAdapter(HomeActivity.this, quickStories));
        }
    }

    protected class RefreshButtonOnClickHandler implements View.OnClickListener {
        public void onClick(View v) {
            // Retrieve all stories visibles to the user
            new GetStoriesVisiblesToUserTask().execute(LocalStorage.getUser().id);
        }
    }
}
