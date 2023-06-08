package com.example.audace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.gioithieu3edited);*/
        Intent intent = new Intent(this,VoucherScreen.class);
        startActivity(intent);
    }
}