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
        vch.setDieukien("Đơn hàng đạt 200k");
        vch.setLuuy("Áp dụng cho đơn trả thẻ");
        vch.setMathang("Túi");
        vch.setThoigian("24/11/2023");
        vch.setSaleoff("Sale off 50%");
        vchList.add(vch);
        Voucher vch1 = new Voucher();
        vch1.setDieukien("Đơn hàng đạt 200k");
        vch1.setLuuy("Áp dụng cho đơn trả thẻ");
        vch1.setMathang("Túi");
        vch1.setThoigian("24/11/2023");
        vch1.setSaleoff("Sale off 50%");
        vchList.add(vch1);


    }
}