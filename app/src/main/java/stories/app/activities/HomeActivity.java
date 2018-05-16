package stories.app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import stories.app.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent navigationIntent;
        switch(item.getItemId()) {
            case R.id.profile:
                navigationIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(navigationIntent);
                return(true);
            case R.id.friendship_requests:
                navigationIntent = new Intent(HomeActivity.this, FriendshipRequestsActivity.class);
                startActivity(navigationIntent);
                return(true);
            case R.id.direct_messages:
                navigationIntent = new Intent(HomeActivity.this, DirectMessagesActivity.class);
                startActivity(navigationIntent);
                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }
}
