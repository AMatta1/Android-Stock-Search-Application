package com.example.stock_search;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Autocomplete {
    private static Autocomplete mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    public Autocomplete(Context ctx) {
        mCtx = ctx;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized Autocomplete getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Autocomplete(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public static void make(Context ctx, String query, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://androidhw9backend-env.eba-dau8fdnw.us-east-1.elasticbeanstalk.com/getDataAPI_4_autoComplete?user_input=" + query;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,listener, errorListener);
        Autocomplete.getInstance(ctx).addToRequestQueue(stringRequest);
    }
}