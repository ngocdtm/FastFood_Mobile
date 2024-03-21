package com.example.foodnhanh.activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.foodnhanh.R;

import com.example.foodnhanh.model.ReadWriteUserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity
{
    TextView txtName, txtEmail, txtGender, txtBirthday, txtPhone;
    String fullName, email, birthday, gender, mobile;
    Button btnUpdateProfile, btnReturntoInfoFrag;
    ImageView imageView, imgRefresh;
    FirebaseAuth authProfile;
    ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //getSupportActionBar().setTitle("Profile");
        //constraintLayout = findViewById(R.id.User_layout);
        txtName = findViewById(R.id.profile_name);
        txtEmail = findViewById(R.id.profile_email);
        txtGender = findViewById(R.id.profile_gender);
        txtBirthday = findViewById(R.id.profile_birthday);
        txtPhone = findViewById(R.id.profile_phone);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser==null)
        {
            Toast.makeText(UserProfileActivity.this, "Something went wrong! User's detail are not available at the moment.", Toast.LENGTH_LONG).show();
        }
        else
        {
            //checkEmailVerified(firebaseUser);
            showUserProfile(firebaseUser);
        }

        imageView = findViewById(R.id.profile_image);
        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(UserProfileActivity.this, UploadProfilePictureActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnReturntoInfoFrag = findViewById(R.id.ReturnButton);
        btnReturntoInfoFrag.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//
                Intent intent = new Intent(UserProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnUpdateProfile = findViewById(R.id.btn_UpdateProfile);
        btnUpdateProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(UserProfileActivity.this, UpdateProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imgRefresh = findViewById(R.id.click_refresh);
        imgRefresh.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Refresh
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);
            }
        });

    }

    //private void checkEmailVerified(FirebaseUser firebaseUser)
    // {
    //     if (!firebaseUser.isEmailVerified())
    //     {
    //    }
    // }

    private void showUserProfile(FirebaseUser firebaseUser)
    {
        String userID = firebaseUser.getUid();

        // Extracting User Reference from Database for "Signup Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readWriteUserDetails != null)
                {
                    fullName = readWriteUserDetails.fullName;
                    email = firebaseUser.getEmail();
                    birthday = readWriteUserDetails.birthday;
                    gender = readWriteUserDetails.gender;
                    mobile = readWriteUserDetails.mobile;

                    txtName.setText(fullName);
                    txtEmail.setText(email);
                    txtGender.setText(gender);
                    txtBirthday.setText(birthday);
                    txtPhone.setText(mobile);

                    //Set User (After user has uploaded)
                    Uri uri = firebaseUser.getPhotoUrl();

                    //ImageView setImageURI() should not be used with regular URIs. So we are using Picasso
                  //  Glide.with(imageView).load(UserProfileActivity.this).into(imageView);
                  // Picasso.with(UserProfileActivity.this).load(uri).into(imageView);
                  //  Glide.with(imageView).load(UserProfileActivity.this).load(uri).load(imageView);
                    //Intent intent=new Intent(v.getContext(), DetailActivity.class);
                //    intent.putExtra("singlename",model.getName());
                    Glide.with(imageView).load(UserProfileActivity.this).load(uri).load(imageView);
                }
                else
                {
                    Toast.makeText(UserProfileActivity.this, "Some thing went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(UserProfileActivity.this, "Some thing went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }
}