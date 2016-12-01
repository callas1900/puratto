package net.callas1900.purattone.flickr;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import net.callas1900.purattone.MainActivity;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.places.Place;
import com.googlecode.flickrjandroid.places.PlacesList;

import org.json.JSONException;

import java.io.IOException;
import java.util.Iterator;

/**
 * Get location via Flickr API.
 *
 * @author ryo tanaka
 */
public class GetLocationFlickrTask extends FlickrTask<Location, Integer, PlacesList> {

    public final static String WOE_ID = "woeId";
    public final static String PLACE_ID = "placeId";

    private final static String TAG = GetLocationFlickrTask.class.getSimpleName();
    private final String apiKey;
    private final MainActivity.Callback<PlacesList> callback;


    public GetLocationFlickrTask(MainActivity.Callback<PlacesList> callback, String apiKey) {
        this.apiKey = apiKey;
        this.callback = callback;
    }

    @Override
    protected PlacesList doInBackground(Location... locations) {
        if (locations.length != 1) {
            return null;
        }
        Flickr flickr = getFlickr(apiKey);
        if (flickr == null) {
            Log.e(TAG, "Flickr api is unavailable.");
            return null;
        }
        int accuracy = 16;
        PlacesList placesList = null;
        try {
            placesList = flickr.getPlacesInterface().findByLatLon(
                    locations[0].getLatitude(),
                    locations[0].getLongitude(),
                    accuracy);
            Iterator<Place> iterator = placesList.iterator();
            while (iterator.hasNext()) {
                Place place = iterator.next();
                Log.d(TAG, place.getName());
            }
        } catch (FlickrException e) {
            Log.e(TAG, "", e);
        } catch (IOException e) {
            Log.e(TAG, "", e);
        } catch (JSONException e) {
            Log.e(TAG, "", e);
        }
        return placesList;
    }

    @Override
    protected void onPostExecute(PlacesList places) {
        super.onPostExecute(places);
        if (callback != null) {
            callback.onFinished(places);
        }
    }
}
