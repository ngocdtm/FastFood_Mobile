package com.example.foodnhanh.activity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodnhanh.R;
import com.example.foodnhanh.adapter.FavoriteAdapter;
import com.example.foodnhanh.databinding.ActivityFavoriteBinding;
import com.example.foodnhanh.model.MainModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FavoriteActivity extends AppCompatActivity {
    BottomNavigationView nav;

   ActivityFavoriteBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    FavoriteAdapter adapter;
    RecyclerView recyclerView;

    public interface OnDataLoadedListener {
        void onDataLoaded(List<MainModel> foodfastList);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recyclerView=findViewById(R.id.favoFmRcv);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        loadMangas(new OnDataLoadedListener() {
            @Override
            public void onDataLoaded(List<MainModel> foodfastList) {
                setRecyclerView(foodfastList);
            }
        });






        //nav bottom
        nav = findViewById(R.id.bottomNavigation);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.favorite) {
                Intent intent = new Intent(FavoriteActivity.this, FavoriteActivity.class);
                startActivity(intent);

                return true;

            }
            else if (id == R.id.profile) {

                Intent intent = new Intent(FavoriteActivity.this, ProfileActivity.class);
                startActivity(intent);

                return true;

            }
            else  if (id == R.id.cart) {
                Intent intent = new Intent(FavoriteActivity.this, SummaryActivity.class);
                startActivity(intent);

                return true;

            }
            else if (id == R.id.home) {
                Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
                startActivity(intent);

                return true;

            }
            return true;
        });
    }
    private void setRecyclerView(List<MainModel> foodList){
        adapter = new FavoriteAdapter(this);
        adapter.setData(foodList);
        binding.favoFmRcv.setLayoutManager(new GridLayoutManager(this, 1));
        binding.favoFmRcv.setAdapter(adapter);
    }
    private void loadMangas(OnDataLoadedListener listener) {
        String uid = currentUser.getUid();
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(uid).child("Favorites");
        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> foodIds = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String foodId = dataSnapshot.child("id").getValue(String.class);
                    foodIds.add(foodId);
                }

                // Tạo một danh sách tạm thời để lưu trữ dữ liệu manga
                List<MainModel> foodList = new ArrayList<>();
                DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Product");
                for (String foodId : foodIds) {
                    foodRef.orderByChild("id").equalTo(foodId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                                        MainModel food= foodSnapshot.getValue(MainModel.class);
                                        foodList.add(food);
                                    }

                                    // Kiểm tra xem đã tải tất cả dữ liệu chưa
                                    if (foodList.size() == foodIds.size()) {
                                        listener.onDataLoaded(foodList);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(FavoriteActivity.this, R.string.loadingInterupted, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FavoriteActivity.this, R.string.loadingInterupted, Toast.LENGTH_SHORT).show();
            }
        });
    }
}