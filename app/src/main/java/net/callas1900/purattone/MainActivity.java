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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.flickrjandroid.places.Place;
import com.googlecode.flickrjandroid.places.PlacesList;

import net.callas1900.purattone.flickr.GetLocationFlickrTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static net.callas1900.purattone.flickr.GetLocationFlickrTask.PLACE_ID;
import static net.callas1900.purattone.flickr.GetLocationFlickrTask.WOE_ID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int PERMISSION_LOCATION_REQUEST_CODE = 7;
    private Activity activity = null;
    private ProgressBar progressBar;
    private TextView progressText;
    private RecyclerView cardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activity = this;
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressText = (TextView) findViewById(R.id.progressBar_text);
        setVisibility4Progress(View.GONE);
        startWork();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                startWork();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void startWork() {
        setupCardView();
        progressText.setText("");
        startSearchLocation();
    }

    private void setupCardView() {
        if (cardList == null) {
            cardList = (RecyclerView) findViewById(R.id.cardList);
        }
        if (cardList.getAdapter() != null) {
            ((CardAdapter) cardList.getAdapter()).clearAll();
        }
        cardList.removeAllViews();
        cardList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cardList.setLayoutManager(llm);
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
                        if (result == null) {
                            setVisibility4Progress(View.GONE);
                            progressText.setText("Error");
                            return;
                        }
                        CardAdapter cardAdapter = new CardAdapter(result);
                        setVisibility4Progress(View.GONE);
                        cardList.setAdapter(cardAdapter);
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

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        protected TextView vTitle;
        protected TextView vLatiLong;
        private String placeId;
        private String woeId;

        public CardViewHolder(final View itemView) {
            super(itemView);
            vTitle = (TextView) itemView.findViewById(R.id.title);
            vLatiLong = (TextView) itemView.findViewById(R.id.txtName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), ViewerActivity.class);
                    intent.putExtra(WOE_ID, woeId);
                    intent.putExtra(PLACE_ID, placeId);
                    itemView.getContext().startActivity(intent);
                }
            });
        }

        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }

        public void setWoeId(String woeId) {
            this.woeId = woeId;
        }

    }

    public static class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {

        private List<Place> placeList;

        public CardAdapter(PlacesList places) {
            placeList = new ArrayList<>();
            Iterator<Place> it = places.iterator();
            while (it.hasNext()) {
                Place place = it.next();
                placeList.add(place);
            }
        }

        @Override
        public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
            return new CardViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(CardViewHolder holder, int position) {
            Place place = placeList.get(position);
            holder.vTitle.setText(place.getName());
            holder.vLatiLong.setText(place.getLatitude() + "," + place.getLongitude());
            holder.setPlaceId(place.getPlaceId());
            holder.setWoeId(place.getWoeId());
        }

        @Override
        public int getItemCount() {
            return placeList.size();
        }

        public void clearAll() {
            int size = placeList.size();
            if (placeList != null && size > 0) {
                for (int i = 0; i < size; i++) {
                    placeList.remove(0);
                }
                this.notifyItemRangeRemoved(0, size);
            }
        }

    }
}
