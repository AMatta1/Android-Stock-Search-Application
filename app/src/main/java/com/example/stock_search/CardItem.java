package com.example.stock_search;
import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.TimeZone;

public class CardItem {
    public static final String OUTPUT_FORMAT_STD_DATE6 = "MM/dd/yy hh:mm a";
    private String Title;
    private String Name;
    private String Url;
    private String Image;
    private String PublishedAt;

    public CardItem() {
    }

    @SuppressLint("LongLogTag")
    public CardItem(String title, String name, String url, String image, String publishedAt) {
        Title = title;
        Name = name;
        Url = url;
        Image = image;
        PublishedAt = publishedAt;
        Log.d("publiced:", publishedAt );


    }


    public String getTitle() {
        return Title;
    }

    public String getName() {
        return Name;
    }

    public String getUrl() {
        return Url;
    }

    public String getImage() {
        return Image;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getPublishedAt()  {

        Log.d("original dat = ",PublishedAt);
        String[] arrOfStr = PublishedAt.split("T");
        String justDate = arrOfStr[0];
        Log.d("justDate", justDate);
        String[] originalDate = justDate.split("-");

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getDefault());
        String justNowDate = df.format(date).split(" ")[0];

String[] currentDate = justNowDate.split("-");
//        Log.d("justNowDate", currentDate[0]+currentDate[1]+currentDate[2]);
        LocalDate pastDate = LocalDate.of(Integer.parseInt(originalDate[0]), Integer.parseInt(originalDate[1]),Integer.parseInt(originalDate[2]));
        LocalDate nowDate = LocalDate.of(Integer.parseInt(currentDate[0]), Integer.parseInt(currentDate[1]),Integer.parseInt(currentDate[2]));

        Period p = Period.between(pastDate, nowDate);
        Log.d("justNowDate", p.toString());
        if (p.getYears()!=0)
        {
            if (p.getYears() == 1)
            {
                return p.getYears()+" year ago";
            }

            return p.getYears()+" years ago";
        }
        else if (p.getMonths()!=0)
        {
            if (p.getMonths()==1)
            {
                return p.getMonths()+" month ago";
            }
            return p.getMonths()+" months ago";
        }
        else
        {
            if (p.getDays()==1)
            {
                return p.getDays()+" day ago";
            }
            return p.getDays()+" days ago";
        }



    }


    public void setTitle(String title) {
        Title = title;
    }

    public void setTag(String tag) {
        Name = tag;
    }

    public void setImg(String img) {
        Image = img;
    }

    public void setUrl(String url) {
         Url = url;
    }

    public void PublishedAt(String publishedAt) {
        PublishedAt = publishedAt;
    }

}
