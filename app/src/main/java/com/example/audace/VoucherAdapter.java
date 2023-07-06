package com.example.audace;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {

    private ArrayList<Voucher> voucherList ;
    private VoucherScreen activity;

    public VoucherAdapter(VoucherScreen activity, ArrayList<Voucher> voucherList
    ){
        this.activity = activity;
        this.voucherList = voucherList;
    }
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.voucher_item, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Voucher item = voucherList.get(position);
        holder.mathangTextView.setText(item.getMathang());
        holder.dieukienTextView.setText(item.getDieukien());
        holder.thoigianTextView.setText(item.getThoigian());
        holder.luuyTextView.setText(item.getLuuy());
        holder.saleoffTextView.setText(item.getSaleoff());
        holder.saveVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });





    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView mathangTextView ;
        TextView dieukienTextView ;
        TextView thoigianTextView ;
        TextView luuyTextView ;
        TextView saleoffTextView;
        ImageButton saveVoucher;

        ViewHolder( View view){
            super(view);
            mathangTextView =view.findViewById(R.id.txtmathang);
            dieukienTextView =view.findViewById(R.id.txtdieukien);
            thoigianTextView =view.findViewById(R.id.txtthoigian);
            luuyTextView = view.findViewById(R.id.txtluuy);
            saleoffTextView = view.findViewById(R.id.saleoff_txt);
            saveVoucher = view.findViewById(R.id.btnSaveVoucher);






        }

        }


    @Override
    public int getItemCount() {

        return voucherList.size();

    }
    public void setVoucherList(ArrayList<Voucher> voucherList){
        this.voucherList = voucherList;

    }
}
