package com.example.foodnhanh.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.foodnhanh.adapter.MainAdapter;
import com.example.foodnhanh.adapter.OrderAdapter;
import com.example.foodnhanh.R;
import com.example.foodnhanh.model.MainModel;
import com.example.foodnhanh.model.Model;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    List<Model> modelList;
    FirebaseAuth authProfile;

    RecyclerView recyclerView1;
    BottomNavigationView nav;
    OrderAdapter mAdapter;
    //Line 1
    MainAdapter mainAdapter;
    RecyclerView recyclerView;
    Button btnAdd;

    MenuItem menuItem;
Spinner spinner;
public static final String[] language= new String[]{"Select Language", "English", "Japanese"};
    SearchView searchView;
    public Toolbar toolbar;

    //Language
    public void setLocal(Activity activity,String langCode){
        Locale locale=new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources=activity.getResources();
        Configuration configuration=resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration,resources.getDisplayMetrics());

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rv);

        //AddPro
        btnAdd=findViewById(R.id.AddPro);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AddProActivity.class));
            }
        });

        //Language Spinner
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> spinneradapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, language);
        spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(spinneradapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("UnsafeIntentLaunch")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectionLang = parent.getItemAtPosition(position).toString();
                if (selectionLang.equals("English")) {
                setLocal(MainActivity.this,"en");
                finish();
                startActivity(getIntent());
                } else if (selectionLang.equals("Japanese")) {
                    setLocal(MainActivity.this,"ja");
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Banner
        ImageSlider imageSlider = findViewById(R.id.imageslider);
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.voucher, getString(R.string.discount_on_food_items), ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.voucher1,  getString(R.string.discount_on_food_items), ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.voucher3,  getString(R.string.discount_on_food_items), ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.voucher2,  getString(R.string.discount_on_food_items), ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.voucher4,  getString(R.string.discount_on_food_items), ScaleTypes.CENTER_CROP));
        imageSlider.setImageList(slideModels);

        //Bottom
        nav = findViewById(R.id.bottomNavigation);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

             if (id == R.id.home) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);

                return true;

            }
           else if (id == R.id.favorite) {
                 if (authProfile.getCurrentUser() == null) {
                     Toast.makeText(MainActivity.this, "Bạn chưa đăng nhập! Không có Product Favorite để hiển thị.", Toast.LENGTH_LONG).show();
                     showAlertDialog();


                 } else {


                 Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                 startActivity(intent);
                 finish();

                 return true;
             }

            }
           else if (id == R.id.cart) {
                Intent intent = new Intent(MainActivity.this, SummaryActivity.class);
                startActivity(intent);

                return true;

            }

            else if (id == R.id.profile)
            {
                if (authProfile.getCurrentUser() == null)
                {
                    Toast.makeText(MainActivity.this, "Bạn chưa đăng nhập! Không có Profile để hiển thị.", Toast.LENGTH_LONG).show();
                    showAlertDialog();


                }
                else
                {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
                }
            }
            return true;
        });
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();


        //List food FIREBASE
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        FirebaseRecyclerOptions<MainModel> options=new FirebaseRecyclerOptions.Builder<MainModel>().setQuery(FirebaseDatabase.getInstance().getReference().
                child("Product"),MainModel.class).build();
        mainAdapter=new MainAdapter(options);
        recyclerView.setAdapter(mainAdapter);

        //Cart SQLITE
        recyclerView1 = findViewById(R.id.rvSQLite);
        //SQL
        modelList = new ArrayList<>();
        modelList.add(new Model(getString(R.string.burger), getString(R.string.greentea), R.drawable.burger ));
        modelList.add(new Model(getString(R.string.chicken), getString(R.string.latte), R.drawable.chicken));
        modelList.add(new Model(getString(R.string.smoothie), getString(R.string.orangesmoothie), R.drawable.cocacola));
        modelList.add(new Model(getString(R.string.vanilla), getString(R.string.orangevanilla), R.drawable.dessert));
        modelList.add(new Model(getString(R.string.cappuccino), getString(R.string.cappcuni), R.drawable.burger));
        modelList.add(new Model(getString(R.string.thai_tea), getString(R.string.thaitea), R.drawable.dessert));
        modelList.add(new Model(getString(R.string.coca_cola), getString(R.string.tea), R.drawable.cocacola));
        modelList.add(new Model(getString(R.string.bubble_tea), getString(R.string.bubbletea), R.drawable.chicken));
        modelList.add(new Model(getString(R.string.dessertcheese), getString(R.string.match), R.drawable.dessert));
        recyclerView1.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        // adapter
        mAdapter = new OrderAdapter(this, modelList);
        // recyclerview
        recyclerView1.setAdapter(mAdapter);


        // Favorite List


        //Search
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void showAlertDialog()
    {
        //Setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Bạn chưa đăng nhập! Không có thông tin để hiển thị.");
        builder.setMessage("Vui lòng đăng nhập để xem thông tin!");

        // Open Email app if User click Continue Button
        builder.setPositiveButton("Đăng nhập", (dialog, which) -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Trở về", (dialog, which) -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Create the AlertDialog
        AlertDialog alertDialog = builder.create();

        // Show the AlertDialog
        alertDialog.show();
    }



    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        menuItem=menu.findItem(R.id.action_search);
        searchView=(SearchView) menuItem.getActionView();
        assert searchView != null;
        searchView.setIconified(true);
        SearchManager searchManager=(SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mysearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
return true;
    }

    private void mysearch(String query) {
        FirebaseRecyclerOptions<MainModel> options=new FirebaseRecyclerOptions.Builder<MainModel>().setQuery(FirebaseDatabase.getInstance().getReference().
                child("Product").orderByChild("name").startAt(query).endAt(query+"\uf8ff"),MainModel.class).build();
        mainAdapter=new MainAdapter(options);
        mainAdapter.startListening();
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
