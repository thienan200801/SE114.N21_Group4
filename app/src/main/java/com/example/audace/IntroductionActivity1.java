package com.example.audace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class IntroductionActivity1 extends AppCompatActivity {
    private Button bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction1);
        bt=findViewById(R.id.bt);
        bt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent t=new Intent(IntroductionActivity1.this,IntroductionActivity2.class);
                startActivity(t);
            }
        });
    }
}