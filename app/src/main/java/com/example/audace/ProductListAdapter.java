package com.example.audace;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private ArrayList<Product> products;

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

        holder.getNameTextView().setText(products.get(position).name);
        holder.getPriceTextVIew().setText(products.get(position).price);
        HttpUrl url = HttpUrl.parse(products.get(position).imgUrl).newBuilder().build();
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
        if(products.get(position).favourite) {
            holder.getFavouriteButton().setButtonDrawable(R.drawable.baseline_favorite_24);
            holder.getFavouriteButton().setChecked(true);
        }
        holder.getFavouriteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.getFavouriteButton().isChecked())
                    holder.getFavouriteButton().setButtonDrawable(R.drawable.baseline_favorite_24);
                else
                    holder.getFavouriteButton().setButtonDrawable(R.drawable.baseline_favorite_border_24);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
