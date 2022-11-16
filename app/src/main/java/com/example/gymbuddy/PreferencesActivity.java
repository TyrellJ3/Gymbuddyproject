package com.example.gymbuddy;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PreferencesActivity extends AppCompatActivity {
    private Button back_button;
    LatLng address;
    private int[] age = new int[2];
    private int distance;
    private String sex;

    private String currentUId;

    private String apiKey;
    AutocompleteSupportFragment autocompleteFragment;

    private DatabaseReference currentUserPrefDb;

    private final HashMap<String, Object> preferenceHolder = new HashMap<>();
    private Slider radiusSlider;
    private RangeSlider ageSlider;
    private ChipGroup chipGroup;
    private TextView address_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        apiKey = getString(R.string.api_key);

        //initializes places and autocomplete fragment
        initPlacesAndFragment();

        back_button = findViewById(R.id.back);
        radiusSlider = findViewById(R.id.radius_slider);
        ageSlider = findViewById(R.id.age_slider);
        chipGroup = findViewById(R.id.chip_group);
        Button submit_button = findViewById(R.id.submit_button);
        address_input = findViewById(R.id.addressInput1);

        //get passed uid
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUId = extras.getString("currentUId");
            address = new LatLng(extras.getDouble("lat"), extras.getDouble("lng"));
            distance = (int)extras.getDouble("distance");
            age[0] = extras.getInt("min_age");
            age[1] = extras.getInt("max_age");
            sex = extras.getString("sexPref");
            Log.i("passed info: ", address.toString());
            Log.i("passed info: ", distance + " ");
            Log.i("passed info: ", age[0] + " ");
            Log.i("passed info: ", age[1] + " ");
            Log.i("passed info: ", sex + " ");
        }

        //set defaults
        switch (sex){
            case "men":
                chipGroup.check(R.id.men);
                break;
            case "women":
                chipGroup.check(R.id.women);
                break;
            case "all":
                chipGroup.check(R.id.all);
                break;
        }
        radiusSlider.setValue(distance);
        ageSlider.setValues((float)age[0], (float)age[1]);
        address_input.setText(address.toString());


        //get reference for preferences
        currentUserPrefDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUId).child("preferences");


        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreferencesActivity.this, MainActivity.class);
                startActivity(intent);
                return;
            }
        });
        //DISTANCE SLIDER SECTION

        radiusSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onStartTrackingTouch(Slider slider) {
                Log.d("***********", "Start: " + slider.getValue());
            }

            @SuppressLint("RestrictedApi")
            public void onStopTrackingTouch(Slider slider) {
                distance = (int)slider.getValue();
                Log.d("***********", "Stop: " + slider.getValue());
            }
        });
// AGE RANGE SLIDER SECTION

        ageSlider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {
                Log.d("***********", "Start: " + slider.getValues());
            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                age[0] = (int)(float)slider.getValues().get(0);
                age[1] = (int)(float)slider.getValues().get(1);
                Log.d("***********", "Stop: " + slider.getValues());
            }
        });
//GET SEX PREFERENCE SECTION

       // chipGroup.check();
        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {

                Chip selected = findViewById(chipGroup.getCheckedChipId());
                if (selected != null){
                    sex = selected.getText().toString();
                }

            }
        });
//SUBMIT BUTTON

        submit_button.setOnClickListener(new View.OnClickListener() {

            //SAVE PREFERENCES TO DB AND TO MAIN ACTIVITY AND RELOAD
            @Override
            public void onClick(View view) {
                Log.d("results", "address: " + address + "\n"
                        + "distance: " + distance + "\n"
                        + "age: " + age[0] + ", " + age[1]+ "\n"
                        + "sex: " + sex );
                saveAddress();
            }
        });
    }
    void initPlacesAndFragment(){
        if (!Places.isInitialized()) {
            Log.i(TAG, "Place " + "not initialized");
            Places.initialize(getApplicationContext(), apiKey);
            Log.i(TAG, "Place " + "initializing done");
        }
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        Log.i(TAG, "Fragment init " + "fragment init done");
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                //TextView textView1 = (TextView) findViewById(R.id.addressInput1);
                address = place.getLatLng();
                address_input.setText(address.toString());
                Log.i(TAG, "Address latlng: " + address.toString());
                //textView1.setText(address1.toString());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }
    public void saveAddress(){
        preferenceHolder.put("lat", address.latitude);
        preferenceHolder.put("lng", address.longitude);
        currentUserPrefDb.child("address").updateChildren(preferenceHolder);
        preferenceHolder.clear();

        //update age
        preferenceHolder.put("min_age", age[0]);
        preferenceHolder.put("max_age", age[1]);
        currentUserPrefDb.child("age").updateChildren(preferenceHolder);
        preferenceHolder.clear();
        preferenceHolder.put("distance", distance);
        preferenceHolder.put("sex", sex);
        currentUserPrefDb.updateChildren(preferenceHolder);
        preferenceHolder.clear();
        return;
    }

}