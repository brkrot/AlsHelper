package com.example.alshelper.processingData;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.alshelper.R;
import com.example.alshelper.processingData.Form;

public class TestResults extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_results);
    }

    public void createHelpingSystem (View v){
        Intent intent = new Intent(this, Form.class);
        startActivity(intent);
    }
}