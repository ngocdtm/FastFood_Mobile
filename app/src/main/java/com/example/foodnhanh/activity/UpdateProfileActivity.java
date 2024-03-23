package com.example.foodnhanh.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.foodnhanh.R;
import com.example.foodnhanh.model.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity
{
    EditText UpdateName, UpdateBirthday, UpdatePhone;
    Button btn_upload_picture, btn_update_email, btn_save_update;
    RadioGroup radioGroupGender;
    RadioButton radioButton;
    String txtFullName, txtBirthday, txtGender, txtPhone;
    FirebaseAuth authProfile;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        progressBar = findViewById(R.id.update_progressBar);
        UpdateName = findViewById(R.id.update_fullName);
        UpdateBirthday = findViewById(R.id.update_birthday);
        UpdatePhone = findViewById(R.id.update_phone);

        radioGroupGender = findViewById(R.id.update_chooseGender);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null)
        {
            Toast.makeText(UpdateProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            checkEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            // Show Profile data
            showwProfile(firebaseUser);
        }

        // Upload Profile Pic
        btn_upload_picture = findViewById(R.id.btn_update_picture);
        btn_upload_picture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(UpdateProfileActivity.this, UploadProfilePictureActivity.class);
                startActivity(intent);

                finish();
            }
        });

        // Update Email
        btn_update_email = findViewById(R.id.btn_update_email);
        btn_update_email.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(UpdateProfileActivity.this, UpdateEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Setting up DatePicker on EditText
        UpdateBirthday.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String txtSADoB [] = txtBirthday.split("/");

                int day = Integer.parseInt(txtSADoB[0]);
                int month = Integer.parseInt(txtSADoB[1]) - 1;
                int year = Integer.parseInt(txtSADoB[2]);

                DatePickerDialog picker;

                //Date Picker Dialog
                picker = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        UpdateBirthday.setText(dayOfMonth +"/" + (month+1)+"/"+year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        // Update Profile
        btn_save_update = findViewById(R.id.btn_update_save);
        btn_save_update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateProfile(firebaseUser);

            }
        });
    }

    private void checkEmailVerified(FirebaseUser firebaseUser)
    {
        if (!firebaseUser.isEmailVerified())
        {
            showAlertDialog();
        }
    }

    private void showAlertDialog()
    {
        //Setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileActivity.this);
        builder.setTitle("Email chưa được xác thực!");
        builder.setMessage("Vui lòng xác thực email. Bạn sẽ không thể đăng nhập lại lần tới nếu chưa xác thực email!");

        // Open Email app if User click Continue Button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // Create the AlertDialog
        AlertDialog alertDialog = builder.create();

        // Show the AlertDialog
        alertDialog.show();
    }

    private void updateProfile(FirebaseUser firebaseUser)
    {
        int selectedGenderID = radioGroupGender.getCheckedRadioButtonId();
        radioButton = findViewById(selectedGenderID);

        // validate Mobile Number using Matcher and Pattern (Regular Expression)
        String mobileRegex = "[0][0-9]{9}"; // First no. just can be {0} and rest 9 nos. can be any no.
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(txtPhone);

        txtFullName = UpdateName.getText().toString();
        txtPhone = UpdatePhone.getText().toString();
        txtBirthday = UpdateBirthday.getText().toString();

        if (txtFullName.isEmpty() || txtBirthday.isEmpty() || txtPhone.isEmpty() || radioGroupGender.getCheckedRadioButtonId() == -1)
        {
            Toast.makeText(UpdateProfileActivity.this, "Thông tin không được để trống!", Toast.LENGTH_LONG).show();
        }
        else if (txtPhone.length() != 10)
        {
            UpdatePhone.setError("Số điện thoại phải đủ 10 số. Vui lòng nhập lại!");
            UpdatePhone.requestFocus();
        }
        else if (!mobileMatcher.find())
        {
            UpdatePhone.setError("Số điện thoại không hợp lệ. Vui lòng nhập lại!");
            UpdatePhone.requestFocus();
        }
        else if (radioGroupGender.getCheckedRadioButtonId() == -1)
        {
            radioButton.setError("Vui lòng chọn giới tính!");
            radioButton.requestFocus();
        }
        else
        {
            txtGender = radioButton.getText().toString();
            txtFullName = UpdateName.getText().toString();
            txtBirthday = UpdateBirthday.getText().toString();
            txtPhone = UpdatePhone.getText().toString();

            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(txtFullName, txtBirthday, txtGender, txtPhone);

            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users");

            String userID = firebaseUser.getUid();

            progressBar.setVisibility(View.VISIBLE);

            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(UpdateProfileActivity.this,"Cập nhật Profile thành công!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(UpdateProfileActivity.this, UserProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        try
                        {
                            throw task.getException();
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(UpdateProfileActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    // Lấy data detail của user hiển thị lên
    private void showwProfile(FirebaseUser firebaseUser)
    {
        String userID = firebaseUser.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
        progressBar.setVisibility(View.VISIBLE);
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readWriteUserDetails != null)
                {
                    txtFullName = readWriteUserDetails.fullName;
                    txtBirthday = readWriteUserDetails.birthday;
                    txtPhone = readWriteUserDetails.mobile;
                    txtGender = readWriteUserDetails.gender;

                    UpdateName.setText(txtFullName);
                    UpdateBirthday.setText(txtBirthday);
                    UpdatePhone.setText(txtPhone);

                    // Show Gender through Radio Button
                    if (txtGender.equals("Male"))
                    {
                        radioButton = findViewById(R.id.update_male);
                    }
                    else
                    {
                        radioButton = findViewById(R.id.update_female);
                    }
                    radioButton.setChecked(true);
                }
                else
                {
                    Toast.makeText(UpdateProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(UpdateProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}