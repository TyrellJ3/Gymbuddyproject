package com.example.gymbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gymbuddy.Matches.MatchesActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;



public class ProfilePic extends AppCompatActivity {
private ImageView profilePic;
private Button close,save,logOut,messages,matches;
private TextView profileChangeBtn;
public Uri imageUri;
private String myUri ="";
private FirebaseStorage storage;
private StorageReference storageReference;
//private StroageTask uploadTask;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proflie_pic);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        //storage = FirebaseStorage.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference().child("profileImageUrl");
        profilePic = findViewById(R.id.profilePic);
        logOut = (Button) findViewById(R.id.logOutButton);
        matches = (Button) findViewById(R.id.MatchesButton);
        messages = (Button) findViewById(R.id.MessagesButton);
        close = findViewById(R.id.close);
        save = findViewById(R.id.save);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                startActivity(new Intent(ProfilePic.this,MainActivity.class));
            }

        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                mAuth.signOut();
                Intent intent = new Intent(ProfilePic.this, loginOrReg.class);
                startActivity(intent);
                finish();
                return;
            }

        });
        matches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                Intent intent = new Intent(ProfilePic.this, MatchesActivity.class);
                startActivity(intent);
            }

        });
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                startActivity(new Intent(ProfilePic.this,MainActivity.class));
            }

        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
               uploadPic();
            }

        });
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            choosePic();
            }
        });
    }






    private void choosePic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode ==RESULT_OK && data!=null && data.getData()!=null){

            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
            uploadPic();
        }else
            Toast.makeText(getApplicationContext(),"Failed", Toast.LENGTH_LONG).show();
    }

    private void uploadPic() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading...");
        pd.show();
        //final String randomKey = UUID.randomUUID().toString();
        //if (imageUri != null) {


       final StorageReference Ref = storageReference.child(mAuth.getCurrentUser().getUid()+ "profileImageUrl");

        Ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content),"Image Uploaded" , Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Failed", Toast.LENGTH_LONG).show();
                    }
                });



//                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                        double progressPercent = (100.00 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
//                        pd.setMessage("Percentage: " + (int) progressPercent + "");
//                    }
//                });


    }

}
