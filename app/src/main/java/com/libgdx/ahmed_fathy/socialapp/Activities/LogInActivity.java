package com.libgdx.ahmed_fathy.socialapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.libgdx.ahmed_fathy.socialapp.R;

import java.util.Arrays;


public class LogInActivity extends AppCompatActivity {
    EditText userMail , userPassword;
    ImageView userImage;
    Button LoginBtn;
    ProgressBar loadingProgress;
    FirebaseAuth firebaseAuth;
    LoginButton loginButton;
    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    boolean isLoggedIn;

    AppEventsLogger appEventsLogger;
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(isLoggedIn){
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
            updateUI();
        }
        if(user != null){
            //user is already connected we need to redirect him to HomePage
            updateUI();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.setApplicationId("1322442707932144");
        FacebookSdk.sdkInitialize(this);

        setContentView(R.layout.activity_log_in);
        appEventsLogger.activateApp(getApplication());

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Toast.makeText(LogInActivity.this, "sucess", Toast.LENGTH_SHORT).show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(LogInActivity.this, "cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(LogInActivity.this, "Error " + exception.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });

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
    private void handleFacebookAccessToken(AccessToken token) {
       // Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LogInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI();
                        }

                        // ...
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showMessage(String s) {
        Toast.makeText(this, "" + s, Toast.LENGTH_SHORT).show();
    }
}
