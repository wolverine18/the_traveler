package com.example.thetraveler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

public class NearbyPlaces extends AppCompatActivity {

    RecyclerView recyclerView;
    NearbyPlacesAdapter adapter;
    TextView type;

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
        JSONArray results = null;

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

            }
        });
    }
}
