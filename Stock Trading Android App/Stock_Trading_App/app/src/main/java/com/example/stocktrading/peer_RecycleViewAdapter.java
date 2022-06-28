package com.example.stocktrading;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class peer_RecycleViewAdapter extends RecyclerView.Adapter<peer_RecycleViewAdapter.ViewHolder> {

    private ArrayList<String> peers = new ArrayList<>();
    private Context mContext;

    public peer_RecycleViewAdapter(ArrayList<String> peers, Context mContext) {
        this.peers = peers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.peers_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.peer.setText(peers.get(position));

        holder.peer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, peers.get(position), Toast.LENGTH_SHORT).show();
                //System.out.println();
                String text = peers.get(position);
                String ticker = text.substring(0, text.length()-1);
                //System.out.println("new ticker: " + ticker);


                Intent peer_ticker_activity = new Intent(mContext, TickerActivity.class);
                peer_ticker_activity.putExtra("ticker", ticker);
                mContext.startActivity(peer_ticker_activity);



            }
        });
    }

    @Override
    public int getItemCount() {
        System.out.println("peers size: " + peers.size());
        return peers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView peer;

        public ViewHolder( View itemView) {
            super(itemView);
            peer = itemView.findViewById(R.id.textView_peers_layout);
            peer.setPaintFlags(peer.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
    }
}
