package com.example.audace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class IntroductionActivity2 extends AppCompatActivity {
    private Button bt2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction2);
        bt2=findViewById(R.id.bt2);
        bt2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent t=new Intent(IntroductionActivity2.this,IntroductionActivity3.class);
                startActivity(t);
            }
        });
    }
}