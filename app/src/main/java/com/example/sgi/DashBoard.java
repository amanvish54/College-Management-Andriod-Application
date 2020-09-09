package com.example.sgi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class DashBoard extends AppCompatActivity {
    private static final int TAKE_IMAGE_CODE = 1023;
    CircleImageView profileimage;
    TextView menroll, memail, mbranch, msemester, fullnametxt;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String userID = (Objects.requireNonNull(firebaseAuth.getCurrentUser())).getUid();
    private DocumentReference docRef = firestore.document("users/" + userID);
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        fullnametxt = findViewById(R.id.full_name_dashboard);
        menroll = findViewById(R.id.enrollment_dashboard);
        memail = findViewById(R.id.email_dashboard);
        mbranch = findViewById(R.id.branch_dashboard);
        msemester = findViewById(R.id.semester_dashboard);
        profileimage = findViewById(R.id.profileImage);
        StorageReference profileRef = storageReference.child("users/"+ userID+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileimage);
            }
        });

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String FullName = documentSnapshot.getString("fName");
                    fullnametxt.setText(FullName);

                    String enroll = documentSnapshot.getString("enrollment");
                    menroll.setText(enroll);
                    memail.setText(documentSnapshot.getString("email"));
                    mbranch.setText(documentSnapshot.getString("branch"));
                    msemester.setText(documentSnapshot.getString("semester"));
                }
            }
        });


    }

    public void pickedImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 555);
            } catch (Exception e) {

            }
        } else {
            pickImage();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 555 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            pickImage();
        }else {
                pickedImage();
        }
    }

    private void pickImage() {
        CropImage.startPickImageActivity(this);
    }
    private void cropImageRequest(Uri uri){
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    public void logout(View view) {
        firebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        finish();
    }


    public void checkresult(View view) {
        startActivity(new Intent(getApplicationContext(),CheckResult.class));
        finish();
    }


    public void uploadProfile(View view) {
        pickedImage();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageuri = CropImage.getPickImageResultUri(this, data);
            cropImageRequest(imageuri);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                try {
                    assert result != null;
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),result.getUri());
                    uploadImageToFirebase(getImageUri(this,bitmap));
                    profileimage.setImageBitmap(bitmap);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private Uri getImageUri(DashBoard dashBoard, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"Profile",null);
        return Uri.parse(path);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void uploadImageToFirebase(Uri imageUri) {
        final StorageReference fileRef = storageReference.child("users/"+ userID+"/profile.jpg");
        final StorageReference imageFilePath = fileRef.child(Objects.requireNonNull(imageUri.getLastPathSegment()));
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {


                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(fullnametxt.getText().toString())
                                .setPhotoUri(Uri.parse(imageFilePath.toString()))
                                .build();

                        assert user != null;
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Picasso.get().load(uri).into(profileimage);


                                            Toast.makeText(DashBoard.this,"Successfully profile updated",Toast.LENGTH_SHORT).show();


                                        }
                                    }
                                });

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DashBoard.this,"Falied"+ e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goToNewsFeed(View view) {
        startActivity(new Intent(getApplicationContext(),NewsFeedActivity.class));
        finish();

    }
}