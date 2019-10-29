package com.prx.tvfdemo.db;

import java.io.File;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;

public class Post extends BmobObject {
    private MyUser myUser;
    private String title;  //标题
    private String content; //内容
    private String name;  //用户名
    private BmobFile image; //用来保存用户头像
    private BmobFile file ; //帖子的图片/文件
    private Comment comment;
    /**
     * 多对多关系：用于存储喜欢该帖子的所有用户  ？？
     */
    private BmobRelation likes;


    private BmobRelation collection;

    public BmobRelation getCollection() {
        return collection;
    }

    public void setCollection(BmobRelation collection) {
        this.collection = collection;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public BmobRelation getLikes() {
        return likes;
    }

    public void setLikes(BmobRelation likes) {
        this.likes = likes;
    }

    public BmobFile getFile() {
        return file;
    }

    public void setFile(BmobFile file) {
        this.file = file;
    }

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MyUser getMyUser() {
        return myUser;
    }

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
