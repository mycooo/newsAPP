package com.prx.tvfdemo.News;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.prx.tvfdemo.R;
import com.prx.tvfdemo.fragment.SocialNewsFragment;

public class JkNews extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categlory);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new SocialNewsFragment()).commit();

    }
}
