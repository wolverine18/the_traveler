package com.example.thetraveler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thetraveler.webservice.DetailsService;
import com.example.thetraveler.webservice.NearbyPlacesService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

public class Details extends AppCompatActivity {

    TextView title, address, pNumber, website;
    TextView monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    ImageView image;
    String placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        title = findViewById(R.id.tvTitle);
        address = findViewById(R.id.tvAddress);
        website = findViewById(R.id.tvWebsite);
        monday = findViewById(R.id.tvMonday);
        tuesday = findViewById(R.id.tvTuesday);
        wednesday = findViewById(R.id.tvWednesday);
        thursday = findViewById(R.id.tvThursday);
        friday = findViewById(R.id.tvFriday);
        saturday = findViewById(R.id.tvSaturday);
        sunday = findViewById(R.id.tvSunday);
        pNumber = findViewById(R.id.tvPNumber);
        image = findViewById(R.id.ivImage);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        placeId = bundle.getString("PLACE_ID");
        String placeAddress = bundle.getString("ADDRESS");
        String name = bundle.getString("NAME");
        String phoneNumber = bundle.getString("PHONE_NUMBER");
        String icon = bundle.getString("ICON");
        String websiteURL = bundle.getString("WEBSITE");
        String weekdayHours[] = bundle.getStringArray("HOURS");

        title.setText(name);
        address.setText(placeAddress);
        Linkify.addLinks(address, Linkify.ALL);
        pNumber.setText(phoneNumber);
        Linkify.addLinks(pNumber, Linkify.ALL);
        website.setText(websiteURL);
        Linkify.addLinks(website, Linkify.ALL);
        monday.setText(weekdayHours[0]);
        tuesday.setText(weekdayHours[1]);
        wednesday.setText(weekdayHours[2]);
        thursday.setText(weekdayHours[3]);
        friday.setText(weekdayHours[4]);
        saturday.setText(weekdayHours[5]);
        sunday.setText(weekdayHours[6]);
        Picasso.get().load(icon).into(image);
    }
}
