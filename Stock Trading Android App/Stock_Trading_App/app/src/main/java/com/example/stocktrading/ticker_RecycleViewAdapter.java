package com.example.stocktrading;

import static android.graphics.Color.BLUE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ticker_RecycleViewAdapter extends RecyclerView.Adapter<ticker_RecycleViewAdapter.ViewHolder>
                                        implements CombinedItemTouchHelper.ItemTouchHelperContract
{

    Context mContext;
    //Portfolio or Favorites
    public ArrayList<String> ticker_label_list = new ArrayList<>();
    private ArrayList<Integer> shares_list = new ArrayList<>();
    private ArrayList<Float> total_cost_Value_list = new ArrayList<>();
    private ArrayList<String> compName_list = new ArrayList<>();

    private ArrayList<Float> change_port = new ArrayList<>();
    private ArrayList<Float> change_port_percentage = new ArrayList<>();
    private ArrayList<Float> price_port = new ArrayList<>();
    private ArrayList<Float> change_fav = new ArrayList<>();
    private ArrayList<Float> change_fav_percentage = new ArrayList<>();
    private ArrayList<Float> price_fav = new ArrayList<>();
    public ArrayList<Integer> order = new ArrayList<>();

    private RequestQueue queue;
    private boolean move = false;

    //private ArrayList<String> changes_list = new ArrayList<>();

    //boolean for Portfolio or Favorites
    boolean portfolio;

    public ticker_RecycleViewAdapter(Context context, boolean portfolio, ArrayList<String> ticker_label_list, ArrayList<Integer> shares_list, ArrayList<Float> total_cost_Value_list, ArrayList<String> compName_list, ArrayList<Float> price, ArrayList<Float> change, ArrayList<Float> change_percentage) {
        this.mContext = context;
        this.portfolio = portfolio;
        this.queue = Volley.newRequestQueue(mContext);
        this.ticker_label_list.clear();
        this.ticker_label_list.addAll(ticker_label_list);
        if (portfolio)
        {
            this.price_port = price;
            this.change_port = change;
            this.change_port_percentage = change_percentage;
            this.shares_list = shares_list;
            this.total_cost_Value_list = total_cost_Value_list;
        }
        else
        {
            this.price_fav = price;
            this.change_fav = change;
            this.change_fav_percentage = change_percentage;
            this.compName_list = compName_list;
        }

        int size = ticker_label_list.size();
        for (int i = 0; i < size; i++)
        {
            order.add(i);
        }


        //getData_from_storage();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticker_layout, parent, false);
        return new ViewHolder(view);
    }

    //TODO: setup portfolio and favorites data on home main activity
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,  int position) {


        System.out.println("Bind ticker_set: "+ ticker_label_list.toString());
        System.out.println("Bind ticker position: "+ position);
        System.out.println("Bind ticker: "+ ticker_label_list.get(position));


        holder.arrow_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent new_activity = new Intent(mContext, TickerActivity.class);
                new_activity.putExtra("ticker", ticker_label_list.get(position));
                mContext.startActivity(new_activity);
            }
        });

        holder.ticker_Label.setText(ticker_label_list.get(position));

        if (portfolio)
        {
            System.out.println("portfolio: " + portfolio);
            System.out.println("position: " + position);
            System.out.println("price_port: " + price_port.get(position));
            System.out.println("change_port: " + change_port.get(position));
            System.out.println("change port string format : " + String.format("$%1.2f", change_port.get(position)));

            holder.share_CompName.setText(shares_list.get(position).toString() + " shares");
            holder.price_Value.setText(String.format("$%1.2f", price_port.get(position)));
            if ((int)(change_port.get(position) * 100) > 0.0) {
                String change_text = String.format("$%1.2f", change_port.get(position)) + " ( " + String.format("%1.2f", change_port_percentage.get(position)) + "% )";
                holder.change_value.setText(change_text);
                holder.change_value.setTextColor(Color.parseColor("#5AEF0A"));
                holder.arrow_change.setImageResource(R.drawable.ic_trend_up);
            } else if ((int)(change_port.get(position) * 100) < 0.0) {
                String change_text = String.format("$%1.2f", change_port.get(position)) + " ( " + String.format("%1.2f", change_port_percentage.get(position)) + "% )";
                holder.change_value.setText(change_text);
                holder.change_value.setTextColor(Color.parseColor("#F10327"));
                holder.arrow_change.setImageResource(R.drawable.ic_trend_down);
            }
            else
            {
                String change_text = "$0.00 ( 0.00% )";
                holder.change_value.setText(change_text);
                holder.change_value.setTextColor(Color.BLACK);
                holder.arrow_change.setImageDrawable(null);
            }

        }
        else  // favorite
        {
            System.out.println("portfolio: " + portfolio);
            System.out.println("position: " + position);
            System.out.println("change_fav: " + change_fav.get(position));
            System.out.println("change fav string format : " + String.format("$%1.2f", change_fav.get(position)));

            holder.share_CompName.setText(compName_list.get(position));
            holder.price_Value.setText(String.format("$%1.2f", price_fav.get(position)));
            String change_text = String.format("$%1.2f", change_fav.get(position)) + " ( " + String.format("%1.2f", change_fav_percentage.get(position)) + "% )";
            holder.change_value.setText(change_text);
            if ((int)(change_fav.get(position)*100) > 0.0) {
                holder.change_value.setTextColor(Color.parseColor("#5AEF0A"));
                holder.arrow_change.setImageResource(R.drawable.ic_trend_up);
            } else if ((int)(change_fav.get(position)*100) < 0.0) {
                holder.change_value.setTextColor(Color.parseColor("#F10327"));
                holder.arrow_change.setImageResource(R.drawable.ic_trend_down);
            }


        }
        //get_latest_price(ticker_label_list.get(position), holder, position);


    }

    @Override
    public int getItemCount() {
        return ticker_label_list.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {



        if (fromPosition < toPosition)
        {
            for (int i = fromPosition; i < toPosition; i++)
            {
                Collections.swap(order, i, i+1);
                Collections.swap(ticker_label_list, i, i+1);
                if (portfolio)
                {
                    Collections.swap(shares_list, i, i+1);
                    Collections.swap(total_cost_Value_list, i, i+1);
                    Collections.swap(price_port, i, i+1);
                    Collections.swap(change_port, i, i+1);
                    Collections.swap(change_port_percentage, i, i+1);
                }
                else
                {
                    Collections.swap(compName_list, i, i+1);
                    Collections.swap(change_fav, i, i+1);
                    Collections.swap(change_fav_percentage, i, i+1);
                    Collections.swap(price_fav, i, i+1);
                }
            }
        }
        else
        {
            for (int i = fromPosition; i > toPosition; i--)
            {
                Collections.swap(order, i, i-1);
                Collections.swap(ticker_label_list, i, i-1);

                if (portfolio)
                {
                    Collections.swap(shares_list, i, i-1);
                    Collections.swap(total_cost_Value_list, i, i-1);
                    Collections.swap(price_port, i, i-1);
                    Collections.swap(change_port, i, i-1);
                    Collections.swap(change_port_percentage, i, i-1);
                }
                else
                {
                    Collections.swap(compName_list, i, i-1);
                    Collections.swap(change_fav, i, i-1);
                    Collections.swap(change_fav_percentage, i, i-1);
                    Collections.swap(price_fav, i, i-1);
                }


            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }



    @Override
    public void onRowSelected(ViewHolder myViewHolder) {
        //myViewHolder.ticker_Label.setBackgroundColor(Color.GRAY);
        move = true;

    }

    @Override
    public void onRowClear(ViewHolder myViewHolder) {
        ///myViewHolder.ticker_Label.setBackgroundColor(Color.GREEN);
        System.out.println("this is from clear view from adapter");
        System.out.println("ticker set order: " + ticker_label_list.toString());
        notifyDataSetChanged();
        move = false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView ticker_Label;
        TextView share_CompName;
        TextView price_Value;
        TextView change_value;
        ImageView arrow_info;
        ImageView arrow_change;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ticker_Label = itemView.findViewById(R.id.textView_ticker_label);
            share_CompName = itemView.findViewById(R.id.textView_ticker_sharesCompName);
            price_Value = itemView.findViewById(R.id.textView_ticker_priceValue);
            change_value = itemView.findViewById(R.id.textView_ticker_changePrice);
            arrow_info = itemView.findViewById(R.id.imageView_ticker_arrow);
            arrow_change = itemView.findViewById(R.id.imageView_ticker_change_arrow);
        }
    }

    //TODO: update all the variables and local storage (delete)


    public void update_all_data(ArrayList<String> ticker_label_list, ArrayList<Integer> shares_list, ArrayList<Float> total_cost_Value_list, ArrayList<String> compName_list, ArrayList<Float> price, ArrayList<Float> change, ArrayList<Float> change_percentage)
    {
        //this.ticker_label_list = ticker_label_list;
        int size = ticker_label_list.size();
        order.clear();
        for (int i = 0; i < size; i++)
        {
            order.add(i);
        }

        if (portfolio)
        {
            this.price_port = price;
            this.change_port = change;
            this.change_port_percentage = change_percentage;
            this.shares_list = shares_list;
            this.total_cost_Value_list = total_cost_Value_list;
            this.ticker_label_list = ticker_label_list;

        }
        else
        {
            this.price_fav = price;
            this.change_fav = change;
            this.change_fav_percentage = change_percentage;
            this.compName_list = compName_list;
            this.ticker_label_list = ticker_label_list;
        }
        System.out.println("update all data function finish");

        notifyDataSetChanged();

    }


    public void removeItem(int position)
    {
        System.out.println("before remove size: " + ticker_label_list.size());
        if (portfolio)
        {
            SharedPreferences sp_port = mContext.getSharedPreferences("Trade", Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sp_port.edit();
            String tick = ticker_label_list.get(position);
            ed.remove(tick+"_shares");
            ed.remove(tick + "_tc");
            ed.remove(tick + "_ch");
            ed.remove(tick + "_chp");
            ed.remove(tick + "_p");
            Set<String>set = sp_port.getStringSet("ticker_set", new HashSet<>());
            set.remove(tick);
            ed.putStringSet("ticker_set", set);
            ed.commit();

            shares_list.remove(position);
            ticker_label_list.remove(position);
            total_cost_Value_list.remove(position);
            price_port.remove(position);
            change_port.remove(position);
            change_port_percentage.remove(position);
        }
        else
        {
            SharedPreferences sp_fav = mContext.getSharedPreferences("Ticker", Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sp_fav.edit();
            String tick = ticker_label_list.get(position);
            ed.remove(tick + "_d");
            ed.remove(tick + "_dp");
            ed.remove(tick + "_p");
            ed.remove(tick+"_cn");
            Set<String>set = sp_fav.getStringSet("ticker_set", new HashSet<>());
            set.remove(tick);
            ed.putStringSet("ticker_set", set);
            ed.commit();

            compName_list.remove(position);
            ticker_label_list.remove(position);
            change_fav.remove(position);
            change_fav_percentage.remove(position);
            price_fav.remove(position);
        }
        order.remove(position);
        notifyItemRemoved(position);


    }

    public ArrayList<String> get_ticker_set() {return ticker_label_list;}
    public ArrayList<Integer> getShares_list() {return shares_list;}
    public ArrayList<Float> getTotal_cost_Value_list() {return total_cost_Value_list;}
    public ArrayList<String> getCompName_list() {return compName_list;}

    public ArrayList<Float> getChange_port() {return change_port;}
    public ArrayList<Float> getChange_port_percentage() {return change_port_percentage;}
    public ArrayList<Float> getPrice_port() {return price_port;}
    public ArrayList<Float> getChange_fav() {return change_fav;}
    public ArrayList<Float> getChange_fav_percentage() {return change_fav_percentage;}
    public ArrayList<Float> getPrice_fav() {return price_fav;}



}
