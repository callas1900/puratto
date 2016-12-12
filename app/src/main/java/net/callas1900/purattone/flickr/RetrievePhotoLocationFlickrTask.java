package net.callas1900.purattone.flickr;

import android.util.Log;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.photos.GeoData;

import org.json.JSONException;

import java.io.IOException;


/**
 * retrieve photos via Flickr API.
 *
 * @author ryo tanaka
 */
public class RetrievePhotoLocationFlickrTask extends FlickrTask<String, Integer, GeoData> {

    private final static String TAG = RetrievePhotoLocationFlickrTask.class.getSimpleName();
    private final String apiKey;
    private final Callback<GeoData> callback;

    public RetrievePhotoLocationFlickrTask(Callback<GeoData> callback, String apiKey) {
        this.apiKey = apiKey;
        this.callback = callback;
    }

    @Override
    protected GeoData doInBackground(String... photoIds) {
        if (photoIds.length != 1) {
            return null;
        }
        Flickr flickr = getFlickr(apiKey);
        GeoData geoData = null;
        try {
            geoData = flickr.getPhotosInterface().getGeoInterface().getLocation(photoIds[0].toString());
            if (geoData == null) {
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "", e);
        } catch (FlickrException e) {
            Log.e(TAG, "", e);
        } catch (JSONException e) {
            Log.e(TAG, "", e);
        }

        return geoData;
    }

    @Override
    protected void onPostExecute(GeoData geoData) {
        super.onPostExecute(geoData);
        if (callback != null) {
            callback.onFinished(geoData);
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
}
