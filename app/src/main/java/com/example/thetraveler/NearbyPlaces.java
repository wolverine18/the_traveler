package com.example.thetraveler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

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

        if (results != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new NearbyPlacesAdapter(this, results);
            recyclerView.setAdapter(adapter);
            setupCardViewClickListeners();
        }
    }

    private void setupCardViewClickListeners() {
        adapter.setOnItemClickListener(new NearbyPlacesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (results != null) {
                    try {
                        JSONObject data = (JSONObject) results.get(position);
                        String placeID = data.getString("place_id");
//                        Intent intent = new Intent(this, );
//                        intent.putExtra("PLACE_ID", placeID);
//                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
