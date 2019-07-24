package com.example.imageuploadandretrivefirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.imageuploadandretrivefirebase.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int REQUESTED_CODE_FOR_IMAGE = 1;
    private String imageUrl="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);






        init();


        binding.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ProfileActivity.class));
            }
        });

        binding.addImageL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent,REQUESTED_CODE_FOR_IMAGE);
            }
        });


        binding.updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name  = binding.nameET.getText().toString();
                String email = binding.emailET.getText().toString();
                if(name.isEmpty() || email.isEmpty()){
                    Toast.makeText(MainActivity.this, "Input name and email", Toast.LENGTH_SHORT).show();
                }
                else {
                    saveToDB(name,email);
                }
            }
        });






    }

    private void saveToDB(String name, String email) {
        DatabaseReference userRef = databaseReference.child("users").child("1");
        User user = new User(name,email,imageUrl);
        userRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    binding.nameET.setText("");
                    binding.emailET.setText("");
                }
            }
        });

    }

    private void init() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUESTED_CODE_FOR_IMAGE && resultCode ==RESULT_OK){
            if(data!=null){
                Uri uri = data.getData();
                binding.newImageIV.setImageURI(uri);
                uploadImageToStorage(uri);
            }
        }
    }

    private void uploadImageToStorage(Uri uri) {
        final StorageReference profileImageRef = storageReference.child(String.valueOf(System.currentTimeMillis()));
        profileImageRef.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                             imageUrl = uri.toString();
                            Toast.makeText(MainActivity.this, "Successssss", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        });
    }
}
