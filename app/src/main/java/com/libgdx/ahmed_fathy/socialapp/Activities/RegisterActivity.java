package com.libgdx.ahmed_fathy.socialapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.libgdx.ahmed_fathy.socialapp.R;

public class RegisterActivity extends AppCompatActivity {

    ImageView imgUserPhoto;
    static int PReqCode = 1;
    static int REQUEST_CODE = 1;
    Uri pickedImageUri;
    EditText userName ,userEmail,userPassword, userCPassword;
    ProgressBar loadingProgress;
    Button regBtn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        imgUserPhoto = findViewById(R.id.regUserPhoto);
        userName = findViewById(R.id.regName);
        userEmail = findViewById(R.id.regEmail);
        userPassword = findViewById(R.id.regPassword);
        userCPassword = findViewById(R.id.regCPassword);
        loadingProgress = findViewById(R.id.progressBar);
        regBtn = findViewById(R.id.regBtn);

        firebaseAuth = FirebaseAuth.getInstance();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);
                String name = userName.getText().toString();
                String email = userEmail.getText().toString();
                String password = userPassword.getText().toString();
                String CPassword = userCPassword.getText().toString();

                if(name.isEmpty() || email.isEmpty() || password.isEmpty() || !CPassword.equals(password) ){
                    showMessage("Some thing is missing !");
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }else{
                    //every thing is OK and we can start registering user account
                    // createUserAccount method will try to create account if email is valid
                    createUserAcoount(name, email , password);
                }


            }
        });

        loadingProgress.setVisibility(View.INVISIBLE);
        imgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT > 22){
                    checkAndRequestForPermession();
                }else{
                    openGallery();
                }
            }
        });





    }

    private void createUserAcoount(final String name, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email , password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //user account created successfully
                            showMessage("Account Created");
                            //after we created user account we need to update his profile photo & name
                            updateUserInfo( name , pickedImageUri , firebaseAuth.getCurrentUser());
                        }else{
                            showMessage("Account creation failed ! " + task.getException().getMessage());
                            regBtn.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }
    //update user pphoto & name
    private void updateUserInfo(final String name, Uri pickedImageUri, final FirebaseUser currentUser) {
        //first we need to uplode user photo to firebase storage & get its URL
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("user_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImageUri.getLastPathSegment());
        imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //image uploaded successfully
                //now we can get our image url
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //uri contains user image
                        UserProfileChangeRequest profileUpdate =  new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            //user info updated
                                            showMessage("Register complete");
                                            //update UI according to successful registration
                                            updateUI();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }

    private void updateUI() {
        Intent intent = new Intent(RegisterActivity.this , Home.class);
        startActivity(intent);
        finish();
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), "" + s , Toast.LENGTH_SHORT).show();
    }

    private void openGallery() {
        //TODO: Open galleryIntent intent and wait for user to pick an image
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUEST_CODE);
    }

    private void checkAndRequestForPermession() {

        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this , Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Please accept for required permission!", Toast.LENGTH_SHORT).show();
            }else{
                ActivityCompat.requestPermissions(RegisterActivity.this ,
                                                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                                        PReqCode);
            }

        }else{
            openGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null){
            // The user successfully picked an image
            pickedImageUri = data.getData();
            imgUserPhoto.setImageURI(pickedImageUri);
        }

    }
}
