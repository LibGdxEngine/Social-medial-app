package com.libgdx.ahmed_fathy.socialapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.libgdx.ahmed_fathy.socialapp.Adapters.CommentAdapter;
import com.libgdx.ahmed_fathy.socialapp.Models.Comment;
import com.libgdx.ahmed_fathy.socialapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostDetails extends AppCompatActivity {
    ImageView postDetailsPostImage , postDetailsCommenterImage, postDetailsPostPublisher;
    TextView postDetailsPostTitle , postDetailsPostDescription , postDetailsPostDate;
    EditText postDetailsComment;
    Button postDetailsCommentBtn;
    RecyclerView commentsRecycler;
    CommentAdapter commentAdapter;
    List<Comment> commentList;
    String postKey;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        //set status bar to transparent
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS , WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();


        commentsRecycler = findViewById(R.id.postDetailsRecyclerComments);
        postDetailsPostImage = findViewById(R.id.post_details_post_image);
        postDetailsCommenterImage = findViewById(R.id.post_details_commenter_image);
        postDetailsPostPublisher = findViewById(R.id.post_details_post_publisher);
        postDetailsPostTitle = findViewById(R.id.post_details_title);
        postDetailsPostDescription = findViewById(R.id.post_details_description);
        postDetailsPostDate = findViewById(R.id.post_details_date);
        postDetailsComment = findViewById(R.id.post_details_comment_text);
        postDetailsCommentBtn = findViewById(R.id.post_details_add_comment_btn);



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();




        //add comment button click listener
        postDetailsCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDetailsCommentBtn.setEnabled(false);

                DatabaseReference databaseReference = firebaseDatabase.getReference("Comments").child(postKey).push();
                String comment_content = postDetailsComment.getText().toString();
                String userID = firebaseUser.getUid();
                String userName = firebaseUser.getDisplayName();
                String userImage = firebaseUser.getPhotoUrl().toString();

                Comment comment = new Comment(comment_content , userID , userImage , userName);
                databaseReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //comment added successfully
                        postDetailsCommentBtn.setEnabled(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostDetails.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                postDetailsComment.setText("");
            }
        });

        //now we need to get Post data that well appear in these views
        final String postImage = getIntent().getExtras().getString("postImage");
        Glide.with(this).load(postImage).into(postDetailsPostImage);
        String postTitle = getIntent().getExtras().getString("title");
        postDetailsPostTitle.setText(postTitle);
        String publisherImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(publisherImage).into(postDetailsPostPublisher);
        String postDescription = getIntent().getExtras().getString("description");
        postDetailsPostDescription.setText(postDescription);

        //set comment userImage
        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(postDetailsCommenterImage);

        //get post ID

        postKey = getIntent().getExtras().getString("postKey");

        String postDate = timeStampToString(getIntent().getExtras().getLong("postDate"));
        postDetailsPostDate.setText(postDate);

        //open post image in a full view
        postDetailsPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fullView = new Intent(PostDetails.this , FullImageView.class);
                fullView.putExtra("postImage" , postImage);
                startActivity(fullView);
            }
        });


        initRVComments();
    }

    //initialize the recyclerView of comments
    private void initRVComments() {
        DatabaseReference databaseReference = firebaseDatabase.getReference("Comments").child(postKey);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList = new ArrayList<>();
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    Comment comment = snap.getValue(Comment.class);
                    commentList.add(comment);
                }

                commentAdapter = new CommentAdapter(getApplicationContext() , commentList);
                commentsRecycler.setHasFixedSize(true);
                commentsRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                commentsRecycler.setAdapter(commentAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String timeStampToString(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy" , calendar).toString();
        return date;
    }

}
