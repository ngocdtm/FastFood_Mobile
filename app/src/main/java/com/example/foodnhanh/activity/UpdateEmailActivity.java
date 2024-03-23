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

import java.util.Objects;

/** @noinspection deprecation*/
public class UpdateEmailActivity extends AppCompatActivity
{
    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    ProgressBar progressBar;
    TextView txtViewAuthenticated;
    Button btn_authenticated, btn_savenewEmail;
    EditText currentEmail, newEmail, curentPassword;
    String userOldEmail, userPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);
        //currentEmail : tên đang nhập vào
        currentEmail = findViewById(R.id.input_currentEmail);
        //curentPass: pass đang nhập vào
        curentPassword = findViewById(R.id.input_password);
        //đổi mới gán vào
        newEmail = findViewById(R.id.input_newEmail);
        txtViewAuthenticated = findViewById(R.id.txt_update_email_authentication);

        btn_authenticated = findViewById(R.id.btn_verify);
        //btnEmail: save lại
        btn_savenewEmail = findViewById(R.id.btn_saveEmail);
btn_savenewEmail.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(UpdateEmailActivity.this, UpdateProfileActivity.class);
        intent.putExtra("BackToProfile", 1);
        startActivity(intent);
        finish();
    }
});
        progressBar = findViewById(R.id.updateEmail_progessBar);
        // đang mới đầu là chưa lưu sau đó sẽ thành true
        btn_savenewEmail.setEnabled(false);
        newEmail.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();//lấy ng dùng

        userOldEmail = firebaseUser.getEmail();//người dùng cũ

        if (firebaseUser.equals(""))
        {
            Toast.makeText(UpdateEmailActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        else
        {
            reAuthenticate(firebaseUser);
        }
    }

    private void reAuthenticate(FirebaseUser firebaseUser)
    {
        btn_authenticated.setOnClickListener(v -> {
         userOldEmail = currentEmail.getText().toString();
            userPassword = curentPassword.getText().toString();

            if (userOldEmail.isEmpty())
            {
                currentEmail.setError("Email không được để trống!");//checked
                currentEmail.requestFocus();
            }
            else if (userPassword.isEmpty())
            {
                curentPassword.setError("Mật khẩu không được để trống!");//checked
                curentPassword.requestFocus();
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(userOldEmail).matches())//checked
            {
                currentEmail.setError("Email không chính xác!");
                currentEmail.requestFocus();
            }
            else
            {
                progressBar.setVisibility(View.VISIBLE);
                AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail, userPassword);//checked password wrong

                firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        progressBar.setVisibility(View.GONE);

                        curentPassword.setEnabled(false);//đóng text ko cho nhap
                        btn_authenticated.setEnabled(false);//đóng text ko cho nhap
                        newEmail.setEnabled(true);//open
                        btn_savenewEmail.setEnabled(true);//open

                        Toast.makeText(UpdateEmailActivity.this, "Mật khẩu đã được xác thực. Bạn có thể thay đổi Email mới!", Toast.LENGTH_SHORT).show();

                        txtViewAuthenticated.setText("Bạn đã xác thực thành công. Hãy cập nhật email mới");


                        // Đổi màu button thay đổi emai
                        btn_savenewEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this, R.color.dargGreen));

                        btn_savenewEmail.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v1)
                            {

                             String  NewEmail=newEmail.getText().toString();
                              updateEmail(NewEmail);
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
                            Toast.makeText(UpdateEmailActivity.this," Ko coa thay doi ne huhu", Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private void updateEmail(String NewEmail)
    {
        String userNewEmail = newEmail.getText().toString();
        if (userNewEmail.isEmpty())
        {
            Toast.makeText(UpdateEmailActivity.this, "Email mới không được để trống", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches())
        {
            Toast.makeText(UpdateEmailActivity.this, " Email ko tồn tại", Toast.LENGTH_SHORT).show();
        }
        else if (userOldEmail.matches(userNewEmail))
        {
            Toast.makeText(UpdateEmailActivity.this, "Email mới không được trùng với email cũ. Vui lòng nhập email khác!", Toast.LENGTH_SHORT).show();
        }
        else{
            firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isComplete())
                    {
                        // Verify Email
                       // firebaseUser.sendEmailVerification();

                        Toast.makeText(UpdateEmailActivity.this, "Email mới đã được cập nhật. Vui lòng xác thực email!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdateEmailActivity.this, UpdateProfileActivity.class);
                        intent.putExtra("BackToProfile", 1);
                        startActivity(intent);
                        finish();;
                    }


                    else
                    {
                        try
                        {
                            throw Objects.requireNonNull(task.getException());
                        }
                        catch (Exception e)
                        {
                            curentPassword.setError("Email ko chinh xac!");
                            curentPassword.requestFocus();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

    }
}