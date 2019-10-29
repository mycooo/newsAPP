package com.prx.tvfdemo;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.prx.tvfdemo.adpater.myViewPagerAdapter;
import com.prx.tvfdemo.db.MyUser;
import com.prx.tvfdemo.db.Post;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.transform.Result;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity{

    private LocationClient mLocationClient;
    private TextView positionText;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ViewPager viewPager;
    private Intent intent;
    private CircleImageView civ;
    private MyUser user = new MyUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        Bmob.initialize(this,"2cf4dd38d80cc0bc5160d5f7c97912bb");;
        StatusBarUtils.setColor(this,getResources().getColor(R.color.colorPrimary)); //沉浸式状态栏
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("新闻一览");
        initView();
        voidLBS();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_xinwen:
                        viewPager.setCurrentItem(0,false);  //跳转到指定的viewpager页面
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case R.id.nav_back:
                        finish();
                        break;
                    case R.id.nav_yejian:    //夜间
                        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                        if(mode == Configuration.UI_MODE_NIGHT_YES){
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        }else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        }
                        recreate();
                        break;
                    case  R.id.nav_shequ:
                        intent = new Intent(MainActivity.this,CommunityActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_setting:
                        intent = new Intent(MainActivity.this,SettingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_about:
                        intent = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }


    private void initView(){
        mNavigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        myViewPagerAdapter myViewPagerAdapter1 = new myViewPagerAdapter(this, getSupportFragmentManager()) ;
        viewPager.setAdapter(myViewPagerAdapter1);
        tabLayout.setupWithViewPager(viewPager);

         /*
        获取用户名信息和头像，并显示在界面上
        */
        View headerLayout = mNavigationView.inflateHeaderView(R.layout.nav_header);
        civ = headerLayout.findViewById(R.id.icon_image);
        TextView tv = mNavigationView.getHeaderView(0).findViewById(R.id.mail);  //应该获取相应控件的上层父控件，从上到下逐级获取
        user = BmobUser.getCurrentUser(MyUser.class);

        if(user != null){
            String username = (String) BmobUser.getObjectByKey("idName");
            tv.setText("欢迎你,"+ username);
            /*判断是否有设置头像*/
            if(user.getImage() == null){
                civ.setImageResource(R.drawable.imge_tx);
                Toast.makeText(this,"未设置头像",Toast.LENGTH_SHORT).show();
            }else {
                //显示bmob表中用户的头像
                String image = user.getImage().getUrl();
                Glide.with(this).asBitmap().load(image).into(civ);
            }
        }else {
            Toast.makeText(this,"尚未登录",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //打开侧边框
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:

        }
        return true;
    }


    void voidLBS(){
         /*
        地图
  */
        positionText = mNavigationView.getHeaderView(0).findViewById(R.id.weizhi);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationClickListener());
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest
                .permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest
                .permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest
                .permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }else {
            requesstLocation();
        }
    }
        /*
     地图定位
    */
    private void requesstLocation(){
        mLocationClient.start();
        initLocation();
    }
        /*
    显示地址
    */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            requesstLocation();
                            return;
                        }
                    }
                    requesstLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
    private class MyLocationClickListener  implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StringBuilder currentPosition = new StringBuilder();
                    currentPosition.append(location.getCity()).append(location.getDistrict());
                    positionText.setText(currentPosition);
                }
            });
        }
    }
}
