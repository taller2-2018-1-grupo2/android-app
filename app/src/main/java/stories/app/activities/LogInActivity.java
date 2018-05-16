package stories.app.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import stories.app.R;
import stories.app.models.User;
import stories.app.services.AuthenticationService;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = this.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new LoginClickHandler());

        TextView signInLink = this.findViewById(R.id.signInLink);
        signInLink.setOnClickListener(new SignInLinkClickHandler());
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
}
