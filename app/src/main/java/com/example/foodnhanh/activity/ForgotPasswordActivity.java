package com.example.foodnhanh.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.foodnhanh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity
{
    EditText ForgotPass_email;
    Button buttonResetPass;
    ProgressBar progressBar;
    FirebaseAuth auth;
    static String TAG ="ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //getSupportActionBar().setTitle("Forgot Password");

        ForgotPass_email = findViewById(R.id.fogotpassword_email);
        buttonResetPass = findViewById(R.id.fogotpassword_button);
        progressBar = findViewById(R.id.forgotPass_progressBar);

        buttonResetPass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = ForgotPass_email.getText().toString();

                if (email.isEmpty())
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Vui lòng điền email đã đăng ký!", Toast.LENGTH_LONG);
                    ForgotPass_email.setError("Cần điền Email!");
                    ForgotPass_email.requestFocus();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Email không tồn tại. Vui lòng điền email đã đăng ký!", Toast.LENGTH_LONG);
                    ForgotPass_email.setError("Cần điền Email!");
                    ForgotPass_email.requestFocus();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    resetPassword(email);
                }
            }
        });
    }

    private void resetPassword(String email)
    {
        auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Vui lòng kiểm tra email để xác nhận quá trình đổi mật khẩu!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    try
                    {
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidUserException e)
                    {
                        ForgotPass_email.setError("Email không tồn tại hoặc chưa đăng ký. Vui lòng đăng ký tài khoản!");
                        ForgotPass_email.requestFocus();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e)
                    {
                        ForgotPass_email.setError("Thông tin chưa được xác thực. Vui lòng thử lại!");
                        ForgotPass_email.requestFocus();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(ForgotPasswordActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();

                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}