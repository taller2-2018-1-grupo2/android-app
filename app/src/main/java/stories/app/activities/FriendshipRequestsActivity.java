package stories.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import stories.app.R;

public class FriendshipRequestsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friendship_requests);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friendship_requests, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.friendship_requests_sent) {
            Intent navigationIntent = new Intent(FriendshipRequestsActivity.this, FriendshipRequestsSentActivity.class);
            startActivity(navigationIntent);
            return(true);
        } else if (item.getItemId() == R.id.friendship_requests_received) {
            Intent navigationIntent = new Intent(FriendshipRequestsActivity.this, FriendshipRequestsReceivedActivity.class);
            startActivity(navigationIntent);
            return(true);
        }
        return(super.onOptionsItemSelected(item));
    }
}
