package com.example.foodnhanh.activity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.foodnhanh.R;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class UploadProfilePictureActivity extends AppCompatActivity
{
    ProgressBar progressBar;
    ImageView imageViewUpPic;
    Button btn_chooseuploadImg, btn_saveImg;
    FirebaseAuth auth;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    static  final int PICK_IMAGE_REQUEST = 1;
    Uri uriImage;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_picture);

        btn_chooseuploadImg = findViewById(R.id.btnUploadImg);
        btn_saveImg = findViewById(R.id.btnSaveImgUpload);
        progressBar = findViewById(R.id.uploadimg_progessBar);
        imageViewUpPic = findViewById(R.id.ImageUpload);

        auth = FirebaseAuth.getInstance();
        firebaseUser =auth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");

        Uri uri = firebaseUser.getPhotoUrl();

        // Set User's current DP in ImageView (if upload already). We will Picasso since ImageViewer setImage
        // Regular URIs
        Glide.with(imageViewUpPic).load(UploadProfilePictureActivity.this).load(uri).load(imageViewUpPic);
       //Picasso.get(UploadProfilePictureActivity.this).load(uri).into(imageViewUpPic);
        //Glide.with(imageViewUpPic).load(UploadProfilePictureActivity.this).into(imageViewUpPic);

        // Chọn ảnh để upload
        btn_chooseuploadImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openFileChooser();
            }
        });
        // Upload image
        btn_saveImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                progressBar.setVisibility(View.VISIBLE);
                UploadPic();
            }
        });
    }

    private void UploadPic()
    {
        if (uriImage != null)
        {
            StorageReference fileReference = storageReference.child(auth.getCurrentUser()
                    .getUid() +"."+getFileExtension(uriImage));

            // Uploaf image to Storage
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri uri)
                        {
                            Uri dowloadUri = uri;
                            firebaseUser = auth.getCurrentUser();

                            // Finally set the display image of the user after upload
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(dowloadUri).build();
                            firebaseUser.updateProfile(profileChangeRequest);
                        }
                    });
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UploadProfilePictureActivity.this,"Upload ảnh profile thành công!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(UploadProfilePictureActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(UploadProfilePictureActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        else
        {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(UploadProfilePictureActivity.this,"Không có file ảnh nào được chọn!", Toast.LENGTH_LONG).show();
        }
    }
    private String getFileExtension(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void openFileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData()!= null)
        {
            uriImage = data.getData();
            imageViewUpPic.setImageURI(uriImage);
        }
    }
}