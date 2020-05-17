package com.example.homework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeliverymanLoginActivity extends AppCompatActivity {
    private EditText dmEmail;
    private EditText dmPassword;
    private Button dmRegister;
    private Button dmLogin;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliveryman_login);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(DeliverymanLoginActivity.this, DeliveryMapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        dmEmail = (EditText) findViewById(R.id.cEmail);
        dmPassword = (EditText) findViewById(R.id.cPassword);

        dmRegister = (Button) findViewById(R.id.cRegister);
        dmLogin = (Button) findViewById(R.id.cLogin);


        dmRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = dmEmail.getText().toString();
                final String password = dmPassword.getText().toString();

//                if (email.equals("") || password.equals("")) {
//                    Toast.makeText(DeliverymanLoginActivity.this, "Email or password cannot be null.", Toast.LENGTH_SHORT).show();
//                }

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(DeliverymanLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(DeliverymanLoginActivity.this, "Sign up error", Toast.LENGTH_SHORT).show();
                        } else {
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("deliveryman").child(user_id);
                            current_user_db.setValue(true);
                        }
                    }
                });
            }
        });

        dmLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = dmEmail.getText().toString();
                final String password = dmPassword.getText().toString();

//                if (email.equals("") || password.equals("")) {
//                    Toast.makeText(DeliverymanLoginActivity.this, "Email or password cannot be null.", Toast.LENGTH_SHORT).show();
//                }
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(DeliverymanLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(DeliverymanLoginActivity.this, "User name/Password is not correct", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
