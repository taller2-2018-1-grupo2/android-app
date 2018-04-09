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
import stories.app.services.LoginService;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = (Button)this.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new LoginClickHandler());
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

    protected class LoginUserTask extends AsyncTask<String, Void, Boolean> {
        private LoginService loginService = new LoginService();

        protected void onPreExecute() {
            Button loginButton = findViewById(R.id.loginButton);
            loginButton.setEnabled(false);
        }

        protected Boolean doInBackground(String... params) {
            return loginService.validateUser(params[0], params[1]);
        }

        protected void onPostExecute(Boolean result) {
            Button loginButton = findViewById(R.id.loginButton);
            loginButton.setEnabled(true);

            TextView loginResult = findViewById(R.id.loginResult);

            if (!result) {
                loginResult.setText(R.string.error_invalid_login);
            } else {
                Intent navigationIntent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(navigationIntent);
            }
        }
    }
}
