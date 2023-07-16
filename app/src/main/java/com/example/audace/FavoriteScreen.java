package com.example.audace;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FavoriteScreen extends AppCompatActivity implements ProductDetailScreen.EditDetailClickListener {

    private ArrayList<Favorite> favoriteArrayList = new ArrayList<>();
    private FavoriteAdapter favoriteAdapter;


    private ImageButton cartIcon;
    private TextView cartCount;



    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite);
        /*View cartLayout = getLayoutInflater().inflate(R.layout.cart_icon, null);
        cartIcon = cartLayout.findViewById(R.id.cart);
        cartCount = cartLayout.findViewById(R.id.cart_count);
        updateCartCount();*/

        recyclerView = findViewById(R.id.favRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        favoriteAdapter = new FavoriteAdapter(this,favoriteArrayList);
        recyclerView.setAdapter(favoriteAdapter);
        setupData();

    }
    @Override
    public void onEditDetailClicked(String selectedColor, String selectedSize) {

        Toast.makeText(this, "Selected Color: " + selectedColor + ", Selected Size: " + selectedSize, Toast.LENGTH_SHORT).show();
    }
    public void setupData() {
        Handler handler = new Handler(getMainLooper());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/users/me/favourites")
                .method("GET", null)
                .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("message", call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray jsonResponse = new JSONArray(response.body().string());
                            for (int i = 0; i < jsonResponse.length(); i++) {
                                JSONObject productObject = jsonResponse.getJSONObject(i);
                                JSONObject product = productObject.getJSONObject("product");
                                String productId = product.getString("_id");
                                String productName = product.getString("name");
                                String imageURL = product.getString("imageURL");
                                int currentPrice = product.getInt("currentPrice");
                                int productQuantity = productObject.getInt("quantity");
                                String selectedColor = productObject.getJSONObject("color").getString("_id");
                                String selectedSize = productObject.getJSONObject("size").getString("_id");

                                Favorite favoriteProduct = new Favorite(productId,productName,imageURL,currentPrice);
                                favoriteProduct.setQuantity(productQuantity);
                                favoriteProduct.setColor(selectedColor);
                                favoriteProduct.setSize(selectedSize);
                                favoriteArrayList.add(favoriteProduct);
                            }
                            favoriteAdapter.notifyDataSetChanged();
                        }catch (JSONException e) {
                            e.printStackTrace();}
                        catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

}
