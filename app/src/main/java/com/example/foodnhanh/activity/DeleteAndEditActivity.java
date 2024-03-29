package com.example.foodnhanh.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.foodnhanh.R;
import com.example.foodnhanh.adapter.DeEditProAdapter;
import com.example.foodnhanh.adapter.MainAdapter;
import com.example.foodnhanh.model.MainModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteAndEditActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DeEditProAdapter mainAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_and_edit);
        recyclerView = findViewById(R.id.rvDEPro);

        //List food FIREBASE
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        FirebaseRecyclerOptions<MainModel> options=new FirebaseRecyclerOptions.Builder<MainModel>().setQuery(FirebaseDatabase.getInstance().getReference().
                child("Product"),MainModel.class).build();
        mainAdapter=new DeEditProAdapter(options);
        recyclerView.setAdapter(mainAdapter);

    }
    @Override
    public void onStart() {
        super.onStart();
        mainAdapter.startListening();

    }
    @Override
    public void onStop() {
        super.onStop();
        mainAdapter.stopListening();
    }
}