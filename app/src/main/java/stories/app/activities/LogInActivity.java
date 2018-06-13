package stories.app.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import stories.app.R;
import stories.app.models.User;
import stories.app.services.AuthenticationService;
import stories.app.services.ChatInstanceIDService;
import stories.app.services.LocationService;
import stories.app.utils.Base64UtilityClass;

public class LogInActivity extends AppCompatActivity {

    private static final String EMAIL = "email";

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        mCallbackManager = CallbackManager.Factory.create();

        LoginButton fbLoginButton = findViewById(R.id.fb_login_button);

        // Call location service to request permissions to get the location
        new LocationService().checkPermissions(this);

        // Set the initial permissions to request from the user while logging in
        fbLoginButton.setReadPermissions(Arrays.asList(EMAIL));

        Button loginButton = this.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new LoginClickHandler());

        TextView signInLink = this.findViewById(R.id.signInLink);
        signInLink.setOnClickListener(new SignInLinkClickHandler());

        // Register a callback to respond to the user
        fbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object,GraphResponse response) {
                                try {
                                    String id = object.getString("id");
                                    String name = object.getString("name");
                                    JSONObject picture = object.getJSONObject("picture");
                                    JSONObject pictureData = picture.getJSONObject("data");
                                    String pictureURL = pictureData.getString("url");
                                    String email = object.getString("email");

                                    String firebaseToken = ChatInstanceIDService.FIREBASE_TOKEN;

                                    new FacebookLoginUserTask().execute(id, name, pictureURL, email, firebaseToken);
                                }
                                catch (JSONException error) {

                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,picture.type(large),email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                setResult(RESULT_CANCELED);
                finish();
            }

            @Override
            public void onError(FacebookException e) {
                // Handle exception
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected class SignInLinkClickHandler implements View.OnClickListener {
        public void onClick(View v){
            Intent navigationIntent = new Intent(LogInActivity.this, SignInActivity.class);
            startActivity(navigationIntent);
        }
    }

    protected class LoginClickHandler implements View.OnClickListener {
        public void onClick(View v){
            EditText username = findViewById(R.id.username);
            EditText password = findViewById(R.id.password);

            new LoginUserTask().execute(
                    username.getText().toString(),
                    password.getText().toString()
            );
        }
    }

    protected class LoginUserTask extends AsyncTask<String, Void, User> {
        private AuthenticationService authenticationService = new AuthenticationService();

        protected void onPreExecute() {
            Button loginButton = findViewById(R.id.loginButton);
            loginButton.setEnabled(false);
        }

        protected User doInBackground(String... params) {
            return authenticationService.loginUser(params[0], params[1]);
        }

        protected void onPostExecute(User result) {
            Button loginButton = findViewById(R.id.loginButton);
            loginButton.setEnabled(true);

            TextView loginResult = findViewById(R.id.loginResult);

            if (result == null) {
                loginResult.setText(R.string.error_invalid_login);
            } else {
                Intent navigationIntent = new Intent(LogInActivity.this, HomeActivity.class);
                startActivity(navigationIntent);
            }
        }
    }

    protected class FacebookLoginUserTask extends AsyncTask<String, Void, User> {
        private AuthenticationService authenticationService = new AuthenticationService();

        protected void onPreExecute() {
            Button fbLoginButton = findViewById(R.id.fb_login_button);
            fbLoginButton.setEnabled(false);
        }

        protected User doInBackground(String... params) {
            return authenticationService.fbLoginUser(
                    params[0],
                    params[1],
                    params[2],
                    params[3],
                    params[4]);
        }

        protected void onPostExecute(User result) {
            Button fbLoginButton = findViewById(R.id.fb_login_button);
            fbLoginButton.setEnabled(true);

            TextView loginResult = findViewById(R.id.loginResult);

            if (result == null) {
                loginResult.setText(R.string.error_invalid_login);
            } else {
                Intent navigationIntent = new Intent(LogInActivity.this, HomeActivity.class);
                startActivity(navigationIntent);
                LoginManager.getInstance().logOut();
            }
        }
    }
}
