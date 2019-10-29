package com.prx.tvfdemo.adpater;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.prx.tvfdemo.R;
import com.prx.tvfdemo.db.Comment;

import java.util.List;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<Comment> mcommentList;
    private Context mContext;
    public CommentAdapter(Context context,List<Comment> commentList){
        this.mContext = context;
        this.mcommentList = commentList;
    }
    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_comment_view_item,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.ViewHolder viewHolder, int i) {
        Comment comment = mcommentList.get(i);
        viewHolder.tv_pinglun.setText(comment.getContent());
        viewHolder.tv_id.setText(comment.getId());
        if(comment.getImage() == null){
            viewHolder.iv_icon.setImageResource(R.drawable.ic_account_box_black_24dp);
        }else {
            Glide.with(mContext)
                    .load(comment.getImage().getUrl())
                    .into(viewHolder.iv_icon);
        }
    }

    @Override
    public int getItemCount() {
        return mcommentList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_pinglun;
        TextView tv_id;
        ImageView iv_icon;
        ImageView iv_file;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_pinglun = itemView.findViewById(R.id.comment_recycler_pl);
            tv_id = itemView.findViewById(R.id.comment_recycler_id);
            iv_icon = itemView.findViewById(R.id.comment_recycler_imge);
            iv_file = itemView.findViewById(R.id.post_image);
        }
    }
}
