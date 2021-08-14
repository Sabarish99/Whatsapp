package com.example.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton,phoneLoginButton;
    private EditText useremail,userpassword;
    private TextView neednewaccntlink,forgetpasswdlink;
  private FirebaseAuth mAuth;
  private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialisefields();
        mAuth= FirebaseAuth.getInstance();


        neednewaccntlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });
        phoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phneIntent = new Intent(LoginActivity.this,PhoneLoginActivity.class);
                startActivity(phneIntent);
            }
        });
    }

    private void AllowUserToLogin()
    {
        String email = useremail.getText().toString();
        String password = userpassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter email...", Toast.LENGTH_SHORT).show();
        }


        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter password..", Toast.LENGTH_SHORT).show();
        }
        else
        { loadingBar.setTitle("Signing In...");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        SendUserToMainActivity();
                        Toast.makeText(LoginActivity.this, "Logged in Succesfully...", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                    else
                    {
                        String err = task.getException().toString();
                        Toast.makeText(LoginActivity.this, "Errored occured: "+err, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });

        }
    }

    private void initialisefields()
    {
        loginButton = findViewById(R.id.login_button);
        phoneLoginButton = findViewById(R.id.phone_login_button);
        useremail = findViewById(R.id.login_email);
        userpassword = findViewById(R.id.login_passwd);
        neednewaccntlink=findViewById(R.id.need_new_accnt_link);
        forgetpasswdlink=findViewById(R.id.forget_passwd_link);
        loadingBar = new ProgressDialog(this);

    }



    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivty.class);
        startActivity(registerIntent);

    }
}
