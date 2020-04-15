package com.example.thetraveler.webservice;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;

import com.example.thetraveler.Details;
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
public class DetailsService extends IntentService {
    private static final String TAG = "GooglePlacesService";
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    private static final String ACTION_PLACE_DETAILS = "com.example.thetraveler.webservice.action.PLACE_DETAILS";
    public static final String BROADCAST_PLACE_DETAILS = "com.example.thetraveler.webservice.action.BROADCAST_DETAILS";
    private static final String EXTRA_KEY = "com.example.thetraveler.webservice.extra.KEY";
    private static final String EXTRA_PLACE_ID = "com.example.thetraveler.webservice.extra.PLACE_ID";

    public DetailsService() {
        super("GooglePlacesService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startGetPlaceDetails(Context context, String key, String placeID) {
        Intent intent = new Intent(context, DetailsService.class);
        intent.setAction(ACTION_PLACE_DETAILS);
        intent.putExtra(EXTRA_KEY, key);
        intent.putExtra(EXTRA_PLACE_ID, placeID);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PLACE_DETAILS.equals(action)) {
                final String key = intent.getStringExtra(EXTRA_KEY);
                final String placeID = intent.getStringExtra(EXTRA_PLACE_ID);
                fetchPlaceDetails(key, placeID);
            }
        }
    }

    private void fetchPlaceDetails(String key, String placeID) {
        try {
            URL url = new URL(BASE_URL + placeID + "&key=" + getString(R.string.google_places_api_key));

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
                JSONObject results = data.getJSONObject("result");

                String weekdayHours[] = new String[7];
                if(results.has("opening_hours")) {
                    JSONObject accessHours = results.getJSONObject("opening_hours");
                    JSONArray weekHours = accessHours.getJSONArray("weekday_text");
                    for (int i = 0; i < 7; i++) {
                        weekdayHours[i] = weekHours.getString(i);
                    }
                }
                else {
                    for (int i = 0; i < 7; i++) {
                        weekdayHours[i] = "";
                    }
                }

                String address = "";
                if(results.has("formatted_address")){
                    address = results.getString("formatted_address");
                }

                String name = "";
                if(results.has("name")){
                    name = results.getString("name");
                }

                String pNumber = "";
                if(results.has("formatted_phone_number")){
                    pNumber = results.getString("formatted_phone_number");
                }

                String image = "";
                if(results.has("icon")){
                    image = results.getString("icon");
                }

                String website = "";
                if(results.has("website")){
                    website = results.getString("website");
                }


                Intent intent = new Intent(BROADCAST_PLACE_DETAILS);

                intent.putExtra("KEY", key);
                intent.putExtra("PLACE_ID", placeID);
                intent.putExtra("ADDRESS", address);
                intent.putExtra("NAME", name);
                intent.putExtra("PHONE_NUMBER", pNumber);
                intent.putExtra("ICON", image);
                intent.putExtra("WEBSITE", website);
                intent.putExtra("HOURS", weekdayHours);
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
