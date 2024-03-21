package com.example.foodnhanh.activity;
import androidx.appcompat.app.AppCompatActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import com.example.foodnhanh.R;
import com.example.foodnhanh.adapter.CartAdapter;
import com.example.foodnhanh.database.OrderContract;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SummaryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public CartAdapter mAdapter;
    BottomNavigationView nav;
    public static final int LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        nav = findViewById(R.id.bottomNavigation);
        Button clearthedata = findViewById(R.id.clearthedatabase);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.cart) {
                Intent intent = new Intent(SummaryActivity.this, SummaryActivity.class);
                startActivity(intent);
                return true;
            }
           else if (id == R.id.favorite) {
                Intent intent = new Intent(SummaryActivity.this, FavoriteActivity.class);
                startActivity(intent);
                return true;
            }
            else if (id == R.id.home) {
                Intent intent = new Intent(SummaryActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            else if (id == R.id.profile) {
                Intent intent = new Intent(SummaryActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return true;
        });
        clearthedata.setOnClickListener(v -> {
            int deletethedata = getContentResolver().delete(OrderContract.OrderEntry.CONTENT_URI, null, null);
        });

        getLoaderManager().initLoader(LOADER, null, this);

        ListView listView = findViewById(R.id.list);
        mAdapter = new CartAdapter(this, null);
        listView.setAdapter(mAdapter);



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

        return new CursorLoader(this, OrderContract.OrderEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);
    }
}