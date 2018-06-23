package stories.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import java.io.File;
import java.util.Calendar;

import stories.app.R;
import stories.app.activities.images.ImageFiltersActivity;
import stories.app.models.Story;
import stories.app.services.FileService;
import stories.app.services.LocationService;
import stories.app.models.responses.ServiceResponse;
import stories.app.services.StoryService;
import stories.app.utils.FileUtils;
import stories.app.utils.LocalStorage;

public class CreateStoryActivity extends AppCompatActivity {

    private static final int START_FILE_UPLOAD = 5;
    private static final int FINISH_IMAGE_FILTER = 6;
    private enum SUPPORTED_IMAGE_FORMATS { jpeg, jpg, png }
    private enum SUPPORTED_VIDEO_FORMATS { mov, mp4 }
    private static final int MAX_FILE_SIZE_MB = 15;
    private LocationService locationService = new LocationService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Crear Historia");
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Button createStoryButton = this.findViewById(R.id.createStoryButton);
        createStoryButton.setOnClickListener(new CreateStoryHandler());

        this.locationService.startLocationUpdates(this);
    }

    private boolean isSupportedImage(String fileExtension) {
        for (SUPPORTED_IMAGE_FORMATS imageFormat : SUPPORTED_IMAGE_FORMATS.values()) {
            if (imageFormat.name().equals(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSupportedVideo(String fileExtension) {
        for (SUPPORTED_VIDEO_FORMATS videoFormat : SUPPORTED_VIDEO_FORMATS.values()) {
            if (videoFormat.name().equals(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    private boolean validatesFileSizeRestriction(String filePath){
        File file = new File(filePath);
        double fileBytes = file.length();
        double fileMegabytes = (fileBytes / (1024 * 1024));
        return fileMegabytes <= MAX_FILE_SIZE_MB;
    }

    private boolean isSupportedFormat(String fileExtension){
        return isSupportedImage(fileExtension) || isSupportedVideo(fileExtension);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_FILE_UPLOAD) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                Uri selectedImage = data.getData();
                String filePath = FileUtils.getFilePathFromUri(this, selectedImage);
                String fileExtension = filePath.substring(filePath.lastIndexOf(".") + 1);

                if (isSupportedFormat(fileExtension)){
                    if (validatesFileSizeRestriction(filePath)) {
                        if (isSupportedImage(fileExtension)) {
                            Intent navigationIntent = new Intent(CreateStoryActivity.this, ImageFiltersActivity.class);
                            navigationIntent.putExtra("imageUri", filePath);
                            startActivityForResult(navigationIntent, FINISH_IMAGE_FILTER);
                        } else {
                            uploadStory(filePath);
                        }
                    }
                    else{
                        startFileUpload("File selected is bigger than " + MAX_FILE_SIZE_MB + " MB");
                    }
                }
                else{
                    startFileUpload("File format not supported.");
                }
            }
        }
        else if (requestCode == FINISH_IMAGE_FILTER){
            if (resultCode == AppCompatActivity.RESULT_OK) {
                String filePath = FileUtils.getFilePathFromUri(this, Uri.parse(data.getDataString()));
                uploadStory(filePath);
            }
        }
    }

    private void uploadStory(String filePath) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.createStoryLayout), "Uploading story...", Snackbar.LENGTH_INDEFINITE);
        ViewGroup contentLay = (ViewGroup) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
        ProgressBar item = new ProgressBar(this);
        item.setIndeterminate(true);
        contentLay.addView(item,0);
        snackbar.show();

        EditText createStoryTitle = findViewById(R.id.createStoryTitle);
        EditText createStoryDescription = findViewById(R.id.createStoryDescription);
        CheckBox createStoryIsQuickStory = findViewById(R.id.createStoryIsQuickStory);
        RadioGroup createStoryVisibility = findViewById(R.id.createStoryVisibility);

        Story story = new Story();
        story.userId = LocalStorage.getUser().id;
        story.title = createStoryTitle.getText().toString();
        story.description = createStoryDescription.getText().toString();
        story.isQuickStory = createStoryIsQuickStory.isChecked();
        story.visibility = createStoryVisibility.getCheckedRadioButtonId() == R.id.createStoryVisibilityIsPublic ? "public" : "private";
        story.timestamp = Calendar.getInstance().getTime().toString();
        story.location = this.locationService.getLocation();

        new CreateStoryTask(filePath).execute(story);
    }

    protected class CreateStoryHandler implements View.OnClickListener {

        public void onClick(View v){
            startFileUpload();
        }
    }

    private void startFileUpload(String messageToDisplay){
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.createStoryLayout), messageToDisplay, Snackbar.LENGTH_LONG);
        snackbar.show();
        startFileUpload();
    }

    private void startFileUpload() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*, video/*");
        //photoPickerIntent.setTypeAndNormalize("image/jpeg, image/jpg, image/png, video/mp4");
        startActivityForResult(photoPickerIntent, START_FILE_UPLOAD);
    }

    protected class CreateStoryTask extends AsyncTask<Story, Void, ServiceResponse<Story>> {
        private StoryService storyService = new StoryService();
        private String mFilePath;

        public CreateStoryTask(String filePath) {
            this.mFilePath = filePath;
        }

        protected void onPreExecute() {
            Button createStoryButton = findViewById(R.id.createStoryButton);
            createStoryButton.setEnabled(false);
        }

        protected ServiceResponse<Story> doInBackground(Story... params) {
            Story storyToUpload = params[0];
            return storyService.createStory(storyToUpload);
        }

        protected void onPostExecute(ServiceResponse<Story> response) {
            ServiceResponse.ServiceStatusCode statusCode = response.getStatusCode();
            if (statusCode == ServiceResponse.ServiceStatusCode.SUCCESS){
                Story newStory = response.getServiceResponse();
                new UploadFileTask(this.mFilePath).execute(newStory.id);
            } else {
                String messageToDisplay = "Error al subir la historia. Intente de nuevo.";
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.createStoryLayout), messageToDisplay, Snackbar.LENGTH_LONG);
                snackbar.show();

                Button createStoryButton = findViewById(R.id.createStoryButton);
                createStoryButton.setEnabled(true);
            }
        }
    }

    protected class UploadFileTask extends AsyncTask<String, Void, ServiceResponse<Story>> {
        private File file;
        private FileService fileService = new FileService();
        private String mStoryID;

        public UploadFileTask(String filePath) {
            this.file = new File(filePath);
        }

        protected ServiceResponse<Story> doInBackground(String... storyId) {
            this.mStoryID = storyId[0];
            return fileService.uploadFileToStory(storyId[0], this.file);
        }

        protected void onPostExecute(ServiceResponse<Story> response) {
            ServiceResponse.ServiceStatusCode statusCode = response.getStatusCode();
            if (statusCode == ServiceResponse.ServiceStatusCode.SUCCESS) {
                // Navigate to Home page
                Intent navigationIntent = new Intent(CreateStoryActivity.this, HomeActivity.class);
                startActivity(navigationIntent);
                Button createStoryButton = findViewById(R.id.createStoryButton);
                createStoryButton.setEnabled(true);
            } else {
                new DeleteStoryTask().execute(this.mStoryID);
                if (statusCode == ServiceResponse.ServiceStatusCode.UNAUTHORIZED) {
                    Intent navigationIntent = new Intent(CreateStoryActivity.this, LogInActivity.class);
                    startActivity(navigationIntent);
                    Button createStoryButton = findViewById(R.id.createStoryButton);
                    createStoryButton.setEnabled(true);
                }
                else{
                    String messageToDisplay = "Error al subir la historia. Intente de nuevo.";
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.createStoryLayout), messageToDisplay, Snackbar.LENGTH_LONG);
                    snackbar.show();

                    Button createStoryButton = findViewById(R.id.createStoryButton);
                    createStoryButton.setEnabled(true);
                }
            }
        }
    }

    protected class DeleteStoryTask extends AsyncTask<String, Void, ServiceResponse<String>> {
        private StoryService storyService = new StoryService();

        protected ServiceResponse<String> doInBackground(String... storyId) {
            return storyService.deleteStory(storyId[0]);
        }
    }
}
