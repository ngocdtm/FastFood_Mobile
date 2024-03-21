package com.example.foodnhanh.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.foodnhanh.R;

public class DetailActivity extends AppCompatActivity {
    ImageView detailimg;
    TextView quantity;
    int totalquantity=1;
    TextView price,name;

Button btncart,btnBack;
ImageView add,remove;
    Intent intent=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        detailimg =findViewById(R.id.ivHinhCT);
              price=findViewById(R.id.Price_detail);
        name =findViewById(R.id.TenspCT);
        btncart=findViewById(R.id.btnCart);
        add=findViewById(R.id.addpro);
        remove=findViewById(R.id.removepro);
        quantity=findViewById(R.id.quantity);
        intent=getIntent();
        btnBack=findViewById(R.id.btnBack);


        //detail from MainAdapter
       Glide.with(detailimg).load(intent.getStringExtra("i")).into(detailimg);
       int price1 = intent.getIntExtra("p", 1);
       price.setText("Price:  " + price1);
        name.setText(intent.getStringExtra("singlename"));



btnBack.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(DetailActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
});
    add.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(totalquantity < 20){
                totalquantity++;
                quantity.setText(String.valueOf(totalquantity));
            }
        }
    });
    remove.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(totalquantity >1){
                totalquantity--;
                quantity.setText(String.valueOf(totalquantity));
            }
        }
    });
    }
}
