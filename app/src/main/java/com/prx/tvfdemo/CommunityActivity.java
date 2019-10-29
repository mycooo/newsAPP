package com.prx.tvfdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.prx.tvfdemo.adpater.CommuintyAdapter;
import com.prx.tvfdemo.db.Post;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
    社区界面
 */
public class CommunityActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        Bmob.initialize(this,"2cf4dd38d80cc0bc5160d5f7c97912bb");
        initLoad(); //初始化控件
        initData(); //添加数据
        StatusBarUtils.setColor(this,getResources().getColor(R.color.colorPrimary)); //沉浸式状态栏
        /*
        下拉刷新
         */
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);//颜色
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(CommunityActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
                //refresh();

            }
        });

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_black_24dp);
        }
        actionBar.setTitle("社区");

    }

    void initLoad(){
        //初始化控件
        recyclerView = findViewById(R.id.View_recycler);
        swipeRefreshLayout = findViewById(R.id.swipe_community_layout);

    }
    void initData(){
        /*
        加载recyclerview
         */
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);  //设置布局管理器
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        /*
        添加帖子数据
         */
        final BmobQuery<Post> query = new BmobQuery<>();
        query.order("-createdAt");
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if(e==null){
                    List<Post> postList = new ArrayList<>();
                    postList.addAll(list);
                    CommuintyAdapter adapter = new CommuintyAdapter(getApplicationContext(),postList);
                    recyclerView.setAdapter(adapter); //设置适配器
                }else {
                    Log.i("Bmob获取通知","获取通知失败:"+e.getMessage()+","+e.getErrorCode());
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    /**
     * 点击返回键做了处理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(CommunityActivity.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.add_item:
                /*
                跳转至发布帖子界面，BmobUser.isLogin()用于检查是否登陆
                 */
                if(BmobUser.isLogin()){
                    intent = new Intent(CommunityActivity.this,ReleaseActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(this,"尚未登陆，请前往登陆",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
        return true;
    }

}
