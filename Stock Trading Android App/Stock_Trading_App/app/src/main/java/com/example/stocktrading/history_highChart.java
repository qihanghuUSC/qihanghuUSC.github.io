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
 * Use the {@link history_highChart#newInstance} factory method to
 * create an instance of this fragment.
 */
public class history_highChart extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private WebView webView_histocial_chart;

    public history_highChart() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment history_highChart.
     */
    // TODO: Rename and change types and number of parameters
    public static history_highChart newInstance(String param1, String param2) {
        history_highChart fragment = new history_highChart();
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

        View view = inflater.inflate(R.layout.fragment_history_high_chart, container, false);
        // Inflate the layout for this fragment

        webView_histocial_chart = view.findViewById(R.id.WebView_historical_chart);
        webView_histocial_chart.loadUrl("file:///android_asset/historical_chart.html");
        WebSettings webSettings = webView_histocial_chart.getSettings();
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptEnabled(true);
        webView_histocial_chart.setWebViewClient(new MyWebViewClient());
        webView_histocial_chart.setWebChromeClient(new MyWebChromeClient());
        webView_histocial_chart.addJavascriptInterface(new JavaScriptInterface(getActivity()), "Android");


        return view;
    }
}