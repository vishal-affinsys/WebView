package com.example.webviewjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openCamera(View view){
        Intent i = new Intent(this, CameraActivity.class);
        startActivity(i);
    }

    public void getLocation(View view){
        Intent i = new Intent(this, Location.class);
        startActivity(i);
    }

    public void uploadFile(View view){
        Intent i = new Intent(this, FileUpload.class);
        startActivity(i);
    }


}