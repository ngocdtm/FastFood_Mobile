package com.example.foodnhanh.adapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodnhanh.activity.DetailActivity;
import com.example.foodnhanh.R;
import com.example.foodnhanh.activity.LoginActivity;
import com.example.foodnhanh.activity.MainActivity;
import com.example.foodnhanh.model.MainModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainAdapter extends FirebaseRecyclerAdapter<MainModel,MainAdapter.myViewHolder> {
    List<MainModel> list;
    Context context;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    public MainAdapter(@NonNull FirebaseRecyclerOptions<MainModel> options, List<MainModel> list, Context context) {
        super(options);
        this.list = list;
        this.context = context;

    }

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MainAdapter(@NonNull FirebaseRecyclerOptions<MainModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MainAdapter.myViewHolder holder, int position, @NonNull MainModel model) {
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        holder.name.setText(model.getName());
        holder.id.setText(model.getId());
        holder.price.setText(model.getPrice()+"VND");
        Glide.with(holder.img.getContext()).load(model.getImg_url()).placeholder(com.firebase.ui.database.R.drawable.
                        common_google_signin_btn_icon_dark).circleCrop().
                error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal).into(holder.img);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (firebaseAuth.getCurrentUser() == null) {
                //Setup the Alert Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Bạn chưa đăng nhập! Không có Product Favorite để hiển thị.");
                builder.setMessage("Vui lòng đăng nhập để xem Product Favorite");

                // Open Email app if User click Continue Button
                builder.setPositiveButton("Đăng nhập", (dialog, which) -> {
                    Intent intent = new Intent(v.getContext(), LoginActivity.class);
                    v.getContext().startActivity(intent);

                });
                builder.setNegativeButton("Trở về", (dialog, which) -> {
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    v.getContext().startActivity(intent);

                });

                // Create the AlertDialog
                AlertDialog alertDialog = builder.create();

                // Show the AlertDialog
                alertDialog.show();
            }
            else {

                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra("id", model.getId());
                intent.putExtra("singlename", model.getName());
                intent.putExtra("i", model.getImg_url());
                intent.putExtra("p", model.getPrice());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);

            }
        }
    });
    }

    @NonNull
    @Override
    public MainAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item,parent,false);
        return new myViewHolder(view);
    }
    public class myViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img;
        TextView name,price,id;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.img1);
            name= itemView.findViewById(R.id.nametext);
            price=itemView.findViewById(R.id.price);
            id=itemView.findViewById(R.id.idtext);

        }
    }
}

