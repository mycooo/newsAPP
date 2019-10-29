package com.prx.tvfdemo;


import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.prx.tvfdemo.db.MyUser;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/*
    设置界面
 */
public class SettingActivity extends AppCompatActivity {
    private LinearLayout layout_feed_back,layout_username,layout_password,layout_image,layout_about,layou_collection;
    private Toolbar toolbar;
    private Button bt_back_login;
    private TextView tv_setting_name;
    private CircleImageView civ;
    private String path = null;
    private String imagePath = null;
    private ProgressBar progressBar;
    private static final int CHOOSE_PHOTO = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Bmob.initialize(this,"2cf4dd38d80cc0bc5160d5f7c97912bb");
        initLoad();
        initItem();
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_backspace_black_24dp);
        }
        actionBar.setTitle("设置");


        StatusBarUtils.setColor(this,getResources().getColor(R.color.colorWhite)); //沉浸式状态栏
        if(tv_setting_name != null){
            String username = (String) BmobUser.getObjectByKey("idName");
            tv_setting_name.setText(username);
        }else {
            //获取ModifyNameActivity传过来的用户名
            Intent intent = getIntent();
            String name = intent.getStringExtra("username");
            tv_setting_name.setText(name);
        }

        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if (user.getImage() == null) {
            Toast.makeText(this,"未知错误",Toast.LENGTH_SHORT).show();
        }else {
            //显示bmob表中用户的头像
            String image = user.getImage().getUrl();
            Glide.with(this)
                    .load(image)
                    .into(civ);
        }

    }
    /**
     * 点击返回键做了处理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                MyUser user = BmobUser.getCurrentUser(MyUser.class);
                Intent intent = new Intent(SettingActivity.this,MainActivity.class);
                intent.putExtra("image",user.getImage().getUrl());
                startActivity(intent);
                Log.e("aaa",user.getImage().getUrl());
                break;
            default:
        }
        return true;
    }

    void initLoad(){
        toolbar = findViewById(R.id.top_shezhi);
        layout_feed_back = findViewById(R.id.linerLayout_fankui);
        bt_back_login = findViewById(R.id.bt_ss_back);
        layout_password = findViewById(R.id.layout_password);
        layout_username = findViewById(R.id.layout_username);
        tv_setting_name = findViewById(R.id.setting_username);
        layou_collection = findViewById(R.id.layout_collection);
        layout_image = findViewById(R.id.layout_image);
        layout_about = findViewById(R.id.layout_about);
        civ = findViewById(R.id.civ_image);
        progressBar = findViewById(R.id.pb_main_download);
    }
    void initItem(){
        //跳转至更换用户名界面
        layout_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,ModifyNameActivity.class);
                startActivity(intent);
            }
        });
        //跳转至更换密码界面
        layout_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,ModifyPasswordActivity.class);
                startActivity(intent);
            }
        });
        //跳转至反馈界面
        layout_feed_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, FeedBacKActivity.class);
                startActivity(intent);
            }
        });
        //退出登陆
        bt_back_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,MainActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                BmobUser.logOut();
            }
        });
        /*跳转至关于界面*/
        layout_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,AboutActivity.class);
                startActivity(intent);
            }
        });
        /*跳转至我的收藏*/
        layou_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,CollectionActivity.class);
                startActivity(intent);
            }
        });
        /*
        更换头像
        */
        layout_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(SettingActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(SettingActivity.this
                            ,new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE },1);
                }else {
                    openAlum();
                }

            }

        });
    }
    /*
    调用相册
     */
    private void openAlum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case CHOOSE_PHOTO:
                if(Build.VERSION.SDK_INT >= 19){
                    //4.4及以上系统用这个方法
                    handleImageOnKitKat(data);
                }else {
                    //4.4以下系统用这个方法
                    handleImageBeforeKiKat(data);
                }
                break;
            default:
                break;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data) {
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                //根据Uri和selection来获取真实的图片路径
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }

            //如果是content类型的Uri,则通过普通方式处理
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);

            //如果是file类型的Uri,直接获取图片路径即可
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        InitLuBan(new File(imagePath));
        //根据图片路径显示图片
        displayImage(imagePath);
        //上传图片
        upload(imagePath);
    }
    private void handleImageBeforeKiKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);

        InitLuBan(new File(imagePath));
        //根据图片路径显示图片
        displayImage(imagePath);
        //上传图片
        upload(imagePath);
    }
    //根据Uri和selection来获取真实的图片路径
    private String getImagePath(Uri uri, String selection) {
        //通过uri和selection获取图片路径
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //根据图片路径显示图片
    private void displayImage(String imagePath) {
        if(imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            civ.setImageBitmap(bitmap);
        }else {
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }


    /*
    上传图片
     */
    private void upload(final String imagePath){
        final BmobFile bmobFile = new BmobFile(new File(imagePath));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    saveFive(bmobFile);
                }
            }
            @Override
            public void onProgress(Integer value) {
                int progress = value;
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(progress);
                super.onProgress(value);
            }
        });
    }

    private void saveFive(BmobFile file){
        MyUser user = new MyUser();
        String objectId = (String) BmobUser.getObjectByKey("objectId");
        user.setObjectId(objectId);
        user.setImage(file);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SettingActivity.this,"上传照片成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SettingActivity.this,"上传照片失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void InitLuBan(File file){
        Luban.with(this).
                load(file)
                .ignoreBy(100)
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File file) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }).launch();
    }


}
