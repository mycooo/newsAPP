package com.prx.tvfdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.prx.tvfdemo.db.MyUser;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/*
    修改密码
 */
public class ModifyPasswordActivity extends AppCompatActivity {
    private Button bt_back,bt_send;
    private EditText et_old_password,et_new_password,et_new_password2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        StatusBarUtils.setColor(this,getResources().getColor(R.color.colorWhite)); //沉浸式状态栏
        initView();
        onItem();
    }
    void initView(){
        bt_back = findViewById(R.id.bt_ModifyPassword_back);
        bt_send =findViewById(R.id.bt_ModifyPassword_send);
        et_old_password = findViewById(R.id.et_modify_password);
        et_new_password = findViewById(R.id.et_new_password_one);
        et_new_password2 = findViewById(R.id.et_new_password_two);
    }
    void onItem(){
        /*
        返回上个界面
         */
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyPasswordActivity.this.finish();
            }
        });
        /*
        点击更换密码
         */
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUser user = new MyUser();
                String oldPassword = et_old_password.getText().toString().trim();
                String newPassword = et_new_password.getText().toString().trim();
                String newPasswordAgain = et_new_password2.getText().toString().trim();
                Log.e("aaa",oldPassword+"");
                Log.e("aaa","密码1："+newPassword+" 新密码2："+newPasswordAgain+"");
                if(TextUtils.isEmpty(oldPassword) ) {
                    Toast.makeText(ModifyPasswordActivity.this,"请输入原始密码",Toast.LENGTH_SHORT).show();
                }else if(newPassword.equals(oldPassword)){
                    Toast.makeText(ModifyPasswordActivity.this,"输入新密码与原始密码不能一致",Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(newPassword) && TextUtils.isEmpty(newPasswordAgain)){
                    Toast.makeText(ModifyPasswordActivity.this,"请输入新密码",Toast.LENGTH_SHORT).show();
                } else if(!newPassword.equals(newPasswordAgain)){
                    Toast.makeText(ModifyPasswordActivity.this,"两次新密码不一致",Toast.LENGTH_SHORT).show();
                }else {
                    user.updateCurrentUserPassword(oldPassword, newPassword, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                                ModifyPasswordActivity.this.finish();
                                Toast.makeText(ModifyPasswordActivity.this,"修改密码成功",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(ModifyPasswordActivity.this,"输入原密码错误",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
