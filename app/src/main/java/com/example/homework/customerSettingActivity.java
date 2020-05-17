package com.example.homework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class customerSettingActivity extends AppCompatActivity {
    private Button submit;
    private Button back;
    private EditText name;
    private EditText phone;
    private TextView userId;
//    private TextView model;

    private FirebaseAuth mAuth;
    private DatabaseReference dbref;

    private String tempUserId;
    private String mName;
    private String mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_setting);
        name = (EditText) findViewById(R.id.sName);
        phone = (EditText) findViewById(R.id.sPhone);
        userId = (TextView) findViewById(R.id.sUserId);
//        model = (TextView) findViewById(R.id.sModel);
        submit = (Button) findViewById(R.id.sSubmit);
        back = (Button) findViewById(R.id.sdmBack);

        mAuth = FirebaseAuth.getInstance();
        tempUserId = "User Id: " + mAuth.getCurrentUser().getUid();

        userId.setText(tempUserId);
        dbref = FirebaseDatabase.getInstance().getReference().child("Users").child("client").child(mAuth.getCurrentUser().getUid());
        getUserInfo();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
                Intent intent = new Intent(customerSettingActivity.this, CustomerMapActivity.class);
                startActivity(intent);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(customerSettingActivity.this, CustomerMapActivity.class);
//                intent.putExtra(EXTRA_TEXT, activityName);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getUserInfo() {
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        mName = (String) map.get("name");
                        name.setText(mName);
                    }
                    if (map.get("phone") != null) {
                        mPhone = (String) map.get("phone");
                        phone.setText(mPhone);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveUserInfo() {
        mName = name.getText().toString();
        mPhone = phone.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("name", mName);
        userInfo.put("phone", mPhone);

        dbref.updateChildren(userInfo);
    }
}
