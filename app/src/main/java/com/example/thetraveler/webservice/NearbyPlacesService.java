package com.example.thetraveler.webservice;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.example.thetraveler.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NearbyPlacesService extends IntentService {
    private static final String TAG = "GooglePlacesService";
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String ACTION_NEARBY_PLACES = "com.example.thetraveler.webservice.action.NEARBY_PLACES";
    public static final String BROADCAST_NEARBY_PLACES = "com.example.thetraveler.webservice.action.BROADCAST";
    private static final String EXTRA_KEY = "com.example.thetraveler.webservice.extra.KEY";
    private static final String EXTRA_RADIUS = "com.example.thetraveler.webservice.extra.RADIUS";
    private static final String EXTRA_TYPE = "com.example.thetraveler.webservice.extra.TYPE";
    private static final String EXTRA_LAT = "com.example.thetraveler.webservice.extra.LAT";
    private static final String EXTRA_LNG = "com.example.thetraveler.webservice.extra.LNG";

    public NearbyPlacesService() {
        super("GooglePlacesService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startGetNearbyPlaces(Context context, String key, String radius, String type, String lat, String lng) {
        Intent intent = new Intent(context, NearbyPlacesService.class);
        intent.setAction(ACTION_NEARBY_PLACES);
        intent.putExtra(EXTRA_KEY, key);
        intent.putExtra(EXTRA_RADIUS, radius);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_LAT, lat);
        intent.putExtra(EXTRA_LNG, lng);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_NEARBY_PLACES.equals(action)) {
                final String key = intent.getStringExtra(EXTRA_KEY);
                final String radius = intent.getStringExtra(EXTRA_RADIUS);
                final String type = intent.getStringExtra(EXTRA_TYPE);
                final String lat = intent.getStringExtra(EXTRA_LAT);
                String lng = intent.getStringExtra(EXTRA_LNG);
                fetchNearbyPlaces(key, radius, type, lat, lng);
            }
        }
    }

    private void fetchNearbyPlaces(String key, String radius, String type, String lat, String lng) {
        try {
            URL url = new URL(BASE_URL + "location=" + lat + "," + lng + "&radius=" + radius + "&type=" + type + "&&key=" + getString(R.string.google_places_api_key));

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            int response = conn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int len;
                while ((len = bis.read(buffer)) > 0) {
                    baos.write(buffer, 0, len);
                }

                JSONObject data = new JSONObject(new String(baos.toByteArray()));
                JSONArray results = data.getJSONArray("results");

                Intent intent = new Intent(BROADCAST_NEARBY_PLACES);

                intent.putExtra("KEY", key);
                intent.putExtra("RESULTS", results.toString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
