package com.example.stocktrading;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class news_RecycleViewAdapter extends RecyclerView.Adapter<news_RecycleViewAdapter.ViewHolder> {


    private Context mContext;
    private ArrayList<String> source_list = new ArrayList<>();
    private ArrayList<String> time_list = new ArrayList<>();
    private ArrayList<String> title_list = new ArrayList<>();
    private ArrayList<String> image_URL_list = new ArrayList<>();
    private ArrayList<String> sum_list = new ArrayList<>();
    private ArrayList<String> date_list = new ArrayList<>();
    private ArrayList<String> URL_list = new ArrayList<>();


    public news_RecycleViewAdapter(Context mContext, ArrayList<String> source_list, ArrayList<String> time_list, ArrayList<String> title_list, ArrayList<String> image_URL_list, ArrayList<String> sum_list, ArrayList<String> date_list, ArrayList<String> URL_list) {
        this.mContext = mContext;
        this.source_list = source_list;
        this.time_list = time_list;
        this.title_list = title_list;
        this.image_URL_list = image_URL_list;
        this.sum_list = sum_list;
        this.date_list = date_list;
        this.URL_list = URL_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(mContext)
                .asBitmap()
                .load(image_URL_list.get(position))
                .into(holder.image);

        holder.source.setText(source_list.get(position));
        holder.time.setText(time_list.get(position));
        holder.title.setText(title_list.get(position));

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(mContext, "this is on position " + position + " out of " + source_list.size(), Toast.LENGTH_SHORT).show();

                final Dialog dialog = new Dialog(mContext);


                /*
                dialog.setContentView(R.layout.testing_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView short_view = dialog.findViewById(R.id.textView_testing_short);
                TextView long_view = dialog.findViewById(R.id.textView_test_long);

                short_view.setText(source_list.get(position));
                long_view.setText(sum_list.get(position));

                 */




                dialog.setContentView(R.layout.news_dialog_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



                TextView source_dl = dialog.findViewById(R.id.textView_Source_newsDia);
                TextView time_dl = dialog.findViewById(R.id.textView_time_newsDia);
                TextView title_dl = dialog.findViewById(R.id.textView_headline_newDia);
                TextView sum_dl = dialog.findViewById(R.id.textView_sum_newsDia);

                source_dl.setText(source_list.get(position));
                time_dl.setText(date_list.get(position));
                title_dl.setText(title_list.get(position));
                sum_dl.setText(sum_list.get(position));

                ImageButton chrome = dialog.findViewById(R.id.imageButton_chrome);
                ImageButton twitter = dialog.findViewById(R.id.imageButton_tw);
                ImageButton facebook = dialog.findViewById(R.id.imageButton_fb);

                chrome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent openURL = new Intent(Intent.ACTION_VIEW);
                        openURL.setData(Uri.parse(URL_list.get(position)));
                        mContext.startActivity(openURL);
                    }
                });

                twitter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent openURL = new Intent(Intent.ACTION_VIEW);
                        String url = "https://twitter.com/intent/tweet?text=" + title_list.get(position) + "+%20+" + URL_list.get(position);
                        openURL.setData(Uri.parse(url));
                        System.out.println("twitter link");
                        System.out.println(url);
                        mContext.startActivity(openURL);
                    }
                });

                facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent openURL = new Intent(Intent.ACTION_VIEW);
                        String url = "https://www.facebook.com/sharer/sharer.php?u=" + URL_list.get(position);
                        openURL.setData(Uri.parse(url));
                        System.out.println(url);
                        mContext.startActivity(openURL);
                    }
                });






                dialog.show();


            }
        });


    }

    @Override
    public int getItemCount() {
        System.out.println("get item size: " + source_list.size());
        return source_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView source;
        TextView time;
        TextView title;
        ImageView image;
        CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            source = itemView.findViewById(R.id.textView_news_sources_layout);
            time = itemView.findViewById(R.id.textView_time_news_layout);
            title = itemView.findViewById(R.id.textView_news_title_layout);
            image = itemView.findViewById(R.id.imageView_news_layout);
            card = itemView.findViewById(R.id.cardView_news_layout);
            image.setClipToOutline(true);
        }
    }
}
