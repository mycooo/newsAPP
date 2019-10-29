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
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.prx.tvfdemo.db.MyUser;
import com.prx.tvfdemo.db.Post;
import java.io.File;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/*
    发布帖子界面
*/
public class ReleaseActivity extends AppCompatActivity {

    private EditText et1,et2;
    private Button bt_send,bt_back;
    private ImageButton bt_imgle;
    private ImageView content_image;
    private String path = null;
    private String imagePath = null;
    private ProgressBar progressBar;
    private static final int CHOOSE_PHOTO = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);
        initLoad();
        StatusBarUtils.setColor(this,getResources().getColor(R.color.colorWhite)); //沉浸式状态栏
        Bmob.initialize(this,"2cf4dd38d80cc0bc5160d5f7c97912bb");


        /*
        发布帖子
         */
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                            Post post = new Post();
                            post.setMyUser(BmobUser.getCurrentUser(MyUser.class)); //关联帖子id
                            post.setTitle(et1.getText().toString().trim());
                            post.setContent(et2.getText().toString().trim());
                            String username = (String) BmobUser.getObjectByKey("idName");
                            post.setImage(post.getMyUser().getImage());
                            post.setFile(post.getMyUser().getFile());
                            post.setName(username);
                            post.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if(e==null) {
                                        Intent intent = new Intent(ReleaseActivity.this,CommunityActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(ReleaseActivity.this, "发帖成功", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Log.e("aaa","未知错误");
                                    }
                                }
                            });
            }
        });

        /*
        返回
         */
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReleaseActivity.this.finish();
            }
        });

        /*
        上传图片
         */
        bt_imgle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(ReleaseActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ReleaseActivity.this
                            ,new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE },1);
                }else {
                    openAlum();
                }
            }
        });
    }

    void initLoad(){
        et1 = findViewById(R.id.release_bt);
        et2 = findViewById(R.id.release_rr);
        bt_send = findViewById(R.id.release_button);
        bt_back = findViewById(R.id.posting_fh);
        bt_imgle = findViewById(R.id.release_add_photo);
        content_image = findViewById(R.id.posting_image);
        progressBar = findViewById(R.id.pb_main_download);
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
        //上传图片
        upload(imagePath);
        //根据图片路径显示图片
        displayImage(imagePath);
    }
    private void handleImageBeforeKiKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        //上传图片
        upload(imagePath);
        //根据图片路径显示图片
        displayImage(imagePath);
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
            content_image.setImageBitmap(bitmap);
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
        user.setFile(file);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ReleaseActivity.this,"上传照片成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ReleaseActivity.this,"上传照片失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
