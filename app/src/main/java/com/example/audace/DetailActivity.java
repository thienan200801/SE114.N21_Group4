package com.example.audace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.audace.adapter.ColorAdapter;
import com.example.audace.adapter.SizeAdapter;
import com.example.audace.api.ApiItemDetails;
import com.example.audace.model.ColorObject;
import com.example.audace.model.DetailOfItem;
import com.example.audace.model.SizeObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {
    private TextView nameofproduct, rating, description, colorItem, sizeItem;
    private Handler handler;
    Button orderBtn;

    private RecyclerView rvSize, rvColor;
    ArrayList<SizeObject> sizeObjectList;
    ArrayList<ColorObject> colorObjectList;
    SizeAdapter sizeAdapter;
    ColorAdapter colorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("message", "start on create");
        handler = new Handler();
        super.onCreate(savedInstanceState);
        renderData();
        setContentView(R.layout.activity_detail);

        nameofproduct = (TextView) findViewById(R.id.nameofproduct);
        rating = (TextView) findViewById(R.id.rating);
        description = findViewById(R.id.description);
        colorItem = (TextView) findViewById(R.id.color_item_detail);
        sizeItem = (TextView)findViewById(R.id.size_item_detail);
        orderBtn = findViewById(R.id.bt3);
        Log.i("message", nameofproduct.toString());

        sizeObjectList = new ArrayList<>();
        sizeObjectList.add(new SizeObject("test"));

        rvSize = (RecyclerView) findViewById(R.id.sizeRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvSize.setLayoutManager(layoutManager);
        rvSize.setHasFixedSize(false);
        sizeAdapter = new SizeAdapter(sizeObjectList);
        rvSize.setAdapter(sizeAdapter);

        colorObjectList = new ArrayList<>();
        colorObjectList.add(new ColorObject("#E0AB0980"));

        rvColor = (RecyclerView) findViewById(R.id.colorRecycler);
        LinearLayoutManager layoutManagerColor = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvColor.setLayoutManager(layoutManagerColor);
        rvColor.setHasFixedSize(false);
        colorAdapter = new ColorAdapter(colorObjectList);
        rvColor.setAdapter(colorAdapter);

        orderBtn=findViewById(R.id.bt3);
        orderBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent t=new Intent(DetailActivity.this,Checkout.class);
                startActivity(t);
            }
        });
    }
    private void renderData() {
        Log.i("message","start crawl information");
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/products/product/6459ed39b33d8814d8301f2a")
                .method("GET", null)
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NDQxMTU4ZmVhZjQ5MmY0OGI0NzE3MzEiLCJpYXQiOjE2ODM3MDE4MDN9.dA-agPqUSJ-g2mdmw7lTBzzfszH7TUYpNAh-Lh9xQ24")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("failure", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    Log.i("message","Call API successful");
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    DetailOfItem d = new DetailOfItem();
                    d.setName(jsonObject.getString("name"));
                    d.setRating(Float.parseFloat(jsonObject.getString("rating")));
                    d.setDescription(jsonObject.getString("description"));
                    d.setFavourite(jsonObject.getBoolean("isFavourite"));
                    JSONArray sizeJsonArray = jsonObject.getJSONArray("sizes");
                    JSONArray colorJsonArray = jsonObject.getJSONArray("colors");

                    sizeObjectList.clear();
                    for(int i = 0; i<sizeJsonArray.length(); i++){
                        String res = sizeJsonArray.getJSONObject(i).getString("widthInCentimeter")
                                + "cm x "
                                + sizeJsonArray.getJSONObject(i).getString("heightInCentimeter")
                                + "cm";
                        SizeObject sizeItemObject = new SizeObject(res);
                        sizeObjectList.add(sizeItemObject);
                    }

                    colorObjectList.clear();
                    for(int i = 0; i<colorJsonArray.length(); i++){
                        String res = colorJsonArray.getJSONObject(i).getString("hex");
                        ColorObject colorItemObject = new ColorObject(res);
                        colorObjectList.add(colorItemObject);
                    }
                    Log.i("message",colorObjectList.toString());
                    if(d != null) {
                        handler.post(() -> {
                                nameofproduct.setText(d.getName());
                                rating.setText(Float.toString(d.getRating()));
                                description.setText(d.getDescription());
                                sizeAdapter.notifyDataSetChanged();
                                colorAdapter.notifyDataSetChanged();
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }
}