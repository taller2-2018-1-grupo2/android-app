package stories.app.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class LocationService {

    private boolean hasPermissions = false;
    private LocationManager locationManager;
    private LocationListener locationListenerGPS;
    private LocationListener locationListenerNetwork;

    private Location locationGPS;
    private Location locationNetwork;

    @SuppressLint("MissingPermission")
    public void startLocationUpdates(Activity activity) {

        this.checkPermissions(activity);

        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        this.locationListenerGPS = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationGPS = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        this.locationListenerNetwork = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationNetwork = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (this.checkPermissions(activity)) {
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.locationListenerGPS);
            this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this.locationListenerGPS);
        }
    }

    @SuppressLint("MissingPermission")
    public String getLocation() {

        if (this.locationGPS != null) {
            return Location.convert(this.locationGPS.getLatitude(), Location.FORMAT_DEGREES)
                + ","
                + Location.convert(this.locationGPS.getLongitude(), Location.FORMAT_DEGREES
            );
        }

        if (this.locationNetwork != null) {
            return Location.convert(this.locationNetwork.getLatitude(), Location.FORMAT_DEGREES)
                + ","
                + Location.convert(this.locationNetwork.getLongitude(), Location.FORMAT_DEGREES
            );
        }

        // there is no known location, return empty
        return "";
    }

    public boolean checkPermissions(Activity activity) {

        this.hasPermissions =
            ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!this.hasPermissions) {
            // Requests permissions to get the history's location
            ActivityCompat.requestPermissions(
                activity,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                1
            );
        }

        return this.hasPermissions;
    }

    /*public class MobileLocationListener implements LocationListener {

        private Consumer<Location> locationChangedCallback;

        public MobileLocationListener(Consumer<Location> locationChangedCallback) {
            this.locationChangedCallback = locationChangedCallback;
        }
        @Override
        public void onLocationChanged(Location location) {
            this.locationChangedCallback.accept(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }*/
}
