package stories.app.activities;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import stories.app.R;
import stories.app.models.responses.ServiceResponse;
import stories.app.services.ProfileService;
import stories.app.utils.Base64UtilityClass;
import stories.app.utils.LocalStorage;

public class ProfileActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Editar Perfil");
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Button applyChangesButton = this.findViewById(R.id.applyChangesButton);
        ApplyChangesClickHandler clickHandler = new ApplyChangesClickHandler(this);
        applyChangesButton.setOnClickListener(clickHandler);

        ImageView profilePicture = this.findViewById(R.id.profile_pic);
        profilePicture.setOnClickListener(new ProfilePicClickHandler());

        TextView changeProfilePicButton = this.findViewById(R.id.change_pic);
        changeProfilePicButton.setOnClickListener(new ProfilePicClickHandler());

        new GetUserDataTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.friends_list) {
            Intent navigationIntent = new Intent(ProfileActivity.this, FriendsListActivity.class);
            startActivity(navigationIntent);
            return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                CircleImageView imageView = (CircleImageView) findViewById(R.id.profile_pic);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected class ProfilePicClickHandler implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent();
            // Show only images, no videos or anything else
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // Always show the chooser (if there are multiple options available)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
    }

    protected class ApplyChangesClickHandler implements View.OnClickListener {

        private Context context;

        public ApplyChangesClickHandler(Context context) {
            this.context = context;
        }

        public void onClick(View v) {

            EditText name = findViewById(R.id.name);
            EditText email = findViewById(R.id.email);

            UpdateUserDataTask task = new UpdateUserDataTask(this.context);
            task.execute(
                    name.getText().toString(),
                    email.getText().toString()
            );
        }
    }

    protected class GetUserDataTask extends AsyncTask<String, Void, ServiceResponse<String>> {
        private ProfileService profileService = new ProfileService();

        protected void onPreExecute() {
        }

        protected ServiceResponse<String> doInBackground(String... params) {
            return profileService.getUserData(LocalStorage.getUser().id);
        }

        protected void onPostExecute(ServiceResponse<String> response) {
            ServiceResponse.ServiceStatusCode statusCode = response.getStatusCode();
            if (statusCode == ServiceResponse.ServiceStatusCode.SUCCESS){
                String result = response.getServiceResponse();

                try {

                    JSONObject jsonObject = new JSONObject(result);
                    String userJSON = jsonObject.getString("user");

                    jsonObject = new JSONObject(userJSON);

                    EditText name = findViewById(R.id.name);
                    EditText email = findViewById(R.id.email);

                    name.setText(jsonObject.getString("name"));
                    email.setText(jsonObject.getString("email"));

                    String profilePicString = jsonObject.getString("profile_pic");

                    if (!profilePicString.isEmpty()) {
                        CircleImageView profilePic = findViewById(R.id.profile_pic);

                        profilePic.setImageBitmap(Base64UtilityClass.toBitmap(profilePicString));
                    }

                } catch (JSONException error) {
                    //Handle JSON Exceptions
                }
            }
        }
    }

    protected class UpdateUserDataTask extends AsyncTask<String, Void, ServiceResponse<Boolean>> {
        private ProfileService profileService = new ProfileService();
        private String profilePicString;
        private Context context;

        public UpdateUserDataTask (Context context){
            this.context = context;
        }

        protected void onPreExecute() {
            Button applyChangesButton = findViewById(R.id.applyChangesButton);
            applyChangesButton.setEnabled(false);

            CircleImageView profilePic = findViewById(R.id.profile_pic);

            Bitmap bitmap = ((BitmapDrawable) profilePic.getDrawable()).getBitmap();
            this.profilePicString = Base64UtilityClass.toBase64String(bitmap);
        }

        protected ServiceResponse<Boolean> doInBackground(String... params) {
            return profileService.updateUserData(
                    LocalStorage.getUser().id,
                    params[0],
                    params[1],
                    this.profilePicString
            );
        }

        protected void onPostExecute(ServiceResponse<Boolean> response) {
            ServiceResponse.ServiceStatusCode statusCode = response.getStatusCode();
            if (statusCode == ServiceResponse.ServiceStatusCode.SUCCESS) {
                Button applyChangesButton = findViewById(R.id.applyChangesButton);
                applyChangesButton.setEnabled(true);

                Toast.makeText(this.context, "Datos Actualizados.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
