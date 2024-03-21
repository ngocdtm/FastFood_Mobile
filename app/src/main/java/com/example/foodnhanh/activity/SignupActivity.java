package com.example.foodnhanh.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.example.foodnhanh.R;
import androidx.annotation.NonNull;


import android.app.DatePickerDialog;
import android.content.Intent;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodnhanh.model.ReadWriteUserDetails;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import com.google.firebase.auth.FirebaseUser;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignupActivity extends AppCompatActivity {

    EditText signupfullName, signupEmail, signupBirthday, signupPhone, signupPassword, confirmPassword;
    Button btnSignup;
    RadioGroup radioGroup;
    RadioButton radioButton;
    TextView loginRedirectText;
    ProgressBar progressBar;
    private  DatePickerDialog picker;
    private  static final  String TAG = "SignupAvtivity";
    //FirebaseDatabase database;
    //DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://projectfastfood-8a851-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //getSupportActionBar().setTitle("Signup");
        Toast.makeText(SignupActivity.this,"You can signup now!", Toast.LENGTH_LONG).show();

        signupfullName = findViewById(R.id.signup_fullName);
        signupEmail = findViewById(R.id.signup_email);
        signupBirthday = findViewById(R.id.signup_birthday);
        signupPhone = findViewById(R.id.signup_phone);
        signupPassword = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.confirmpassword);

        progressBar = findViewById(R.id.progressBar);

        btnSignup = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        //RadioButton for Gender
        radioGroup = findViewById(R.id.signup_chooseGender);
        radioGroup.clearCheck();

        //Setting up DatePicker on EditText
        signupBirthday.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(calendar.DAY_OF_MONTH);
                int month = calendar.get(calendar.MONTH);
                int year = calendar.get(calendar.YEAR);

                //DAte Picker Dialog
                picker = new DatePickerDialog(SignupActivity.this, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        signupBirthday.setText(dayOfMonth +"/" + (month+1)+"/"+year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int selectedGender = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(selectedGender);

                String txtFullName = signupfullName.getText().toString();
                String txtEmail = signupEmail.getText().toString();
                String txtBirthday = signupBirthday.getText().toString();
                String txtPhone = signupPhone.getText().toString();
                String password = signupPassword.getText().toString();
                String checkpassword = confirmPassword.getText().toString();
                String txtGender;

                // validate Mobile Number using Matcher and Pattern (Regular Expression)
                String mobileRegex = "[0][0-9]{9}"; // First no. just can be {0} and rest 9 nos. can be any no.
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(txtPhone);

                if (txtFullName.isEmpty() || txtEmail.isEmpty() || txtBirthday.isEmpty() || txtPhone.isEmpty() || password.isEmpty() || checkpassword.isEmpty() || radioGroup.getCheckedRadioButtonId() == -1)
                {
                    Toast.makeText(SignupActivity.this, "Thông tin không được để trống!", Toast.LENGTH_LONG).show();
                }
                else if (txtPhone.length() != 10)
                {
                    signupPhone.setError("Số điện thoại phải đủ 10 số. Vui lòng nhập lại!");
                    signupPhone.requestFocus();
                }
                else if (!mobileMatcher.find())
                {
                    signupPhone.setError("Số điện thoại không hợp lệ. Vui lòng nhập lại!");
                    signupPhone.requestFocus();
                }
                else if (radioGroup.getCheckedRadioButtonId() == -1)
                {
                    radioButton.setError("Vui lòng chọn giới tính!");
                    radioButton.requestFocus();
                }
                else if (password.length() < 6)
                {
                    signupPassword.setError("Mật khẩu phải có ít nhất 6 ký tự. Vui lòng nhập lại!");
                    signupPassword.requestFocus();
                }
                else if (!password.equals(checkpassword))
                {
                    confirmPassword.setError("Mật khẩu không chính xác!");
                    confirmPassword.requestFocus();
                    confirmPassword.clearComposingText();;
                }
                else
                {
                    txtGender = radioButton.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    signupUser (txtFullName, txtEmail, txtBirthday, txtGender, txtPhone, password);
                }

            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void signupUser (String txtFullName, String txtEmail, String txtBirthday, String txtGender, String txtPhone, String password)
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(txtEmail, password).addOnCompleteListener(SignupActivity.this,
                new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            //Update Display name of Users
                            //UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(txtFullName).build();
                            //firebaseUser.updateProfile(profileChangeRequest);

                            //Enter User Data into the Firebase realtime database
                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(txtFullName, txtBirthday, txtGender, txtPhone);

                            // Extracting user reference from DB for "Users"
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users");

                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        //Send Verification Email
                                        firebaseUser.sendEmailVerification();

                                        Toast.makeText(SignupActivity.this, "Đăng ký tài khoản thành công! Vui lòng xác thực email của bạn trước khi đăng nhập.", Toast.LENGTH_LONG).show();

                                        //Open Main Activity after successful signup
                                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(SignupActivity.this, "Đăng ký tài khoản không thành công. Vui lòng thử lại!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                        else
                        {
                            try
                            {
                                throw  task.getException();
                            }
                            catch (FirebaseAuthInvalidCredentialsException e)
                            {
                                signupPassword.setError("Email không tồn tại hoặc đã được sử dụng. Vui lòng đăng ký email mới!");
                                signupPassword.requestFocus();
                            }
                            catch (FirebaseAuthUserCollisionException e)
                            {
                                signupPassword.setError("Người dùng đã tồn tại. Vui lòng đăng ký tài khoản mới!");
                                signupPassword.requestFocus();
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }
}