package com.example.sgi;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.sgi.R.*;

public class Register extends AppCompatActivity {
    TextView mName , mEmail , mEnrollment , mPassword , mLogintxt;
    Spinner sp_branch,sp_semester;
    Button mRegisterbtn;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressBar progressBar;
    String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_register);

        mName = (TextView)findViewById(id.user_fullName);
        mEmail = (TextView) findViewById(id.user_email_id);
        mEnrollment = (TextView)findViewById(id.user_enrollment_id);
        mPassword = (TextView) findViewById(id.user_password);
        mLogintxt = (TextView) findViewById(id.login_text);
        mRegisterbtn = (Button)findViewById(id.register_btn);

        sp_semester = (Spinner)findViewById(id.user_semester);
        sp_branch = (Spinner)findViewById(id.user_branch);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressBar = findViewById(id.progressbar_register);
        topicSubsciption();



        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),DashBoard.class));
            finish();
        }

    }

    private void topicSubsciption() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("weather");
        FirebaseMessaging.getInstance().subscribeToTopic("aman");
    }

    public void onClick(View view) {

    }

    public void goToLogin(View view) {
        startActivity(new Intent(getApplicationContext(),StudentActivity.class));
        finish();
    }

    public void registerUserToFirebase(View view) {


        final String fullname = mName.getText().toString();
        final String email = mEmail.getText().toString().trim();
        final String enrollment = mEnrollment.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        final String semester = (String) sp_semester.getSelectedItem();
        final String branch = (String) sp_branch.getSelectedItem();
        String branchName = enrollment.substring(4,6).toUpperCase();



        if (TextUtils.isEmpty(fullname)) {
            mName.setError("Name is Required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Email is Required");
            return;
        }
        if (TextUtils.isEmpty(enrollment)) {
            mEnrollment.setError("Enrollment Number is Required");
            return;
        }
        if (enrollment.length() != 12){
            mEnrollment.setError("Enrollment length must be 12 digits");
            return;
        }

        if (!enrollment.substring(0,4).contains("0186")){
            mEnrollment.setError("Please enter a valid enrollment");
            return;
        }




        /*if (!checkEnroll(enrollment)){
            mEnrollment.setError("Enrollment is not valid");
        }

         */





        if(sp_branch.getSelectedItem().equals("Select")){
            ((TextView)sp_branch.getSelectedView()).setError("Please select Branch first");
            return;
        }
        if(sp_semester.getSelectedItem().equals("Select")){
            ((TextView)sp_semester.getSelectedView()).setError("Please select Semester first");
            return;
        }
        if(TextUtils.isEmpty(password)){
            mPassword.setError("Password is Required");
            return;
        }
        if(password.length() < 6){
            mPassword.setError("Password length must be >= 6");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        //register the user to firebase
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Register.this,"User Created",Toast.LENGTH_SHORT).show();
                    userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                    DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
                    Map<String, String> user = new HashMap<>();
                    user.put("fName",fullname);
                    user.put("email",email);
                    user.put("enrollment",enrollment);
                    user.put("branch",branch);
                    user.put("semester",semester);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {

                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Register.this, "user Profile is created", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this,""+e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    });

                    startActivity(new Intent(getApplicationContext(),DashBoard.class));
                    finish();

                }
                else {
                    Toast.makeText(Register.this, "ERROR! "+ task.getException(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });




    }

    private boolean checkEnroll(String enrollment) {
        String branch = enrollment.substring(4,6).toLowerCase();
        String firstfour = enrollment.substring(0,4);
        if (firstfour.contains("0186")){
            return branch.contains("cs") || branch.contains("ce") || branch.contains("me") || branch.contains("it") || branch.contains("ex") || branch.contains("ec");
        }
        else {
            return false;
        }


    }
}


