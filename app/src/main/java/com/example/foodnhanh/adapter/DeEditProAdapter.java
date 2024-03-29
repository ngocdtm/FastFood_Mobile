package com.example.foodnhanh.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodnhanh.R;
import com.example.foodnhanh.activity.AddProActivity;
import com.example.foodnhanh.activity.DetailActivity;
import com.example.foodnhanh.activity.LoginActivity;
import com.example.foodnhanh.activity.MainActivity;
import com.example.foodnhanh.model.MainModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DeEditProAdapter extends FirebaseRecyclerAdapter<MainModel,DeEditProAdapter.myViewHolder> {
    List<MainModel> list;
    Context context;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

    public DeEditProAdapter(@NonNull FirebaseRecyclerOptions<MainModel> options, List<MainModel> list, Context context) {
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
    public DeEditProAdapter(@NonNull FirebaseRecyclerOptions<MainModel> options) {
        super(options);
    }


    @Override
      protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull MainModel model) {
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
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus=DialogPlus.newDialog(holder.img.getContext())
                        .setContentHolder(new ViewHolder(R.layout.update_popup))
                        .setExpanded(true,1500).create();
               // dialogPlus.show();
                View view=dialogPlus.getHolderView();
                EditText name=view.findViewById(R.id.txtTextPro);
                EditText img=view.findViewById(R.id.txtImagePro);
                EditText price=view.findViewById(R.id.txtPricePro);
                Button btnEdit1=view.findViewById(R.id.btnUpdateProDetail);
                name.setText(model.getName());
                price.setText(model.getPrice()+"");
                img.setText(model.getImg_url());
                dialogPlus.show();
                btnEdit1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String,Object> map=new HashMap<>();
                        map.put("name",name.getText().toString());
                        map.put("img_url",img.getText().toString());
                        Integer price1=Integer.parseInt(price.getText().toString());
                        map.put("price",price1);
                        FirebaseDatabase.getInstance().getReference().child("Product").child(getRef(position).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(holder.name.getContext(),"Data Update Successfully",Toast.LENGTH_SHORT).show();
                                dialogPlus.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(holder.name.getContext(),"Error Fail",Toast.LENGTH_SHORT).show();
                                dialogPlus.dismiss();
                            }
                        });
                    }
                });
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            AlertDialog.Builder builder=new AlertDialog.Builder(holder.name.getContext());
            builder.setTitle("Are you sure?");
            builder.setMessage("Deleted data can't be Undo.");
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                FirebaseDatabase.getInstance().getReference().child("Product").child(getRef(position).getKey()).removeValue();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(holder.name.getContext(),"Cancelled",Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
            }
        });

    }

    @NonNull
    @Override
    public DeEditProAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.delete_edit_item,parent,false);
       return new myViewHolder(view);
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img;
        TextView name, price, id;
        Button btnEdit,btnDelete;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img1);
            name = itemView.findViewById(R.id.nametext);
            price = itemView.findViewById(R.id.price);
            id = itemView.findViewById(R.id.idtext);
            btnEdit = itemView.findViewById(R.id.EditPro);
            btnDelete = itemView.findViewById(R.id.deletePro);
        }
    }
    }
