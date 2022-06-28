package com.example.stocktrading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {



    //variable


    //search variables
    //private AutoCompleteTextView search_autoComplete_listView;
    private String[] names;
    private ArrayAdapter<String> search_suggestions_adapter;
    private SearchView.SearchAutoComplete search_list;
    private RequestQueue queue;
    private String ticker;

    private Intent ticker_activity;
    private boolean first_get_data_from_local = true;

    //RecyclerView
    RecyclerView port_RV;
    RecyclerView fav_RV;

    //Finnhub
    TextView finnhub;

    ticker_RecycleViewAdapter fav_Adapter;
    ticker_RecycleViewAdapter port_Adapter;

    //local storage
    ArrayList<String> ticker_port_list = new ArrayList<>();
    ArrayList<String> ticker_fav_list = new ArrayList<>();
    private ArrayList<Integer> shares_list = new ArrayList<>();
    private ArrayList<Float> total_cost_Value_list = new ArrayList<>();
    private ArrayList<String> compName_list = new ArrayList<>();

    private ArrayList<Float> change_port = new ArrayList<>();
    private ArrayList<Float> change_port_percentage = new ArrayList<>();
    private ArrayList<Float> price_port = new ArrayList<>();
    private ArrayList<Float> change_fav = new ArrayList<>();
    private ArrayList<Float> change_fav_percentage = new ArrayList<>();
    private ArrayList<Float> price_fav = new ArrayList<>();
    private int API_complete = 0;
    private float balance, net_worth;

    TextView net_worth_textView, balance_textView;
    ConstraintLayout cl;
    ProgressBar pb;

    Timer time;

    Handler handler = new Handler();


    private Runnable timeInterval = new Runnable() {
        @Override
        public void run() {
            System.out.println("run interval runnable");
            get_latest_price();
            handler.postDelayed(timeInterval, 15000);
        }
    };






    @Override
    protected void onCreate(Bundle savedInstanceState) {

        API_complete = 0;
        queue = Volley.newRequestQueue(this);
        Handler setDelay = new Handler();



        /*
        setDelay.postDelayed(new Runnable() {
            @Override
            public void run() {
                setTheme(R.style.Theme_StockTradingModify);
            }
        }, 2000);

         */


        super.onCreate(savedInstanceState);
        //setTheme(R.style.Theme_StockTrading);
        setContentView(R.layout.activity_main);

        cl = findViewById(R.id.Con_layout);
        cl.setVisibility(View.GONE);

        pb = findViewById(R.id.progressbar_ticker);
        pb.setVisibility(View.VISIBLE);


        //set date under stocks action bar
        TextView date_textView = findViewById(R.id.textView_date);

        //set net worth and cash balance
        net_worth_textView = findViewById(R.id.textView_net_worth_value);
        balance_textView = findViewById(R.id.textView_cash_balance_value);

        Calendar calendar = Calendar.getInstance();
        date_textView.setText(new SimpleDateFormat("dd MMMM yyyy").format(calendar.getTime() ) );

        setup_balance();
        setUp_netWorth();
        initial_ticker_Recycle();
        get_all_data_from_Local();


        //set time interval





        //get_latest_price();
        timeInterval.run();






        //initial_ticker_Recycle();
        finnhub_setup();

    }

    private void time_in()
    {
        time = new Timer();
        time.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("run interval");
                get_latest_price();
            }
        },0, 15000);

    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(timeInterval);
        super.onPause();
    }

    //TODO:set the balance and initial_ticker
    @Override
    protected void onResume() {
        super.onResume();


        setup_balance();
        get_all_data_from_Local();
        setUp_netWorth();
        port_Adapter.update_all_data(ticker_port_list, shares_list, total_cost_Value_list, compName_list, price_port, change_port, change_port_percentage);
        fav_Adapter.update_all_data(ticker_fav_list, shares_list, total_cost_Value_list, compName_list, price_fav, change_fav, change_fav_percentage);
        timeInterval.run();

        //initial_ticker_Recycle();
    }



    private void get_latest_price()
    {
        if (first_get_data_from_local)
        {
            first_get_data_from_local = false;
            get_all_data_from_Local();
        }
        /*
        else
        {
            ArrayList<String> ticker_temp_port = new ArrayList<>();
            ArrayList<Integer> share_temp_port = new ArrayList<>();
            ArrayList<Float> total_cost_temp_port = new ArrayList<>();

            ArrayList<String> ticker_temp_fav = new ArrayList<>();
            ArrayList<String> compName_temp_fav = new ArrayList<>();

            for (int i : port_Adapter.order)
            {
                ticker_temp_port.add(this.ticker_port_list.get(i));
                share_temp_port.add(this.shares_list.get(i));
                total_cost_temp_port.add(this.total_cost_Value_list.get(i));
            }
            System.out.println("Before Order: ticker_port: " + ticker_port_list.toString());
            System.out.println("Before Order: ticker_fav: " + ticker_fav_list.toString());
            ticker_port_list.clear();
            shares_list.clear();
            total_cost_Value_list.clear();
            ticker_port_list.addAll(ticker_temp_port);
            shares_list.addAll(share_temp_port);
            total_cost_Value_list.addAll(total_cost_temp_port);


            for (int i : fav_Adapter.order)
            {
                ticker_temp_fav.add(ticker_fav_list.get(i));
                compName_temp_fav.add(compName_list.get(i));
            }

            ticker_fav_list.clear();
            compName_list.clear();
            ticker_fav_list.addAll(ticker_temp_fav);
            compName_list.addAll(compName_temp_fav);

            System.out.println("after Order: ticker_port: " + ticker_port_list.toString());
            System.out.println("after Order: ticker_fav: " + ticker_fav_list.toString());
        }

         */

        //get_data_from_adapter();
        System.out.println("Before");
        System.out.println("get data local ticker port: " + ticker_port_list.toString() + "\nshare list: " + shares_list.toString() + "\ntotal cost list: " + total_cost_Value_list.toString()  + "\nprice list: " + price_port.toString()  + "\nchange list: " + change_port.toString() + "\nchange percentage list: " + change_port_percentage.toString());
        System.out.println("get data local ticker fav: " + ticker_fav_list.toString() + "\ncomp name list: " + compName_list.toString()  + "\nprice list: " + price_fav.toString()  + "\nchange list: " + change_fav.toString() + "\nchange percentage list: " + change_fav_percentage.toString());

        if (ticker_fav_list.size() + ticker_port_list.size() == 0)
        {
            pb.setVisibility(View.GONE);
            cl.setVisibility(View.VISIBLE);
        }

        API_complete = 0;
        for (String ele : ticker_fav_list)
        {
            API_call(ele, false);
        }
        for (String ele : ticker_port_list)
        {
            API_call(ele, true);
        }


    }

    private void setUp_netWorth()
    {
        if (price_port.size() == 0)
        {
            net_worth = balance;
        }
        else
        {
            System.out.println("networth is not equal to balance");
            float sum = 0;
            for (float c : price_port)
            {
                sum += c;
            }
            net_worth = sum + balance;
        }
        System.out.println("price size: " + price_port.size() + price_port.toString()+ ticker_port_list.toString());
        System.out.println("netWorth: " + net_worth);
        net_worth_textView.setText(String.format("$%.2f", net_worth));

    }

    private void update_data_to_adapter_and_local()
    {

        System.out.println("start calculate data");
        if (API_complete == (ticker_port_list.size() + ticker_fav_list.size()))
        {
            setUp_netWorth();
            System.out.println("inside calculate data");

            if (ticker_port_list.size() > 0)
            {
                SharedPreferences sp_port = getSharedPreferences("Trade", MODE_PRIVATE);
                SharedPreferences.Editor ed = sp_port.edit();
                for (String ele : ticker_port_list)
                {
                    int index = ticker_port_list.indexOf(ele);
                    ed.putFloat(ticker + "_ch", change_port.get(index));
                    ed.putFloat(ticker + "_chp",  change_port_percentage.get(index));
                    ed.putFloat(ticker + "_p", price_port.get(index));
                }
                ed.commit();
            }

            if (ticker_fav_list.size() > 0)
            {
                SharedPreferences sp_fav = getSharedPreferences("Ticker", MODE_PRIVATE);
                SharedPreferences.Editor ed = sp_fav.edit();
                for (String ele : ticker_fav_list)
                {
                    int index = ticker_fav_list.indexOf(ele);
                    ed.putFloat(ticker + "_d", change_fav.get(index));
                    ed.putFloat(ticker + "_dp", change_fav_percentage.get(index));
                    ed.putFloat(ticker+"_p", price_fav.get(index));
                }
                ed.commit();
            }



            System.out.println("get data local ticker port: " + ticker_port_list.toString() + "\nshare list: " + shares_list.toString() + "\ntotal cost list: " + total_cost_Value_list.toString()  + "\nprice list: " + price_port.toString()  + "\nchange list: " + change_port.toString() + "\nchange percentage list: " + change_port_percentage.toString());
            System.out.println("get data local ticker fav: " + ticker_fav_list.toString() + "\ncomp name list: " + compName_list.toString()  + "\nprice list: " + price_fav.toString()  + "\nchange list: " + change_fav.toString() + "\nchange percentage list: " + change_fav_percentage.toString());


            port_Adapter.update_all_data(ticker_port_list, shares_list, total_cost_Value_list, compName_list, price_port, change_port, change_port_percentage);
            fav_Adapter.update_all_data(ticker_fav_list, shares_list, total_cost_Value_list, compName_list, price_fav, change_fav, change_fav_percentage);

            cl.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
        }
    }

    private void API_call(String tick, boolean port)
    {
        String url = "https://espressjsbackend-6688.wl.r.appspot.com/price/" + tick;
        JsonObjectRequest myRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(tick + " data_ticker: " + response.toString());
                try {
                        if (port)
                        {
                            int index = ticker_port_list.indexOf(tick);
                            float c = (float) Double.parseDouble(response.getString("c"));
                            float price = c * shares_list.get(index);
                            float change_price = price - total_cost_Value_list.get(index);
                            float change_price_percentage = change_price / total_cost_Value_list.get(index) * 100;


                            change_port.set(index, change_price);
                            change_port_percentage.set(index, change_price_percentage);
                            price_port.set(index, price);


                        }
                        else
                        {
                            int index = ticker_fav_list.indexOf(tick);
                            price_fav.set(index, (float) Double.parseDouble(response.getString("c")));
                            change_fav.set(index, (float) Double.parseDouble(response.getString("d")));
                            change_fav_percentage.set(index, (float) Double.parseDouble(response.getString("dp")));
                        }

                        API_complete += 1;
                        update_data_to_adapter_and_local();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Volley Request Error on " + tick + ": " + error);
            }
        });

        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
    }

    private void get_all_data_from_Local()
    {
        ticker_port_list.clear();
        price_port.clear();
        shares_list.clear();
        total_cost_Value_list.clear();
        change_port.clear();
        change_port_percentage.clear();

        ticker_fav_list.clear();
        compName_list.clear();
        price_fav.clear();
        change_fav.clear();
        change_fav_percentage.clear();

        System.out.println("get data from local");
        System.out.println("ticker from port adapter: " + port_Adapter.ticker_label_list.toString());
        System.out.println("ticker from fav adapter: " + fav_Adapter.ticker_label_list.toString());
        SharedPreferences sp_port = getSharedPreferences("Trade", MODE_PRIVATE);
        SharedPreferences sp_fav = getSharedPreferences("Ticker", MODE_PRIVATE);
        if (sp_fav.contains("ticker_set"))
        {
            Set<String> set = sp_fav.getStringSet("ticker_set", new HashSet<>());
            ticker_fav_list = new ArrayList<String>(set);

            for (String ele : set)
            {
                compName_list.add(sp_fav.getString(ele + "_cn", "No Company"));
                price_fav.add(sp_fav.getFloat(ele + "_p", 1.2f));
                change_fav.add(sp_fav.getFloat(ele + "_d", 1.2f));
                change_fav_percentage.add(sp_fav.getFloat(ele + "_dp", 1.2f));

            }

        }



        if (sp_port.contains("ticker_set"))
        {
            Set<String> set = sp_port.getStringSet("ticker_set", new HashSet<>());
            ticker_port_list = new ArrayList<String>(set);

            for (String ele : set) {
                String share = ele + "_shares";
                String tc = ele + "_tc";
                shares_list.add(sp_port.getInt(share, -1));
                total_cost_Value_list.add(sp_port.getFloat(tc, 1.2f));
                change_port.add(sp_port.getFloat(ele + "_ch", 1.2f));
                change_port_percentage.add(sp_port.getFloat(ele + "_chp", 1.2f));
                price_port.add(sp_port.getFloat(ele + "_p", 1.2f));

            }
        }
        System.out.println("function");
        System.out.println("get data local ticker port: " + ticker_port_list.toString() + "\nshare list: " + shares_list.toString() + "\ntotal cost list: " + total_cost_Value_list.toString()  + "\nprice list: " + price_port.toString()  + "\nchange list: " + change_port.toString() + "\nchange percentage list: " + change_port_percentage.toString());
        System.out.println("get data local ticker fav: " + ticker_fav_list.toString() + "\ncomp name list: " + compName_list.toString()  + "\nprice list: " + price_fav.toString()  + "\nchange list: " + change_fav.toString() + "\nchange percentage list: " + change_fav_percentage.toString());

    }

    private void enableSwipeToDelete()
    {
        //favorites
        CombinedItemTouchHelper combinedItemTouchHelper_fav = new CombinedItemTouchHelper(this, fav_Adapter) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int positioin = viewHolder.getBindingAdapterPosition();
                fav_Adapter.removeItem(positioin);
            }
        };

        ItemTouchHelper itemTouchHelper_fav = new ItemTouchHelper(combinedItemTouchHelper_fav);
        itemTouchHelper_fav.attachToRecyclerView(fav_RV);

        //portfolio
        CombinedItemTouchHelper combinedItemTouchHelper_port = new CombinedItemTouchHelper(this, port_Adapter) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int positioin = viewHolder.getBindingAdapterPosition();
                port_Adapter.removeItem(positioin);
            }
        };

        ItemTouchHelper itemTouchHelper_port = new ItemTouchHelper(combinedItemTouchHelper_port);
        itemTouchHelper_port.attachToRecyclerView(port_RV);
    }


    private void finnhub_setup()
    {
        finnhub = findViewById(R.id.textView_finnhub);
        finnhub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openURL = new Intent(Intent.ACTION_VIEW);
                openURL.setData(Uri.parse("https://finnhub.io/"));
                startActivity(openURL);
            }
        });
    }


    private void setup_balance()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("Trade", MODE_PRIVATE);


        if (sharedPreferences.contains("balance"))
        {
            balance = sharedPreferences.getFloat("balance", 1.2f);
        }
        else
        {
            balance = 25000.00F;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("balance", balance);

            editor.commit();
        }

        balance_textView.setText(String.format("$%.2f", balance));

    }


    //search icon on the action bar
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);


        MenuItem menuItem = menu.findItem(R.id.action_bar_search);

        //todo: change this code to define search view
        SearchView searchView = (SearchView) menuItem.getActionView();
        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setQueryHint("Search...");
        searchView.setOnQueryTextListener(this);



        //search_autoComplete_listView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        search_list = (SearchView.SearchAutoComplete)searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        

        names = new String[]{"Christopher", "Jenny", "Maria", "Steve", "Chris", "Ivana", "Micheal", "Craig",
                "Kelly", "Joseph", "Christine", "Sergio", "Mbariz", "Mike", "Alex"};

        search_suggestions_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, names);
        search_list.setThreshold(1);
        search_list.setAdapter(search_suggestions_adapter);


        //set up search list click listener
        search_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String queryString = (String) adapterView.getItemAtPosition(i);
                System.out.println("click listener: " + queryString);
                int index = queryString.indexOf(" |");
                ticker = queryString.substring(0, index);
                search_list.setText(ticker);
                onQueryTextSubmit(ticker);
            }
        });





        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        ticker = query;
        System.out.println("submit text: " + query);
        ticker_activity = new Intent(this, TickerActivity.class);
        ticker_activity.putExtra("ticker", ticker);
        startActivity(ticker_activity);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (newText.length() > 0) {
            search_suggestions_adapter.clear();
            queue.cancelAll("Tag");
            System.out.println("changing text: |" + newText + "|" + newText.length());
            //search_suggestions_adapter.getFilter().filter(newText);

            // volley http call


            //todo: need to be change
            String url = "https://espressjsbackend-6688.wl.r.appspot.com/auto?ticker=" + newText;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    System.out.println("\n\n\nResponse: ");
                    System.out.println(response);
                    System.out.println("finish\n\n\n");
                    try {
                        JSONArray data = new JSONArray(response);
                        System.out.println("\n\njson object: ");
                        System.out.println(data);

                        names = new String[data.length()];

                        for (int i = 0; i < data.length(); i++)
                        {
                            String temp = data.getJSONObject(i).getString("symbol") + " | " + data.getJSONObject(i).getString("description");
                            names[i] = temp;
                        }

                        //search_suggestions_adapter.clear();
                        search_suggestions_adapter.addAll(names);
                        //search_suggestions_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, names);
                        search_list.setAdapter(search_suggestions_adapter);
                        search_suggestions_adapter.getFilter().filter(newText);

                    } catch (JSONException e) {
                        System.out.println("Cast Error from String to JSON");
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("\n\n\nAutocomplete Error: ");
                    System.out.println(error);
                }
            });

            stringRequest.setTag("Tag");
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(stringRequest);
        }



        return false;
    }


    public void initial_ticker_Recycle()
    {
        //portfolio
        LinearLayoutManager ll_port = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        port_RV = findViewById(R.id.recyclerView_main_portfolio);
        port_RV.setLayoutManager(ll_port);
        port_Adapter = new ticker_RecycleViewAdapter(this, true, ticker_port_list, shares_list, total_cost_Value_list, compName_list, price_port, change_port, change_port_percentage);
        //port_Adapter.setHasStableIds(true);
        port_RV.setAdapter(port_Adapter);
        port_RV.setNestedScrollingEnabled(false);

        //ItemTouchHelper port_itemTH = new ItemTouchHelper(simpleCallback);
        //port_itemTH.attachToRecyclerView(port_RV);

        RecyclerView.ItemAnimator animator_port = port_RV.getItemAnimator();
        if (animator_port instanceof SimpleItemAnimator)
        {
            ((SimpleItemAnimator) animator_port).setSupportsChangeAnimations(false);
        }


        //favorites
        LinearLayoutManager ll_fav = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        fav_RV = findViewById(R.id.recyclerView_main_favorites);
        fav_RV.setLayoutManager(ll_fav);
        fav_Adapter = new ticker_RecycleViewAdapter(this, false, ticker_fav_list, shares_list, total_cost_Value_list, compName_list, price_fav, change_fav, change_fav_percentage);
        //fav_Adapter.setHasStableIds(true);
        fav_RV.setAdapter(fav_Adapter);
        fav_RV.setNestedScrollingEnabled(false);

        RecyclerView.ItemAnimator animator_fav = fav_RV.getItemAnimator();
        if (animator_fav instanceof SimpleItemAnimator)
        {
            ((SimpleItemAnimator) animator_fav).setSupportsChangeAnimations(false);
        }



        //swipe
        enableSwipeToDelete();

    }

    /*
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getBindingAdapterPosition();
            if (direction == ItemTouchHelper.LEFT)
            {
                port_Adapter.removeItem(position);
            }
        }
    };

     */



}










