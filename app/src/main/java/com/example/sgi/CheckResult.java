package com.example.sgi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class CheckResult extends AppCompatActivity {
    TextView resulttxt;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String userId = firebaseAuth.getCurrentUser().getUid();
    DocumentReference docRef = firestore.document("users/"+userId);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_result);
        resulttxt = findViewById(R.id.resid);




        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String enroll = documentSnapshot.getString("enrollment");
                    DocumentReference doc = firestore.document("res/"+enroll);
                    doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){

                                Map<String, Object> data = documentSnapshot.getData();
                                for (Map.Entry<String , Object> pair : data.entrySet()){
                                    resulttxt.setText(resulttxt.getText() +"\n" + pair.getKey()   + ":" +pair.getValue());
                                }



                            }
                            else{
                                Toast.makeText(CheckResult.this,"RESULT NOT FOUND",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CheckResult.this,"ERROR!" + e.toString(),Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                else{
                    Toast.makeText(CheckResult.this,"not found",Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CheckResult.this,"ERROR!" + e.toString(),Toast.LENGTH_SHORT).show();

            }
        });


    }
}
