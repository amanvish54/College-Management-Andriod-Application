package com.example.sgi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.util.Objects.requireNonNull;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class NewsFeedActivity extends AppCompatActivity {
    private static int PReqCode = 2;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    FirebaseUser user  = firebaseAuth.getCurrentUser();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String userID = requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    private DocumentReference docRef = firestore.document("users/" + userID);

    Dialog popAddPost;
    CircleImageView popup_user_image;
    ImageView popup_post_image;
    Button popup_add_postbtn;
    TextView popop_post_description;
    ProgressBar popup_progressBar;
    String userId = requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    Uri pickedImageUri = null;
    Uri photoUri;
    String userName;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        FloatingActionButton fab = findViewById(R.id.add_post);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popAddPost.show();
            }
        });
        initiatePopup();
        getSupportFragmentManager().beginTransaction().replace(R.id.container_news,new NewsFeedFragment()).commit();

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initiatePopup() {
        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        requireNonNull(popAddPost.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;


        //popup post values
        popup_user_image = popAddPost.findViewById(R.id.popup_image_user);
        popop_post_description = popAddPost.findViewById(R.id.popup_post_description);
        popup_post_image = popAddPost.findViewById(R.id.popup_post_image);
        popup_add_postbtn = popAddPost.findViewById(R.id.popup_add_post);
        popup_progressBar = popAddPost.findViewById(R.id.popup_post_progressbar);
        popup_post_image.setImageDrawable(null);
        popup_post_image.setImageResource(0);

        popup_post_image.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(NewsFeedActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(NewsFeedActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Toast.makeText(NewsFeedActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
                    } else {
                        ActivityCompat.requestPermissions(NewsFeedActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PReqCode);
                    }
                }
                else {
                  Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,1000);

                }
            }



        });



        //set image of user to popup
        StorageReference profileRef = storageReference.child("users/"+userId+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(popup_user_image);
                photoUri = uri;
            }
        });






    }

    public void uploadPost(View view) {
        popup_progressBar.setVisibility(View.VISIBLE);
        popup_add_postbtn.setVisibility(View.INVISIBLE);


        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userName = documentSnapshot.getString("fName");


            }
        });
        if (!popop_post_description.getText().toString().isEmpty()
                && pickedImageUri == null ){
            Post post = new Post(user.getDisplayName(),
                    popop_post_description.getText().toString(),
                    user.getUid(),
                    photoUri.toString());
            addPost(post);
        }
        else if (!popop_post_description.getText().toString().isEmpty()
                && pickedImageUri != null ) {

            //everything is okey no empty or null value
            // TODO Create Post Object and add it to firebase database
            // first we need to upload post Image
            // access firebase storage


            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("post_images");
            final StorageReference imageFilePath = storageReference.child(requireNonNull(pickedImageUri.getLastPathSegment()));
            imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageDownlaodLink = uri.toString();
                            // create post Object
                            Post post = new Post(popop_post_description.getText().toString(),
                                    imageDownlaodLink,
                                    user.getUid(),
                                    user.getDisplayName(),
                                    photoUri.toString());

                            // Add post to firebase database

                            addPost(post);



                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // something goes wrong uploading picture

                            showMessage(e.getMessage());
                            popup_progressBar.setVisibility(View.INVISIBLE);
                            popup_add_postbtn.setVisibility(View.VISIBLE);



                        }
                    });


                }
            });








        }
        else {
            showMessage("Please verify all input fields and choose Post Image") ;
            popup_add_postbtn.setVisibility(View.VISIBLE);
            popup_progressBar.setVisibility(View.INVISIBLE);

        }
    }




    private void addPost(Post post) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Posts").push();

        String key = myRef.getKey();
        post.setPostKey(key);

        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                showMessage("Post added Successfullly");
                popup_progressBar.setVisibility(View.INVISIBLE);
                popup_add_postbtn.setVisibility(View.VISIBLE);
                popop_post_description.setText(null);
                popup_post_image.setImageResource(0);
                popup_post_image.setImageDrawable(null);
                pickedImageUri = null;

                popAddPost.dismiss();

            }
        });








    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == Activity.RESULT_OK){



              pickedImageUri = requireNonNull(data).getData();
               popup_post_image.setImageURI(pickedImageUri);


        }

    }

}
