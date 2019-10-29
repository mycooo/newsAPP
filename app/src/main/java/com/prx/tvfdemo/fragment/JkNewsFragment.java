package com.prx.tvfdemo.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;

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

public class JkNewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>> {

    private ListView jkListView;

    private MyNewsAdapter myNewsAdapter;

    private static  final  String KEY = "757e2879d43d780e291e05f525503327";

    private static final int LOADER_ID = 6;

    private static final String AKURI = "https://api.tianapi.com/health/?";

    private TextView emptyText ;

    private ProgressBar noWorkProgressBar;

    public JkNewsFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.jiankang_news, container, false);

        myNewsAdapter = new MyNewsAdapter(getActivity(), new ArrayList<News>());

        jkListView = rootView.findViewById(R.id.jiankang_news_list_view);
        emptyText = rootView.findViewById(R.id.no_network_info);
        noWorkProgressBar = rootView.findViewById(R.id.progressBar);


        if (jkListView != null) {
            jkListView.setAdapter(myNewsAdapter);

            jkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    public android.support.v4.content.Loader<List<News>> onCreateLoader(int id, Bundle args) {
        //偏好设置


        //构建UriBUilder
        Uri baseUri = Uri.parse(AKURI);

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
