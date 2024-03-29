package com.prx.tvfdemo.fragment;

import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.prx.tvfdemo.NewsLoader;
import com.prx.tvfdemo.R;
import com.prx.tvfdemo.adpater.MyNewsAdapter;
import com.prx.tvfdemo.db.News;
import com.thefinestartist.finestwebview.FinestWebView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScienceNewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>> {
    private MyNewsAdapter myNewsAdapter;

    private ListView scienceListView ;

    private static final String APIScienceUrl = "https://api.tianapi.com/it/?";
    private static final String KEY = "757e2879d43d780e291e05f525503327";

    private static  final int News_LOADER_ID_2 = 2;

    private TextView emptyText ;

    private ProgressBar noWorkProgressBar;


    public ScienceNewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.science_new, container, false);

        myNewsAdapter = new MyNewsAdapter(getActivity(), new ArrayList<News>());
        scienceListView = rootView.findViewById(R.id.science_news_list_view);
        emptyText = rootView.findViewById(R.id.no_network_info);
        noWorkProgressBar = rootView.findViewById(R.id.progressBar);


        if (scienceListView != null) {
            scienceListView.setAdapter(myNewsAdapter);
            scienceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    News scienceItemNews= myNewsAdapter.getItem(position);

                    new FinestWebView.Builder(getActivity()).titleDefault(scienceItemNews.getTitile())
                            .show(scienceItemNews.getUrl());
                }
            });
        }




        LoaderManager scienceLoaderManager = getLoaderManager();

        //检测网络连接
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            scienceLoaderManager.initLoader(News_LOADER_ID_2, null,this);
            //没有网络状态的操作
        }else {
            noWorkProgressBar.setVisibility(View.GONE);
            emptyText.setText(R.string.no_network);


        }



        return rootView;

    }

    @Override
    public android.support.v4.content.Loader<List<News>> onCreateLoader(int id, Bundle args) {
        //偏好设置
        myNewsAdapter.clear();

        //构建Url
        Uri baseUri = Uri.parse(APIScienceUrl);
        Uri.Builder builder = baseUri.buildUpon();

        //开始构建uri
        builder.appendQueryParameter("key", KEY);
        builder.appendQueryParameter("num", "20");


        return new NewsLoader(getActivity(), builder.toString());
//        return new NewsLoader(this, "https://api.tianapi.com/keji/?key=b89464eb3a045df413329379481743b6&num=10");
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<News>> loader, List<News> data) {
        myNewsAdapter.clear();
        noWorkProgressBar.setVisibility(View.GONE);

        if (loader != null && !data.isEmpty()) {
            myNewsAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<News>> loader) {
        myNewsAdapter.clear();
    }



}


