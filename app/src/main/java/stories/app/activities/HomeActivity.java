package stories.app.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import stories.app.R;
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

        if (itemId == R.id.story_menu) {
            Intent navigationIntent = new Intent(HomeActivity.this, CreateStoryActivity.class);
            startActivity(navigationIntent);
            return true;
        }

        if(itemId == R.id.profile_menu) {
            Intent navigationIntent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(navigationIntent);
            return true;
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

            ListView storiesList = (ListView)findViewById(R.id.storiesList);
            storiesList.setAdapter(new StoriesAdapter(HomeActivity.this, result));
        }
    }
}
