package com.example.project_app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {

    private ImageView postImage;
    private TextView postDescription;
    private TextView postType;
    private TextView postTitle;
    private Spinner postRating;
    private Button deletePostButton;
    private Button editPostButton;

    private DatabaseReference clickPostRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private String databaseUserId;

    private String description;
    private String type;
    private String title;
    private String rating;
    private String image;
    private String postKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        postKey = getIntent().getExtras().get("PostKey").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        clickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey);

        postImage = findViewById(R.id.click_post_image);
        postDescription = findViewById(R.id.click_post_description);
        postType = findViewById(R.id.click_post_type);
        postTitle = findViewById(R.id.click_post_title);
        postRating = findViewById(R.id.click_post_rating);
        deletePostButton = findViewById(R.id.click_delete_post_button);
        editPostButton = findViewById(R.id.click_edit_post_button);

        deletePostButton.setVisibility(View.INVISIBLE);
        editPostButton.setVisibility(View.INVISIBLE);

        clickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    description = dataSnapshot.child("description").getValue().toString();
                    rating = dataSnapshot.child("rating").getValue().toString();
                    title = dataSnapshot.child("title").getValue().toString();
                    type = dataSnapshot.child("type").getValue().toString();
                    image = dataSnapshot.child("postimage").getValue().toString();
                    databaseUserId = dataSnapshot.child("uid").getValue().toString();

                    postDescription.setText(description);
                    postRating.setSelection(((ArrayAdapter) postRating.getAdapter()).getPosition(rating));
                    postTitle.setText(title);
                    postType.setText(type);

                    Picasso.get()
                            .load(image)
                            .resize(1200, 1000)
                            .placeholder(R.drawable.add_post_high)
                            .into(postImage);

                    if (currentUserId.equals(databaseUserId)) {
                        deletePostButton.setVisibility(View.VISIBLE);
                        editPostButton.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        deletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCurrentPost();
            }
        });

        editPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCurrentPost(description);
            }
        });
    }

    private void editCurrentPost(String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Post");

        final EditText inputField = new EditText(ClickPostActivity.this);
        inputField.setText(description);
        builder.setView(inputField);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clickPostRef.child("description").setValue(inputField.getText().toString());
                Toast.makeText(ClickPostActivity.this, "Post has been updated", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.color.colorPrimaryLight);

    }

    private void deleteCurrentPost() {
        clickPostRef.removeValue();
        Toast.makeText(this, "Your post has been deleted.", Toast.LENGTH_SHORT).show();
        sendUserToMainActivity();
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(ClickPostActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
