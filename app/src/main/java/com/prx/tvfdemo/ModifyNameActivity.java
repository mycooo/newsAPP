package com.prx.tvfdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.prx.tvfdemo.db.MyUser;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/*
    修改用户名
 */
public class ModifyNameActivity extends AppCompatActivity {
    private Button bt_back,bt_send;
    private EditText et_modify_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_name);
        Bmob.initialize(this,"2cf4dd38d80cc0bc5160d5f7c97912bb");
        StatusBarUtils.setColor(this,getResources().getColor(R.color.colorWhite)); //沉浸式状态栏
        initView();
        onItem();
    }
    void initView(){
        bt_back = findViewById(R.id.bt_ModifyName_back);
        bt_send =findViewById(R.id.bt_ModifyName_send);
        et_modify_name = findViewById(R.id.user_Modify_name);
    }
    void onItem(){
        //返回上个界面
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyNameActivity.this.finish();
            }
        });

        String idName = (String) BmobUser.getObjectByKey("idName");
        et_modify_name.setText(idName);

        //修改用户名
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_modify_name != null){
                    final MyUser user = new MyUser();
                    String objectId = (String) BmobUser.getObjectByKey("objectId");
                    final String user_name = et_modify_name.getText().toString().trim();
                    user.setIdName(user_name);
                    Log.e("bbb",user_name);
                    user.update(objectId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Intent intent = new Intent(ModifyNameActivity.this,SettingActivity.class);
                                intent.putExtra("username",user.getIdName());
                                startActivity(intent);
                                Toast.makeText(ModifyNameActivity.this,"成功修改用户名",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(ModifyNameActivity.this,"未知错误",Toast.LENGTH_SHORT).show();
                                Log.e("ccc",user.getObjectId()+"");
                            }
                        }
                    });
                }else {
                    Toast.makeText(ModifyNameActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
