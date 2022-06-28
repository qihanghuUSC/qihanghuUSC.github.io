package com.example.stocktrading;

import static android.graphics.Color.BLUE;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class TickerActivity extends AppCompatActivity {


    public String ticker;
    public static String ticker_high_Chart;
    public static String chart_color = "";
    private Intent previous_activity;
    private RequestQueue queue;
    //TODO: change this URL_pre link
    private String URL_pre = "https://espressjsbackend-6688.wl.r.appspot.com/";
    private ImageView logo_img, trend_img;
    private TextView company_sym, company_name, latest_price, price_change;

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter viewPagerAdapter;

    //highChart
    private hourly_highChart hourly_chart_fragment;
    private history_highChart history_highChart_frament;

    //TextView
    private TextView open_price_textView;
    private TextView low_price_textView;
    private TextView high_price_textView;
    private TextView pre_close_price_textView;
    private TextView ipo_textView;
    private TextView industry_textView;
    private TextView webpage_textview;

    //peer
    private RecyclerView peer_RecyclerView;
    private ArrayList<String> peers_list;

    //high chart
    private WebView webView_RT;
    private WebView webView_EPS;

    //news
    private ArrayList<String> source_list = new ArrayList<>();
    private ArrayList<String> time_list = new ArrayList<>();
    private ArrayList<String> title_list = new ArrayList<>();
    private ArrayList<String> image_URL_list = new ArrayList<>();
    private ArrayList<String> sum_list = new ArrayList<>();
    private ArrayList<String> date_list = new ArrayList<>();
    private RecyclerView news_RecycleView;
    private ImageView first_news_image;
    private TextView first_news_source;
    private TextView first_news_time;
    private TextView first_news_title;
    private CardView first_news_cardView;
    private ArrayList<String> URL_list = new ArrayList<>();

    //local storage
    //SharedPreferences sharedPreferences;
    //SharedPreferences.Editor editor;

    private String comp_name;
    private float last_price;
    private float value;
    private int trade_shares_numb;
    private int total_shares_numb;
    private float total_cost;
    private float balance;
    private float change_price;
    private float change_price_percentage;

    //portfolio
    private TextView shares_own_textView;
    private TextView ave_cost_textView;
    private TextView total_cost_textView;
    private TextView change_textView;
    private TextView market_value_textView;

    ProgressBar spinner;
    ConstraintLayout cl;
    int loaded_number = 0;






    //TODO: set up net balance and set time out
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticker);

        spinner = findViewById(R.id.progressbar_ticker);
        cl = findViewById(R.id.Con_Layout_ticker);

        spinner.setVisibility(View.VISIBLE);
        cl.setVisibility(View.GONE);

        queue = Volley.newRequestQueue(this);
        previous_activity = getIntent();
        ticker = previous_activity.getStringExtra("ticker").toUpperCase(Locale.ROOT);
        chart_color = "";
        ticker_high_Chart = ticker;




        //declare textViews and imageView
        logo_img = findViewById(R.id.imageView_company_logo);
        trend_img = findViewById(R.id.imageView_trend);
        company_sym = findViewById(R.id.textView_company_symbol);
        company_name = findViewById(R.id.textView_company_name);
        latest_price = findViewById(R.id.textView_latest_price);
        price_change = findViewById(R.id.textView_price_change);


        //high chart

        //TextView
        open_price_textView = findViewById(R.id.textView_open_price);
        low_price_textView = findViewById(R.id.textView_low_price);
        high_price_textView = findViewById(R.id.textView_high_price);
        pre_close_price_textView = findViewById(R.id.textView_prev_close);
        ipo_textView = findViewById(R.id.textView_ipo);
        industry_textView = findViewById(R.id.textView_industry);
        webpage_textview = findViewById(R.id.textView_webpage);


        //peer
        peer_RecyclerView = findViewById(R.id.peer_recycle_view);

        //high chart
        webView_RT = findViewById(R.id.webView_RT);
        webView_EPS = findViewById(R.id.webView_EPS);

        //news
        first_news_image = findViewById(R.id.imageView_frist_news);
        first_news_source = findViewById(R.id.textView_first_news_source);
        first_news_cardView = findViewById(R.id.cardView_first_news);
        first_news_time = findViewById(R.id.textView_first_news_time);
        first_news_title = findViewById(R.id.textView_first_news_tilte);







        //high chart


        //set backward action
        /*
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

         */

        System.out.println("Ticker: " + ticker);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(ticker.toUpperCase(Locale.ROOT));

        //portfolio_setUp();
        trade_btn_setup();


        get_Data();
        //TODO: check out toast on trade dialog

    }

    private void complete_loading()
    {
        if (loaded_number == 5)
        {
            spinner.setVisibility(View.GONE);
            cl.setVisibility(View.VISIBLE);
        }
    }

    private void portfolio_setUp()
    {
        SharedPreferences sp = getSharedPreferences("Trade", MODE_PRIVATE);
        balance = sp.getFloat("balance", 1.2f);
        if (sp.contains(ticker + "_tc"))
        {
            total_cost = sp.getFloat(ticker + "_tc", 1.2f);
            total_shares_numb = sp.getInt(ticker + "_shares", -1);
        }
        else
        {
            total_cost = 0;
            total_shares_numb = 0;
        }

        shares_own_textView = findViewById(R.id.textView_shares_owned);
        ave_cost_textView = findViewById(R.id.textView_Avg_Cost_Share);
        total_cost_textView = findViewById(R.id.textView_total_cost);
        change_textView = findViewById(R.id.textView_change);
        market_value_textView = findViewById(R.id.textView_market_value);


        //System.out.println("total shoares number: " + total_shares_numb);
        shares_own_textView.setText(Integer.toString(total_shares_numb));
        total_cost_textView.setText(String.format("$%.2f", total_cost));


        if (total_shares_numb == 0)
        {
            ave_cost_textView.setText("$0.00");
            change_textView.setText("$0.00");
            market_value_textView.setText("$0.00");
        }
        else
        {
            float dif = (last_price * total_shares_numb - total_cost);
            ave_cost_textView.setText(String.format("$%.2f", total_cost/total_shares_numb));
            change_textView.setText(String.format("$%.2f", dif));
            market_value_textView.setText(String.format("$%.2f", total_shares_numb * last_price));

            if (dif < 0)
            {
                change_textView.setTextColor(Color.parseColor("#F10327"));
                market_value_textView.setTextColor(Color.parseColor("#F10327"));
            }
            else if (dif > 0)
            {
                change_textView.setTextColor(Color.parseColor("#5AEF0A"));
                market_value_textView.setTextColor(Color.parseColor("#5AEF0A"));
            }
        }

    }

    private void trade_btn_setup()
    {
        Button btn = findViewById(R.id.button_trade);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println("click trade button");
                final Dialog dialog = new Dialog(TickerActivity.this);
                dialog.setContentView(R.layout.trade_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView title = dialog.findViewById(R.id.textView_title_trade_layout);
                EditText shares = dialog.findViewById(R.id.editText_trade_layout);
                TextView calculation = dialog.findViewById(R.id.textView_trade_calculation);
                TextView fund = dialog.findViewById(R.id.textView_trade_fund);
                Button buy = dialog.findViewById(R.id.button_buy_trade_layout);
                Button sell = dialog.findViewById(R.id.button_sell_trade_layout);

                title.setText("Trade " + comp_name + " shares");



                fund.setText(String.format("$%.2f", balance) + " to buy " + ticker);

                String cal = "0*$" + last_price +
                        "/share = " + String.format("$%.2f", 0 * last_price);
                calculation.setText(cal);

                shares.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {


                        if (s.toString().length() > 0) {
                            trade_shares_numb = Integer.parseInt(s.toString());
                            value = (float)Double.parseDouble(s.toString()) * last_price;
                            String cal = String.format("$%.1f", Double.parseDouble(s.toString())) + "*$" + last_price +
                                    "/share = " + String.format("$%.2f", value);
                            calculation.setText(cal);

                        }
                        else
                        {
                            String cal = "0*$" + last_price +
                                    "/share = " + String.format("$%.2f", 0 * last_price);
                            calculation.setText(cal);
                            value = 0;
                            trade_shares_numb = 0;
                        }

                    }
                });

                buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (value == 0.0)
                        {
                            Toast.makeText(dialog.getContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                        }
                        else if (value > balance)
                        {
                            Toast.makeText(dialog.getContext(), "Not enough money to buy", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            dialog.setContentView(R.layout.buy_sell_success_layout);
                            TextView message = dialog.findViewById(R.id.textView_message_sell_buy_layout);
                            message.setText("You have successfully brought " + trade_shares_numb + " shares of " + ticker);
                            Button done = dialog.findViewById(R.id.button_done_sell_buy_layout);
                            done.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });

                            total_shares_numb += trade_shares_numb;
                            total_cost += value;
                            balance -= value;

                            update_on_shares_and_balance();


                        }
                    }
                });


                sell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //System.out.println("Sell button clicks");
                        //System.out.println("value: " + value);
                        if (trade_shares_numb == 0.0)
                        {
                            //System.out.println("value equal to zero");
                            Toast.makeText(dialog.getContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                        }
                        else if (trade_shares_numb > total_shares_numb)
                        {
                            Toast.makeText(dialog.getContext(), "Not enough shares to sell", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            dialog.setContentView(R.layout.buy_sell_success_layout);
                            TextView message = dialog.findViewById(R.id.textView_message_sell_buy_layout);
                            message.setText("You have successfully sold " + trade_shares_numb + " shares of " + ticker);
                            Button done = dialog.findViewById(R.id.button_done_sell_buy_layout);
                            done.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });

                            total_shares_numb -= trade_shares_numb;
                            total_cost -= value;
                            balance += value;
                            if (total_shares_numb == 0)
                            {
                                total_cost = 0;
                                change_textView.setTextColor(Color.BLACK);
                                market_value_textView.setTextColor(Color.BLACK);
                            }

                            update_on_shares_and_balance();
                        }

                    }
                });

                dialog.show();
            }
        });

    }

    private void update_on_shares_and_balance()
    {
        SharedPreferences sp = getSharedPreferences("Trade", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        Set<String> set;
        int dif = (int) (last_price * total_shares_numb - total_cost)*100;


        if (total_shares_numb == 0)
        {
            ed.remove(ticker+"_shares");
            ed.remove(ticker + "_tc");
            ed.remove(ticker + "_ch");
            ed.remove(ticker + "_chp");
            ed.remove(ticker + "_p");
            //ed.remove(ticker+"_cn");

            set = sp.getStringSet("ticker_set", new HashSet<>());

            set.remove(ticker);


        }
        else
        {
            ed.putInt(ticker+"_shares", total_shares_numb);
            ed.putFloat(ticker + "_tc", total_cost);
            ed.putFloat(ticker + "_ch", dif);
            ed.putFloat(ticker + "_chp",  dif / total_cost * 100);
            ed.putFloat(ticker + "_p", total_shares_numb * last_price);
            //ed.putString(ticker+"_cn", comp_name);

            if (sp.contains("ticker_set"))
            {
                set = sp.getStringSet("ticker_set", new HashSet<>());
            }
            else
            {
                set = new HashSet<>();
            }
            set.add(ticker);
        }
        ed.putStringSet("ticker_set", set);
        ed.putFloat("balance", balance);
        ed.commit();

        //update portfolio section
        shares_own_textView.setText(Integer.toString(total_shares_numb));
        total_cost_textView.setText(String.format("$%.2f", total_cost));



        if (total_shares_numb == 0)
        {
            ave_cost_textView.setText("$0.00");
            change_textView.setText("$0.00");
            market_value_textView.setText("$0.00");
        }
        else
        {
            ave_cost_textView.setText(String.format("$%.2f", total_cost/total_shares_numb));

            market_value_textView.setText(String.format("$%.2f", total_shares_numb * last_price));

            System.out.println("dif: " + dif);
            if (dif < 0)
            {
                change_textView.setText(String.format("$%.2f", dif/100.0));
                change_textView.setTextColor(Color.parseColor("#F10327"));
                market_value_textView.setTextColor(Color.parseColor("#F10327"));
            }
            else if (dif > 0)
            {
                change_textView.setText(String.format("$%.2f", dif/100.0));
                change_textView.setTextColor(Color.parseColor("#5AEF0A"));
                market_value_textView.setTextColor(Color.parseColor("#5AEF0A"));
            }
            else
            {
                change_textView.setText(String.format("$%.2f", dif/100.0));
                change_textView.setTextColor(Color.BLACK);
                market_value_textView.setTextColor(Color.BLACK);
            }
        }


    }

    private void get_Data()
    {
        //loadBookmarked();

        //set up url
        String url_profile = URL_pre + "profile/" + ticker;
        String url_price = URL_pre + "price/" + ticker;
        String url_peer = URL_pre + "peers/" + ticker;
        String url_social = URL_pre + "social/" + ticker;
        String url_news = URL_pre + "news/" + ticker;


        //set up volley
        profile_data(url_profile);
        price_data(url_price);
        peer_data(url_peer);
        social_data(url_social);
        news_data(url_news);


        highChart_RT_EPS();




    }



    private void news_data(String url)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response = response.substring(1, response.length()-1);
                //System.out.println("\n\n\nnews data : " + response);


                Gson gson = new Gson();
                news[] news_list = gson.fromJson(response, news[].class);



                //System.out.println("\n\n\nconvert data : " );

                //first news setup
                first_news_image.setClipToOutline(true);
                Glide.with(TickerActivity.this)
                        .asBitmap()
                        .load(news_list[0].image)
                        .into(first_news_image);

                first_news_source.setText(news_list[0].source);
                first_news_time.setText(news_list[0].time_df);
                first_news_title.setText(news_list[0].headline);


                first_news_cardView = findViewById(R.id.cardView_first_news);
                first_news_cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //System.out.println("Click the first news card view");
                        final Dialog dialog = new Dialog(TickerActivity.this);
                        dialog.setContentView(R.layout.news_dialog_layout);

                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



                        TextView source_dl = dialog.findViewById(R.id.textView_Source_newsDia);
                        TextView time_dl = dialog.findViewById(R.id.textView_time_newsDia);
                        TextView title_dl = dialog.findViewById(R.id.textView_headline_newDia);
                        TextView sum_dl = dialog.findViewById(R.id.textView_sum_newsDia);

                        source_dl.setText(news_list[0].source);
                        time_dl.setText(news_list[0].datetime);
                        title_dl.setText(news_list[0].headline);
                        sum_dl.setText(news_list[0].summary);

                        ImageButton chrome = dialog.findViewById(R.id.imageButton_chrome);
                        ImageButton twitter = dialog.findViewById(R.id.imageButton_tw);
                        ImageButton facebook = dialog.findViewById(R.id.imageButton_fb);

                        chrome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent openURL = new Intent(Intent.ACTION_VIEW);
                                openURL.setData(Uri.parse(news_list[0].url));
                                TickerActivity.this.startActivity(openURL);
                            }
                        });

                        twitter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent openURL = new Intent(Intent.ACTION_VIEW);
                                String url = "https://twitter.com/intent/tweet?text=" + news_list[0].headline + "+%20+" + news_list[0].url;
                                openURL.setData(Uri.parse(url));
                                //System.out.println("twitter link");
                                //System.out.println(url);
                                TickerActivity.this.startActivity(openURL);
                            }
                        });

                        facebook.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent openURL = new Intent(Intent.ACTION_VIEW);
                                String url = "https://www.facebook.com/sharer/sharer.php?u=" + news_list[0].url;
                                openURL.setData(Uri.parse(url));
                                //System.out.println(url);
                                TickerActivity.this.startActivity(openURL);
                            }
                        });

                        dialog.show();

                    }
                });

                boolean first = true;
                for (news n : news_list)
                {
                    if (first)
                    {
                        first = false;
                        continue;
                    }
                    source_list.add(n.source);
                    time_list.add(n.time_df);
                    image_URL_list.add(n.image);
                    title_list.add(n.headline);
                    sum_list.add(n.summary);
                    date_list.add(n.datetime);
                    URL_list.add(n.url);
                    //System.out.println(n.headline);

                }
                //System.out.println("news size: " + source_list.size());
                initNewsRecycleView();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("\n\n\nNews volley call Error: " + error);
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    private void initNewsRecycleView()
    {
        LinearLayoutManager news_layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        news_RecycleView = findViewById(R.id.recycleView_news);
        news_RecycleView.setLayoutManager(news_layoutManager);
        news_RecycleViewAdapter adapter = new news_RecycleViewAdapter(this, source_list, time_list, title_list, image_URL_list, sum_list, date_list, URL_list);
        news_RecycleView.setAdapter(adapter);
        news_RecycleView.setNestedScrollingEnabled(false);
    }






    private void highChart_RT_EPS()
    {
        webView_RT.loadUrl("file:///android_asset/RT_chart.html");
        WebSettings webSettings = webView_RT.getSettings();
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptEnabled(true);
        webView_RT.setWebViewClient(new MyWebViewClient());
        webView_RT.setWebChromeClient(new MyWebChromeClient());
        webView_RT.addJavascriptInterface(new JavaScriptInterface(this), "Android");


        webView_EPS.loadUrl("file:///android_asset/EPS_chart.html");
        WebSettings webSettings_EPS = webView_EPS.getSettings();
        webSettings_EPS.setAllowFileAccess(true);
        webSettings_EPS.setJavaScriptEnabled(true);
        webView_EPS.setWebViewClient(new MyWebViewClient());
        webView_EPS.setWebChromeClient(new MyWebChromeClient());
        webView_EPS.addJavascriptInterface(new JavaScriptInterface(this), "Android");


        loaded_number += 1;
        complete_loading();
        System.out.println("high chart EPS and RT loaded number: " + loaded_number);
    }


    private void social_data(String url)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.replace("[", "");
                response = response.replace("]", "");
                //System.out.println("\n\n\nsocial data : " + response);
                String[] resp = response.split(",");
                ArrayList<String> list = new ArrayList<String>(Arrays.asList(resp));
                //System.out.println(list.get(2));

                //TextView
                TextView tm_re = findViewById(R.id.textView_tm_re);
                TextView tm_tw = findViewById(R.id.textView_tm_tw);

                TextView pm_re = findViewById(R.id.textView_pm_re);
                TextView pm_tw = findViewById(R.id.textView_pm_tw);

                TextView nm_re = findViewById(R.id.textView_nm_re);
                TextView nm_tw = findViewById(R.id.textView_nm_tw);

                pm_re.setText(list.get(0));
                nm_re.setText(list.get(1));
                tm_re.setText(list.get(2));

                pm_tw.setText(list.get(3));
                nm_tw.setText(list.get(4));
                tm_tw.setText(list.get(5));


                loaded_number += 1;
                complete_loading();
                System.out.println("social loaded number: " + loaded_number);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("\n\n\nSocial volley call Error: " + error);
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }


    private void peer_data(String url)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                peers_list = new ArrayList<>();
                response = response.substring(1, response.length()-1);
               // System.out.println("\n\n\npeer data : " + response);
                String[] str = response.split(",");
                //System.out.println("\n\n\npeer data : " + str[3]);
                for (String ele: str)
                {
                    //System.out.println(ele.substring(1,ele.length()-1) );
                    peers_list.add(ele.substring(1,ele.length()-1) + ",");
                }

                loaded_number += 1;
                complete_loading();
                System.out.println("peer loaded number: " + loaded_number);
                initPeersRecyclerView();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("\n\n\nPeer volley call Error: " + error);
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    private void initPeersRecyclerView()
    {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        peer_RecyclerView.setLayoutManager(linearLayoutManager);
        peer_RecycleViewAdapter peer_recycleViewAdapter = new peer_RecycleViewAdapter(peers_list, this);
        peer_RecyclerView.setAdapter(peer_recycleViewAdapter);
        SpacingItemDecorator peers_item_divider = new SpacingItemDecorator(20);
        peer_RecyclerView.addItemDecoration(peers_item_divider);

    }





    private void price_data(String url)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject data;
                //System.out.println("\n\n\nprice data : " + response);
                try {
                    data = new JSONObject(response);
                    //System.out.println("\n\n\ncurrent price : " + data.getString("c"));

                    latest_price.setText("$" + data.getString("c"));
                    last_price = (float)Double.parseDouble(data.getString("c"));
                    change_price = (float) Double.parseDouble(data.getString("d"));
                    change_price_percentage = (float) Double.parseDouble(data.getString("dp"));
                    portfolio_setUp();
                    price_change.setText("$" + data.getString("d") + "(" + data.getString("dp") + "%)");

                    open_price_textView.setText(" $" + data.getString("o"));
                    low_price_textView.setText(" $" + data.getString("l"));
                    high_price_textView.setText(" $" + data.getString("h"));
                    pre_close_price_textView.setText(" $" + data.getString("pc"));


                    Double change = Double.parseDouble( data.getString("d") );

                    if (change > 0)
                    {
                        price_change.setTextColor(Color.parseColor("#5AEF0A"));
                        trend_img.setImageResource(R.drawable.ic_trend_up);
                        chart_color = "green";

                    }
                    else if (change < 0)
                    {
                        price_change.setTextColor(Color.parseColor("#F10327"));
                        trend_img.setImageResource(R.drawable.ic_trend_down);
                        chart_color = "red";
                    }
                    else
                    {
                        chart_color = "black";
                    }

                    //declare tabLayout and ViewPager2
                    tabLayout = findViewById(R.id.tabLayout);
                    viewPager2 = findViewById(R.id.fragment_viewPage);
                    viewPagerAdapter = new ViewPagerAdapter(TickerActivity.this);
                    viewPager2.setAdapter(viewPagerAdapter);


                    new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
                        @Override
                        public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                            if (position == 0)
                            {
                                tab.setIcon(R.drawable.ic_hourly_chart);
                            }
                            else
                            {
                                tab.setIcon(R.drawable.ic_historical_chart);
                            }
                        }
                    }).attach();

                    tabLayout.setSelectedTabIndicatorColor(BLUE);

                    tabLayout.getTabAt(0).getIcon().setTint(BLUE);
                    //System.out.println("\n\n\ndefault: " + tabLayout.getSelectedTabPosition());
                    //tabLayout.setTabIconTint(ColorStateList.valueOf(Color.RED));

                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            tab.getIcon().setTint(BLUE);
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {
                            tab.getIcon().setTint(Color.BLACK);
                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });

                    loaded_number += 1;
                    complete_loading();
                    System.out.println("price loaded number: " + loaded_number);

                } catch (JSONException e) {
                    System.out.println("Cannot cast response to JSON Object in price");
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("\n\n\nProfile volley call Error: " + error);
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    private void profile_data(String url)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //System.out.println("\n\n\nProfile data : " + response);
                try {
                    JSONObject data = new JSONObject(response);
                    //System.out.println("\n\n\ntesting data : " + data.getString("logo"));

                    company_sym.setText(data.getString("ticker"));
                    company_name.setText(data.getString("name"));
                    comp_name = data.getString("name");
                    String ipo = data.getString("ipo");
                    TextView table_compName = findViewById(R.id.textView_comName_insight);
                    table_compName.setText(data.getString("name"));

                    ipo_textView.setText(ipo.substring(5) + "-" + ipo.substring(0,4));
                    industry_textView.setText(data.getString("finnhubIndustry"));

                    webpage_textview.setClickable(true);
                    webpage_textview.setMovementMethod(LinkMovementMethod.getInstance());
                    String url = "<a href='" + data.getString("weburl") + "' target='_black'>" + data.getString("weburl") + "</a>";
                    webpage_textview.setText(Html.fromHtml(url));


                    //set company logo
                    Glide.with(TickerActivity.this)
                            .load(data.getString("logo"))
                            .into(logo_img);

                    loaded_number += 1;
                    complete_loading();
                    System.out.println("profile loaded number: " + loaded_number);

                } catch (JSONException e) {
                    System.out.println("Cannot cast response to JSON Object in profile");
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("\n\n\nProfile volley call Error: " + error);
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ticker_activity_menu, menu);

        MenuItem favorite_icon = menu.findItem(R.id.action_favorite);

        //System.out.println("Process bookmark");
        SharedPreferences sharedPreferences = getSharedPreferences("Ticker", MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPreferences.edit();

        if (sharedPreferences.contains("ticker_set"))
        {
            Set<String> set = sharedPreferences.getStringSet("ticker_set", new HashSet<String>());
            List<String> ticker_set = new ArrayList<String>(set);
            if (ticker_set.contains(ticker))
            {
                System.out.println("ticker set contains this ticker");
                favorite_icon.setIcon(R.drawable.ic_filled_star_favorite);
            }
        }
        else
        {
            System.out.println("do not contain this ticker");
        }

        //System.out.println("finish bookmark");


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.action_favorite:

                Drawable favorite_empty = getResources().getDrawable(R.drawable.ic_empty_start_favorite);

                //TODO: add or delete on favorite list from local storage
                if (item.getIcon().getConstantState().equals(favorite_empty.getConstantState()) )
                {
                    //System.out.println("\n\n\n favorite equal to empty start");
                    item.setIcon(R.drawable.ic_filled_star_favorite);

                    SharedPreferences sharedPreferences = getSharedPreferences("Ticker", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    //editor.putString("ticker", ticker);
                    Set<String> set = sharedPreferences.getStringSet("ticker_set", new HashSet<String>());
                    set.add(ticker);

                    editor.putStringSet("ticker_set", set);
                    editor.putString(ticker+"_cn", comp_name);
                    editor.putFloat(ticker + "_d", change_price);
                    editor.putFloat(ticker + "_dp", change_price_percentage);
                    editor.putFloat(ticker+"_p", last_price);

                   // System.out.println("set: |" + set + "|");
                    editor.commit();
                    Toast.makeText(TickerActivity.this, ticker + " is added to favorites", Toast.LENGTH_SHORT).show();


                }
                else
                {
                    //System.out.println("\n\n\n favorite equal to filled start");
                    item.setIcon(R.drawable.ic_empty_start_favorite);
                    SharedPreferences sharedPreferences = getSharedPreferences("Ticker", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Set<String> set = sharedPreferences.getStringSet("ticker_set", new HashSet<String>());
                    set.remove(ticker);
                    editor.remove(ticker + "_d");
                    editor.remove(ticker + "_dp");
                    editor.remove(ticker + "_p");
                    //System.out.println("set: |" + set + "|");
                    editor.putStringSet("ticker_set", set);
                    editor.remove(ticker+"_cn");
                    editor.commit();
                    Toast.makeText(TickerActivity.this, ticker + " is removed from favorites", Toast.LENGTH_SHORT).show();
                }
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }






}