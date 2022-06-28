package com.example.stocktrading;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JavaScriptInterface {

    Context mContext;
    public String color;

    JavaScriptInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public String getTicker()
    {
        return TickerActivity.ticker_high_Chart;
    }

    @JavascriptInterface
    public String chart_color()
    {
        System.out.println("\n\nChart color: " + TickerActivity.chart_color);
        return TickerActivity.chart_color;
    }

    @JavascriptInterface
    public void showText(String text)
    {
        System.out.println("show Text: " + text);
    }
}
