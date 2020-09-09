package com.example.sgi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DepartmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cse:
                startActivity(new Intent(getApplicationContext(),CseActivity.class));
                break;
            case R.id.ce:
                startActivity(new Intent(getApplicationContext(),CivilActivity.class));
                break;
            case R.id.me:
                startActivity(new Intent(getApplicationContext(),MechanicalActivity.class));
                break;
            case R.id.as:
                startActivity(new Intent(getApplicationContext(),AppliedScienceActivity.class));
                break;
        }


    }
}
