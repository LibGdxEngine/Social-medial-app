package com.libgdx.ahmed_fathy.socialapp.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.libgdx.ahmed_fathy.socialapp.Fragments.HomeFragment;
import com.libgdx.ahmed_fathy.socialapp.Fragments.ProfileFragment;
import com.libgdx.ahmed_fathy.socialapp.Fragments.SettingsFragment;
import com.libgdx.ahmed_fathy.socialapp.Models.Post;
import com.libgdx.ahmed_fathy.socialapp.R;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static int PReqCode = 1;
    static int REQUEST_CODE = 1;
    Uri pickedImageUri;

    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    Dialog popAddPost;

    ImageView popUserPhoto , popPostImage , popAddBtn;
    EditText popPostTitle , popPostDescription;

    ProgressBar popProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Load the HomeFragment in the beginning
        getSupportFragmentManager().beginTransaction().replace(R.id.container , new HomeFragment()).commit();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        //init popUp
        initPop();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                popAddPost.show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //TODO: THAT WHAT I DID
        updateNavHeader();



    }

    private void initPop(){
        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT , Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        //init pop widgets
        popUserPhoto = popAddPost.findViewById(R.id.popup_user_photo);
        popPostTitle = popAddPost.findViewById(R.id.popup_post_title);
        popPostDescription = popAddPost.findViewById(R.id.popup_post_description);
        popPostImage = popAddPost.findViewById(R.id.pop_post_image);
        popAddBtn = popAddPost.findViewById(R.id.pop_add_post_btn);
        popProgressBar = popAddPost.findViewById(R.id.pop_progressbar);
        //load current user here and put his image into pop_user_image
        Glide.with(Home.this).load(currentUser.getPhotoUrl()).into(popUserPhoto);

        popPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT > 22){
                    checkAndRequestForPermession();
                }else{
                    openGallery();
                }
            }
        });

        popAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popAddBtn.setVisibility(View.INVISIBLE);
                popProgressBar.setVisibility(View.VISIBLE);

                if( popPostTitle.getText().toString().isEmpty() || popPostDescription.getText().toString().isEmpty() || pickedImageUri == null){
                    showMessage("Some fields are missing !");
                    popAddBtn.setVisibility(View.VISIBLE);
                    popProgressBar.setVisibility(View.INVISIBLE);
                }else{
                    //TODO: Create post object
                    //first we need to upload the post image
                    //access firebase storage
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
                    final StorageReference imageFilePath = storageReference.child(pickedImageUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownloadLink = uri.toString();
                                    Post post = new Post(popPostTitle.getText().toString(),
                                                        popPostDescription.getText().toString(),
                                                        imageDownloadLink,
                                                        currentUser.getUid(),
                                                        currentUser.getPhotoUrl().toString());
                                    //add post to firebase database
                                    addPostToFirebase(post);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //some thing goes wrong while uploading post
                                    showMessage(e.getMessage());
                                    popProgressBar.setVisibility(View.INVISIBLE);
                                    popAddBtn.setVisibility(View.VISIBLE);

                                }
                            });

                        }
                    });
                }

            }
        });





    }

    private void addPostToFirebase(Post post) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Posts").push();

        //get post unique ID & update post key
        String key = myRef.getKey();
        post.setPostKey(key);

        //add post data to firebase database
        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Post added successfully ");
                popProgressBar.setVisibility(View.INVISIBLE);
                popAddBtn.setVisibility(View.VISIBLE);
                popPostTitle.setText("");
                popPostDescription.setText("");
                popPostImage.setBackground(new ColorDrawable(Color.parseColor("#2d2d2d")));
                popPostImage.setColorFilter(getResources().getColor(R.color.nullColor));
                popPostImage = new ImageView(Home.this);
                pickedImageUri = null;
                popAddPost.dismiss();
            }
        });

    }

    private void showMessage(String s) {
        Toast.makeText(this, "" + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportActionBar().setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.container , new HomeFragment()).commit();

        } else if (id == R.id.nav_profile) {
            getSupportActionBar().setTitle("Profile");
            getSupportFragmentManager().beginTransaction().replace(R.id.container , new ProfileFragment()).commit();

        } else if (id == R.id.nav_settings) {
            getSupportActionBar().setTitle("Settings");
            getSupportFragmentManager().beginTransaction().replace(R.id.container , new SettingsFragment()).commit();

        } else if (id == R.id.nav_log_out) {

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Home.this , LogInActivity.class);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_user_name);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
        navUserName.setText(currentUser.getDisplayName());
        navUserMail.setText(currentUser.getEmail());
        ImageView userPhoto = headerView.findViewById(R.id.nav_user_photo);
        //now we will use Glide to load user photo
        Glide.with(this).load(currentUser.getPhotoUrl()).into(userPhoto);
    }

    private void checkAndRequestForPermession() {

        if(ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(Home.this , Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Please accept for required permission!", Toast.LENGTH_SHORT).show();
            }else{
                ActivityCompat.requestPermissions(Home.this ,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        }else{
            openGallery();
        }
    }

    private void openGallery() {
        //TODO: Open galleryIntent intent and wait for user to pick an image
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null){
            // The user successfully picked an image
            pickedImageUri = data.getData();
            popPostImage.setImageURI(pickedImageUri);
        }

    }
}
