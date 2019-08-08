package com.libgdx.ahmed_fathy.socialapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.libgdx.ahmed_fathy.socialapp.R;

public class LogInActivity extends AppCompatActivity {
    EditText userMail , userPassword;
    ImageView userImage;
    Button LoginBtn;
    ProgressBar loadingProgress;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null){
            //user is already connected we need to redirect him to HomePage
            updateUI();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        userImage = findViewById(R.id.userImage);
        userPassword = findViewById(R.id.userPassword);
        userMail = findViewById(R.id.userMail);
        LoginBtn = findViewById(R.id.loginBtn);
        userImage = findViewById(R.id.userImage);
        loadingProgress = findViewById(R.id.loadingProgress_login);

        firebaseAuth = FirebaseAuth.getInstance();

        loadingProgress.setVisibility(View.INVISIBLE);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgress.setVisibility(View.VISIBLE);
                LoginBtn.setVisibility(View.INVISIBLE);
                final String mail = userMail.getText().toString();
                final String password = userPassword.getText().toString();

                if(mail.isEmpty() || password.isEmpty()){
                    showMessage("Some fields are missing !");
                    loadingProgress.setVisibility(View.INVISIBLE);
                    LoginBtn.setVisibility(View.VISIBLE);
                }else{
                    signIn(mail , password);
                }


            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(getApplicationContext() , RegisterActivity.class);
                startActivity(registerActivity);
                finish();
            }
        });
    }

    private void signIn(String mail, String password) {
        firebaseAuth.signInWithEmailAndPassword(mail , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    loadingProgress.setVisibility(View.INVISIBLE);
                    LoginBtn.setVisibility(View.VISIBLE);
                    updateUI();

                }else{

                    showMessage(task.getException().getMessage());
                    loadingProgress.setVisibility(View.INVISIBLE);
                    LoginBtn.setVisibility(View.VISIBLE);

                }

            }
        });
    }

    private void updateUI() {
        Intent intent = new Intent(LogInActivity.this , Home.class);
        startActivity(intent);
        finish();
    }

    private void showMessage(String s) {
        Toast.makeText(this, "" + s, Toast.LENGTH_SHORT).show();
    }
}
