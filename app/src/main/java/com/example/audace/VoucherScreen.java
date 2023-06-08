package com.example.audace;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VoucherScreen extends AppCompatActivity {

    private ArrayList<Voucher> vchList = new ArrayList<>();
    private VoucherAdapter voucherAdapter;


    private RecyclerView vchRV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voucher);


        vchRV = findViewById(R.id.voucherRecyclerView);
        vchRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        voucherAdapter = new VoucherAdapter(this,vchList);
        setupData();
        vchRV.setAdapter(voucherAdapter);

    }
    private void setupData(){
        Voucher vch = new Voucher();
        vch.setDieukien("");
        vch.setLuuy("");
        vch.setMathang("");
        vch.setThoigian("");
        vchList.add(vch);

    }
}