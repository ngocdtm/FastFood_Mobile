package com.example.foodnhanh.activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.example.foodnhanh.R;
import com.example.foodnhanh.model.ReadWriteUserDetails;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class ProfileActivity extends AppCompatActivity {
    BottomNavigationView nav;
    //nightmode
    SwitchCompat Switch;
    boolean nightMODE;
    TextView txtName;
    Button btnViewProfile, btnLogout, btnchangePass;
    String fullName;
    Button btnAdd,btnEditAndDelete;

    FirebaseAuth authProfile;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        txtName =findViewById(R.id.setting_name);
        btnViewProfile =findViewById(R.id.viewProfile);
        btnLogout = findViewById(R.id.btnLogout);
        //AddPro
        btnAdd=findViewById(R.id.AddPro);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AddProActivity.class));
            }
        });
        //DeletePro And EditPro
        btnEditAndDelete=findViewById(R.id.DeAndEdPro);
        btnEditAndDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DeleteAndEditActivity.class));
            }
        });
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        showUserProfile(firebaseUser);
        btnViewProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(v.getContext(), UserProfileActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        btnchangePass = findViewById(R.id.btnChangePassword);
        btnchangePass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(v.getContext(), ChangePasswordActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                authProfile.signOut();
                Toast.makeText(ProfileActivity.this, "Đăng xuất tài khoản thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
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
                    txtName.setText("Welcome, " + fullName + " !");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
            }
        });



    Switch=findViewById(R.id.switcher);
        nav = findViewById(R.id.bottomNavigation);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.profile) {
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(intent);

                return true;

            }
            else if (id == R.id.favorite) {
                Intent intent = new Intent(ProfileActivity.this, FavoriteActivity.class);
                startActivity(intent);

                return true;

            }
            else if (id == R.id.cart) {
                Intent intent = new Intent(ProfileActivity.this, SummaryActivity.class);
                startActivity(intent);

                return true;

            }
            else if (id == R.id.home) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);

                return true;

            }
            return true;
        });

        //nightmode
        sharedPreferences=getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMODE=sharedPreferences.getBoolean("night",false);
        if(nightMODE){
            Switch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        Switch.setOnClickListener(v -> {
            if(nightMODE){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor=sharedPreferences.edit();
                editor.putBoolean("night",false);
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor=sharedPreferences.edit();
                editor.putBoolean("night",true);
            }
            editor.apply();
        });
    }
}