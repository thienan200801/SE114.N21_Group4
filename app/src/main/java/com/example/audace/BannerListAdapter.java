package com.example.audace;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

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
    public void onBindViewHolder(ViewHolder viewHolder, final int position){
        Log.i("message", "start crawl banner image");
        HttpUrl url = HttpUrl.parse(banners.get(position).imgURL).newBuilder().build();
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
                        viewHolder.getImageView().setImageBitmap(bitmap);
                    }
                    catch (Exception e)
                    {
                        Log.i("error", e.toString());
                    }
                }
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
