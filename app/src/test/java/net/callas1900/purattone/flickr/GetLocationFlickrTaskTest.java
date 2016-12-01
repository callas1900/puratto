package net.callas1900.purattone.flickr;

import android.location.Location;
import android.location.LocationManager;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.places.Place;
import com.googlecode.flickrjandroid.places.PlacesInterface;
import com.googlecode.flickrjandroid.places.PlacesList;

import net.callas1900.purattone.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by ryo on 12/2/16.
 */
@RunWith(RobolectricTestRunner.class)
public class GetLocationFlickrTaskTest {

    private final static String DUMMY_API_KEY = "aaaa";

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Mock
    MainActivity.Callback callback;

    @Mock
    Flickr flickr;

    @Mock
    PlacesInterface placesInterface;

    @Spy
    GetLocationFlickrTask task = spy(new GetLocationFlickrTask(callback, DUMMY_API_KEY));

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ShadowLog.stream = System.out;
    }

    @Test
    public void getLocation() throws Exception {
        // preparation
        double latitude = 35.6894875;
        double longitude = 139.69170639999993;

        PlacesList placesList = new PlacesList();
        Place p = new Place();
        p.setPlaceId("dummy_id");
        placesList.add(p);

        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        Location[] locations = new Location[]{location};

        // set up mocks
        doReturn(flickr).when(task).getFlickr(DUMMY_API_KEY);
        when(flickr.getPlacesInterface()).thenReturn(placesInterface);
        when(placesInterface.findByLatLon(latitude, longitude, 16)).thenReturn(placesList);

        // test
        PlacesList result = task.doInBackground(locations);
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getPlaceId(), is("dummy_id"));
    }

}