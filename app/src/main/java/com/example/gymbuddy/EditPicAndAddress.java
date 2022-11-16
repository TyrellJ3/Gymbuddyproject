package com.example.gymbuddy;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gymbuddy.Matches.MatchesActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditPicAndAddress extends AppCompatActivity {
    public FirebaseAuth mauth;

    private String userID;

    Uri myUri;

    private Uri filePath;



    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_pic_address);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(
                Color.parseColor("#0F9D58"));
        actionBar.setBackgroundDrawable(colorDrawable);
        mauth = FirebaseAuth.getInstance();
        userID = mauth.getCurrentUser().getUid();

        ImageView myImg =(ImageView)findViewById(R.id.myPic);
        Button save =(Button)findViewById(R.id.Save);
        Button matches =(Button)findViewById(R.id.MatchesButton);
        Button logOut =(Button)findViewById(R.id.logOutButton);
        Button messages =(Button)findViewById(R.id.MessagesButton);
        // get the Firebase  storage reference


        /*This Part loads images to image views from DB*/
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        StorageReference ref
                = storageReference.child("Users").child(userID)

                .child(
                        "profileImageUrl"
                );



        StorageReference mImageRef =
                ref;
        final long ONE_MEGABYTE = 1024 * 1024;
        mImageRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);

                        myImg.setMinimumHeight(dm.heightPixels);
                        myImg.setMinimumWidth(dm.widthPixels);
                        myImg.setImageBitmap(bm);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

        // ^^^^^ image loader



        final int PICK_IMAGE = 100;







        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    uploadImage();



            }


        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                mauth.signOut();
                Intent intent = new Intent(EditPicAndAddress.this, loginOrReg.class);
                startActivity(intent);
                finish();
                return;
            }

        });
        matches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                Intent intent = new Intent(EditPicAndAddress.this, MatchesActivity.class);
                startActivity(intent);
            }

        });
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                startActivity(new Intent(EditPicAndAddress.this,MainActivity.class));
            }

        });


            myImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent gallery = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(gallery, PICK_IMAGE);


                }


            });


    }






    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        Uri imageUri;
        ImageView imageView = findViewById(R.id.myPic);

        if (resultCode == RESULT_OK && reqCode == 100){
            imageUri = data.getData();
            myUri = data.getData();
            filePath = data.getData();
            imageView.setImageURI(imageUri);
        }
    }
    private void uploadImage()
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference.child("Users").child(userID)

                    .child(
                            "profileImageUrl"
                                    );

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(EditPicAndAddress.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(EditPicAndAddress.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }

}


