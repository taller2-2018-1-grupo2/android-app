package stories.app.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;

import stories.app.R;
import stories.app.activities.images.ImageFiltersActivity;
import stories.app.utils.BitmapUtils;
import stories.app.models.Story;
import stories.app.services.StoryService;
import stories.app.utils.LocalStorage;

public class CreateStoryActivity extends AppCompatActivity {

    private static final int START_IMAGE_FILTER = 5;
    private static final int FINISH_IMAGE_FILTER = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        //Hiding image upload button
        Button uploadImageButton = this.findViewById(R.id.uploadFileButton);
        uploadImageButton.setOnClickListener(new UploadImageHandler());
        uploadImageButton.setVisibility(View.GONE);

        Button createStoryButton = this.findViewById(R.id.createStoryButton);
        createStoryButton.setOnClickListener(new CreateStoryHandler());
    }

    protected class UploadImageHandler implements View.OnClickListener {
        public void onClick(View v){
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        }
    }

    public String getPath(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    private byte[] loadFileAsBytesArray(File file) throws Exception {
        int length = (int) file.length();
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
        byte[] bytes = new byte[length];
        reader.read(bytes, 0, length);
        reader.close();
        return bytes;
    }

    private String getUploadedFile(File file){
        try {
            byte[] byteArrayImage = loadFileAsBytesArray(file);
            String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            return encodedImage;
        } catch(Exception e){
            return "";
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_IMAGE_FILTER) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                Uri selectedImage = data.getData();
                String filePath = BitmapUtils.getBitmapPath(this, selectedImage);

                Intent navigationIntent = new Intent(CreateStoryActivity.this, ImageFiltersActivity.class);
                navigationIntent.putExtra("imageUri", filePath);
                startActivityForResult(navigationIntent, FINISH_IMAGE_FILTER);
            }
        }
        else if (requestCode == FINISH_IMAGE_FILTER){
            if (resultCode == AppCompatActivity.RESULT_OK) {

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

                String filePath = getPath(Uri.parse(data.getDataString()));
                File file = new File(filePath);
                story.uploadedFile = getUploadedFile(file);
                story.uploadedFilename = file.getName();

                new CreateStoryTask().execute(story);
            }
        }
    }



/** COMMENTING OLD IMAGE UPLOAD CODE
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == AppCompatActivity.RESULT_OK) {
                Uri selectedImage = data.getData();

                String filePath = getPath(selectedImage);
                String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);

                TextView uploadedFile = findViewById(R.id.uploadedFilename);
                uploadedFile.setText(filePath);

                Button uploadImageButton = this.findViewById(R.id.uploadFileButton);
                uploadImageButton.setVisibility(View.GONE);

                if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {
                    //FINE
                } else {
                    //NOT IN REQUIRED FORMAT
                }
            }
    }

    public String getPath(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    } **/


    protected class CreateStoryHandler implements View.OnClickListener {

        public void onClick(View v){
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, START_IMAGE_FILTER);
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
