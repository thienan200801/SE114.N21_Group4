package com.example.audace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class IntroductionActivity3 extends AppCompatActivity {
    private Button bt3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction3);
        bt3=findViewById(R.id.bt3);
        bt3.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.apply();
                Intent t=new Intent(IntroductionActivity3.this,LoginScreen.class);
                startActivity(t);
            }
        });
    }
}