package com.example.sgi;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Main3Activity extends AppCompatActivity {
   // final ViewFlipper viewFlipper = findViewById(R.id.viewflipper_image);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Button button = findViewById(R.id.select_photos_btn);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void selectImage(View view) {
        if (ActivityCompat.checkSelfPermission(Main3Activity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Main3Activity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setType("image/*");
        startActivityForResult(intent,1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {


            final List<Bitmap> bitmaps = new ArrayList<>();

            assert data != null;
            ClipData clipData = data.getClipData();

            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        bitmaps.add(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Uri imageUri = data.getData();
                try {
                    assert imageUri != null;
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmaps.add(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

            int count=0;
            for (Bitmap bitmap : bitmaps){
                count = count+1;
            }
            Toast.makeText(getApplicationContext(),"total images "+ bitmaps.size(),Toast.LENGTH_SHORT).show();

            ViewPager viewPager = findViewById(R.id.view_pager1);
            ViewPagerAdapter adapter = new ViewPagerAdapter(this,bitmaps);
            viewPager.setAdapter(adapter);







       /*  new Thread(new Runnable() {
                @Override
                public void run() {

                    for (final Bitmap bitmap : bitmaps) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                ImageView img = new ImageView(Main3Activity.this);
                                img.setImageBitmap(bitmap);
                                viewFlipper.addView(img);
                                viewFlipper.setFlipInterval(1000);
                                viewFlipper.setAutoStart(true);

                                viewFlipper.setInAnimation(Main3Activity.this, android.R.anim.slide_in_left);
                                viewFlipper.setOutAnimation(Main3Activity.this, android.R.anim.slide_out_right);



                               // imageView.setImageBitmap(bitmap);
                            }
                        });

                        try {
                            Thread.sleep(1500);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        */







        }

    }


}
