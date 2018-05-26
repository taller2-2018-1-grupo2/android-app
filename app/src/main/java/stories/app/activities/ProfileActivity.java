package stories.app.activities;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import stories.app.R;
import stories.app.services.ProfileService;
import stories.app.utils.LocalStorage;

public class ProfileActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button applyChangesButton = this.findViewById(R.id.applyChangesButton);
        ApplyChangesClickHandler clickHandler = new ApplyChangesClickHandler(this);
        applyChangesButton.setOnClickListener(clickHandler);

        ImageView profilePicture = this.findViewById(R.id.profile_pic);
        profilePicture.setOnClickListener(new ProfilePicClickHandler());

        new GetUserDataTask().execute();
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

            EditText firstName = findViewById(R.id.first_name);
            EditText lastName = findViewById(R.id.last_name);
            EditText email = findViewById(R.id.email);

            UpdateUserDataTask task = new UpdateUserDataTask(this.context);
            task.execute(
                    firstName.getText().toString(),
                    lastName.getText().toString(),
                    email.getText().toString()
            );
        }
    }

    protected class GetUserDataTask extends AsyncTask<String, Void, String> {
        private ProfileService profileService = new ProfileService();

        protected void onPreExecute() {
        }

        protected String doInBackground(String... params) {
            return profileService.getUserData(LocalStorage.getUser().id);
        }

        protected void onPostExecute(String result) {

            try {

                JSONObject jsonObject = new JSONObject(result);
                String userJSON = jsonObject.getString("user");

                jsonObject = new JSONObject(userJSON);

                EditText firstName = findViewById(R.id.first_name);
                EditText lastName = findViewById(R.id.last_name);
                EditText email = findViewById(R.id.email);

                firstName.setText(jsonObject.getString("first_name"));
                lastName.setText(jsonObject.getString("last_name"));
                email.setText(jsonObject.getString("email"));

                String profilePicString = jsonObject.getString("profile_pic");

                if (!profilePicString.isEmpty()) {
                    CircleImageView profilePic = findViewById(R.id.profile_pic);

                    byte[] imageBytes = Base64.decode(profilePicString, Base64.DEFAULT);
                    Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    profilePic.setImageBitmap(decodedImage);
                }

            } catch (JSONException error) {
                //Handle JSON Exceptions
            }
        }
    }

    protected class UpdateUserDataTask extends AsyncTask<String, Void, Boolean> {
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

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = ((BitmapDrawable) profilePic.getDrawable()).getBitmap();;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageBytes = baos.toByteArray();
            this.profilePicString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }

        protected Boolean doInBackground(String... params) {
            return profileService.updateUserData(
                    LocalStorage.getUser().id,
                    params[0],
                    params[1],
                    params[2],
                    this.profilePicString
            );
        }

        protected void onPostExecute(Boolean result) {
            Button applyChangesButton = findViewById(R.id.applyChangesButton);
            applyChangesButton.setEnabled(true);

            Toast.makeText(this.context, "Datos Actualizados.", Toast.LENGTH_SHORT).show();
        }
    }
}
