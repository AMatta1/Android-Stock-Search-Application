package com.example.stock_search;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class SharedPref  {
    private static final String TAG = "AppCompatActivity";
    Context context;
    public SharedPref(Context context) {
        this.context = context;
    }


    public void arrayInsertToSP(ArrayList<String> jsonMap, String data_) {
        String jsonString = new Gson().toJson(jsonMap);
        SharedPreferences sharedPreferences = context.getSharedPreferences("ArrayList", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(data_, jsonString);
        editor.apply();
    }
    public ArrayList<String> arrayReadFromSP(String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("ArrayList", MODE_PRIVATE);
        String defValue = new Gson().toJson(new ArrayList<String>());
        String json=sharedPreferences.getString(key,defValue);
        TypeToken<ArrayList<String>> token = new TypeToken<ArrayList<String>>() {};
        ArrayList<String> retrievedMap=new Gson().fromJson(json,token.getType());
        return retrievedMap;
    }
    public void insertToSP(HashMap<String, String> jsonMap, String key) {
        String jsonString = new Gson().toJson(jsonMap);
        SharedPreferences sharedPreferences = context.getSharedPreferences("HashMap", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, jsonString);
        editor.apply();
    }
    public HashMap<String, String> readFromSP(String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("HashMap", MODE_PRIVATE);
        String defValue = new Gson().toJson(new HashMap<String, String>());
        String json=sharedPreferences.getString(key,defValue);
        TypeToken<HashMap<String,String>> token = new TypeToken<HashMap<String,String>>() {};
        HashMap<String,String> retrievedMap=new Gson().fromJson(json,token.getType());
        return retrievedMap;
    }

    public void removeFromSP(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("HashMap", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
}

