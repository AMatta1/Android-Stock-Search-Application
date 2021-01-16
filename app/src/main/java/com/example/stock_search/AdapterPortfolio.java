package com.example.stock_search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class AdapterPortfolio extends RecyclerView.Adapter<AdapterPortfolio.ViewHolder> {
    private static  final String TAG = "AdapterPortfolio";
    public ArrayList<String> TICKER_NAME_ARRAY = new ArrayList<>();
    public ArrayList<String> SHARES_ARRAY = new ArrayList<>();
    public ArrayList<String> LAST_PRICE_ARRAY = new ArrayList<>();
    public ArrayList<String> CHANGE_ARRAY = new ArrayList<>();
    public ArrayList<String> UP_DOWN_IMG_ARRAY = new ArrayList<>();
    private Context context;


    public AdapterPortfolio(ArrayList<String> TICKER_NAME_ARRAY, ArrayList<String> SHARES_ARRAY, ArrayList<String> LAST_PRICE_ARRAY, ArrayList<String> CHANGE_ARRAY, ArrayList<String> UP_DOWN_IMG_ARRAY, Context context) {
        this.TICKER_NAME_ARRAY = TICKER_NAME_ARRAY;
        this.SHARES_ARRAY = SHARES_ARRAY;
        this.LAST_PRICE_ARRAY = LAST_PRICE_ARRAY;
        this.CHANGE_ARRAY = CHANGE_ARRAY;
        this.UP_DOWN_IMG_ARRAY = UP_DOWN_IMG_ARRAY;
        this.context = context;
    }
    public void swapOrder(int fromPosition, int toPosition) {
        Collections.swap(TICKER_NAME_ARRAY,fromPosition,toPosition);
        Collections.swap(SHARES_ARRAY,fromPosition,toPosition);
        Collections.swap(LAST_PRICE_ARRAY,fromPosition,toPosition);
        Collections.swap(CHANGE_ARRAY,fromPosition,toPosition);
        Collections.swap(UP_DOWN_IMG_ARRAY,fromPosition,toPosition);
        notifyItemMoved(fromPosition,toPosition);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_portfolio,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.TICKER_NAME.setText(TICKER_NAME_ARRAY.get(position));
        holder.SHARES.setText(SHARES_ARRAY.get(position));
        holder.LAST_PRICE.setText(LAST_PRICE_ARRAY.get(position));
        holder.CHANGE.setText(CHANGE_ARRAY.get(position));
        if(UP_DOWN_IMG_ARRAY.get(position).equalsIgnoreCase("UP")) {
            holder.CHANGE.setTextColor(Color.parseColor("#209900"));
            holder.UP_DOWN_IMG.setImageResource(R.drawable.ic_twotone_trending_up_24);
        }
        else if(UP_DOWN_IMG_ARRAY.get(position).equalsIgnoreCase("DOWN")){
            holder.CHANGE.setTextColor(Color.parseColor("#990000"));
            holder.UP_DOWN_IMG.setImageResource(R.drawable.ic_baseline_trending_down_24);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TickerDetails.class);
                intent.putExtra("query",TICKER_NAME_ARRAY.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return TICKER_NAME_ARRAY.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView TICKER_NAME;
        TextView SHARES;
        TextView LAST_PRICE;
        TextView CHANGE ;
        ImageView UP_DOWN_IMG;
        RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TICKER_NAME = itemView.findViewById(R.id.itemTextView);
            SHARES = itemView.findViewById(R.id.textview_quantity);
            LAST_PRICE = itemView.findViewById(R.id.textview_price);
            CHANGE = itemView.findViewById(R.id.textview_change);
            UP_DOWN_IMG = itemView.findViewById(R.id.imageView_trending);
            ImageView imageView = itemView.findViewById(R.id.goToDetailsArrow);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, TickerDetails.class);
                    intent.putExtra("query",TICKER_NAME.getText().toString());
                    context.startActivity(intent);
                }
            });
        }
    }
}
