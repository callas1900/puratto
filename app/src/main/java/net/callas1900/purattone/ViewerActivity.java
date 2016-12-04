package net.callas1900.purattone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import net.callas1900.purattone.flickr.RetrieveFlickrTask;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.SearchParameters;

import java.util.ArrayList;

import info.androidhive.glide.activity.SlideshowDialogFragment;
import info.androidhive.glide.adapter.GalleryAdapter;
import info.androidhive.glide.model.Image;

import static net.callas1900.purattone.flickr.GetLocationFlickrTask.PLACE_ID;
import static net.callas1900.purattone.flickr.GetLocationFlickrTask.WOE_ID;

public class ViewerActivity extends AppCompatActivity {

    private String TAG = ViewerActivity.class.getSimpleName();
    private ArrayList<Image> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.glide_activity_main);
        activity = this;

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("images", images);
//                bundle.putInt("position", position);
//
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
//                newFragment.setArguments(bundle);
//                newFragment.show(ft, "slideshow");
                Image image = images.get(position);
                if (image.hasGeoData()) {
                    Log.d(TAG, image.getGeoData());
                    Uri gmmIntentUri = Uri.parse(image.getGeoData() + "(test)");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(activity.getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        fetchImages(getIntent().getExtras());
    }

    private void fetchImages(Bundle extras) {

        SearchParameters parameters = new SearchParameters();
        if (extras != null && extras.containsKey(WOE_ID) && extras.containsKey(PLACE_ID)) {
            parameters.setWoeId(extras.getString(WOE_ID));
            parameters.setPlaceId(extras.getString(PLACE_ID));
        }
        parameters.setHasGeo(true);
        parameters.setSort(SearchParameters.INTERESTINGNESS_DESC);

        pDialog.setMessage("Connecting Flickr...");
        pDialog.show();
        new RetrieveFlickrTask(new Callback<PhotoList>() {
            @Override
            public void onFinished(PhotoList result) {
                images.clear();
                Photo p[] = new Photo[result.getPerPage()];
                for (Photo photo : result.toArray(p)) {
                    Image image = new Image();
                    image.setName(photo.getTitle());
                    image.setSmall(photo.getSmallSquareUrl());
                    image.setMedium(photo.getMediumUrl());
                    image.setLarge(photo.getLarge1600Url());
                    if (photo.hasGeoData()) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("geo:")
                                .append(photo.getGeoData().getLatitude())
                                .append(",")
                                .append(photo.getGeoData().getLongitude())
                                .append("?q=")
                                .append(photo.getGeoData().getLatitude())
                                .append(",")
                                .append(photo.getGeoData().getLongitude());
                        image.setGeoData(builder.toString());
                    } else {
                        Log.d(TAG, "geo is null");
                    }
                    images.add(image);
                }
                pDialog.hide();
                mAdapter.notifyDataSetChanged();
            }
        }, getString(R.string.flickr_api_key)).execute(parameters);
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
