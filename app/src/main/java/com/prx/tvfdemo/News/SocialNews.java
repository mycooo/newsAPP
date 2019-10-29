package com.prx.tvfdemo.News;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.prx.tvfdemo.R;
import com.prx.tvfdemo.fragment.SocialNewsFragment;

public class SocialNews extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categlory);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new SocialNewsFragment()).commit();

    }
}
