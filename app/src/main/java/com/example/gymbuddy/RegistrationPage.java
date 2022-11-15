package com.example.gymbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RegistrationPage extends AppCompatActivity {
    private Button mRegister;
    private EditText mEmail, mPassword, mName;

    private RadioGroup mRadioGroup;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user !=null){
                    Intent intent = new Intent(RegistrationPage.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };


        mRegister = (Button) findViewById(R.id.register);

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mName = (EditText) findViewById(R.id.name);
        EditText mage = findViewById(R.id.age);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectId = mRadioGroup.getCheckedRadioButtonId();

                final RadioButton radioButton = (RadioButton) findViewById(selectId);

                if(radioButton.getText() == null){
                    return;
                }

                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();
                final int age = Integer.parseInt(mage.getText().toString());
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationPage.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(RegistrationPage.this, "sign up error", Toast.LENGTH_SHORT).show();
                        }else{
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                            Map userInfo = new HashMap<>();
                            userInfo.put("name", name);
                            userInfo.put("sex", radioButton.getText().toString());
                            userInfo.put("age", age);

                            userInfo.put("bio", " ");
                            userInfo.put("goal", " ");
                            userInfo.put("skillLevel", 0);
                            userInfo.put("interestA", 0);
                            userInfo.put("interestB", 0);
                            userInfo.put("interestC", 0);

                            addDefaultPreferences(userId);

                            currentUserDb.updateChildren(userInfo);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }


    public void addDefaultPreferences(String userId){
        Random random = new Random();
        //snapshot.getkey
        DatabaseReference userPref = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("preferences");
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
}

