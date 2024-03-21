package com.example.foodnhanh.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.foodnhanh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity
{
    EditText loginemail, loginpassword;
    Button btnlogin;
    TextView signupRedirectText, forgotpass;
    ProgressBar progressBar;
    FirebaseAuth authProfile;
    static final String TAG="LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginemail = findViewById(R.id.login_email);
        loginpassword = findViewById(R.id.login_password);
        btnlogin = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        forgotpass = findViewById(R.id.forgotPassword);
        progressBar = findViewById(R.id.login_progressBar);
        authProfile = FirebaseAuth.getInstance();

        btnlogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = loginemail.getText().toString();
                String password = loginpassword.getText().toString();

                if (email.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Thông tin không được để trống!", Toast.LENGTH_LONG).show();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    loginemail.setError("Email không xác thực. Vui lòng thử lại!");
                    loginemail.requestFocus();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(email, password);
                }
            }
        });
        signupRedirectText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        forgotpass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(LoginActivity.this, "You can reset password now!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                finish();
            }
        });
    }

    private void loginUser(String email, String password)
    {
        authProfile.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    //Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_LONG).show();

                    //Get instance of the current User
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();

                    // Check if Email is verified before user can access their profile
                    if (firebaseUser.isEmailVerified())
                    {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlertDialog();
                    }
                }
                else
                {
                    try
                    {
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidUserException e)
                    {
                        loginemail.setError("Email không tồn tại hoặc chưa đăng ký. Vui lòng đăng ký tài khoản!");
                        loginemail.requestFocus();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e)
                    {
                        loginpassword.setError("Mật khẩu không chính xác. Vui lòng thử lại!");
                        loginpassword.requestFocus();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(LoginActivity.this, "Có lỗi xảy ra. Vui lòng thử lại", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog()
    {
        //Setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email chưa được xác thực!");
        builder.setMessage("Vui lòng xác thực email!");

        // Open Email app if User click Continue Button
        builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener()
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
        builder.setNegativeButton("Trở về", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                startActivity(getIntent());
            }
        });

        // Create the AlertDialog
        AlertDialog alertDialog = builder.create();

        // Show the AlertDialog
        alertDialog.show();
    }

    // Check if User is already logged in. In such case, starightaway take the User to the User's Profile
    @Override
    protected void onStart()
    {
        authProfile.signOut();
        super.onStart();
        if (authProfile.getCurrentUser() != null)
        {
            Toast.makeText(LoginActivity.this, "Bạn đang đăng nhập!", Toast.LENGTH_LONG).show();
            // Start the UserProfileActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        else
        {
            Toast.makeText(LoginActivity.this, "You can Login now!", Toast.LENGTH_LONG).show();
        }
    }
}