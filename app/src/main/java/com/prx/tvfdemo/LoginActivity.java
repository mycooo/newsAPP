package com.prx.tvfdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.prx.tvfdemo.db.MyUser;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/*
    注册登录
 */
public class LoginActivity extends AppCompatActivity {
    private Button bt1,bt2;
    private EditText et_id,et_password,et_username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bmob.initialize(this,"2cf4dd38d80cc0bc5160d5f7c97912bb");
        initLoad();

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUser user = new MyUser();
                user.setUsername(et_id.getText().toString().trim());
                user.setPassword(et_password.getText().toString().trim());
                user.setIdName(et_username.getText().toString().trim());
                user.signUp(new SaveListener<Object>() {
                    @Override
                    public void done(Object o, BmobException e) {
                        if(e==null){
                            Toast.makeText(LoginActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Log.e("注册失败","原因",e);
                        }
                    }
                });
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUser bmobUser = new MyUser();
                bmobUser.setUsername(et_id.getText().toString().trim());
                bmobUser.setPassword(et_password.getText().toString().trim());
                bmobUser.login(new SaveListener<Object>() {
                    @Override
                    public void done(Object o, BmobException e) {
                        if(e==null){
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            LoginActivity.this.finish();
                            Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Log.e("登录失败","原因",e);
                        }
                    }
                });
            }
        });
    }

    void initLoad(){
        bt1 = findViewById(R.id.bt_register);
        bt2 = findViewById(R.id.bt_login);
        et_username = findViewById(R.id.et_yh);
        et_password = findViewById(R.id.et_pass);
        et_id = findViewById(R.id.et_id);
    }
}
