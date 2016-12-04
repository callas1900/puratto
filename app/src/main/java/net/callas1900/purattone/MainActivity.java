package net.callas1900.purattone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.flickrjandroid.places.Place;
import com.googlecode.flickrjandroid.places.PlacesList;

import net.callas1900.purattone.flickr.GetLocationFlickrTask;

import java.util.Iterator;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static net.callas1900.purattone.flickr.GetLocationFlickrTask.PLACE_ID;
import static net.callas1900.purattone.flickr.GetLocationFlickrTask.WOE_ID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int PERMISSION_LOCATION_REQUEST_CODE = 7;
    private Activity activity = null;
    private ArrayAdapter<Place> adapter = null;
    private ProgressBar progressBar;
    private TextView progressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressText = (TextView) findViewById(R.id.progressBar_text);
        setVisibility4Progress(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupListView();
        progressText.setText("");
        startSearchLocation();
    }

    private void setupListView() {
        adapter = new PlaceAdapter(this, R.layout.activity_listview);
        adapter.clear();
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Place place = adapter.getItem(i);
                Intent intent = new Intent(activity, ViewerActivity.class);
                intent.putExtra(WOE_ID, place.getWoeId());
                intent.putExtra(PLACE_ID, place.getPlaceId());
                startActivity(intent);
            }
        });
    }

    private void startSearchLocation() {
        // check permission
        if (ActivityCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity
                    , new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}
                    , PERMISSION_LOCATION_REQUEST_CODE);
            return;
        }

        setVisibility4Progress(View.VISIBLE);
        // set up locationListener
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location loc) {
                Log.d(TAG, "lat + " + loc.getLatitude());
                Log.d(TAG, "lon + " + loc.getLongitude());
                new GetLocationFlickrTask(new Callback<PlacesList>() {
                    @Override
                    public void onFinished(PlacesList result) {
                        Iterator<Place> iterator = result.iterator();
                        adapter.clear();
                        while (iterator.hasNext()) {
                            Place place = iterator.next();
                            adapter.add(place);
                        }
                        setVisibility4Progress(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                }, getString(R.string.flickr_api_key)).execute(loc);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d(TAG, "onStatusChanged + " + s);
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d(TAG, "onProviderEnabled + " + s);
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d(TAG, "onProviderDisabled + " + s);
            }
        };
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        Log.d(TAG, provider);
        if (provider == null || "".equals(provider)) {
            Toast.makeText(activity, "location provider not found", Toast.LENGTH_LONG).show();
            return;
        }
        progressText.setText("get location data via " + provider + " provider ....");
        locationManager.requestLocationUpdates(provider, 5000, 10, locationListener);
    }

    private void setVisibility4Progress(int visibility) {
        if (progressBar != null && progressText != null
                && (View.GONE == visibility || View.VISIBLE == visibility)) {
            progressBar.setVisibility(visibility);
            progressText.setVisibility(visibility);
        }
    }

    /**
     * Callback for AsyncTask.
     *
     * @param <T>
     */
    public interface Callback<T> {
        void onFinished(T result);
    }

    /**
     * ListView adapter.
     */
    public static class PlaceAdapter extends ArrayAdapter<Place> {
        public PlaceAdapter(Context context, int resource) {
            super(context, resource);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Place place = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_listview, parent, false);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.list_item);
            textView.setText(place.getName());
            return convertView;
        }
    }
}
