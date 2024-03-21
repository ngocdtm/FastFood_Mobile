package com.example.foodnhanh.adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodnhanh.activity.DetailActivity;
import com.example.foodnhanh.R;
import com.example.foodnhanh.model.MainModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainAdapter extends FirebaseRecyclerAdapter<MainModel,MainAdapter.myViewHolder> {
    List<MainModel> list;
    Context context;
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
        holder.name.setText(model.getName());
        holder.price.setText(model.getPrice()+"VND");
        Glide.with(holder.img.getContext()).load(model.getImg_url()).placeholder(com.firebase.ui.database.R.drawable.
                        common_google_signin_btn_icon_dark).circleCrop().
                error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal).into(holder.img);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(v.getContext(), DetailActivity.class);
            intent.putExtra("singlename",model.getName());
            intent.putExtra("i",model.getImg_url());
            intent.putExtra("p",model.getPrice());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            v.getContext().startActivity(intent);
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
        TextView name,price;
        Button btnCart;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.img1);
            name= itemView.findViewById(R.id.nametext);
            price=itemView.findViewById(R.id.price);
            btnCart=itemView.findViewById(R.id.btnCart);
        }
    }
}

