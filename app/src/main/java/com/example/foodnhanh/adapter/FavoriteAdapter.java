package com.example.foodnhanh.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodnhanh.R;
import com.example.foodnhanh.databinding.ItemFavoriteBinding;
import com.example.foodnhanh.model.MainModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>{
    List<MainModel> foodList;
    Context context;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    ItemFavoriteBinding binding;


    public FavoriteAdapter(Context context){
        this.context = context;
    }

    public FavoriteAdapter(List<MainModel> foodList, Context context) {
        this.foodList = foodList;
        this.context = context;
    }



    public void setData(List<MainModel> foodList){
        this.foodList = foodList;
    }


    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        binding = ItemFavoriteBinding.inflate(LayoutInflater.from(context), parent, false);
        return new FavoriteViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        MainModel food = foodList.get(position);
        setFavorite(food.getId(), holder);
        holder.favoName.setText(food.getName());
        //fix price
        holder.favoPrice.setText(String.valueOf(food.getPrice()));
        Glide.with(context)
                .load(food.getImg_url())
                .into(holder.favoImg);
        holder.favoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite(food.getId(), holder);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(foodList != null) return foodList.size();
        return 0;
    }



    protected void removeFromFavorite(String foodIdToRemove, FavoriteViewHolder holder){
        if(firebaseAuth.getCurrentUser() == null){
            Toast.makeText(context,R.string.isNotLogin, Toast.LENGTH_SHORT).show();

        }else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Favorites").child(foodIdToRemove)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            holder.itemView.setVisibility(View.GONE);
                            Toast.makeText(context, R.string.removeFavoriteSuccess, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, R.string.removeFavoriteFail, Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void toggleFavorite(String mangaId, FavoriteViewHolder holder) {
        DatabaseReference userFavoritesRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUser.getUid()).child("Favorites");
        userFavoritesRef.child(mangaId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().getKey();
                        if (dataSnapshot.exists()) {
                            // Manga is already in favorites, remove it
                            removeFromFavorite(mangaId, holder);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }

    private void setFavorite(String mangaId, FavoriteViewHolder holder){
        DatabaseReference userFavoritesRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUser.getUid()).child("Favorites");
        userFavoritesRef.child(mangaId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            holder.favoButton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#C3662D")));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView favoImg;
        TextView favoName, favoPrice;
        ImageButton favoButton;
        CardView favoButtonCard;
        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            favoButton = itemView.findViewById(R.id.itemFavoButton);
            favoImg =  itemView.findViewById(R.id.itemFavoImg);
            favoName =  itemView.findViewById(R.id.itemFavoName);
            favoPrice =  itemView.findViewById(R.id.itemFavoPrice);
            favoButtonCard = itemView.findViewById(R.id.itemFavoCardButton);
        }
    }
}