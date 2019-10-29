package com.prx.tvfdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.prx.tvfdemo.adpater.CollectionAdapter;
import com.prx.tvfdemo.adpater.CommuintyAdapter;
import com.prx.tvfdemo.db.MyUser;
import com.prx.tvfdemo.db.Post;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/*
    收藏列表
    on 19/10
 */
public class CollectionActivity extends AppCompatActivity {
    private Button bt_back;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        StatusBarUtils.setColor(this,getResources().getColor(R.color.colorWhite)); //沉浸式状态栏
        initView();
        initClick();
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);  //设置布局管理器
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));


        /*
            获取收藏帖子列表信息
        */
        BmobQuery<Post> query = new BmobQuery<>();
        MyUser user = new MyUser();
        String objectId = (String) BmobUser.getObjectByKey("objectId");
        user.setObjectId(objectId);
        query.addWhereRelatedTo("likes", new BmobPointer(user));
        query.order("-createdAt");
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if(e==null){
                    List<Post> postList = new ArrayList<>();
                    postList.addAll(list);
                    CollectionAdapter adapter = new CollectionAdapter(getApplicationContext(),postList);
                    recyclerView.setAdapter(adapter); //设置适配器
                }else{
                    Log.i("Bmob获取通知","获取通知失败:"+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }
    void initView(){
        bt_back = findViewById(R.id.collection_back);
        recyclerView = findViewById(R.id.recy_collection);
    }
    void initClick(){
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionActivity.this.finish();
            }
        });
    }
}
