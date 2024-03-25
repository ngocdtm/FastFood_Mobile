package com.example.foodnhanh.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodnhanh.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

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

        assert firebaseUser != null;
        if (firebaseUser.equals(""))
        {
            Toast.makeText(ChangePasswordActivity.this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            reAuthenticateUser(firebaseUser);
        }
    }

    @SuppressLint("SetTextI18n")
    private void reAuthenticateUser(FirebaseUser firebaseUser)
    {
        btn_authenticated.setOnClickListener(v -> {
            userPassword = currentPassword.getText().toString();
            if (userPassword.isEmpty())
            {
                currentPassword.setError("Mật khẩu không được để trống");
                currentPassword.requestFocus();
            }
            else
            {
                progressBar.setVisibility(View.VISIBLE);

                AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseUser.getEmail()), userPassword);

                firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
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

                        btn_savenewPassword.setOnClickListener(v1 -> changePass(firebaseUser));
                    }
                    else
                    {
                        try
                        {
                            throw Objects.requireNonNull(task.getException());
                        }
                        catch (Exception e)
                        {
                            currentPassword.setError("Mật khẩu không trùng khớp. Vui lòng thử lại!");
                            currentPassword.requestFocus();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                });
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
            confirmNewPass.setError("Mật khẩu không chính xác! Vui lòng nhập lại");
            confirmNewPass.requestFocus();
        }
        else if (userPassword.matches(userpassNew))
        {
            newPassword.setError("Mật khẩu mới không được trùng với mật khẩu cũ. Vui lòng nhập mật khẩu khác!");
            newPassword.requestFocus();
        }
        else
        {
            progressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userpassNew).addOnCompleteListener(task -> {
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
                        throw Objects.requireNonNull(task.getException());
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            });
        }
    }
}