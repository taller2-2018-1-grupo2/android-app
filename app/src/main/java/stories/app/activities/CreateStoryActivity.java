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
import java.util.TimeZone;

import stories.app.R;
import stories.app.activities.images.ImageFiltersActivity;
import stories.app.models.Story;
import stories.app.services.FileService;
import stories.app.services.LocationService;
import stories.app.models.responses.ServiceResponse;
import stories.app.services.StoryService;
import stories.app.utils.Dates;
import stories.app.utils.FileUtils;
import stories.app.utils.LocalStorage;

public class CreateStoryActivity extends AppCompatActivity {

    private static final int START_FILE_UPLOAD = 5;
    private static final int FINISH_IMAGE_FILTER = 6;
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == AppCompatActivity.RESULT_CANCELED) {
            this.startFileUpload();
            return;
        }

        if (requestCode == FINISH_IMAGE_FILTER){
            String filePath = FileUtils.getFilePathFromUri(this, Uri.parse(data.getDataString()));
            this.uploadStory(filePath);
            return;
        }

        Uri selectedImage = data.getData();
        String filePath = FileUtils.getFilePathFromUri(this, selectedImage);
        String fileExtension = filePath.substring(filePath.lastIndexOf(".") + 1);

        if (!FileUtils.isSupportedFormat(fileExtension)) {
            this.startFileUpload("Formato de archivo no soportado.");
            return;
        }

        if (!FileUtils.isSizeValid(filePath)) {
            startFileUpload("El archivo es mayor a " + FileUtils.MAX_FILE_SIZE_MB + "MB");
            return;
        }

        if (FileUtils.isSupportedImage(fileExtension)) {
            Intent navigationIntent = new Intent(CreateStoryActivity.this, ImageFiltersActivity.class);
            navigationIntent.putExtra("imageUri", filePath);
            startActivityForResult(navigationIntent, FINISH_IMAGE_FILTER);
            return;
        }

        if (FileUtils.isSupportedVideo(fileExtension)) {
            this.uploadStory(filePath);
            return;
        }

        this.startFileUpload("Algo ha ocurrido. Por favor intente nuevamente.");
    }

    private void uploadStory(String filePath) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.createStoryLayout), "Subiendo la historia...", Snackbar.LENGTH_INDEFINITE);
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
        story.timestamp = Dates.getCurrentUtcTime();
        story.location = this.locationService.getLocation();

        new CreateStoryTask(filePath).execute(story);
    }

    protected class CreateStoryHandler implements View.OnClickListener {

        public void onClick(View v){
            startFileUpload();
        }
    }


    public void startFileUpload(String messageToDisplay){
        Snackbar snackbar = Snackbar.make(findViewById(R.id.createStoryLayout), messageToDisplay, Snackbar.LENGTH_LONG);
        snackbar.show();
        startFileUpload();
    }

    private void startFileUpload() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("*/*");
        String[] mimeTypes = {"image/*", "video/*"};
        photoPickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
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
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.createStoryLayout), messageToDisplay, Snackbar.LENGTH_LONG);
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
