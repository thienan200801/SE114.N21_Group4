package com.example.audace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import com.example.audace.adapter.CheckoutItemDetailAdapter;
import com.example.audace.adapter.ColorAdapter;
import com.example.audace.adapter.SizeAdapter;
import com.example.audace.model.CheckoutItemDetails;
import com.example.audace.model.ColorObject;
import com.example.audace.model.SizeObject;

import java.util.ArrayList;

public class Checkout extends AppCompatActivity {
    private TextView nameofproduct, price, size, quantity, color, imgUrl;
    private Handler handler;
    Button checkoutBtn;

    private RecyclerView rvCheckoutItemDetails;
    ArrayList<CheckoutItemDetails> checkoutItemDetailsArrayList;
    CheckoutItemDetailAdapter checkoutItemDetailAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        checkoutItemDetailsArrayList = new ArrayList<>();
        checkoutItemDetailsArrayList.add(new CheckoutItemDetails("Tui Gucci", "50cmx60cm", "den", "$2000", "1", "https://i.ibb.co/HnV0rm3/Rectangle-10.png"));

        rvCheckoutItemDetails = (RecyclerView) findViewById(R.id.rvCheckoutList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCheckoutItemDetails.setLayoutManager(layoutManager);
        rvCheckoutItemDetails.setHasFixedSize(false);
        checkoutItemDetailAdapter = new CheckoutItemDetailAdapter(checkoutItemDetailsArrayList);
        rvCheckoutItemDetails.setAdapter(checkoutItemDetailAdapter);
    }
}