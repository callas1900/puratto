package net.callas1900.purattone.flickr;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.GeoData;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;
import com.googlecode.flickrjandroid.photos.SearchParameters;
import com.googlecode.flickrjandroid.photos.geo.GeoInterface;

import net.callas1900.purattone.ViewerActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

import java.util.concurrent.ExecutionException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ryo on 12/2/16.
 */
@RunWith(RobolectricTestRunner.class)
public class RetrieveFlickrTaskTest {

    private final static String DUMMY_API_KEY = "aaaa";

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Spy
    RetrieveFlickrTask task = new RetrieveFlickrTask(null, DUMMY_API_KEY);

    @Mock
    Flickr flickr;

    @Mock
    PhotosInterface photosInterface;

    @Mock
    GeoInterface geoInterface;

    @Mock
    GeoData geoData;

    @Mock
    Photo photo;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ShadowLog.stream = System.out;
    }

    @Test
    public void getFlickrPhotos() throws Exception {
        SearchParameters parameters = new SearchParameters();
        String id = "dummy_id";
        PhotoList photos = new PhotoList();
        photos.add(photo);

        // set up mocks
        doReturn(flickr).when(task).getFlickr(DUMMY_API_KEY);
        when(flickr.getPhotosInterface()).thenReturn(photosInterface);
        when(photosInterface.search(parameters, 20, 1)).thenReturn(photos);
        when(photosInterface.getGeoInterface()).thenReturn(geoInterface);
        when(geoInterface.getLocation(id)).thenReturn(geoData);
        when(photo.getId()).thenReturn(id);

        PhotoList result = task.doInBackground(parameters);
        verify(photo).setGeoData(geoData);
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(id));
    }

}