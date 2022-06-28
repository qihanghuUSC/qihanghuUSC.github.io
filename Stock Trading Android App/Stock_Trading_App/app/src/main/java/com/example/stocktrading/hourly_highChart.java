package com.example.stocktrading;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link hourly_highChart#newInstance} factory method to
 * create an instance of this fragment.
 */
public class hourly_highChart extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WebView hourly_chart_webView;



    public hourly_highChart() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment hourly_highChart.
     */
    // TODO: Rename and change types and number of parameters
    public static hourly_highChart newInstance(String param1, String param2) {
        hourly_highChart fragment = new hourly_highChart();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_hourly_high_chart, container, false);


        hourly_chart_webView = view.findViewById(R.id.webview_hourly_chart);
        hourly_chart_webView.loadUrl("file:///android_asset/hourly_chart.html");
        WebSettings hourly_webSettings = hourly_chart_webView.getSettings();
        hourly_webSettings.setAllowFileAccess(true);
        hourly_webSettings.setJavaScriptEnabled(true);
        hourly_chart_webView.setWebViewClient(new MyWebViewClient(){
            public void onPageFinished(WebView view, String url)
            {
                //System.out.println("testing call js function");
                hourly_chart_webView.loadUrl("javascript:init()");
               // System.out.println("finish");
            }
        });
        hourly_chart_webView.setWebChromeClient(new MyWebChromeClient());
        hourly_chart_webView.addJavascriptInterface(new JavaScriptInterface(getActivity()), "Android");







        return view;
    }


}