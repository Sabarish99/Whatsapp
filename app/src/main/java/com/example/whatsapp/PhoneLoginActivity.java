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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private Button SendverificationCodeButton,VerifyButton;
    private EditText inputPhno,inputVerificationcode;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    private  String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        SendverificationCodeButton = findViewById(R.id.send_verification_code_button);
        VerifyButton = findViewById(R.id.verify_button);
        inputPhno = findViewById(R.id.phone_number_input);
        inputVerificationcode=findViewById(R.id.verification_code_input);
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        SendverificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String phoneNumber =inputPhno.getText().toString();

                if(TextUtils.isEmpty(phoneNumber))
                    Toast.makeText(PhoneLoginActivity.this, "Enter Phone Number...", Toast.LENGTH_SHORT).show();

                else
                {
                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Please wait while we are authenticating your phone");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                           PhoneLoginActivity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks

                }
            }
        });

        VerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendverificationCodeButton.setVisibility(View.INVISIBLE);
                inputPhno.setVisibility(View.INVISIBLE);

                String verificationcode = inputVerificationcode.getText().toString();

                if(TextUtils.isEmpty(verificationcode))
                {
                    Toast.makeText(PhoneLoginActivity.this, "Please write OTP", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    loadingBar.setTitle(" OTP Verification");
                    loadingBar.setMessage("Please wait while we are verifying OTP");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode);
                    signInWithPhoneAuthCredential(credential);

                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loadingBar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Invalid Phone number..", Toast.LENGTH_SHORT).show();
                SendverificationCodeButton.setVisibility(View.VISIBLE);
                inputPhno.setVisibility(View.VISIBLE);
                inputVerificationcode.setVisibility(View.INVISIBLE);
                VerifyButton.setVisibility(View.INVISIBLE);

            }
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                //Log.d(TAG, "onCodeSent:" + verificationId);
            loadingBar.dismiss();

                mVerificationId = verificationId;
                mResendToken = token;

                Toast.makeText(PhoneLoginActivity.this, "Code has been sent successfully...", Toast.LENGTH_SHORT).show();

                SendverificationCodeButton.setVisibility(View.INVISIBLE);
                inputPhno.setVisibility(View.INVISIBLE);
                inputVerificationcode.setVisibility(View.VISIBLE);
                VerifyButton.setVisibility(View.VISIBLE);


            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                           loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Congratulations.. You are logged in successfully", Toast.LENGTH_SHORT).show();

                            Intent mainIntent = new Intent(PhoneLoginActivity.this,MainActivity.class);
                            startActivity(mainIntent);

                        }
                        else {
                                 String mssg =task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error :" + mssg, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }


}
