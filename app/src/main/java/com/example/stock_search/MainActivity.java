package com.example.stock_search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private AdapterSearch adapterSearch;
    private Handler handler;
    private static final int TRIGGER_AUTO_COMPLETE = 1300;
    private static final long AUTO_COMPLETE_DELAY = 100;
    private static  final String TAG = "MainActivity";
    private ColorDrawable colorDrawable = new ColorDrawable(Color.rgb(182, 0, 0));
    private Drawable deleteIcon;
    private ArrayList<String> TICKER_NAME_ARRAY = new ArrayList<>();
    private ArrayList<String> SHARES_ARRAY = new ArrayList<>();
    private ArrayList<String> LAST_PRICE_ARRAY = new ArrayList<>();
    private ArrayList<String> CHANGE_ARRAY = new ArrayList<>();
    private ArrayList<String> UP_DOWN_IMG_ARRAY = new ArrayList<>();
    private ArrayList<String> TICKER_NAME_ARRAY_FAV;
    private ArrayList<String> SHARES_ARRAY_FAV ;
    private ArrayList<String> LAST_PRICE_ARRAY_FAV ;
    private ArrayList<String> CHANGE_ARRAY_FAV ;
    private ArrayList<String> UP_DOWN_IMG_ARRAY_FAV ;
    static AdapterPortfolio adapterPortfolio = null;
    RecyclerView recyclerView;
    static AdapterFavourite adapterFavourite = null;
    private RelativeLayout progressBar;
    private ConstraintLayout whole_layout;
    boolean loadedFav = false;
    boolean loadedPortfolio = false;
    private static SharedPref sharedPref;
    static Timer timer;
    static TimerTask doTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        Toolbar tol = (Toolbar) findViewById(R.id.tool);
        setSupportActionBar(tol);
        progressBar = findViewById(R.id.progress_bar_layout);
        whole_layout = findViewById(R.id.whole_layout);
        sharedPref = new SharedPref(getApplicationContext());
        HashMap<String,String> available = sharedPref.readFromSP("available");
        if (!available.containsKey("amount")){
            available.put("amount",Double.toString(20000.0));
            sharedPref.insertToSP(available,"available");
        }

        TextView todaysDateView1=findViewById(R.id.mainPageDate);
        String currentDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());
        String datebreak = currentDate.split(" ")[0]+" "+String.valueOf(Integer.parseInt(currentDate.split(" ")[1].replace(",",""))) + ", " + currentDate.split(" ")[2];


        todaysDateView1.setText(datebreak);

        TextView footerText = (TextView)findViewById(R.id.footer);
        footerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.tiingo.com"));
                startActivity(intent);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        progressBar.setVisibility(View.VISIBLE);
        whole_layout.setVisibility(View.INVISIBLE);
        this.loadedFav = false;
        this.loadedPortfolio = false;
        super.onResume();
        final Handler handler = new Handler();
        timer = new Timer();
        doTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            Log.d(TAG,"Autorefreshed after 15 seconds");
                            loadPortFolieList();
                            loadFavoriteList();
                        }
                        catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doTask, 0,15000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"RestrictedApi", "ResourceType"})
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        adapterSearch = new AdapterSearch(this,
                android.R.layout.simple_dropdown_item_1line);
        getMenuInflater().inflate(R.menu.menu_main,menu);
        MenuItem menuItem = menu.findItem(R.id.search_icon);
        SearchView search = (SearchView) menuItem.getActionView();
        final SearchView.SearchAutoComplete autoCompleteTextView = search.findViewById(androidx.appcompat.R.id.search_src_text);
        autoCompleteTextView.setAdapter(adapterSearch);
        autoCompleteTextView.setDropDownHeight(1100);
        autoCompleteTextView.setThreshold(3);
        autoCompleteTextView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    autoCompleteTextView.setText(adapterSearch.getObject(position));
                    Intent intent = new Intent(MainActivity.this, TickerDetails.class);
                    intent.putExtra("query", adapterSearch.getObject(position));
                    startActivity(intent);
                }
            });
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
                        makeApiCall(autoCompleteTextView.getText().toString());
                    }
                }
                return false;
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, TickerDetails.class);
                intent.putExtra("query",query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadFavoriteList() {
        TICKER_NAME_ARRAY_FAV = new ArrayList<>();
        SHARES_ARRAY_FAV = new ArrayList<>();
        LAST_PRICE_ARRAY_FAV = new ArrayList<>();
        CHANGE_ARRAY_FAV = new ArrayList<>();
        UP_DOWN_IMG_ARRAY_FAV = new ArrayList<>();
        ArrayList<String> favList = sharedPref.arrayReadFromSP("favorite");
        getSummary(String.join(",",favList),"ForFav");
    }

    private void getSummary(String ticker,String checkVariable){
        if(ticker.isEmpty())
        {
            JSONArray response = new JSONArray();
            if(checkVariable.equalsIgnoreCase("ForFav")) buidFavDataArray(response);
            else buildPortfolioDataArray(response);
            return;
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://androidhw9backend-env.eba-dau8fdnw.us-east-1.elasticbeanstalk.com/getDataAPI_2?tickerVal=" + ticker;
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(checkVariable.equalsIgnoreCase("ForFav")) buidFavDataArray(response);
                else buildPortfolioDataArray(response);
            }
        },
        new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,error.toString());
            }
        }
        );
        requestQueue.add(getRequest);
    }

    private void buildPortfolioDataArray(JSONArray response) {
        double net_worth = 0;
        HashMap<String,JSONObject> data_map = processResponse(response);
        ArrayList<String> input = sharedPref.arrayReadFromSP("portfolio");

        for(int i=0;i<input.size();i++)
        {
            try {
                JSONObject data = data_map.get(input.get(i));
                TICKER_NAME_ARRAY.add(data.getString("ticker"));
                HashMap<String, String> storedData = sharedPref.readFromSP("fav"+data.getString("ticker"));
                SHARES_ARRAY.add(String.format("%.2f",Float.valueOf(storedData.get("bought")))+" shares");
                HashMap<String,String> temp = sharedPref.readFromSP("fav"+data.getString("ticker"));
                net_worth += Float.valueOf(temp.get("bought"))*Float.valueOf(data.getString("last"));
                LAST_PRICE_ARRAY.add(data.getString("last"));

//                float change = Float.valueOf(data.getString("last")) - Float.valueOf(data.getString("prevClose"));
//                change = Float.valueOf(String.format("%.2f", change));
//                CHANGE_ARRAY.add(String.valueOf(change));
//                if(change>0) UP_DOWN_IMG_ARRAY.add("UP");
//                else UP_DOWN_IMG_ARRAY.add("DOWN");

                double change = Float.valueOf(data.getString("last")) - Float.valueOf(data.getString("prevClose"));
                change = Math.round(change*100.0)/100.0;
                if (change<0){
                    change = Math.abs(change);
                    UP_DOWN_IMG_ARRAY.add("DOWN");
                }
                else{
                    UP_DOWN_IMG_ARRAY.add("UP");
                }
                CHANGE_ARRAY.add(String.valueOf(change));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        RecyclerView recyclerView = findViewById(R.id.portfolio_list);
        if(adapterPortfolio == null)
        {
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback_for_portfolio);
            itemTouchHelper.attachToRecyclerView(recyclerView);
            //recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
            adapterPortfolio = new AdapterPortfolio(TICKER_NAME_ARRAY,SHARES_ARRAY,LAST_PRICE_ARRAY,CHANGE_ARRAY,UP_DOWN_IMG_ARRAY,this);
            adapterPortfolio.notifyDataSetChanged();
            recyclerView.setAdapter(adapterPortfolio);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        else
        {
            adapterPortfolio.TICKER_NAME_ARRAY = this.TICKER_NAME_ARRAY;
            adapterPortfolio.SHARES_ARRAY = this.SHARES_ARRAY;
            adapterPortfolio.LAST_PRICE_ARRAY = this.LAST_PRICE_ARRAY;
            adapterPortfolio.CHANGE_ARRAY = this.CHANGE_ARRAY;
            adapterPortfolio.UP_DOWN_IMG_ARRAY = this.UP_DOWN_IMG_ARRAY;
            adapterPortfolio.notifyDataSetChanged();
        }
        TextView  netWorth_value = findViewById(R.id.netWorth_value);
        HashMap<String,String> available = sharedPref.readFromSP("available");
        net_worth += Float.valueOf(available.get("amount"));
        netWorth_value.setText(String.format("%.2f",net_worth));
        this.loadedPortfolio = true;
        if(this.loadedFav && this.loadedPortfolio) {
            progressBar.setVisibility(View.INVISIBLE);
            whole_layout.setVisibility(View.VISIBLE);
        }
    }

    private void buidFavDataArray(JSONArray response) {
        HashMap<String,JSONObject> data_map = processResponse(response);
        ArrayList<String> input = sharedPref.arrayReadFromSP("favorite");

        for(int i=0;i<input.size();i++)
        {
            try {
                JSONObject data = data_map.get(input.get(i));
                TICKER_NAME_ARRAY_FAV.add(data.getString("ticker"));
                HashMap<String, String> storedData = sharedPref.readFromSP("fav" + data.getString("ticker"));
                if (storedData.isEmpty() || Float.valueOf(storedData.get("bought")) == 0) {
                    HashMap<String, String> name_mapping = sharedPref.readFromSP("name_mapping");
                    SHARES_ARRAY_FAV.add(name_mapping.get(data.getString("ticker")));
                } else {
                    SHARES_ARRAY_FAV.add(String.format("%.2f", Float.valueOf(storedData.get("bought"))) + " shares");
                }
                LAST_PRICE_ARRAY_FAV.add(data.getString("last"));


//                float change = Float.valueOf(data.getString("last")) - Float.valueOf(data.getString("prevClose"));
//                change = Float.valueOf(String.format("%.2f", change));
//                CHANGE_ARRAY_FAV.add(String.valueOf(change));
//                if (change > 0) UP_DOWN_IMG_ARRAY_FAV.add("UP");
//                else UP_DOWN_IMG_ARRAY_FAV.add("DOWN");


                double change = Float.valueOf(data.getString("last")) - Float.valueOf(data.getString("prevClose"));
                change = Math.round(change*100.0)/100.0;
                if (change<0){
                    change = Math.abs(change);
                    UP_DOWN_IMG_ARRAY_FAV.add("DOWN");
                }
                else{
                    UP_DOWN_IMG_ARRAY_FAV.add("UP");
                }
                CHANGE_ARRAY_FAV.add(String.valueOf(change));


            }catch (Exception e){
                e.printStackTrace();
            }
        }

        RecyclerView recyclerView = findViewById(R.id.fav_list);

        if(adapterFavourite ==null)
        {
            //recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
            adapterFavourite = new AdapterFavourite(TICKER_NAME_ARRAY_FAV,SHARES_ARRAY_FAV,LAST_PRICE_ARRAY_FAV,CHANGE_ARRAY_FAV,UP_DOWN_IMG_ARRAY_FAV,this);
            adapterFavourite.notifyDataSetChanged();
            recyclerView.setAdapter(adapterFavourite);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);

        }
        else
        {
            adapterFavourite.TICKER_NAME_ARRAY = this.TICKER_NAME_ARRAY_FAV;
            adapterFavourite.SHARES_ARRAY = this.SHARES_ARRAY_FAV;
            adapterFavourite.LAST_PRICE_ARRAY = this.LAST_PRICE_ARRAY_FAV;
            adapterFavourite.CHANGE_ARRAY = this.CHANGE_ARRAY_FAV;
            adapterFavourite.UP_DOWN_IMG_ARRAY = this.UP_DOWN_IMG_ARRAY_FAV;
            adapterFavourite.notifyDataSetChanged();
        }
        this.loadedFav = true;
        if(this.loadedFav && this.loadedPortfolio) {
            progressBar.setVisibility(View.INVISIBLE);
            whole_layout.setVisibility(View.VISIBLE);
        }
    }

    private HashMap<String,JSONObject> processResponse(JSONArray response) {
        HashMap<String,JSONObject> ans = new HashMap<>();
        for(int i=0;i<response.length();i++)
        {
            try {
                JSONObject data = response.getJSONObject(i);
                ans.put(data.getString("ticker"),data);
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  ans;
    }

    private void makeApiCall(String text) {
        RequestQueue queue = Volley.newRequestQueue(this);
        final List<String> stringList = new ArrayList<>();
        String url = "http://androidhw9backend-env.eba-dau8fdnw.us-east-1.elasticbeanstalk.com/getDataAPI_4_autoComplete?user_input=" + text;
        JsonArrayRequest jjj = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for(int i=0;i<response.length();i++ ){
                        JSONObject obj = (JSONObject) response.get(i);
                        String addto = obj.getString("ticker")+" - "+obj.getString("name");
                        if(addto.length()>36) addto = addto.substring(0,34)+"..";
                        stringList.add(addto);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapterSearch.setData(stringList);
                adapterSearch.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR","error => "+error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Ocp-Apim-Subscription-Key", "ac024cec74564fddaf6810bf13a1702a");

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jjj);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadPortFolieList(){
        TICKER_NAME_ARRAY = new ArrayList<>();
        SHARES_ARRAY = new ArrayList<>();
        LAST_PRICE_ARRAY = new ArrayList<>();
        CHANGE_ARRAY = new ArrayList<>();
        UP_DOWN_IMG_ARRAY = new ArrayList<>();
        ArrayList<String> favList = sharedPref.arrayReadFromSP("portfolio");
        getSummary(String.join(",",favList),"ForPortfolio");
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT ) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            adapterFavourite.swapOrder(fromPosition,toPosition);
            ArrayList<String> temp = sharedPref.arrayReadFromSP("favorite");
            Collections.swap(temp,fromPosition,toPosition);
            sharedPref.arrayInsertToSP(temp,"favorite");
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int dir) {
            final int index = viewHolder.getAdapterPosition();
            ArrayList<String> favList = sharedPref.arrayReadFromSP("favorite");
            favList.remove(index);
            sharedPref.arrayInsertToSP(favList,"favorite");
            adapterFavourite.removeItem(index);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            View view = viewHolder.itemView;

            deleteIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_delete_24);
            int iconMargin = 20;
            colorDrawable.setBounds(view.getRight()+(int)dX ,view.getTop(),view.getRight(), view.getBottom());
            deleteIcon.setBounds(view.getRight()-iconMargin-deleteIcon.getIntrinsicWidth(),view.getTop()+iconMargin*2,view.getRight()-iconMargin,view.getBottom()-iconMargin);
            colorDrawable.draw(c);
            deleteIcon.draw(c);
            c.clipRect(view.getRight()+(int)dX ,view.getTop(),view.getRight(), view.getBottom());
            c.restore();
            view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    ItemTouchHelper.SimpleCallback simpleCallback_for_portfolio = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0 ) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            adapterPortfolio.swapOrder(fromPosition,toPosition);
            ArrayList<String> temp = sharedPref.arrayReadFromSP("portfolio");
            Collections.swap(temp,fromPosition,toPosition);
            sharedPref.arrayInsertToSP(temp,"portfolio");
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };
}