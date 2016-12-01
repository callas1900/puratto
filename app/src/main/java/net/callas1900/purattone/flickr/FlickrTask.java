package net.callas1900.purattone.flickr;

import android.os.AsyncTask;

import com.googlecode.flickrjandroid.Flickr;

import net.callas1900.purattone.util.StringUtilities;

/**
 * Created by ryo on 12/2/16.
 */

abstract class FlickrTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    protected Flickr getFlickr(String apiKey) {
        if (StringUtilities.isEmpty(apiKey)) {
            return null;
        }
        return new Flickr(apiKey);
    }
}
