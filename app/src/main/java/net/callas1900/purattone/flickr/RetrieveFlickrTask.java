package net.callas1900.purattone.flickr;

import android.os.AsyncTask;
import android.util.Log;

import net.callas1900.purattone.ViewerActivity.Callback;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.photos.GeoData;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.SearchParameters;

import org.json.JSONException;

import java.io.IOException;
import java.util.Iterator;


/**
 * retrieve photos via Flickr API.
 *
 * @author ryo tanaka
 */
public class RetrieveFlickrTask extends FlickrTask<SearchParameters, Integer, PhotoList> {

    private final static String TAG = RetrieveFlickrTask.class.getSimpleName();
    private final Callback<PhotoList> callback;
    private final String apiKey;
    private final int targetPage;

    public RetrieveFlickrTask(Callback<PhotoList> callback, String apiKey) {
        this.callback = callback;
        this.apiKey = apiKey;
        this.targetPage = 1;
    }

    public RetrieveFlickrTask(Callback<PhotoList> callback, String apiKey, int  targetPage) {
        this.callback = callback;
        this.apiKey = apiKey;
        this.targetPage = targetPage;
    }

    @Override
    protected PhotoList doInBackground(SearchParameters... parameters) {
        if (parameters.length != 1) {
            return null;
        }
        Log.d(TAG, "targetPage => " + targetPage);
        Flickr flickr = getFlickr(apiKey);
        PhotoList photos = new PhotoList();
        try {
            photos = flickr.getPhotosInterface().search(parameters[0], 10, targetPage);
        } catch (IOException e) {
            Log.e(TAG, "", e);
        } catch (FlickrException e) {
            Log.e(TAG, "", e);
        } catch (JSONException e) {
            Log.e(TAG, "", e);
        }

        return photos;
    }

    @Override
    protected void onPostExecute(PhotoList photos) {
        super.onPostExecute(photos);
        if (callback != null) {
            callback.onFinished(photos);
        }
    }
}
