package com.example.thetraveler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thetraveler.webservice.DetailsService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NearbyPlaces extends AppCompatActivity {

    RecyclerView recyclerView;
    NearbyPlacesAdapter adapter;
    TextView type;
    JSONArray results = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_places);

        recyclerView = findViewById(R.id.recyclerView);
        type = findViewById(R.id.tvType);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String typeString = bundle.getString("TYPE");
        typeString = typeString.replace("_", " ");
        typeString = typeString.substring(0, 1).toUpperCase() + typeString.substring(1) + "s";
        type.setText(typeString);
        String resString = bundle.getString("RESULTS");

        try {
            results = new JSONArray(resString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (results != null && results.length() > 0) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new NearbyPlacesAdapter(this, results);
            recyclerView.setAdapter(adapter);
            setupCardViewClickListeners();
        } else {
            Toast.makeText(this, "No results found for " + typeString + ".", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        IntentFilter placeDetailsFilter = new IntentFilter(DetailsService.BROADCAST_PLACE_DETAILS);
        LocalBroadcastManager.getInstance(this).registerReceiver(placeDetailsReceiver, placeDetailsFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(placeDetailsReceiver);
    }

    private void setupCardViewClickListeners() {
        adapter.setOnItemClickListener(new NearbyPlacesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (results != null) {
                    try {
                        JSONObject data = (JSONObject) results.get(position);
                        String placeID = data.getString("place_id");
                        startGetPlaceDetails(placeID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void startGetPlaceDetails(String placeID) {
        if (placeID != null) {
            DetailsService.startGetPlaceDetails(this, "p2", placeID);
        }
    }

    private BroadcastReceiver placeDetailsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String key = bundle.getString("KEY");
            String placeId = bundle.getString("PLACE_ID");
            String address =  bundle.getString("ADDRESS");
            String name =  bundle.getString("NAME");
            String pNumber =  bundle.getString("PHONE_NUMBER");
            String image = bundle.getString("ICON");
            String website = bundle.getString("WEBSITE");
            String weekdayHours[] = bundle.getStringArray("HOURS");

            if (key.equals("p2")) {
                Intent i = new Intent(context, Details.class);
                i.putExtra("ADDRESS", address);
                i.putExtra("PLACE_ID", placeId);
                i.putExtra("NAME", name);
                i.putExtra("PHONE_NUMBER", pNumber);
                i.putExtra("ICON", image);
                i.putExtra("WEBSITE", website);
                i.putExtra("HOURS", weekdayHours);
                startActivity(i);
            }
        }
    };
}
