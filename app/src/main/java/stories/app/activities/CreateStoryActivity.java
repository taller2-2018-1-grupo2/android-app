package stories.app.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.Calendar;

import stories.app.R;
import stories.app.models.Story;
import stories.app.services.StoryService;
import stories.app.utils.LocalStorage;

public class CreateStoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        Button createStoryButton = this.findViewById(R.id.createStoryButton);
        createStoryButton.setOnClickListener(new CreateStoryHandler());
    }

    protected class CreateStoryHandler implements View.OnClickListener {
        public void onClick(View v){
            EditText title = findViewById(R.id.createStoryTitle);
            EditText description = findViewById(R.id.createStoryDescription);
            CheckBox isQuickStory = findViewById(R.id.createStoryIsQuickStory);
            RadioGroup visibility = findViewById(R.id.createStoryVisibility);

            int selectedVisibilityId = visibility.getCheckedRadioButtonId();
            String visibilityType = selectedVisibilityId == R.id.createStoryVisibilityIsPublic ? "public" : "private";

            Story story = new Story();
            story.userId = LocalStorage.getUser().id;
            story.title = title.getText().toString();
            story.description = description.getText().toString();
            story.isQuickStory = isQuickStory.isSelected();
            story.visibility = visibilityType;
            story.timestamp = Calendar.getInstance().getTime().toString();

            // TODO: get current or last location
            story.location = "40.714224,-73.961452";

            // TODO: get file
            story.fileUrl = "";

            new CreateStoryTask().execute(story);
        }
    }

    protected class CreateStoryTask extends AsyncTask<Story, Void, Story> {
        private StoryService storyService = new StoryService();

        protected void onPreExecute() {
            Button createStoryButton = findViewById(R.id.createStoryButton);
            createStoryButton.setEnabled(false);
        }

        protected Story doInBackground(Story... params) {
            return storyService.createStory(params[0]);
        }

        protected void onPostExecute(Story result) {
            Button createStoryButton = findViewById(R.id.createStoryButton);
            createStoryButton.setEnabled(true);

            if (result != null) {
                // Navigate to Home page
                Intent navigationIntent = new Intent(CreateStoryActivity.this, HomeActivity.class);
                startActivity(navigationIntent);
            }
        }
    }
}
