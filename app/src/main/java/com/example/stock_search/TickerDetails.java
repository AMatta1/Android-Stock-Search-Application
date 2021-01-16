package com.example.stock_search;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TickerDetails extends AppCompatActivity {

    private static  final String TAG = "TickerDetails";
    WebView wv;
    private ImageView star;
    private String tickerName;
    private String stockName="";
    private boolean starClicked = false;
    JSONObject summary = new JSONObject();
    JSONObject details = new JSONObject();
    private static SharedPref sharedPref;


    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private List<CardItem> last;
    private AdapterCard adapter;
    private RecyclerView recyclerView;
    private RelativeLayout progressBar;
    private RelativeLayout whole_layout;
    Context context;
    private Button showMoreButton;
    private TextView aboutStock;
    private ArrayList<ArrayList<String>> newsArrayData = new ArrayList<ArrayList<String>>();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);
        String[] query = getIntent().getStringExtra("query").split("-");
        this.tickerName = query[0].trim().toUpperCase();

        //Log.d("aaaayuuuu",tickerName+" "+stockName);
        webViewChart(tickerName);
        Toolbar toolbar = findViewById(R.id.top_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_grey_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG,"clicked Back....");
                finish();
            }
        });
        sharedPref = new SharedPref(getApplicationContext());
        star = findViewById(R.id.starToggle);
        star.setVisibility(View.INVISIBLE);

        context = this;
        recyclerView = findViewById(R.id.recyclerv_view);
        whole_layout = findViewById(R.id.whole_layout_details);
        progressBar = findViewById(R.id.progress_bar_layout_details);
        progressBar.setVisibility(View.VISIBLE);
        whole_layout.setVisibility(View.INVISIBLE);
        showMoreButton = (Button) findViewById(R.id.button_showmore);
        aboutStock = findViewById(R.id.textView_about);
        final int[] about_count_lines = {0};
        this.getSummary(this.tickerName);
    }
    private void checkStarClicked() {
        ArrayList<String> favList = sharedPref.arrayReadFromSP("favorite");
        if(!favList.contains(tickerName)) {
            starClicked = false;
            star.setImageResource(R.drawable.ic_baseline_star_border_24);
        }
        else{
            starClicked = true;
            star.setImageResource(R.drawable.ic_baseline_star_24);
        }
        star.setVisibility(View.VISIBLE);
    }

    private void webViewChart(String query)
    {
        wv = (WebView) findViewById(R.id.webView);
        wv.setWebViewClient(new WebViewClient());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl("file:///android_asset/charts.html?data="+query);
    }
    private void getSummary(String ticker){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = "http://androidhw9backend-env.eba-dau8fdnw.us-east-1.elasticbeanstalk.com/getDataAPI_1?tickerVal=" + ticker;
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                summary =response;
                getDetails(ticker);
            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        requestQueue.add(getRequest);
    }
    private void getDetails(String ticker){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = "http://androidhw9backend-env.eba-dau8fdnw.us-east-1.elasticbeanstalk.com/getDataAPI_2?tickerVal=" + ticker;

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null, (Response.Listener<JSONArray>) response -> {

            try {
                details = response.getJSONObject(0);
                stockName = summary.getString("name");
                loadDataToGUI();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        },
                (Response.ErrorListener) error -> Log.e("error",error.toString())
        );
        requestQueue.add(getRequest);
    }

    private void loadDataToGUI() throws JSONException {
        checkStarClicked();


/*
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(starClicked)
                {
                    //remove from storage
                    ArrayList<String> favList = sharedPref.arrayReadFromSP("favorite");
                    favList.remove(tickerName);
                    sharedPref.arrayInsertToSP(favList,"favorite");
                    sharedPref.removeFromSP("fav" +tickerName);
                    Toast.makeText(getApplicationContext(),"\"" + tickerName+ "\" was removed from favorites",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //insert into storage
                    ArrayList<String> favList = sharedPref.arrayReadFromSP("favorite");
                    favList.add(tickerName);
                    sharedPref.arrayInsertToSP(favList,"favorite");
                    Toast.makeText(getApplicationContext(),"\"" + tickerName+ "\" was added to favorites",Toast.LENGTH_SHORT).show();
                    HashMap<String, String> name_mapping= sharedPref.readFromSP("name_mapping");
                    name_mapping.put(tickerName,stockName);
                    sharedPref.insertToSP(name_mapping,"name_mapping");
                }
                starClicked=!starClicked;
                checkStarClicked();
            }
        });*/



        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(starClicked)
                {
                    //remove from storage
                    ArrayList<String> favList = sharedPref.arrayReadFromSP("favorite");
                    favList.remove(tickerName);
                    sharedPref.arrayInsertToSP(favList,"favorite");
                    Toast.makeText(getApplicationContext(),"\"" + tickerName+ "\" was removed from favorites",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //insert into storage
                    ArrayList<String> favList = sharedPref.arrayReadFromSP("favorite");
                    favList.add(tickerName);
                    sharedPref.arrayInsertToSP(favList,"favorite");
                    Toast.makeText(getApplicationContext(),"\"" + tickerName+ "\" was added to favorites",Toast.LENGTH_SHORT).show();
                    HashMap<String, String> name_mapping= sharedPref.readFromSP("name_mapping");
                    name_mapping.put(tickerName,stockName);
                    sharedPref.insertToSP(name_mapping,"name_mapping");
                }
                starClicked=!starClicked;
                checkStarClicked();
            }
        });
        TextView tickerShortName = findViewById(R.id.tickerShortName);
        tickerShortName.setText(tickerName);
        ((TextView)findViewById(R.id.tickerFullName)).setText(stockName);
        ((TextView)findViewById(R.id.tickerFullName)).setText(stockName);
        //Log.d(TAG,summary.toString());
        ((TextView)findViewById(R.id.currPrice)).setText("$"+details.getString("last"));


        double change = Float.valueOf(details.getString("last")) - Float.valueOf(details.getString("prevClose"));
        change = Math.round(change*100.0)/100.0;
        String change_str;
        if (change<0){
            change = Math.abs(change);
            change_str = "-$"+change;
            ((TextView)findViewById(R.id.change)).setTextColor(Color.parseColor("#990000"));
        }
        else{
            if (change>0){
                ((TextView)findViewById(R.id.change)).setTextColor(Color.parseColor("#209900"));
            }
            change_str = "$"+change;
        }
        ((TextView)findViewById(R.id.change)).setText(change_str);



        updatedportFolio();
        GridView simpleList = (GridView) findViewById(R.id.simpleGridView);
        ArrayList<String> SummaryList = new ArrayList<>();
        SummaryList.add("Current Price: " + getValue(details.getString("last")));
        SummaryList.add("Low: " +getValue(details.getString("low")));
        SummaryList.add("Bid Price: " + getValue(details.getString("bidPrice")));
        SummaryList.add("OpenPrice: " + getValue(details.getString("open")));
        SummaryList.add("Mid: " + getValue(details.getString("mid")));
        SummaryList.add("High: " + getValue(details.getString("high")));
        SummaryList.add("Volume: " + String.format("%,d",Integer.valueOf(details.getString("volume"))));

        AdapterGrid adapterGrid =new AdapterGrid(this,R.layout.prices_layout, SummaryList);
        simpleList.setAdapter(adapterGrid);
        Button buttonShowTradeDialog = (Button) findViewById(R.id.buttonShowTradeDialog);
        aboutStock.setText(summary.getString("description"));
        aboutStock.post(new Runnable() {
            @Override
            public void run() {
                int temp  = aboutStock.getLineCount();
                if (temp > 2) {
                    showMoreButton.setVisibility(View.VISIBLE);
                    aboutStock.setMaxLines(2);
                    aboutStock.setEllipsize(TextUtils.TruncateAt.END);
                } else {
                    showMoreButton.setVisibility(View.INVISIBLE);
                }
            }
        });
        showMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//            Toast.makeText(getApplicationContext(),"show more",Toast.LENGTH_SHORT).show();
                if(showMoreButton.getText().equals("Show more...")){
                    showMoreButton.setText("Show less");
                    aboutStock.setEllipsize(null);

                    aboutStock.setMaxLines(Integer.MAX_VALUE);
                }
                else {//clicked show less
                    showMoreButton.setText("Show more...");
                    aboutStock.setEllipsize(TextUtils.TruncateAt.END);
                    aboutStock.setMaxLines(2);
                }
            }
        });
        buttonShowTradeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);


                dialog.setContentView(R.layout.dialog_calculations);
                // set the dialog_cards dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.textTrade);
                try {
                    text.setText("Trade "+summary.getString("name")+" shares");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String quantity = "0";
                String multiply = "0.00";
                String last="";
                try {
                    last = Float.toString((float)details.getDouble("last"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                TextView text3 = (TextView) dialog.findViewById(R.id.textTrade3);
                text3.setText(quantity+" x $"+last+"/share =$ "+multiply);

                EditText et=(EditText)(dialog.findViewById(R.id.trade_input));
                et.setText("0");

                HashMap<String,String> available = sharedPref.readFromSP("available");
                Double available_d;
                if (available.containsKey("amount")){
                    available_d = Double.parseDouble(available.get("amount"));
                }
                else{
                    available_d = 20000.0;
                    available.put("amount",Double.toString(available_d));
                    sharedPref.insertToSP(available,"available");
                }

                TextView text4 = (TextView) dialog.findViewById(R.id.textTrade4);
                text4.setText("$"+available_d.toString()+" available to buy "+tickerName);
                final Dialog[] dialog_success = new Dialog[1];
                Button dialogButtonBuy = (Button) dialog.findViewById(R.id.buy_button);
                // if button is clicked, close the dialog_cards dialog
                dialogButtonBuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //("buy clicked","here");
                        dialog_success[0] = null;
                        String quantity = et.getText().toString();
                        Double quantity_d = 0.00;
                        if (!quantity.equals("")){
                            quantity_d = Double.parseDouble(quantity);
                        }
                        if (quantity_d<=0 || quantity.equals("")){
                            Context context = getApplicationContext();
                            CharSequence text = "Cannot buy less than 0 shares";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }

                        else{
                            HashMap<String, String> name_mapping= sharedPref.readFromSP("name_mapping");
                            try {
                                name_mapping.put(tickerName,summary.getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            sharedPref.insertToSP(name_mapping,"name_mapping");
                            HashMap<String, String> portfolio= sharedPref.readFromSP("fav"+tickerName);
                            HashMap<String,String> available = sharedPref.readFromSP("available");
                            Double available_d = Double.parseDouble(available.get("amount"));

                            Double last = 0.00;
                            try {
                                last = details.getDouble("last");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            available_d -= last*quantity_d;
                            available_d = Math.round(available_d * 10000.0) / 10000.0;

                            if (available_d<0){
                                Context context = getApplicationContext();
                                CharSequence text = "Not enough money to buy";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                            else{
                                try {
                                    double new_quantity;
                                    if(!portfolio.isEmpty() && Float.valueOf(portfolio.get("bought"))>0 ){
                                        new_quantity = Double.parseDouble(portfolio.get("bought"))+quantity_d;
                                    }
                                    else{
                                        new_quantity = quantity_d;
                                    }
                                    new_quantity = Math.round(new_quantity*10000.0)/10000.0;
                                    //quantity = String.valueOf(new_quantity);
                                    portfolio.put("bought",String.valueOf(new_quantity));
                                    name_mapping.put(tickerName,summary.getString("name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                sharedPref.insertToSP(name_mapping,"name_mapping");
                                sharedPref.insertToSP(portfolio,"fav"+tickerName);
                                ArrayList<String> portfolioLst= sharedPref.arrayReadFromSP("portfolio");
                                if(!portfolioLst.contains(tickerName))
                                {
                                    portfolioLst.add(tickerName);
                                }
                                sharedPref.arrayInsertToSP(portfolioLst,"portfolio");
                                available.put("amount",available_d.toString());
                                sharedPref.insertToSP(available,"available");
                                dialog_success[0] = new Dialog(context);
                                dialog_success[0].setContentView(R.layout.dialog_green);
                                TextView ts = (TextView) dialog_success[0].findViewById(R.id.successfulTag);
                                ts.setText("You have successfully bought "+quantity+" shares of "+tickerName);
                                dialog.dismiss();
                                dialog_success[0].show();
                                //Log.e("mapping is",name_mapping.toString());
                                //Log.e("portfolio is",sharedPref.readFromSP("fav"+tickerName).toString());
                            }
                            if (dialog_success[0]!=null) {
                                Button done_button = (Button) dialog_success[0].findViewById(R.id.doneButton);
                                done_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog_success[0].dismiss();
                                        updatedportFolio();
                                    }
                                });
                            }

                        }
                    }
                });

                Button dialogButtonSell = (Button) dialog.findViewById(R.id.sell_button);
                // if button is clicked, close the dialog_cards dialog
                dialogButtonSell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.e("sell clicked","here");
                        dialog_success[0] = null;
                        String quantity = et.getText().toString();
                        Double quantity_d = 0.00;
                        if (!quantity.equals("")){
                            quantity_d = Double.parseDouble(quantity);
                        }

                        if (quantity_d<=0 || quantity.equals("")){
                            Context context = getApplicationContext();
                            CharSequence text = "Cannot sell less than 0 shares";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                        else{
                            HashMap<String, String> portfolio= sharedPref.readFromSP("fav"+tickerName);
                            HashMap<String,String> available = sharedPref.readFromSP("available");
                            Double available_d = Double.parseDouble(available.get("amount"));
                            Double last = 0.00;
                            try {
                                last = details.getDouble("last");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            available_d += last*quantity_d;
                            available_d = Math.round(available_d * 10000.0) / 10000.0;
                            available.put("amount",available_d.toString());


                            double new_quantity;
                            if (!portfolio.isEmpty() && Float.valueOf(portfolio.get("bought"))>0){
                                new_quantity = Double.parseDouble(portfolio.get("bought"))-quantity_d;
                            }
                            else{
                                new_quantity = -quantity_d;
                            }
                            if (new_quantity<0){
                                Context context = getApplicationContext();
                                CharSequence text = "Not enough shares to sell";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                            else if(new_quantity==0){
                                portfolio.remove("bought");
                                ArrayList<String> portfolioLst= sharedPref.arrayReadFromSP("portfolio");
                                if(portfolioLst.contains(tickerName))
                                {
                                    portfolioLst.remove(tickerName);
                                }
                                sharedPref.arrayInsertToSP(portfolioLst,"portfolio");
                            }
                            else{
                                new_quantity = Math.round(new_quantity*10000.0)/10000.0;
                                //quantity = String.valueOf(new_quantity);
                                portfolio.put("bought",String.valueOf(new_quantity));
                                ArrayList<String> portfolioLst= sharedPref.arrayReadFromSP("portfolio");
                                if(!portfolioLst.contains(tickerName))
                                {
                                    portfolioLst.add(tickerName);
                                }
                                sharedPref.arrayInsertToSP(portfolioLst,"portfolio");
                            }
                            if (new_quantity>=0){
                                sharedPref.insertToSP(portfolio,"fav"+tickerName);
                                sharedPref.insertToSP(available,"available");
                                dialog_success[0] = new Dialog(context);
                                dialog_success[0].setContentView(R.layout.dialog_green);
                                TextView ts = (TextView) dialog_success[0].findViewById(R.id.successfulTag);
                                ts.setText("You have successfully sold "+quantity+" shares of "+tickerName);
                                dialog.dismiss();
                                dialog_success[0].show();

                            }
                            if (dialog_success[0]!=null) {
                                Button done_button = (Button) dialog_success[0].findViewById(R.id.doneButton);
                                done_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog_success[0].dismiss();
                                        updatedportFolio();
                                    }
                                });
                            }
                            //Log.d("portfolio is",sharedPref.readFromSP("fav"+tickerName).toString());
                        }
                    }
                });
                EditText trade_input = (EditText) dialog.findViewById(R.id.trade_input);
                trade_input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        String quantity = s.toString();
                        String multiply = "0.00";
                        String last="";
                        Float ls;

                        try {
                            ls = (float)details.getDouble("last");
                            last = Float.toString(ls);
                            if (!quantity.equals("")) {
                                float ml = ls * Float.parseFloat(quantity);
                                multiply = String.valueOf(ml);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        TextView text3 = (TextView) dialog.findViewById(R.id.textTrade3);
                        text3.setText(quantity+" x $"+last+"/share =$ "+multiply);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                dialog.show();
            }
        });
        initImageBitmaps();
    }

    private void updatedportFolio() {
        float temp = getSharedOwned();
        if(temp==0)
        {
            ((TextView)findViewById(R.id.sharesOwned)).setText("You have 0 shares of "+tickerName);
            ((TextView)findViewById(R.id.marketValues)).setText("Start trading!");
        }
        else
        {
            ((TextView)findViewById(R.id.sharesOwned)).setText("Shares owned: "+String.format("%.4f", temp));
            try {
                ((TextView)findViewById(R.id.marketValues)).setText("Market Value: $"+String.format("%.2f",
                        temp*Float.valueOf(details.getString("last"))));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private String getValue(String val)
    {
        return val=="null"?"0.0":val;
    }
    private float getSharedOwned() {
        HashMap<String,String> temp = sharedPref.readFromSP("fav"+tickerName);
        if(temp.isEmpty()) return 0;
        else return Float.valueOf(temp.get("bought"));
    }
    private void initImageBitmaps() {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://androidhw9backend-env.eba-dau8fdnw.us-east-1.elasticbeanstalk.com/getDataAPI_3_News?tickerVal=" + tickerName;
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        last = new ArrayList<>();
                        for (int i = 0; i < 20; i++)
                            try {
                                JSONArray newsdata = response.getJSONArray("articles");
                                JSONObject tempData = newsdata.getJSONObject(i).getJSONObject("source");
                                String title1 = newsdata.getJSONObject(i).getString("title");
                                String name = tempData.getString("name");
                                String articleURL = newsdata.getJSONObject(i).getString("url");
                                String articleImage = newsdata.getJSONObject(i).getString("urlToImage");
                                String publishedAt = newsdata.getJSONObject(i).getString("publishedAt");
                                last.add(new CardItem(title1, name, articleURL, articleImage, publishedAt));
                            } catch (Exception e) {
                                //("Proper ----Response -", e.toString());
                            }

                        if (last.size() > 0) {
                            try {
                                adapter = new AdapterCard(context, last);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            } catch (Exception e) {
                                //Log.d("Proper ----Response", e.toString());
                            }
                        }
                        stopLoading();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d("Error.Response", error.toString());
                    }
                }
        );
        queue.add(getRequest);
    }

    private void stopLoading() {
        progressBar.setVisibility(View.INVISIBLE);
        whole_layout.setVisibility(View.VISIBLE);
    }
}