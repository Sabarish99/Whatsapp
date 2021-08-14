package com.example.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private Button updateAccountSetting;
    private EditText username, userstatus;
    private CircleImageView userprofileimg;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    public static final int gallerypick =1;
    private StorageReference userProfileImgsRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        userProfileImgsRef= FirebaseStorage.getInstance().getReference().child("Profile Images");

        Initialise();

        username.setVisibility(View.VISIBLE);

        updateAccountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });

        RetrieveUserInfo();

        userprofileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("Images/");
                startActivityForResult(galleryIntent,gallerypick);
            }
        });

    }

    private void RetrieveUserInfo()
    {
        RootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image")))
                   {
                       String retrieve_Username = dataSnapshot.child("name").getValue().toString();
                       String retrieve_UserStatus = dataSnapshot.child("status").getValue().toString();
                       String retrieve_Profileimg= dataSnapshot.child("image").getValue().toString();

                       username.setText(retrieve_Username);
                       userstatus.setText(retrieve_UserStatus);
                       Picasso.get().load(retrieve_Profileimg).into(userprofileimg);

                   }

                   else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                   {
                       String retrieve_Username = dataSnapshot.child("name").getValue().toString();
                       String retrieve_UserStatus = dataSnapshot.child("status").getValue().toString();
            

                       username.setText(retrieve_Username);
                       userstatus.setText(retrieve_UserStatus);
                   }

                   else
                   {
                       username.setVisibility(View.VISIBLE);
                       Toast.makeText(SettingActivity.this, "Please Set and Update your profile info..", Toast.LENGTH_SHORT).show();

                   }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UpdateSettings() {
        String set_user_name = username.getText().toString();
        String set_user_status = userstatus.getText().toString();

        if (TextUtils.isEmpty(set_user_name)) {
            Toast.makeText(this, "Please enter your Username...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(set_user_status)) {
            Toast.makeText(this, "Please enter your Status...", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserId);
            profileMap.put("name", set_user_name);
            profileMap.put("status", set_user_status);

            RootRef.child("Users").child(currentUserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        SendUserToMainActivity();
                        Toast.makeText(SettingActivity.this, "Profile Updated Succesfully...", Toast.LENGTH_SHORT).show();

                    } else {
                        String err = task.getException().toString();
                        Toast.makeText(SettingActivity.this, "Error: " + err, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void Initialise() {
        updateAccountSetting = findViewById(R.id.update_setting_button);
        username = findViewById(R.id.set_user_name);
        userstatus = findViewById(R.id.set_user_status);
        userprofileimg = findViewById(R.id.profile_image);
        loadingBar =  new ProgressDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == gallerypick && resultCode ==RESULT_OK && data != null)
            {
                Uri ImageUri = data.getData();
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);

            }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Please wait while we are loading your profile image");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                Uri resultUri = result.getUri();

                StorageReference filePath = userProfileImgsRef.child(currentUserId +".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                               if(task.isSuccessful())
                               {
                                   Toast.makeText(SettingActivity.this, "Profile Photo uploaded successfully", Toast.LENGTH_SHORT).show();
                                   final String downloadedUri = task.getResult().getMetadata().getReference().getDownloadUrl().toString();

                                   RootRef.child("Users").child(currentUserId).child("image")
                                           .setValue(downloadedUri)
                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                 if(task.isSuccessful()){
                                                     Toast.makeText(SettingActivity.this, "Image saved in DB successfully", Toast.LENGTH_SHORT).show();
                                                     loadingBar.dismiss();
                                                 }
                                                 else
                                                 {
                                                     String err = task.getException().toString();
                                                     Toast.makeText(SettingActivity.this, "Error: "+err, Toast.LENGTH_SHORT).show();
                                                     loadingBar.dismiss();
                                                 }
                                               }
                                           });
                               }

                               else
                               {
                                   String mssg = task.getException().toString();
                                   Toast.makeText(SettingActivity.this, "Error :"+mssg, Toast.LENGTH_SHORT).show();
                                   loadingBar.dismiss();
                               }
                    }
                });
            }


        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
