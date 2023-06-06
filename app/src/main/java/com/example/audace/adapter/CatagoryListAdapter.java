package com.example.audace.adapter;

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

import com.example.audace.DataStorage;
import com.example.audace.R;
import com.example.audace.model.Catagory;

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
    public void onBindViewHolder(ViewHolder viewHolder, final int position){

        viewHolder.getTextView().setText(catagories.get(position).getCatagoryName());
        viewHolder.getTextView().setTextSize(12);
        if(!Objects.equals(catagories.get(position).getImgUrl(), ""))
        {
            HttpUrl url = HttpUrl.parse(catagories.get(position).getImgUrl()).newBuilder().build();
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
                    com.example.audace.homeDirections.ActionHomeToFragmentSubcatagory action = com.example.audace.homeDirections.actionHomeToFragmentSubcatagory();
                    action.setId(catagories.get(viewHolder.getAdapterPosition()).getCatagoryID());
                    Navigation.findNavController(view).navigate(R.id.action_home_to_fragment_subcatagory);
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
