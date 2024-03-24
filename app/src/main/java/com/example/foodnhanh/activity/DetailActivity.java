package com.example.foodnhanh.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.foodnhanh.R;
import com.example.foodnhanh.databinding.ActivityDetailBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity {
    ImageView detailimg;

    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    ActivityDetailBinding binding;

    TextView price, name, id;
String id1;
    private static SharedPreferences sharedPreferences;

    Button btnBack;

    Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        sharedPreferences = this.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        setContentView(binding.getRoot());
        id = findViewById(R.id.idCT);
        detailimg = findViewById(R.id.ivHinhCT);
        price = findViewById(R.id.Price_detail);
        name = findViewById(R.id.TenspCT);
        intent = getIntent();
        btnBack = findViewById(R.id.btnBack);
        //detail from MainAdapter
        id1 = intent.getStringExtra("id");
      setFavorite();
        onClickEvent();
        Glide.with(detailimg).load(intent.getStringExtra("i")).into(detailimg);
        int price1 = intent.getIntExtra("p", 1);
        price.setText("Price:  " + price1);
        name.setText(intent.getStringExtra("singlename"));
        id.setText(intent.getStringExtra("id"));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void onClickEvent() {
        binding.IconFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               toggleFavorite();
            }
        });
    }
    protected void addToFavorite() {
        if (firebaseAuth.getCurrentUser() == null) {
            showAlertDialog();
        }
        else{
                long timestamp = System.currentTimeMillis();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", id1);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(Objects.requireNonNull(firebaseAuth.getUid())).child("Favorites").child(id1)
                        .setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(DetailActivity.this, R.string.addFavoriteSuccess, Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        }


    private void showAlertDialog()
    {
        //Setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setTitle("Bạn chưa đăng nhập! Không có Product Favorite để hiển thị.");
        builder.setMessage("Vui lòng đăng nhập để xem Product Favorite!");

        // Open Email app if User click Continue Button
        builder.setPositiveButton("Đăng nhập", (dialog, which) -> {
            Intent intent = new Intent(DetailActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Trở về", (dialog, which) -> {
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Create the AlertDialog
        AlertDialog alertDialog = builder.create();

        // Show the AlertDialog
        alertDialog.show();
    }

    protected void removeFromFavorite(String mangaIdToRemove) {
        if (firebaseAuth.getCurrentUser() == null) {
            showAlertDialog();
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(Objects.requireNonNull(firebaseAuth.getUid())).child("Favorites").child(mangaIdToRemove)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(DetailActivity.this, R.string.removeFavoriteSuccess, Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DetailActivity.this, R.string.removeFavoriteFail, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(DetailActivity.this, R.string.dataOccurred, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void toggleFavorite() {
        DatabaseReference userFavoritesRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUser.getUid()).child("Favorites");
        userFavoritesRef.child(id1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().getKey();
                        if (dataSnapshot.exists()) {
                            // Manga is already in favorites, remove it
                            binding.OverlayIconFavorite.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#42000000")));
                            binding.IconFavorite.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                            removeFromFavorite(id1);
                        } else {
                            // Manga is not in favorites, add it
                            binding.OverlayIconFavorite.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#42F44336")));
                            binding.IconFavorite.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
                           addToFavorite();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }
    private void setFavorite() {
        DatabaseReference userFavoritesRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUser.getUid()).child("Favorites");
        userFavoritesRef.child(id1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            binding.OverlayIconFavorite.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#42F44336")));
                            binding.IconFavorite.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }
}
