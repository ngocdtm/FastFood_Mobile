package com.example.foodnhanh.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodnhanh.R;
import com.example.foodnhanh.database.OrderContract;

public class ChickenActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ImageView imageView;
    ImageButton plusquantity, minusquantity;
    TextView quantitynumber, foodName, foodPrice;
    CheckBox addSalad, addFries;
    Button addtoCart;
    int quantity;
    public Uri mCurrentCartUri;
    boolean hasAllRequiredValues = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chicken);
        imageView = findViewById(R.id.imageViewInfo);
        plusquantity = findViewById(R.id.addquantity);
        minusquantity  = findViewById(R.id.subquantity);
        quantitynumber = findViewById(R.id.quantity);
        foodName = findViewById(R.id.drinkNameinInfo);
        foodPrice = findViewById(R.id.coffeePrice);
        addSalad = findViewById(R.id.addToppings);
        addtoCart = findViewById(R.id.addtocart);
        addFries = findViewById(R.id.addCream);

        // Đặt tên cho món ăn

        foodName.setText("Chicken");

        addtoCart.setOnClickListener(v -> {
            Intent intent = new Intent(ChickenActivity.this, SummaryActivity.class);
            startActivity(intent);
            SaveCart();
        });

        plusquantity.setOnClickListener(v -> {
            // coffee price
            int basePrice = 100000;
            quantity++;
            displayQuantity();
            int Price = basePrice * quantity;
            String setnewPrice = String.valueOf(Price);
            foodPrice.setText(setnewPrice);


            // checkBoxes functionality

            int ifCheckBox = CalculatePrice(addFries, addSalad);
            foodPrice.setText(ifCheckBox+"₫");

        });

        minusquantity.setOnClickListener(v -> {

            int basePrice = 100000;
            // because we dont want the quantity go less than 0
            if (quantity == 0) {
                Toast.makeText(ChickenActivity.this, "Không thể giảm nữa", Toast.LENGTH_SHORT).show();
            } else {
                quantity--;
                displayQuantity();


                int Price = basePrice * quantity;
                String setnewPrice = String.valueOf(Price);
                foodPrice.setText(setnewPrice);


                // checkBoxes functionality

                int ifCheckBox = CalculatePrice(addFries, addSalad);
                foodPrice.setText( ifCheckBox+" ₫");
            }
        });



    }

    private boolean SaveCart() {

        // getting the values from our views
        String name = foodName.getText().toString();
        String price = foodPrice.getText().toString();
        String quantity = quantitynumber.getText().toString();

        ContentValues values = new ContentValues();
        values.put(OrderContract.OrderEntry.COLUMN_NAME, name);
        values.put(OrderContract.OrderEntry.COLUMN_PRICE, price);
        values.put(OrderContract.OrderEntry.COLUMN_QUANTITY, quantity);

        if (addFries.isChecked()) {
            values.put(OrderContract.OrderEntry.COLUMN_CREAM, "Has Fries: YES");
        } else {
            values.put(OrderContract.OrderEntry.COLUMN_CREAM, "Has Fries: NO");

        }

        if (addSalad.isChecked()) {
            values.put(OrderContract.OrderEntry.COLUMN_HASTOPPING, "Has Salad: YES");
        } else {
            values.put(OrderContract.OrderEntry.COLUMN_HASTOPPING, "Has Salad: NO");

        }

        if (mCurrentCartUri == null) {
            Uri newUri = getContentResolver().insert(OrderContract.OrderEntry.CONTENT_URI, values);
            if (newUri==null) {
                Toast.makeText(this, "Thêm vào giỏ thất bại", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Thêm vào giỏ thành công", Toast.LENGTH_SHORT).show();
            }
        }

        hasAllRequiredValues = true;
        return true;
    }

    private int CalculatePrice(CheckBox addSalad, CheckBox addFries) {

        int basePrice = 100000;

        if (addSalad.isChecked()) {
            // add the Salad cost 20000
            basePrice = basePrice + 20000;
        }
        if (addFries.isChecked()) {
            // Fries cost is 30000
            basePrice = basePrice + 30000;
        }
        return basePrice * quantity;
    }

    private void displayQuantity() {
        quantitynumber.setText(String.valueOf(quantity));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {OrderContract.OrderEntry._ID,
                OrderContract.OrderEntry.COLUMN_NAME,
                OrderContract.OrderEntry.COLUMN_PRICE,
                OrderContract.OrderEntry.COLUMN_QUANTITY,
                OrderContract.OrderEntry.COLUMN_CREAM,
                OrderContract.OrderEntry.COLUMN_HASTOPPING
        };

        return new CursorLoader(this, mCurrentCartUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int name = cursor.getColumnIndex(OrderContract.OrderEntry.COLUMN_NAME);
            int price = cursor.getColumnIndex(OrderContract.OrderEntry.COLUMN_PRICE);
            int quantity = cursor.getColumnIndex(OrderContract.OrderEntry.COLUMN_QUANTITY);

            String nameof= cursor.getString(name);
            String priceof = cursor.getString(price);
            String quantityof = cursor.getString(quantity);

            foodName.setText(nameof);
            foodPrice.setText(priceof);
            quantitynumber.setText(quantityof);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        foodName.setText("");
        foodPrice.setText("");
        quantitynumber.setText("");
    }
}