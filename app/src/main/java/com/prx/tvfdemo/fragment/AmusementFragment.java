package com.prx.tvfdemo.fragment;


import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.prx.tvfdemo.adpater.MyNewsAdapter;
import com.prx.tvfdemo.NewsLoader;
import com.prx.tvfdemo.R;
import com.prx.tvfdemo.db.News;
import com.thefinestartist.finestwebview.FinestWebView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AmusementFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>>{


    private ListView amusementListView;
    private MyNewsAdapter myNewsAdapter;
    private static  final  String KEY = "757e2879d43d780e291e05f525503327";

    private static final int LOADER_ID = 5;

    private static final String AMUSEMENTURI = "https://api.tianapi.com/huabian/?";

    private TextView emptyText ;

    private ProgressBar noWorkProgressBar;


    public AmusementFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.amusement_news, container, false);
       myNewsAdapter = new MyNewsAdapter(getActivity(), new ArrayList<News>());

        amusementListView = rootView.findViewById(R.id.amusement_news_list_view);
        emptyText = rootView.findViewById(R.id.no_network_info);
        noWorkProgressBar = rootView.findViewById(R.id.progressBar);

        if (amusementListView != null) {
            amusementListView.setAdapter(myNewsAdapter);

            amusementListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    News amusementNews = myNewsAdapter.getItem(position);

                    new FinestWebView.Builder(getActivity()).titleDefault(amusementNews.getTitile())
                            .show(amusementNews.getUrl());
                }
            });
        }


        LoaderManager loaderManager = getLoaderManager();

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            loaderManager.initLoader(LOADER_ID, null, this);

        }else {
            noWorkProgressBar.setVisibility(View.GONE);
            emptyText.setText(R.string.no_network);
        }

        return rootView;
    }


    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
            //偏好设置


            //构建UriBUilder
            Uri baseUri = Uri.parse(AMUSEMENTURI);

            Uri.Builder builder = baseUri.buildUpon();

            builder.appendQueryParameter("key", KEY);

            builder.appendQueryParameter("num", "20");

            return new NewsLoader(getActivity(), builder.toString());
    }




    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<News>> loader, List<News> data) {
        myNewsAdapter.clear();
        noWorkProgressBar.setVisibility(View.GONE);
        if (data != null && !data.isEmpty()) {
            myNewsAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<News>> loader) {
        myNewsAdapter.clear();
    }




}


