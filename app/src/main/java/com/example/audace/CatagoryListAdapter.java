package com.example.audace;

import android.app.Notification;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ImageReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CatagoryListAdapter extends RecyclerView.Adapter<CatagoryListAdapter.ViewHolder> {
    ArrayList<Catagory> catagories;
    private int selectedCatagory;
    public CatagoryListAdapter(ArrayList<Catagory> input) {

        catagories = input;
        selectedCatagory = 0;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView imgView;

        public ViewHolder(View view){
            super(view);
            textView = (TextView) view.findViewById(R.id.catagoryTextView);
            imgView = (ImageView) view.findViewById(R.id.catagoryImageView);
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageView getImageView(){
            return imgView;
        }

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view;
        if(!Objects.equals(catagories.get(0).ImgUrl, ""))
        {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.catagory_button, viewGroup, false);
            view.getLayoutParams().width = 150;
        }
        else
        {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.catagory_textview, viewGroup, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position){

        Log.i("message", "start crawl image");
        viewHolder.getTextView().setText(catagories.get(position).CatagoryName);
        viewHolder.getTextView().setTextSize(12);
        if(!Objects.equals(catagories.get(position).ImgUrl, ""))
        {
            HttpUrl url = HttpUrl.parse(catagories.get(position).ImgUrl).newBuilder().build();
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

                        }
                    }
                }
            });
            viewHolder.getImageView().getLayoutParams().height = viewHolder.getImageView().getLayoutParams().width;
            viewHolder.getImageView().setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    com.example.audace.homeDirections.ActionHomeToFragmentSubcatagory action = homeDirections.actionHomeToFragmentSubcatagory();
                    action.setId(catagories.get(viewHolder.getAdapterPosition()).imgID);
                    Navigation.findNavController(view).navigate(R.id.action_home_to_fragment_subcatagory);
                }
            });
        }
        else {
            viewHolder.getTextView().setHeight(21);
        }
    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }

    @Override
    public int getItemCount(){
        return catagories.size();
    }

    public int getSelectedCatagory() {
        return selectedCatagory;
    }
    public void setSelectedCatagory(int value){
        if(value < catagories.size() && value > 0)
            selectedCatagory = value;
    }
}
