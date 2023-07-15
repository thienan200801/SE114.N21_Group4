package com.example.audace.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.example.audace.DataStorage;
import com.example.audace.HomepageActivity;
import com.example.audace.R;
import com.example.audace.fragment_productDetail;
import com.example.audace.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

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
    private Fragment fragment;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;

        private final TextView priceTextVIew;
        private final ImageView imgView;

        private final ToggleButton favouriteButton;

        private final TextView saleOff;

        private final View saleOffLayout;

        public ViewHolder(View view) {
            super(view);
            nameTextView = (TextView) view.findViewById(R.id.itemCardNameTextView);
            imgView = (ImageView) view.findViewById(R.id.itemCardImageView);
            priceTextVIew = (TextView) view.findViewById(R.id.itemCardPriceTextView);
            favouriteButton = (ToggleButton) view.findViewById(R.id.itemCardImageButton);
            saleOff = (TextView) view.findViewById(R.id.saleOffTextView);
            saleOffLayout = view.findViewById(R.id.saleOffIconLayout);
        }

        public TextView getNameTextView(){ return nameTextView;}
        public TextView getPriceTextVIew(){ return priceTextVIew;}
        public ImageView getImgView() {return imgView;}

        public TextView getSaleOff() {
            return saleOff;
        }

        public View getSaleOffLayout() {
            return saleOffLayout;
        }

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ProductListAdapter.ViewHolder holder, int position) {
        Log.i("message", "binding product");

        holder.getNameTextView().setText(products.get(position).getName());
        holder.getPriceTextVIew().setText(products.get(position).getPrice());
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(holder.getImgView().getContext());
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();
        Picasso.get()
                .load(products.get(position).getImgUrl())
                .placeholder(circularProgressDrawable)
                .error(R.drawable.baseline_wifi_tethering_error_24)
                .fit()
                .into(holder.getImgView());
        if(!Objects.equals(products.get(position).getStablePrice(), products.get(position).getPrice()))
        {
            Float stablePrice = Float.parseFloat(products.get(position).getStablePrice());
            Integer price =Integer.parseInt(products.get(position).getPrice());
            if(price < stablePrice)
                holder.getSaleOff().setText(Float.toString((price/stablePrice) * 100).substring(0, 2) + "%");
            else
                holder.getSaleOffLayout().setVisibility(View.GONE);
        }
        else
            holder.getSaleOffLayout().setVisibility(View.GONE);
        if(products.get(position).isFavourite()) {
            holder.getFavouriteButton().setButtonDrawable(R.drawable.baseline_favorite_24);
            holder.getFavouriteButton().setChecked(true);
        }
        holder.getImgView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataStorage.getInstance().setProductId(products.get(holder.getAdapterPosition()).getProductID());
                FragmentContainerView navHostFragment = (FragmentContainerView) view.getRootView().findViewById(R.id.bottomNavigationContainer);
                FragmentTransaction transaction = navHostFragment.getFragment().getFragmentManager().beginTransaction();
                transaction.replace(R.id.bottomNavigationContainer, new fragment_productDetail());
                transaction.addToBackStack(null);
                transaction.commit();
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
                            .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
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
                            .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
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
    private Activity getActivity(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }
}
