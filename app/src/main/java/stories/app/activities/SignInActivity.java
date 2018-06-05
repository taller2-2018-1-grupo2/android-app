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

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Button signInButton = this.findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new SignInClickHandler());
        signInButton.setEnabled(false);

        EditText username = findViewById(R.id.username);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        EditText confirmPassword = findViewById(R.id.confirmPassword);
        EditText name = findViewById(R.id.name);

        username.setOnFocusChangeListener(new FocusChangeHandler());
        email.setOnFocusChangeListener(new FocusChangeHandler());
        password.setOnFocusChangeListener(new FocusChangeHandler());
        confirmPassword.setOnFocusChangeListener(new FocusChangeHandler());
        name.setOnFocusChangeListener(new FocusChangeHandler());
    }

    protected class FocusChangeHandler implements View.OnFocusChangeListener {
        public void onFocusChange(View v, boolean hasFocus){
            TextView signInResult = findViewById(R.id.signInResult);
            Button signInButton = findViewById(R.id.signInButton);
            EditText username = findViewById(R.id.username);
            EditText email = findViewById(R.id.email);
            EditText password = findViewById(R.id.password);
            EditText confirmPassword = findViewById(R.id.confirmPassword);
            EditText name = findViewById(R.id.name);

            boolean arePasswordEqual = password.getText().toString().equals(confirmPassword.getText().toString());

            if (!arePasswordEqual) {
                signInResult.setText(R.string.error_invalid_passwords);
                return;
            }

            signInResult.setText("");
            signInButton.setEnabled(
                username.getText().toString() != ""
                && email.getText().toString() != ""
                && password.getText().toString() != ""
                && confirmPassword.getText().toString() != ""
                && arePasswordEqual
                && name.getText().toString() != ""
            );
        }
    }

    protected class SignInClickHandler implements View.OnClickListener {
        public void onClick(View v){
            EditText username = findViewById(R.id.username);
            EditText email = findViewById(R.id.email);
            EditText password = findViewById(R.id.password);
            EditText name = findViewById(R.id.name);

            User user = new User();
            user.username = username.getText().toString();
            user.email = email.getText().toString();
            user.name = name.getText().toString();

            new SignInUserTask().execute(user,  password.getText().toString());
        }
    }

    protected class SignInUserTask extends AsyncTask<Object, Void, User> {
        private AuthenticationService authenticationService = new AuthenticationService();

        protected void onPreExecute() {
            Button signInButton = findViewById(R.id.signInButton);
            signInButton.setEnabled(false);
        }

        protected User doInBackground(Object... params) {
            return authenticationService.signinUser((User)params[0], (String)params[1]);
        }

        protected void onPostExecute(User result) {
            Button signInButton = findViewById(R.id.signInButton);
            signInButton.setEnabled(true);

            TextView signInResult = findViewById(R.id.signInResult);

            if (result == null) {
                signInResult.setText(R.string.error_invalid_signin);
            } else {
                // Navigate to Home page
                Intent navigationIntent = new Intent(SignInActivity.this, HomeActivity.class);
                startActivity(navigationIntent);
            }
        }
    }
}
