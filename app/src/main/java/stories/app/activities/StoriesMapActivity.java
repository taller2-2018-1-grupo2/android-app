package stories.app.activities;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Locale;

import stories.app.R;
import stories.app.adapters.StoriesAdapter;
import stories.app.models.Story;
import stories.app.services.StoryService;
import stories.app.utils.LocalStorage;

public class StoriesMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ArrayList<Story> stories = new ArrayList<>();
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories_map);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Mapa de Historias");
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stories_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.stories_list) {
            Intent navigationIntent = new Intent(StoriesMapActivity.this, HomeActivity.class);
            startActivity(navigationIntent);
            return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng center = new LatLng(-34.606336, -58.381572);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 10));

        new GetStoriesVisiblesToUserTask().execute(LocalStorage.getUser().id);
    }

    protected class GetStoriesVisiblesToUserTask extends AsyncTask<String, Void, ArrayList<Story>> {
        private StoryService storyService = new StoryService();

        protected void onPreExecute() {
        }

        protected ArrayList<Story> doInBackground(String... params) {
            return storyService.getStoriesVisiblesToUser(params[0]);
        }

        protected void onPostExecute(ArrayList<Story> result) {
            stories = result;

            for (int i = 0; i < stories.size(); i++) {
                String[] latLngSplit = stories.get(i).location.split(",");
                LatLng storyLatLng = new LatLng(Double.parseDouble(latLngSplit[0]), Double.parseDouble(latLngSplit[1]));

                String snippet = String.format(Locale.getDefault(),
                        "Lat: %1$.5f, Long: %2$.5f",
                        storyLatLng.latitude,
                        storyLatLng.longitude);

                mMap.addMarker(new MarkerOptions()
                        .position(storyLatLng)
                        .title("Ubicacion")
                        .snippet(snippet));
            }
        }
    }
}
