package com.example.audace.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.example.audace.DataStorage;
import com.example.audace.R;
import com.example.audace.model.Catagory;
import com.squareup.picasso.Picasso;

import java.io.IOException;
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

    private Runnable runnable;

    public CatagoryListAdapter(ArrayList<Catagory> input) {

        catagories = input;
        selectedCatagory = RecyclerView.SCROLLBAR_POSITION_DEFAULT;
        String catagoryID = DataStorage.getInstance().getCatagoryId();
        for(int i = 0; i < catagories.size(); i ++)
        {
            if(catagories.get(i).getCatagoryID() == catagoryID)
            {
                selectedCatagory = i;
                break;
            }
        }
        runnable = null;
    }

    public void setRunnable(Runnable callback) {
        this.runnable = callback;
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
        if(!Objects.equals(catagories.get(0).getImgUrl(), ""))
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
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position){

        viewHolder.getTextView().setText(catagories.get(position).getCatagoryName());
        viewHolder.getTextView().setTextSize(12);
        if(!Objects.equals(catagories.get(position).getImgUrl(), ""))
        {
            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(viewHolder.getImageView().getContext());
            circularProgressDrawable.setStrokeWidth(5f);
            circularProgressDrawable.setCenterRadius(30f);
            circularProgressDrawable.start();
            Picasso.get()
                    .load(catagories.get(position).getImgUrl())
                    .error(R.drawable.baseline_wifi_tethering_error_24)
                    .placeholder(circularProgressDrawable)
                    .into(viewHolder.getImageView());
            int index = position;
            viewHolder.getImageView().getLayoutParams().height = viewHolder.getImageView().getLayoutParams().width;
            viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Log.i("message", Integer.toString(index));
                    DataStorage.getInstance().setCatagoryId(DataStorage.getInstance().getCatagoryArrayList().get(index).getCatagoryID());
                    Navigation.findNavController(view).navigate(R.id.action_global_fragment_subcatagory);
                }
            });
        }
        else {
            final float scale = viewHolder.getTextView().getContext().getResources().getDisplayMetrics().density;
            int pixels = (int) (94 * scale + 0.5f);
            if(viewHolder.getTextView().getWidth()  < pixels)
                viewHolder.getTextView().setWidth(pixels);
            if(selectedCatagory == position)
                viewHolder.getTextView().setBackgroundColor(Color.parseColor("#FFFAD0"));
            else
                viewHolder.getTextView().setBackgroundColor(Color.WHITE);
            viewHolder.getTextView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notifyItemChanged(selectedCatagory);
                    selectedCatagory = viewHolder.getLayoutPosition();
                    DataStorage.getInstance().setCatagoryId(catagories.get(selectedCatagory).getCatagoryID());
                    notifyItemChanged(selectedCatagory);
                    if(runnable != null)
                        new Handler().post(runnable);
                }
            });
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
    public void Update(ArrayList<Catagory>catagoryArrayList)
    {
        catagories.clear();
        catagories.addAll(catagoryArrayList);
        notifyDataSetChanged();
    }

}
