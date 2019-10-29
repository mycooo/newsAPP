package com.prx.tvfdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.prx.tvfdemo.adpater.CommentAdapter;
import com.prx.tvfdemo.db.Comment;
import com.prx.tvfdemo.db.MyUser;
import com.prx.tvfdemo.db.Post;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/*
    帖子详情界面
 */
public class NRCommunityActivity extends AppCompatActivity {
    private TextView user_name,user_infor,user_title;
    private ImageView user_image,commment_file;
    private EditText send_et;
    private RecyclerView recyclerView;
    private String objectId;
    private LinearLayout layout_like;
    private TextView comment_reply,comment_like,editText;
    private static final String TAG = "NRCommunityActivity";
    private Button bt_back,bt_follow;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_view_item);
        Bmob.initialize(this, "2cf4dd38d80cc0bc5160d5f7c97912bb");
        StatusBarUtils.setColor(this,getResources().getColor(R.color.colorWhite)); //沉浸式状态栏
        initLoad(); //初始化控件
        initData();
        onClick();  //点击事件
        findComment();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); //默认不弹出软键盘
        /*
        获取评论
         */
        BmobQuery<Comment> query = new BmobQuery<>();
        final List<Comment> commentList = new ArrayList<>();
        Post post = new Post();
        post.setObjectId(objectId);
        query.addWhereEqualTo("post",new BmobPointer(post));
        query.include("user,post.myUser");
        query.order("-createdAt");
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if (e == null) {
                    commentList.addAll(list);
                    CommentAdapter adapter = new CommentAdapter(getApplicationContext(),commentList);
                    recyclerView.setAdapter(adapter); //设置适配器
                } else {
                    Log.i("Bmob获取通知", "获取通知失败:" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });

    }
    /*
    点击事件
     */
    void onClick(){

        //发送评论
        editText.setInputType(InputType.TYPE_NULL);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CommentDialog("说点什么", new CommentDialog.SendListener() {
                    @Override
                    public void sendComment(String inputText) {
                        publishComment(inputText);  //上传评论
                    }
                }).show(getSupportFragmentManager(), "comment");
            }
        });
        //点击返回上个界面
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NRCommunityActivity.this.finish();
            }
        });

    }

    /*
    评论
     */
    private void publishComment(String content){
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        Comment comment = new Comment();
        Post post = new Post();
        post.setObjectId(objectId);
        comment.setContent(content);
        comment.setPost(post);
        comment.setMyUser(user);
        comment.setId(comment.getMyUser().getIdName());
        comment.setImage(comment.getMyUser().getImage());
        comment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Log.d(TAG,"评论成功");
                    send_et.setText("");
                    Toast.makeText(NRCommunityActivity.this, "成功发表评论", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("失败", e.toString());
                }
            }
        });
    }

    void initLoad(){
        user_name = findViewById(R.id.user_name);
        user_infor = findViewById(R.id.infor);
        user_title = findViewById(R.id.user_title);
        send_et = findViewById(R.id.pinglun);
        recyclerView = findViewById(R.id.comment_recyclerview);
        bt_back = findViewById(R.id.bt_sq_back);
        bt_follow = findViewById(R.id.bt_follow);
        user_image = findViewById(R.id.user_image);
        commment_file = findViewById(R.id.comment_recycler_file);
        comment_like = findViewById(R.id.comment_like);
        comment_reply = findViewById(R.id.comment_reply);
        editText = findViewById(R.id.pinglun);
        layout_like = findViewById(R.id.layout_like);
    }
    void initData(){
        /*
        接收传递过来的值
         */
        Intent intent = getIntent();
        objectId  = intent.getStringExtra("objectId");
        String name = intent.getStringExtra("id");
        String content = intent.getStringExtra("Content");
        String title = intent.getStringExtra("Title");
        String image = intent.getStringExtra("image");
        String file = intent.getStringExtra("file");
        Glide.with(this).load(file).into(commment_file); //显示发布文件/头像等
        Glide.with(this).load(image).into(user_image); //显示头像
        user_name.setText(name);      //显示用户名
        user_infor.setText(content);   //显示内容
        user_title.setText(title);     //显示标题
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);  //设置布局管理器
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    void findComment(){
        /*
        查询评论数量
         */
        BmobQuery<Comment> bmobQuery = new BmobQuery<>();
        Post post = new Post();
        post.setObjectId(objectId);
        bmobQuery.addWhereEqualTo("post",new BmobPointer(post));
        bmobQuery.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if(e == null){
                    Log.e("likePost", "评论："+list.size());
                    comment_reply.setText(list.size()+"");
                }else {
                    Log.e("number","失败："+e.getMessage());
                }
            }
        });
        /*
        点亮
        多对多关系
        一个帖子可以被很多用户所喜欢，一个用户也可能会喜欢很多帖子，
        那么可以使用Relation类型来表示这种多对多关联关系。
         */
        final ImageView iv_comment = findViewById(R.id.iv_comment);
        layout_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                添加点赞关联
                 */
                MyUser user = BmobUser.getCurrentUser(MyUser.class);
                Post post = new Post();
                post.setObjectId(objectId);
                BmobRelation relation = new BmobRelation();
                relation.add(user);
                post.setLikes(relation);
                post.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            iv_comment.setImageDrawable(getResources().getDrawable(R.drawable.bt_like));
                            Toast.makeText(NRCommunityActivity.this,"点亮成功",Toast.LENGTH_SHORT).show();
                            Log.e("bmob","多对多关联添加成功");
                        }else{
                            Log.e("bmob","失败："+e.getMessage());
                        }
                    }
                });
            }
        });
        /*
        获取喜欢数量
         */
        BmobQuery<MyUser> query = new BmobQuery<>();
        final String object = post.getObjectId();
        post.setObjectId(object);
        //likes是Post表中的字段，用来存储所有喜欢该帖子的用户
        query.addWhereRelatedTo("likes", new BmobPointer(post));
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(final List<MyUser> object, BmobException e) {
                if(e==null){
                    Log.e("likePost", "喜欢："+object.size());
                    comment_like.setText(object.size()+"");
                }else{
                    Log.e("likePost","失败："+e.getMessage());
                }
            }
        });
        /*我的收藏*/
        bt_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUser user = new MyUser();
                Post post = new Post();
                post.setObjectId(object);
                String objectId = (String) BmobUser.getObjectByKey("objectId");
                user.setObjectId(objectId);
                BmobRelation relation = new BmobRelation();
                relation.add(post);
                user.setLikes(relation);
                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                            if(e==null){
                                Toast.makeText(NRCommunityActivity.this,"收藏成功",Toast.LENGTH_SHORT).show();
                                Log.i("bmob","多对多关联添加成功");
                            }else{
                                Log.i("bmob","失败："+e.getMessage());
                            }
                        }
                });
            }
        });
    }

}
