package com.example.stock_search;

import android.app.Dialog;
import android.net.Uri;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;

import java.util.List;

//import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 1/1/2018.
 */

public class AdapterCard extends RecyclerView.Adapter<AdapterCard.ViewHolder>{

    private static final String TAG = "AdapterCard";


    Context mContext;
    List<CardItem> mData;

    public AdapterCard(Context context, List<CardItem> mData ) {
        this.mContext = context;
        this.mData = mData;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
        {
            return  0;
        }
        else
        {
            return 1;
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1)
        {
         view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);}
        else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_layout, parent, false);
        }
        ViewHolder holder = new ViewHolder(view);
        return holder;
        }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Glide.with(holder.image.getContext()).load(mData.get(position).getImage()).into(holder.image);
//        Picasso.with(mContext).load(mData.get(position).getImage()).into(holder.image);

        holder.sourceName.setText(mData.get(position).getName());
        holder.headlines.setText(mData.get(position).getTitle());
        holder.daysAgo.setText(mData.get(position).getPublishedAt());

//        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Log.d(TAG, "onClick: clicked on: " + mImageNames.get(position));
//
////                Toast.makeText(mContext, mImageNames.get(position), Toast.LENGTH_SHORT).show();
//
//                Intent intent = new Intent(mContext, TestActivity.class);
//                intent.putExtra("image_url", mData.get(position).getImage());
//                intent.putExtra("image_name", mData.get(position).getImage());
//                mContext.startActivity(intent);
//            }
//        });

        final String temp1 = mData.get(position).getUrl();
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(temp1));
                mContext.startActivity(intent);
            }
        });

        holder.linearLayout.setOnLongClickListener(v -> {
            Log.i("from","adapter");
            final Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.dialog_cards);
            final TextView title11 = dialog.findViewById(R.id.textTD);
            ImageView img11 = dialog.findViewById(R.id.imageD);
            ImageView img22 = dialog.findViewById(R.id.imageView2);
            final ImageView chroma = dialog.findViewById(R.id.imageView3);

            title11.setText(mData.get(position).getTitle());
//            Glide.with(holder..getContext()).load(mData.get(position).getImage()).into(holder.image);
            Glide.with(img11).load(mData.get(position).getImage()).into(img11);
            final String temp = mData.get(position).getUrl();
            Log.d("temp = ",temp);
            img22.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.d("lisrener set","here");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://twitter.com/intent/tweet?text=Check%20out%20this%20Link:&url="+temp+"&hashtags=CSCI571NewsSearch"));
                    mContext.startActivity(intent);
                }
            });

            chroma.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.d("lisrener set","here");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(temp));
                    mContext.startActivity(intent);
                }
            });


//            Glide.with(holder.image.getContext()).load(mData.get(position).getImage()).into(holder.image);
//                Picasso.with(img11).load(mData.get(position).getImg()).into(img11);
//                Picasso.with(this).load(imageUrl).into(image);
            dialog.show();
            return true;
        });









    }

    @Override
    public int getItemCount() {
//        return mData.size();
        return mData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView sourceName;
        TextView daysAgo;
        LinearLayout linearLayout;
        TextView headlines;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.newsImage);
            sourceName = itemView.findViewById(R.id.source
             );
            daysAgo = itemView.findViewById(R.id.newsTime
            );
            headlines =  itemView.findViewById(R.id.newsHeadline
            );

//             parentLayout = itemView.findViewById(R.id.parent_layout);
            linearLayout = itemView.findViewById(R.id.lini);
        }
    }

}
