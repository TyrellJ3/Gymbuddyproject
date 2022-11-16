package com.example.gymbuddy;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MatchBioActivity extends AppCompatActivity {
    DatabaseReference swipedUser;

    String [] interestList = {"running", "lifting", "yoga", "dancing","P90x","soccer","basketball","hiking","swimming"};
    String [] skillLevelList = {"Newbie","Beginner","Intermediate","Advanced"};

    int c_ia, c_ib, c_ic, ia, ib, ic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_bio);

        String uname, ubio, ulevel;
        int uinterestA, uinterestB, uinterestc;

        String value = "";
        String key;
        String [] interestList = {"running", "lifting", "yoga"};
        Bundle extras = getIntent().getExtras();

        TextView name = (TextView) findViewById(R.id.name);
        TextView intro = (TextView) findViewById(R.id.intro);
        TextView bio = (TextView) findViewById(R.id.bio);
        TextView goal = (TextView) findViewById(R.id.goal);
        TextView skillLevel = (TextView) findViewById(R.id.skillLevel);
//        TextView interest = (TextView) findViewById(R.id.interest);
        //ImageButton interest1b = (ImageButton) findViewById(R.id.interest1_b);
//        ImageButton interest2b = (ImageButton) findViewById(R.id.interest2_b);
//        ImageButton interest3b = (ImageButton) findViewById(R.id.interest3_b);
//
//        //TextView interest1t = (TextView) findViewById(R.id.interest1_t);
//        TextView interest2t = (TextView) findViewById(R.id.interest2_t);
//        TextView interest3t = (TextView) findViewById(R.id.interest3_t);

        Chip interestA_chip = findViewById(R.id.interestA);
        Chip interestB_chip = findViewById(R.id.interestB);
        Chip interestC_chip = findViewById(R.id.interestC);

        int colorInt = getResources().getColor(R.color.pink);
        ColorStateList csl = ColorStateList.valueOf(colorInt);

        if (extras != null) {
            int c = 3;
            c_ia = (int)extras.getLong("c_interestA");
            c_ib = (int)extras.getLong("c_interestB");
            c_ic = (int)extras.getLong("c_interestC");

            uname = extras.getString("swipedUserName");
            key = extras.getString("swipedUserId");
            bio.setText(extras.getString("bio"));
            goal.setText(extras.getString("goal"));
            name.setText(uname);
            skillLevel.setText(skillLevelList[(int)extras.getLong("skillLevel")]);
            ia = (int)extras.getLong("interestA");
            interestA_chip.setText(interestList[ia]);
            if (ia == c_ia || ia == c_ib || ia == c_ic){
                interestA_chip.setChipStrokeColor(csl);
                interestA_chip.setChipStrokeWidth(c);
            }
            ib = (int)extras.getLong("interestB");
            interestB_chip.setText(interestList[ib]);
            if (ib == c_ia || ib == c_ib || ib == c_ic){
                interestB_chip.setChipStrokeColor(csl);
                interestB_chip.setChipStrokeWidth(c);
            }
            ic = (int)extras.getLong("interestC");
            interestC_chip.setText(interestList[ic]);
            if (ic == c_ia || ic == c_ib || ic == c_ic){
                interestC_chip.setChipStrokeColor(csl);
                interestC_chip.setChipStrokeWidth(c);
            }


            Log.i("swiped user interest a", ia + "");
            Log.i("current user interest a", c_ia + "");

//            swipedUser = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
//            swipedUser.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    name.setText(snapshot.child("name").getValue(String.class));
//                    bio.setText(snapshot.child("bio").getValue(String.class));
//                    goal.setText(snapshot.child("goal").getValue(String.class));
//                    skillLevel.setText(Long.toString(snapshot.child("skillLevel").getValue(Long.class)));
//                    interest1t.setText(Long.toString(snapshot.child("interestA").getValue(Long.class)));
//                    interest2t.setText(Long.toString(snapshot.child("interestB").getValue(Long.class)));
//                    interest3t.setText(Long.toString(snapshot.child("interestC").getValue(Long.class)));
//                    Log.i("swidedUser age", snapshot.child("age").getValue().toString());
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });

            //The key argument here must match that used in the other activity
        }



//        String bioDesc = "I do not have a gym membership, I much prefer trails and hiking or playing rounds of disc golf, you know the lighter activities that doesn't always feel like working out, but more of being active. I am not opposed to finding a gym partner if that's your thing, but I have reached a plateau after losing 65 lbs in the last half year and I think having someone to be active with can help me turn the corner. Any ages or fitness levels are welcome as well as any gender. I work in Raleigh area and live in Sanford to give a better idea on a more local area.\n" +
//                "\n";
//
//        bio.setText(bioDesc.length() > 100 ? bioDesc.substring(0, 100) : bioDesc);
//        goal.setText("Develop gym routine");
//        skillLevel.setText("Beginner");
////        interest.setText("Weightlifting" + " " + "Yoga");
//
//        setIcon(interestList[0], interest1b, interest1t);
//        setIcon(interestList[1], interest2b, interest2t);
//        setIcon(interestList[2], interest3b, interest3t);



        CardView card = findViewById(R.id.cardView);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void setIcon(String activity, ImageButton interestb, TextView interestt){
        switch(activity){
            case "sports":
                interestb.setImageResource(R.drawable.sport);
                interestt.setText("sports");
                break;
            case "cycling":
                interestb.setImageResource(R.drawable.bicycle);
                interestt.setText("bicycle");
                break;
            case "walking":
                interestb.setImageResource(R.drawable.footprint);
                interestt.setText("footprint");
                break;
            case "running":
                interestb.setImageResource(R.drawable.running);
                interestt.setText("running");
                break;
            case "dancing":
                interestb.setImageResource(R.drawable.mirror_ball);
                interestt.setText("sports");
                break;
            case "lifting":
                interestb.setImageResource(R.drawable.weight_lifting);
                interestt.setText("dancing");
                break;
            case "boxing":
                interestb.setImageResource(R.drawable.kickboxing);
                interestt.setText("boxing");
                break;
            case "yoga":
                interestb.setImageResource(R.drawable.yoga_mat);
                interestt.setText("yoga");
                break;
        }
    }
}