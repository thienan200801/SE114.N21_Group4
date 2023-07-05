package com.example.audace.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.audace.R;
import com.example.audace.model.NamePrice;

public class NamePriceOfItemAdapter extends ArrayAdapter<NamePrice> {
    private Context context;
    private NamePrice[] mList;

    public NamePriceOfItemAdapter(@NonNull Context context, NamePrice[] mList) {
        super(context, R.layout.name_price_item, mList);
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.name_price_item, null, true);
        TextView tname = v.findViewById(R.id.textView11);
        TextView tprice = v.findViewById(R.id.textView15);
        NamePrice np = mList[position];
        tname.setText(np.getNameOfCheckoutItem());
        tprice.setText(np.getPriceOfCheckoutItem());
        return v;
    }
    public NamePrice getItem(int position){
        return mList[position];
    }
}