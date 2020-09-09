package com.example.sgi;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 1;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    TextView name_navigation_Drawer;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    FirebaseUser currentuser = firebaseAuth.getCurrentUser();

    ViewFlipper v_flipper;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("RestrictedApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        name_navigation_Drawer = findViewById(R.id.name_navigation_header);
       drawerLayout = findViewById(R.id.drawer_menu);
        toolbar = findViewById(R.id.toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        navigationView = findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.dashboard:
                        if (firebaseAuth.getCurrentUser() != null){
                            startActivity(new Intent(getApplicationContext(),DashBoard.class));
                        }
                        else{
                            Toast.makeText(HomeActivity.this,"Please Login/SignUp before going to Dashboard",Toast.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.login:
                        startActivity(new Intent(getApplicationContext(),StudentActivity.class));
                        break;
                    case R.id.signup:
                        startActivity(new Intent(getApplicationContext(),Register.class));
                        break;
                    case R.id.exit:
                        finish();
                        break;
                }
                return false;
            }
        });




        if (firebaseAuth.getCurrentUser()!=null){
            updateNavHeader();
        }








    int[] image = {R.drawable.clg_1,R.drawable.clg_2,R.drawable.clg_5,R.drawable.clg_6};
    v_flipper = findViewById(R.id.v_flipper);
    for (int img : image){
        flipperimages(img);
    }
    }

    private void alertdialog() {
            AlertDialog.Builder dialog=new AlertDialog.Builder(this);
            dialog.setMessage("Thankyou for using Our application");
            dialog.setTitle("SGI");
            dialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            finish();
                        }
                    });

        }



    public  void flipperimages(int image){
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(image);

        v_flipper.addView(imageView);
        v_flipper.setFlipInterval(4000);
        v_flipper.setAutoStart(true);

        //animation
        v_flipper.setInAnimation(this, android.R.anim.fade_in);
        v_flipper.setOutAnimation(this, android.R.anim.fade_out);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.visit_id:
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(HomeActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                        new AlertDialog.Builder(this)
                                .setTitle("Required Location Permission")
                                .setMessage("You have to give the permission to access this feature")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(HomeActivity.this,
                                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                                MY_PERMISSIONS_REQUEST_READ_LOCATION);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create()
                                .show();
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(HomeActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_READ_LOCATION);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                    Intent intent = new Intent(HomeActivity.this,VisitActivity.class);
                    startActivity(intent);
                    break;
                }


            case R.id.student_id:
                Intent istudent = new Intent(HomeActivity.this,StudentActivity.class);
                startActivity(istudent);
                break;
            case R.id.fee_payment_id:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.sirtbhopal.ac.in/onlinepayment")));

                break;
            case R.id.contact_id:
                startActivity(new Intent(getApplicationContext(),ContactActivity.class));

                break;

            case R.id.academic_id:
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(HomeActivity.this, NewsFeedActivity.class));
                }
                else {
                    Toast.makeText(getApplicationContext(),"First Login to See News feed",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.department_id:
                startActivity(new Intent(HomeActivity.this,DepartmentActivity.class));

                break;

            case R.id.about_us_id:
                startActivity(new Intent(getApplicationContext(),Gallery.class));
                break;
            case R.id.fests_id:
                startActivity(new Intent(getApplicationContext(),ClubActivity.class));
                break;

            case R.id.facilities_id:
                startActivity(new Intent(getApplicationContext(),FacilityActivity.class));
                break;




           // case R.id.facilities_id:
                //startActivity(new Intent(getApplicationContext(),FacilityActivity.class));
                //finish();
                //break;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void updateNavHeader(){
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        final CircleImageView imageView = view.findViewById(R.id.userImage_navigation_header);
        StorageReference profileRef = FirebaseStorage.getInstance().getReference().child("users/"+firebaseAuth.getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageView);
            }
        });
        TextView username = view.findViewById(R.id.name_navigation_header);
        username.setText(currentuser.getEmail());

    }


    @Override
    public  void  onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

        builder.setMessage("Do you Really want to exit?");

        builder.setTitle("Alert!");

        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

}
