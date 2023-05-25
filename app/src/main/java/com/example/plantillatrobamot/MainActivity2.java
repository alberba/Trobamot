package com.example.plantillatrobamot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity2 extends AppCompatActivity {
    private boolean w;
    private String palabra;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent=this.getIntent();
        w=intent.getBooleanExtra("VICTORIA",false);
        palabra=intent.getStringExtra("PALABRA");

    }
}