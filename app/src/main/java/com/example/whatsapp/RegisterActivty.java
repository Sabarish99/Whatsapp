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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivty extends AppCompatActivity {

    private Button CreateAccntButton;
    private EditText usermail,userpasswd;
    private TextView alreadyhaveaacntlink;

    private FirebaseAuth mAuth;
    private ProgressDialog  loadingBar;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activty);

        CreateAccntButton = findViewById(R.id.register_button);
        usermail = findViewById(R.id.register_email);
        userpasswd= findViewById(R.id.register_passwd);
        alreadyhaveaacntlink = findViewById(R.id.already_have_accnt_link);
        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        alreadyhaveaacntlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivty.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        CreateAccntButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatenewAccnt();
            } });
        }

            private void CreatenewAccnt() {
                String email = usermail.getText().toString();
                String password = userpasswd.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(RegisterActivty.this, "Please enter email...", Toast.LENGTH_SHORT).show();
                }


                if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(RegisterActivty.this, "Please enter password..", Toast.LENGTH_SHORT).show();
            }
               else
                {
                    loadingBar.setTitle("Creating new Account...");
                    loadingBar.setMessage("Please wait while we are creating new account for you...");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                String currentUserId = mAuth.getCurrentUser().getUid();
                                RootRef.child("Users").child(currentUserId).setValue("");

                                Intent mainIntent = new Intent(RegisterActivty.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                                Toast.makeText(RegisterActivty.this, "Account created Successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                            else
                            {
                                String err = task.getException().toString();
                                Toast.makeText(RegisterActivty.this, "Errored occured: "+err, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
                }

    }
}
