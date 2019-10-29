package com.prx.tvfdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.prx.tvfdemo.db.FeedBack;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


/*
    反馈信息界面
 */
public class FeedBacKActivity extends AppCompatActivity {

    private EditText et_rr,et_lx;
    private Button bt_back,bt_send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initLoad();
        Bmob.initialize(this,"2cf4dd38d80cc0bc5160d5f7c97912bb");
        StatusBarUtils.setColor(this,getResources().getColor(R.color.colorWhite)); //沉浸式状态栏

        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedBack feedBack = new FeedBack();
                feedBack.setDescribe(et_rr.getText().toString().trim());
                feedBack.setPhone(et_lx.getText().toString().trim());
                feedBack.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null) {
                            if(BmobUser.isLogin()){
                                et_rr.setText("");
                                et_lx.setText("");
                                Toast.makeText(FeedBacKActivity.this, "反馈信息成功", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(FeedBacKActivity.this, "请登陆成功后在反馈哦。", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Log.e("反馈信息失败","原因",e);
                        }
                    }
                });
            }
        });
        //返回上一个界面
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedBacKActivity.this.finish();
            }
        });
    }




    protected void initLoad(){
        et_lx = findViewById(R.id.et_feedback_lx);
        et_rr = findViewById(R.id.et_feedback_rr);
        bt_back = findViewById(R.id.bt_feedback_back);
        bt_send = findViewById(R.id.bt_feedback_send);
    }
}
