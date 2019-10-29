package com.prx.tvfdemo.adpater;
import android.content.Context;
import android.content.Intent;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.prx.tvfdemo.NRCommunityActivity;
import com.prx.tvfdemo.R;
import com.prx.tvfdemo.db.Comment;
import com.prx.tvfdemo.db.MyUser;
import com.prx.tvfdemo.db.Post;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class CommuintyAdapter extends RecyclerView.Adapter<CommuintyAdapter.ViewHolder> {
    @NonNull
    private List<Post> mPostList;
    private Context mContext;
    public CommuintyAdapter(Context context,List<Post> PostList) {
        this.mContext = context;
        this.mPostList = PostList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView name;
        ImageView imageView;
        TextView tv_reply;
        TextView tv_like;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.View_recycler_id);
            title = itemView.findViewById(R.id.View_recycler_bt);
            imageView = itemView.findViewById(R.id.View_recycler_imge);
            tv_reply = itemView.findViewById(R.id.tv_reply);
            tv_like = itemView.findViewById(R.id.tv_like);
        }
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recy_view_item,viewGroup,false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        final Post post = mPostList.get(position);
        viewHolder.name.setText(post.getName());
        viewHolder.title.setText(post.getTitle());
        if(post.getImage() == null){
            viewHolder.imageView.setImageResource(R.drawable.ic_account_box_black_24dp);
        }else {
            Glide.with(mContext)
                    .load(post.getImage().getUrl())
                    .into(viewHolder.imageView);
        }
         /*
            获取喜欢数量
         */
        BmobQuery<MyUser> query = new BmobQuery<>();
        String objectId = post.getObjectId();
        post.setObjectId(objectId);
        //likes是Post表中的字段，用来存储所有喜欢该帖子的用户
        query.addWhereRelatedTo("likes", new BmobPointer(post));
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(final List<MyUser> object, BmobException e) {
                if(e==null){
                    viewHolder.tv_like.setText(String.valueOf(object.size()));
                }else{
                    Log.e("likePost","失败："+e.getMessage());
                }
            }
        });
        /*
        查询评论数量
         */
        BmobQuery<Comment> bmobQuery = new BmobQuery<>();
        String object = post.getObjectId();
        post.setObjectId(object);
        bmobQuery.addWhereEqualTo("post",new BmobPointer(post));
        bmobQuery.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if(e == null){
                    viewHolder.tv_reply.setText(String.valueOf(list.size()));
                }else {
                    Log.e("number","失败："+e.getMessage());
                }
            }
        });

        //跳转帖子详情，并传值
        viewHolder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NRCommunityActivity.class);
                intent.putExtra("id",post.getName());
                intent.putExtra("Title",post.getTitle());
                intent.putExtra("Content",post.getContent());
                intent.putExtra("objectId",post.getObjectId());
                intent.putExtra("image",post.getImage().getUrl());
                if(post.getFile() == null){
                }else {
                    intent.putExtra("file",post.getFile().getUrl());
                }
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

}
