package com.example.dell.phoneauth;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    ProgressDialog mDialog;
    private FirebaseAuth mAuth;

    private LinearLayout mPhoneLayout;
    private LinearLayout mCodeLayout;

    private EditText mPhoneText;
    private EditText mCodeText;

    private Button mSendBtn;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String TAG, mVerificationId;
    private int mBtn = 0;
    LinearLayout l1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        l1 = findViewById(R.id.vi);


        mPhoneText = (EditText) findViewById(R.id.UNo);
        mCodeText = (EditText) findViewById(R.id.phcode);

        mSendBtn = (Button) findViewById(R.id.gst);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(MainActivity.this);

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mBtn == 0) {
                    String phoneNum = mPhoneText.getText().toString();

                    phoneNum="+91"+phoneNum;
                    phoneNum.trim();
                    mPhoneText.setEnabled(false);
                    mSendBtn.setEnabled(false);
                    // mCodeLayout.setVisibility(View.VISIBLE);
                    mDialog.setMessage(" Sending Verification Code.....");
                    mDialog.show();


                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNum,
                            60,
                            TimeUnit.SECONDS,
                            MainActivity.this,
                            mCallBacks
                    );

                } else {
                    mSendBtn.setEnabled(false);
                    mDialog.setMessage(" Verifying Code....");
                    mDialog.show();
                    String verificationCode = mCodeText.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }

            }
        });

        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                mDialog.setMessage(" Verifying Code....");
                mDialog.show();
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(), " Verification Failed "+e, Toast.LENGTH_LONG).show();
                mSendBtn.setText(" Resend Code");
                mSendBtn.setEnabled(true);
                mBtn = 0;

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                mBtn = 1;
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(), " Verification Code Send", Toast.LENGTH_SHORT).show();
                l1.setVisibility(View.VISIBLE);

                // mCodeLayout.setVisibility(View.VISIBLE);
                mSendBtn.setText(" Verify Code");
                mSendBtn.setEnabled(true);

                // ...
            }
        };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            mDialog.dismiss();
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(getApplicationContext()," You are "+user.getPhoneNumber(),Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(getApplicationContext(), " Error Signing In!!! Retry ", Toast.LENGTH_SHORT).show();
                            mBtn = 0;
                            mDialog.dismiss();
                            mSendBtn.setText(" Send Verification Code");
                            mSendBtn.setEnabled(true);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }


}






