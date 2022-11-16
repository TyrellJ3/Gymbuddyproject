package com.example.gymbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gymbuddy.Cards.cards;
import com.example.gymbuddy.Matches.MatchesActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private cards cards_data[];
    private com.example.gymbuddy.arrayAdapter arrayAdapter;
    private int i;
    private Button goToMatches;
    private FirebaseAuth mAuth;

    private String currentUId;

    private DatabaseReference usersDb;

    private String sexPreference;

    private LatLng user2Address;

    private LatLng address;
    private double distance;

    ListView listView;
    List<cards> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();

        getUserPreferences();

        //sexPreference = "female";
        //checkUserSex();
//        switch (sexPreference){
//            case "male":
//                getMaleUsers();
//                break;
//            case "female":
//                getFemaleUsers();
//                break;
//            default:
//                getAllSexUsers();
//                break;
//        }

        rowItems = new ArrayList<cards>();

        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems );

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {

                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("nope").child(currentUId).setValue(true);
                Toast.makeText(MainActivity.this, "Nope", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("yeps").child(currentUId).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(MainActivity.this, "Yes", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                cards swipedUser = (cards) dataObject;
                openBio(swipedUser);
                Toast.makeText(MainActivity.this, "Item Clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void openBio(cards swipedUser) {
        Intent intent = new Intent(this, MatchBioActivity.class);
        intent.putExtra("swipedUserName", swipedUser.getName());
        intent.putExtra("swipedUserId", swipedUser.getUserId());
        startActivity(intent);
    }

    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUId).child("connections").child("yeps").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Toast.makeText(MainActivity.this, "new Connection", Toast.LENGTH_LONG).show();

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    usersDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUId).child("ChatId").setValue(key);
                    usersDb.child(currentUId).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getUserPreferences(){
        DatabaseReference currentUserPrefDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUId).child("preferences");
        currentUserPrefDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("sex").getValue() != null){
                        sexPreference = snapshot.child("sex").getValue(String.class);
                        switch (sexPreference){
                            case "men":
                                getMaleUsers();
                                break;
                            case "women":
                                getFemaleUsers();
                                break;
                            default:
                                getAllSexUsers();
                                break;
                        }
                    }
                    if (snapshot.child("address").getValue() != null) {
                        address = new LatLng(snapshot.child("address").child("lat").getValue(Double.class),
                                snapshot.child("address").child("lng").getValue(Double.class));
                    }
                    if(snapshot.child("distance").getValue() != null){
                        distance = snapshot.child("distance").getValue(Double.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private String userSex;
    private String oppositeUserSex;
    public void checkUserSex(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDb = usersDb.child(user.getUid());
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("sex").getValue() != null){
                        userSex = dataSnapshot.child("sex").getValue().toString();
                        switch (userSex){
                            case "Male":
                                oppositeUserSex = "Female";
                                break;
                            case "Female":
                                oppositeUserSex = "Male";
                                break;
                        }
                        getOppositeSexUsers();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getOppositeSexUsers(){
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("sex").getValue() != null) {
                    if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUId) && !dataSnapshot.child("connections").child("yeps").hasChild(currentUId) && dataSnapshot.child("sex").getValue().toString().equals(oppositeUserSex)) {
                        String profileImageUrl = "default";
                        if (!dataSnapshot.child("profileImageUrl").getValue().equals("default")) {
                            profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                        }
                        cards item = new cards(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profileImageUrl,
                                Integer.parseInt(dataSnapshot.child("age").getValue().toString()));
                        rowItems.add(item);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getMaleUsers(){
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey() != currentUId && dataSnapshot.child("sex").getValue() != null) {
                    if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUId) && !dataSnapshot.child("connections").child("yeps").hasChild(currentUId) && dataSnapshot.child("sex").getValue().toString().equals("Male")) {
                        String profileImageUrl = "default";
//                        if (!dataSnapshot.child("profileImageUrl").getValue().equals("default")) {
//                            profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
//                        }
                        //Log.i("profileImageUrl", ((Boolean)(dataSnapshot.child("profileImageUrl") == null)).toString());
                        if(dataSnapshot.child("preferences").getValue() == null){
                            //add default preferences if they don't exist
                            addDefaultPreferences(dataSnapshot.getKey());
                        }
                        if (dataSnapshot.child("preferences").getValue() != null){
                            user2Address = new LatLng(dataSnapshot.child("preferences").child("address").child("lat").getValue(Double.class),
                                    dataSnapshot.child("preferences").child("address").child("lng").getValue(Double.class));
//                            Log.i("cal distance", ((Double)getDistance(address, user2Address)).toString());
                            if(getDistance(address, user2Address) <= distance){
                                cards item = new cards(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profileImageUrl,
                                        Integer.parseInt(dataSnapshot.child("age").getValue().toString()));
                                rowItems.add(item);
                                Collections.shuffle(rowItems, new Random());
                                arrayAdapter.notifyDataSetChanged();
                            }
                        }


                    }
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void getFemaleUsers(){
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey() != currentUId && dataSnapshot.child("sex").getValue() != null) {
                    if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUId) && !dataSnapshot.child("connections").child("yeps").hasChild(currentUId) && dataSnapshot.child("sex").getValue().toString().equals("Female")) {
                        String profileImageUrl = "default";
//                        if (!dataSnapshot.child("profileImageUrl").getValue().equals("default")) {
//                            profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
//                        }
                        if(dataSnapshot.child("preferences").getValue() == null){
                            //add default preferences if they don't exist
                            addDefaultPreferences(dataSnapshot.getKey());
                        }
                        if(dataSnapshot.child("age").getValue() == null){
                            //add random default age if doesn't have one

                        }
                        if (dataSnapshot.child("preferences").getValue() != null){
                            user2Address = new LatLng(dataSnapshot.child("preferences").child("address").child("lat").getValue(Double.class),
                                    dataSnapshot.child("preferences").child("address").child("lng").getValue(Double.class));
//                            Log.i("cal distance", ((Double)getDistance(address, user2Address)).toString());
                            if(getDistance(address, user2Address) <= distance){
                                cards item = new cards(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profileImageUrl,
                                        Integer.parseInt(dataSnapshot.child("age").getValue().toString()));
                                rowItems.add(item);
                                Collections.shuffle(rowItems, new Random());
                                arrayAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void getAllSexUsers(){
//        getMaleUsers();
//        getFemaleUsers();

        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey() != currentUId && dataSnapshot.child("sex").getValue() != null) {
                    if (dataSnapshot.exists()
                            && !dataSnapshot.child("connections").child("nope").hasChild(currentUId)
                            && !dataSnapshot.child("connections").child("yeps").hasChild(currentUId)) {
                        String profileImageUrl = "default";
//                        if (!dataSnapshot.child("profileImageUrl").getValue().equals("default")) {
//                            profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
//                        }
                        if(dataSnapshot.child("preferences").getValue() == null){
                            //add default preferences if they don't exist
                            addDefaultPreferences(dataSnapshot.getKey());
                        }
                        //addDefaultBio(dataSnapshot.getKey());
                        if (dataSnapshot.child("preferences").getValue() != null){
                            user2Address = new LatLng(dataSnapshot.child("preferences").child("address").child("lat").getValue(Double.class),
                                    dataSnapshot.child("preferences").child("address").child("lng").getValue(Double.class));
//                            Log.i("cal distance", ((Double)getDistance(address, user2Address)).toString());
                            if(getDistance(address, user2Address) <= distance){
                                cards item = new cards(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profileImageUrl,
                                        Integer.parseInt(dataSnapshot.child("age").getValue().toString()));
                                rowItems.add(item);
                                Collections.shuffle(rowItems, new Random());
                                arrayAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void logoutUser(android.view.View view) {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, loginOrReg.class);
        startActivity(intent);
        finish();
        return;
    }

    public void goToSettings(android.view.View view) {
        Intent intent = new Intent(MainActivity.this, EditUserActivity.class);
        intent.putExtra("userSex", userSex);
        startActivity(intent);
        return;
    }

    public void goToMatches(android.view.View view) {
        Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
        startActivity(intent);
        return;
    }
    public void goToPictures(android.view.View view) {
        Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
        intent.putExtra("currentUId", currentUId);
        startActivity(intent);
        return;
    }
    public LatLng randomTown(){
        LatLng [] towns = {new LatLng(36.044659, -79.766235),
        new LatLng(36.112478,-80.015112),
        new LatLng(36.173469, -79.988928),
        new LatLng(35.994303, -79.935314),
        new LatLng(36.208747, -79.904758)};
        //greensboro downtown
        //colfax
        //oak ridge
        //jamestown
        //summerfield
        return towns[(int)(Math.random()*(towns.length-1))];
    }
    public void addDefaultBio(String userId){
        final HashMap<String, Object> userInfo = new HashMap<>();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        Random rand = new Random();
        int age = rand.nextInt((35 - 18) + 1) + 18;
        userInfo.put("age", age);

        userInfo.put("bio", " ");
        userInfo.put("goal", " ");
        userInfo.put("skillLevel", 0);
        userInfo.put("interestA", 0);
        userInfo.put("interestB", 0);
        userInfo.put("interestC", 0);

        userRef.updateChildren(userInfo);
        userInfo.clear();

    }
    public void addDefaultPreferences(String userId){
        Random random = new Random();
        //snapshot.getkey
        DatabaseReference userPref = usersDb.child(userId).child("preferences");
        final HashMap<String, Object> preferenceHolder = new HashMap<>();
        LatLng randomAddress = randomTown();
        preferenceHolder.put("lat", randomAddress.latitude);
        preferenceHolder.put("lng", randomAddress.longitude);
        userPref.child("address").updateChildren(preferenceHolder);
        preferenceHolder.clear();

        //update age
        preferenceHolder.put("min_age", 18);
        preferenceHolder.put("max_age", 100);
        userPref.child("age").updateChildren(preferenceHolder);
        preferenceHolder.clear();
        preferenceHolder.put("distance", 20);
        preferenceHolder.put("sex", "all");
        userPref.updateChildren(preferenceHolder);
        preferenceHolder.clear();
    }
    public double getDistance(LatLng start, LatLng end) {
        double distance;
        distance = SphericalUtil.computeDistanceBetween(start, end);
        Toast.makeText(this, (distance / 1000) * 0.621 + " mi", Toast.LENGTH_SHORT).show();
        // /1000 gives km, *.621 converts to mi
        Log.i("distance", "distance in mi: " + (distance / 1000) * 0.621);
        return (distance / 1000) * 0.621;
    }


}

