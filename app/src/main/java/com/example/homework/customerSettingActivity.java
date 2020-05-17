package com.example.homework;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class customerSettingActivity extends AppCompatActivity {
    private Button submit;
    private Button back;
    private EditText name;
    private EditText phone;
    private TextView userId;
    private ImageView mIcon;
    private Uri result;
    private FirebaseAuth mAuth;
    private DatabaseReference dbref;

    private String tempUserId;
    private String mName;
    private String mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_setting);
        mAuth = FirebaseAuth.getInstance();

        name = (EditText) findViewById(R.id.sName);
        phone = (EditText) findViewById(R.id.sPhone);
        userId = (TextView) findViewById(R.id.sUserId);
//        model = (TextView) findViewById(R.id.sModel);
        submit = (Button) findViewById(R.id.sSubmit);
        back = (Button) findViewById(R.id.sdmBack);
        mIcon = (ImageView) findViewById(R.id.sImage);
        tempUserId = mAuth.getCurrentUser().getUid();


        String tmp = "User Id: " + mAuth.getCurrentUser().getUid();

        userId.setText(tmp);
        dbref = FirebaseDatabase.getInstance().getReference().child("Users").child("client").child(tempUserId);
        getUserInfo();


        mIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

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
                    if (map.get("iconImageUrl") != null) {
                        String mProfileUrl = (String) map.get("iconImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileUrl).into(mIcon);
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
        if (result != null) {
            Log.d("tempUserId", tempUserId);
            StorageReference imagePath = FirebaseStorage.getInstance().getReference().child("images").child("icon_images").child(tempUserId);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Compress Image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = imagePath.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                    while(!uri.isComplete());
                    Uri downloadUrl = uri.getResult();

                    Map newImage = new HashMap();
                    newImage.put("iconImageUrl", downloadUrl.toString());
                    dbref.updateChildren(newImage);

                    finish();
                    return;
                }
            });
//            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
//                    while (!uri.isComplete()) ;
//                    Uri downloadUrl = uri.getResult();
//                    Map newImage = new HashMap();
//                    newImage.put("iconImageUrl", downloadUrl.toString());
//                    dbref.updateChildren(newImage);
//                }
//            });

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });

        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            result = data.getData();
            mIcon.setImageURI(result);
        }
    }
}