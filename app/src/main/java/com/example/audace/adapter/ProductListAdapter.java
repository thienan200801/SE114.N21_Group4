package com.example.audace.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audace.DataStorage;
import com.example.audace.R;
import com.example.audace.model.Product;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private ArrayList<Product> products;

    private int destinationId;

    public int getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;

        private final TextView priceTextVIew;
        private final ImageView imgView;

        private final ToggleButton favouriteButton;
        public ViewHolder(View view) {
            super(view);
            nameTextView = (TextView) view.findViewById(R.id.itemCardNameTextView);
            imgView = (ImageView) view.findViewById(R.id.itemCardImageView);
            priceTextVIew = (TextView) view.findViewById(R.id.itemCardPriceTextView);
            favouriteButton = (ToggleButton) view.findViewById(R.id.itemCardImageButton);
        }

        public TextView getNameTextView(){ return nameTextView;}
        public TextView getPriceTextVIew(){ return priceTextVIew;}
        public ImageView getImgView() {return imgView;}

        public ToggleButton getFavouriteButton() {return favouriteButton; }
    }

    public ProductListAdapter(ArrayList<Product> products)
    {
        this.products = products;
    }
    @Override
    public ProductListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductListAdapter.ViewHolder holder, int position) {
        Log.i("message", "binding product");

        holder.getNameTextView().setText(products.get(position).getName());
        holder.getPriceTextVIew().setText(products.get(position).getPrice());
        HttpUrl url = HttpUrl.parse(products.get(position).getImgUrl()).newBuilder().build();
        Request request = new Request.Builder().url(url).build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("message", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful())
                {
                    try{
                        final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                        holder.getImgView().setImageBitmap(bitmap);
                    }
                    catch (Exception e)
                    {

                    }
                }
            }
        });
        if(products.get(position).isFavourite()) {
            holder.getFavouriteButton().setButtonDrawable(R.drawable.baseline_favorite_24);
            holder.getFavouriteButton().setChecked(true);
        }
        holder.setIsRecyclable(true);
        holder.getImgView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataStorage.getInstance().setProductId(products.get(holder.getAdapterPosition()).getProductID());
                Navigation.findNavController(view).navigate(R.id.action_global_detailActivity);
            }
        });
        holder.getFavouriteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.getFavouriteButton().isChecked())
                {
                    holder.getFavouriteButton().setButtonDrawable(R.drawable.baseline_favorite_24);
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("application/json");
                    String string = String.format("{\r\n    \"id\": \"%s\"\r\n}", products.get(holder.getAdapterPosition()).getProductID());
                    RequestBody body = RequestBody.create(mediaType, string);
                    Request request = new Request.Builder()
                            .url("https://audace-ecomerce.herokuapp.com/users/me/favourites")
                            .method("POST", body)
                            .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NDQxMTU4ZmVhZjQ5MmY0OGI0NzE3MzEiLCJpYXQiOjE2ODM3MDE4MDN9.dA-agPqUSJ-g2mdmw7lTBzzfszH7TUYpNAh-Lh9xQ24")
                            .addHeader("Content-Type", "application/json")
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.i("message", "fail to delete");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.i("message", "Success to delete");
                        }
                    });
                }
                else
                {
                    holder.getFavouriteButton().setButtonDrawable(R.drawable.baseline_favorite_border_24);
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("application/json");
                    String string = String.format("{\r\n    \"id\": \"%s\"\r\n}", products.get(holder.getAdapterPosition()).getProductID());

                    RequestBody body = RequestBody.create(mediaType, string);
                    Request request = new Request.Builder()
                            .url("https://audace-ecomerce.herokuapp.com/users/me/favourites")
                            .method("DELETE", body)
                            .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NDQxMTU4ZmVhZjQ5MmY0OGI0NzE3MzEiLCJpYXQiOjE2ODM3MDE4MDN9.dA-agPqUSJ-g2mdmw7lTBzzfszH7TUYpNAh-Lh9xQ24")
                            .addHeader("Content-Type", "application/json")
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.i("message", "fail to add");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.i("message", "Susccess to add");
                        }
                    });
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
