package com.example.gymbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


//import com.example.gymbuddy.editPicAndAddress;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class EditUserActivity extends AppCompatActivity {
    public FirebaseAuth mauth;
    private DatabaseReference myDatabase;
    private String userID,nameS,bioS,goalS,skillLevelS, MF;
    int interestAIndex,interestBIndex,interestCIndex,skillLevelIndex;
    // only add new interests to end of array, or it will mess up saved values for existing users
    String [] interestList = {"running", "lifting", "yoga", "dancing","P90x","soccer","basketball","hiking","swimming"};
    String [] skillLevelList = {"Newbie","Beginner","Intermediate","Advanced"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_bio);







        EditText bio = (EditText) findViewById(R.id.bio);
        EditText goal = (EditText) findViewById(R.id.goal);
        EditText name = (EditText) findViewById(R.id.name);
        com.google.android.material.chip.Chip skillLevel = (com.google.android.material.chip.Chip) findViewById(R.id.skillLevel);
        com.google.android.material.chip.Chip interestChip1 = (com.google.android.material.chip.Chip) findViewById(R.id.chip1);
        com.google.android.material.chip.Chip interestChip2 = (com.google.android.material.chip.Chip) findViewById(R.id.chip2);
        com.google.android.material.chip.Chip interestChip3 = (com.google.android.material.chip.Chip) findViewById(R.id.chip3);

        mauth = FirebaseAuth.getInstance();
        userID = mauth.getCurrentUser().getUid();
        myDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);



        String value = "";
       Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("userMatchName");
            //The key argument here must match that used in the other activity
        }

        Button Back = (Button) findViewById(R.id.back);
        Button Save = (Button) findViewById(R.id.save);
        Button picButton = (Button) findViewById(R.id.PicButton);


        TextView intro = (TextView) findViewById(R.id.intro);


        bio.setFilters(new InputFilter[] {new InputFilter.LengthFilter(120)});
        name.setFilters(new InputFilter[] {new InputFilter.LengthFilter(30)});
        goal.setFilters(new InputFilter[] {new InputFilter.LengthFilter(30)});




        String bioDesc = "I do not have a gym membership, I much prefer trails and hiking or playing rounds of disc golf, you know the lighter activities that doesn't always feel like working out, but more of being active. I am not opposed to finding a gym partner if that's your thing, but I have reached a plateau after losing 65 lbs in the last half year and I think having someone to be active with can help me turn the corner. Any ages or fitness levels are welcome as well as any gender. I work in Raleigh area and live in Sanford to give a better idea on a more local area.\n" +
                "\n";




        bio.setText(bioDesc.length() > 100 ? bioDesc.substring(0, 100) : bioDesc);
        goal.setText("Develop gym routine");
        skillLevel.setText("Beginner");

       getName(name,bio,goal,skillLevel,interestChip1,interestChip2,interestChip3);




        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInfo(bio,name,skillLevel,goal);
            }
        });
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditUserActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        interestChip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(interestAIndex +1 < interestList.length){
                    interestAIndex += 1;
                }
                else{
                    interestAIndex = 0;
                }
                setIcon(interestList[interestAIndex], interestChip1);
            }
        });
        skillLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(skillLevelIndex +1 < skillLevelList.length){
                    skillLevelIndex += 1;
                }
                else{
                    skillLevelIndex = 0;
                }
                setIcon(skillLevelList[skillLevelIndex], skillLevel);
            }
        });
        interestChip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(interestBIndex +1 < interestList.length){
                    interestBIndex += 1;
                }
                else{
                    interestBIndex = 0;
                }
                setIcon(interestList[interestBIndex], interestChip2);
            }
        });
        interestChip3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(interestCIndex +1 < interestList.length){
                    interestCIndex += 1;
                }
                else{
                    interestCIndex = 0;
                }
                setIcon(interestList[interestCIndex], interestChip3);
            }
        });


        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditUserActivity.this, EditPicAndAddress.class);
                startActivity(intent);
            }
        });





        // name.setText("myName");
        CardView card = findViewById(R.id.cardView);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }






    public void setIcon(String activity, com.google.android.material.chip.Chip interestC){
        interestC.setText(activity);



    }




    public void getName(EditText n,EditText b,EditText g, com.google.android.material.chip.Chip sl,com.google.android.material.chip.Chip interestA,com.google.android.material.chip.Chip interestB,com.google.android.material.chip.Chip interestC){


        if (mauth.getCurrentUser() != null) {

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            // String uSex = FirebaseAuth.getInstance().getCurrentUser();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");


            DatabaseReference userReference = databaseReference.child(uid);
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                             @Override
                                                             public void onDataChange(DataSnapshot dataSnapshot) {
                                                                 for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                                                     String N = dataSnapshot.child("name").getValue().toString();
                                                                     String B = dataSnapshot.child("bio").getValue().toString();
                                                                     String G = dataSnapshot.child("goal").getValue().toString();




                                                                     interestAIndex = Integer.parseInt(dataSnapshot.child("interestA").getValue().toString());
                                                                     interestBIndex = Integer.parseInt(dataSnapshot.child("interestB").getValue().toString());
                                                                     interestCIndex = Integer.parseInt(dataSnapshot.child("interestC").getValue().toString());
                                                                     skillLevelIndex = Integer.parseInt(dataSnapshot.child("skillLevel").getValue().toString());

                                                                     setIcon(interestList[interestAIndex], interestA);
                                                                     setIcon(interestList[interestBIndex], interestB);
                                                                     setIcon(interestList[interestCIndex], interestC);

                                                                     setIcon(skillLevelList[skillLevelIndex], sl);
                                                                     n.setText(N);
                                                                     b.setText(B);
                                                                     g.setText(G);

                                                                 }
                                                             }


                                                             @Override
                                                             public void onCancelled(DatabaseError databaseError) {
                                                                 throw databaseError.toException();
                                                             }
                                                         }

            );}


    }
























    // private ArrayList<String> MatchResults = new ArrayList<String>()   ;
    //private List<String> getDataSetMatches() {return MatchResults} ;
    private void saveUserInfo(EditText b, EditText n, com.google.android.material.chip.Chip s, EditText g) {
        bioS = b.getText().toString();
        nameS = n.getText().toString();
       // skillLevelS = s.getText().toString();
        goalS = g.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("bio", bioS);
        userInfo.put("name", nameS);
        userInfo.put("skillLevel", skillLevelIndex);
        userInfo.put("goal", goalS);
        userInfo.put("interestA", interestAIndex);
        userInfo.put("interestB", interestBIndex);
        userInfo.put("interestC", interestCIndex);
        DatabaseReference currentUserDb = FirebaseDatabase.getInstance()
                .getReference().child("Users").child(userID);
        currentUserDb.updateChildren(userInfo);


        AlertDialog alertDialog = new AlertDialog.Builder(EditUserActivity.this).create();
        alertDialog.setTitle("Success!");
        alertDialog.setMessage("Your Profile was successfully updated");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}