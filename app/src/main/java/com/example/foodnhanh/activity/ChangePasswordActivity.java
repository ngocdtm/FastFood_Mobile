package com.example.foodnhanh.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity
{
    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    ProgressBar progressBar;
    TextView txtViewAuthenticated;
    Button btn_authenticated, btn_savenewPassword;
    EditText currentPassword, newPassword, confirmNewPass;
    String userPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        progressBar = findViewById(R.id.updatePassword_progessBar);

        currentPassword = findViewById(R.id.input_currentPass);
        newPassword = findViewById(R.id.input_newPassword);
        confirmNewPass = findViewById(R.id.input_confirmNewPas);

        txtViewAuthenticated = findViewById(R.id.txt_update_password_authentication);

        btn_authenticated = findViewById(R.id.btn_verifyPass);
        btn_savenewPassword = findViewById(R.id.btn_savePassword);

        newPassword.setEnabled(false);
        confirmNewPass.setEnabled(false);
        btn_savenewPassword.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser.equals(""))
        {
            Toast.makeText(ChangePasswordActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            reAuthenticateUser(firebaseUser);
        }
    }

    private void reAuthenticateUser(FirebaseUser firebaseUser)
    {
        btn_authenticated.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                userPassword = currentPassword.getText().toString();
                if (userPassword.isEmpty())
                {
                    //Toast.makeText(ChangePasswordActivity.this, "Mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
                    currentPassword.setError("Mật khẩu không được để trống");
                    currentPassword.requestFocus();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPassword);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                progressBar.setVisibility(View.GONE);

                                currentPassword.setEnabled(false);
                                newPassword.setEnabled(true);
                                confirmNewPass.setEnabled(true);

                                btn_authenticated.setEnabled(false);
                                btn_savenewPassword.setEnabled(true);

                                Toast.makeText(ChangePasswordActivity.this, "Mật khẩu hiện tại đã được xác thực. Bạn có thể thay đổi mật khẩu mới!", Toast.LENGTH_SHORT).show();

                                txtViewAuthenticated.setText("Bạn đã xác thực thành công. Hãy cập nhật mật khẩu mới");


                                // Đổi màu button thay đổi mật khẩu
                                btn_savenewPassword.setBackgroundTintList(ContextCompat.getColorStateList(ChangePasswordActivity.this, R.color.dargGreen));

                                btn_savenewPassword.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        changePass(firebaseUser);
                                    }
                                });
                            }
                            else
                            {
                                try
                                {
                                    throw task.getException();
                                }
                                catch (Exception e)
                                {
                                    //Toast.makeText(ChangePasswordActivity.this, "Mật khẩu không chính xác!", Toast.LENGTH_LONG).show();
                                    currentPassword.setError("Mật khẩu không trùng khớp. Vui lòng thử lại!");
                                    currentPassword.requestFocus();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePass(FirebaseUser firebaseUser)
    {
        String userpassNew = newPassword.getText().toString();
        String userconfirmPass = confirmNewPass.getText().toString();

        if (userpassNew.isEmpty() || userconfirmPass.isEmpty())
        {
            Toast.makeText(ChangePasswordActivity.this, "Thông tin không được để trống", Toast.LENGTH_SHORT).show();
        }
        else if (!userpassNew.matches(userconfirmPass))
        {
            //Toast.makeText(ChangePasswordActivity.this, "Mật khẩu không chính xác! Vui lòng nhập lại", Toast.LENGTH_SHORT).show();
            confirmNewPass.setError("Mật khẩu không chính xác! Vui lòng nhập lại");
            confirmNewPass.requestFocus();
        }
        else if (userPassword.matches(userpassNew))
        {
            //Toast.makeText(ChangePasswordActivity.this, "Mật khẩu mới không được trùng với mật khẩu cũ. Vui lòng nhập mật khẩu khác!", Toast.LENGTH_SHORT).show();
            newPassword.setError("Mật khẩu mới không được trùng với mật khẩu cũ. Vui lòng nhập mật khẩu khác!");
            newPassword.requestFocus();
        }
        else
        {
            progressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userpassNew).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ChangePasswordActivity.this,"Mật khẩu đã đươc thay đổi thành công!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
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
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}