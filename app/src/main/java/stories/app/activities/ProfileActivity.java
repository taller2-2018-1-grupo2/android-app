package stories.app.activities;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import stories.app.R;
import stories.app.services.ProfileService;
import stories.app.utils.LocalStorage;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button applyChangesButton = this.findViewById(R.id.applyChangesButton);
        applyChangesButton.setOnClickListener(new ApplyChangesClickHandler());

        new GetUserDataTask().execute();
    }

    protected class ApplyChangesClickHandler implements View.OnClickListener {
        public void onClick(View v) {

            EditText firstName = findViewById(R.id.first_name);
            EditText lastName = findViewById(R.id.last_name);
            EditText email = findViewById(R.id.email);

            new UpdateUserDataTask().execute(
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
            return profileService.getUserData(LocalStorage.getUserID());
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

            } catch (JSONException error) {
                //Handle JSON Exceptions
            }
        }
    }

    protected class UpdateUserDataTask extends AsyncTask<String, Void, Boolean> {
        private ProfileService profileService = new ProfileService();

        protected void onPreExecute() {
            Button applyChangesButton = findViewById(R.id.applyChangesButton);
            applyChangesButton.setEnabled(false);
        }

        protected Boolean doInBackground(String... params) {
            return profileService.updateUserData(
                    LocalStorage.getUserID(),
                    params[0],
                    params[1],
                    params[2],
                    ""
            );
        }

        protected void onPostExecute(Boolean result) {
            Button applyChangesButton = findViewById(R.id.applyChangesButton);
            applyChangesButton.setEnabled(true);
        }
    }
}
