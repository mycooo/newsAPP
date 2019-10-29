package com.prx.tvfdemo;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import com.prx.tvfdemo.db.News;

import java.util.List;



public class NewsLoader extends AsyncTaskLoader<List<News>> {
    public String myUrl;

    public NewsLoader(Context context, String myurl){
        super(context);
        myUrl = myurl;
    }


    @Override
    public List<News> loadInBackground() {
        if (myUrl == null){
            return null;
        }
        Query query = new Query();
        List<News> newsList = query.fetchNews(myUrl);

        return newsList;
    }
    //使用forceLoad（）
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
