package stories.app.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import stories.app.R;
import stories.app.models.User;
import stories.app.services.AuthenticationService;
import stories.app.services.ChatInstanceIDService;
import stories.app.utils.TextValidator;

public class SignInActivity extends AppCompatActivity {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_USERNAME =
            Pattern.compile("^[a-zA-Z0-9_.-]*$", Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_NAME =
            Pattern.compile("^[a-zA-Z\\s_.-]*$", Pattern.CASE_INSENSITIVE);

    private static final int MINIMUM_PASSWORD_CHARS = 3;

    private boolean okUsername = false;
    private boolean okEmail = false;
    private boolean okPassword = false;
    private boolean okConfirmPassword = false;
    private boolean okName = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Button signInButton = this.findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new SignInClickHandler());
        signInButton.setEnabled(false);

        TextView signInResult = findViewById(R.id.signInResult);
        signInResult.setText("");

        EditText username = findViewById(R.id.username);
        EditText email = findViewById(R.id.email);
        EditText name = findViewById(R.id.name);
        EditText password = findViewById(R.id.password);
        EditText confirmPassword = findViewById(R.id.confirmPassword);

        username.setOnFocusChangeListener(new FocusChangeHandler());
        email.setOnFocusChangeListener(new FocusChangeHandler());
        password.setOnFocusChangeListener(new FocusChangeHandler());
        confirmPassword.setOnFocusChangeListener(new FocusChangeHandler());
        name.setOnFocusChangeListener(new FocusChangeHandler());

        username.addTextChangedListener(new TextValidator(username) {
            @Override public void validate(TextView textView, String text) {
                /* Validation code here */
                TextView invalidUsername = findViewById(R.id.invalid_username);
                Matcher matcher = VALID_USERNAME .matcher(text);

                if (!matcher.find()) {
                    invalidUsername.setVisibility(View.VISIBLE);
                    okUsername = false;
                } else {
                    invalidUsername.setVisibility(View.INVISIBLE);
                    okUsername = true;
                }

                Button signInButton = findViewById(R.id.signInButton);
                signInButton.setEnabled(okUsername && okEmail && okName && okPassword && okConfirmPassword);
            }
        });

        name.addTextChangedListener(new TextValidator(name) {
            @Override public void validate(TextView textView, String text) {
                /* Validation code here */
                TextView invalidName = findViewById(R.id.invalid_name);
                Matcher matcher = VALID_NAME .matcher(text);

                if (!matcher.find()) {
                    invalidName.setVisibility(View.VISIBLE);
                    okName = false;
                } else {
                    invalidName.setVisibility(View.INVISIBLE);
                    okName = true;
                }

                Button signInButton = findViewById(R.id.signInButton);
                signInButton.setEnabled(okUsername && okEmail && okName && okPassword && okConfirmPassword);
            }
        });

        email.addTextChangedListener(new TextValidator(email) {
            @Override public void validate(TextView textView, String text) {
                /* Validation code here */
                TextView invalidEmail = findViewById(R.id.invalid_email);
                Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(text);

                if (!matcher.find()) {
                    invalidEmail.setVisibility(View.VISIBLE);
                    okEmail = false;
                } else {
                    invalidEmail.setVisibility(View.INVISIBLE);
                    okEmail = true;
                }

                Button signInButton = findViewById(R.id.signInButton);
                signInButton.setEnabled(okUsername && okEmail && okName && okPassword && okConfirmPassword);
            }
        });

        password.addTextChangedListener(new TextValidator(password) {
            @Override public void validate(TextView textView, String text) {
                /* Validation code here */
                TextView invalidPassword = findViewById(R.id.invalid_password);
                TextView invalidPasswordConfirmation = findViewById(R.id.invalid_password_confirmation);
                EditText confirmPassword = findViewById(R.id.confirmPassword);

                if (text.length() < MINIMUM_PASSWORD_CHARS) {
                    invalidPassword.setVisibility(View.VISIBLE);
                    okPassword = false;
                } else {
                    invalidPassword.setVisibility(View.INVISIBLE);
                    okPassword = true;
                }

                if (!text.equals(confirmPassword.getText().toString())) {
                    invalidPasswordConfirmation.setVisibility(View.VISIBLE);
                    okConfirmPassword = false;
                } else {
                    invalidPasswordConfirmation.setVisibility(View.INVISIBLE);
                    okConfirmPassword = true;
                }

                Button signInButton = findViewById(R.id.signInButton);
                signInButton.setEnabled(okUsername && okEmail && okName && okPassword && okConfirmPassword);
            }
        });

        confirmPassword.addTextChangedListener(new TextValidator(confirmPassword) {
            @Override public void validate(TextView textView, String text) {
                /* Validation code here */
                TextView invalidPasswordConfirmation = findViewById(R.id.invalid_password_confirmation);
                EditText password = findViewById(R.id.password);

                if (!text.equals(password.getText().toString())) {
                    invalidPasswordConfirmation.setVisibility(View.VISIBLE);
                    okConfirmPassword = false;
                } else {
                    invalidPasswordConfirmation.setVisibility(View.INVISIBLE);
                    okConfirmPassword = true;
                }

                Button signInButton = findViewById(R.id.signInButton);
                signInButton.setEnabled(okUsername && okEmail && okName && okPassword && okConfirmPassword);
            }
        });
    }

    protected class FocusChangeHandler implements View.OnFocusChangeListener {
        public void onFocusChange(View v, boolean hasFocus) {
            Button signInButton = findViewById(R.id.signInButton);
            EditText username = findViewById(R.id.username);
            EditText email = findViewById(R.id.email);
            EditText password = findViewById(R.id.password);
            EditText confirmPassword = findViewById(R.id.confirmPassword);
            EditText name = findViewById(R.id.name);

            signInButton.setEnabled(okUsername && okEmail && okName && okPassword && okConfirmPassword);
        }
    }

    protected class SignInClickHandler implements View.OnClickListener {
        public void onClick(View v) {
            EditText username = findViewById(R.id.username);
            EditText email = findViewById(R.id.email);
            EditText password = findViewById(R.id.password);
            EditText name = findViewById(R.id.name);

            User user = new User();
            user.username = username.getText().toString();
            user.email = email.getText().toString();
            user.name = name.getText().toString();
            user.firebaseToken = ChatInstanceIDService.FIREBASE_TOKEN;

            new SignInUserTask().execute(user, password.getText().toString());
        }
    }

    protected class SignInUserTask extends AsyncTask<Object, Void, User> {
        private AuthenticationService authenticationService = new AuthenticationService();

        protected void onPreExecute() {
            Button signInButton = findViewById(R.id.signInButton);
            signInButton.setEnabled(false);
        }

        protected User doInBackground(Object... params) {
            return authenticationService.signinUser((User) params[0], (String) params[1]);
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
