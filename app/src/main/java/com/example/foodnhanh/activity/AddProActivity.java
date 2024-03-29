package com.example.foodnhanh.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foodnhanh.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddProActivity extends AppCompatActivity {
Button btnBack,btnAdd;
EditText img_url,name,price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pro);
        btnBack=findViewById(R.id.btnBackPro);
        name=findViewById(R.id.txtTextPro);
        price=findViewById(R.id.txtPricePro);
        img_url=findViewById(R.id.txtImagePro);
        btnAdd=findViewById(R.id.btnAddProDetail);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            insertData();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddProActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private void insertData(){
        long timestamp = System.currentTimeMillis();
        Map<String,Object> map=new HashMap<>();
       map.put("id", ""+timestamp);
        map.put("name",name.getText().toString());
        Integer price1=Integer.parseInt(price.getText().toString());
        map.put("price",price1);
        map.put("img_url",img_url.getText().toString());
        FirebaseDatabase.getInstance().getReference().child("Product").push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(AddProActivity.this,"Data Inserted Successfully",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddProActivity.this,"Error",Toast.LENGTH_SHORT).show();
            }
        });
    }
}