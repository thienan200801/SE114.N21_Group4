package com.example.audace.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.example.audace.DataStorage;
import com.example.audace.R;
import com.example.audace.model.Banner;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BannerListAdapter extends RecyclerView.Adapter<BannerListAdapter.ViewHolder> {
    final ArrayList<Banner> banners;
    public BannerListAdapter(ArrayList<Banner> input) {
        banners = input;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgView;

        public ViewHolder(View view){
            super(view);
            imgView = (ImageView) view.findViewById(R.id.bannerImageView);
        }

        public ImageView getImageView(){
            return imgView;
        }

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.banner, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position){
        Log.i("message", "start crawl banner image");
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(viewHolder.getImageView().getContext());
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();
        Picasso.get()
                .load(banners.get(position).getImgURL())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(circularProgressDrawable)
                .fit()
                .into(viewHolder.getImageView(), new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get()
                                .load(banners.get(position).getImgURL())
                                .error(R.drawable.baseline_wifi_tethering_error_24)
                                .placeholder(circularProgressDrawable)
                                .into(viewHolder.getImageView());
                    }
                });
        viewHolder.getImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataStorage.getInstance().setSearchText("*BST:" + banners.get(position).getBannerID());
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_global_searchFragment);
            }
        });
    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }

    @Override
    public int getItemCount(){
        return banners.size();
    }
}
