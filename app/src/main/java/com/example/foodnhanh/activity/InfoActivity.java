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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodnhanh.R;
import com.example.foodnhanh.database.OrderContract;


public class InfoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // first of all we will get the views that are  present in the layout of info

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
        setContentView(R.layout.activity_info);

        imageView = findViewById(R.id.imageViewInfo);
        plusquantity = findViewById(R.id.addquantity);
        minusquantity  = findViewById(R.id.subquantity);
        quantitynumber = findViewById(R.id.quantity);
        foodName = findViewById(R.id.drinkNameinInfo);
        foodPrice = findViewById(R.id.coffeePrice);
        addSalad = findViewById(R.id.addToppings);
        addtoCart = findViewById(R.id.addtocart);
        addFries = findViewById(R.id.addCream);

        // setting the name of drink

        foodName.setText("Burger");
        addtoCart.setOnClickListener(v -> {
            Intent intent = new Intent(InfoActivity.this, SummaryActivity.class);
            startActivity(intent);
            // once this button is clicked we want to save our values in the database and send those values
            // right away to summary activity where we display the order info

            SaveCart();
        });

        plusquantity.setOnClickListener(v -> {
            // food price
            int basePrice = 50000;
            quantity++;
            displayQuantity();
            int Pricebur = basePrice * quantity;
            String setnewPrice = String.valueOf(Pricebur);
            foodPrice.setText(setnewPrice);


            // checkBoxes functionality

            int ifCheckBox = CalculatePrice(addFries, addSalad);
            foodPrice.setText(ifCheckBox+" ₫");

        });

        minusquantity.setOnClickListener(v -> {

            int basePrice = 50000;
            // because we dont want the quantity go less than 0
            if (quantity == 0) {
                Toast.makeText(InfoActivity.this, "Không thể giảm nữa!", Toast.LENGTH_SHORT).show();
            } else {
                quantity--;
                displayQuantity();
                int coffePrice = basePrice * quantity;
                String setnewPrice = String.valueOf(coffePrice);
                foodPrice.setText(setnewPrice);


                // checkBoxes functionality

                int ifCheckBox = CalculatePrice(addFries, addSalad);
                foodPrice.setText(ifCheckBox+" ₫");
            }
        });



    }

    private void SaveCart() {

        // getting the values from our views
        String name = foodName.getText().toString();
        String price = foodPrice.getText().toString();
        String quantity = quantitynumber.getText().toString();

        ContentValues values = new ContentValues();
        values.put(OrderContract.OrderEntry.COLUMN_NAME, name);
        values.put(OrderContract.OrderEntry.COLUMN_PRICE, price);
        values.put(OrderContract.OrderEntry.COLUMN_QUANTITY, quantity);

        if (addFries.isChecked()) {
            values.put(OrderContract.OrderEntry.COLUMN_CREAM, "Has Cream: YES");
        } else {
            values.put(OrderContract.OrderEntry.COLUMN_CREAM, "Has Cream: NO");

        }

        if (addSalad.isChecked()) {
            values.put(OrderContract.OrderEntry.COLUMN_HASTOPPING, "Has Toppings: YES");
        } else {
            values.put(OrderContract.OrderEntry.COLUMN_HASTOPPING, "Has Toppings: NO");

        }

        if (mCurrentCartUri == null) {
            Uri newUri = getContentResolver().insert(OrderContract.OrderEntry.CONTENT_URI, values);
            if (newUri==null) {
                Toast.makeText(this, "Failed to add to Cart", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Success  adding to Cart", Toast.LENGTH_SHORT).show();

            }
        }

        hasAllRequiredValues = true;

    }

    private int CalculatePrice(CheckBox addExtraCream, CheckBox addToppings) {

        int basePrice = 50000;

        if (addExtraCream.isChecked()) {
            // add the cream cost 20000
            basePrice = basePrice + 20000;
        }

        if (addToppings.isChecked()) {
            // topping cost is 3
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
            int hasCream = cursor.getColumnIndex(OrderContract.OrderEntry.COLUMN_CREAM);
            int hasTopping = cursor.getColumnIndex(OrderContract.OrderEntry.COLUMN_HASTOPPING);


            String nameofdrink = cursor.getString(name);
            String priceofdrink = cursor.getString(price);
            String quantityofdrink = cursor.getString(quantity);
            String yeshasCream = cursor.getString(hasCream);
            String yeshastopping = cursor.getString(hasTopping);

            foodName.setText(nameofdrink);
            foodPrice.setText(priceofdrink);
            quantitynumber.setText(quantityofdrink);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


        foodName.setText("");
        foodPrice.setText("");
        quantitynumber.setText("");

    }
}