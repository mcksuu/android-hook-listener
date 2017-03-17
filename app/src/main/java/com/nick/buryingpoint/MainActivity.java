package com.nick.buryingpoint;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, HookUtils.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View view = findViewById(R.id.tv_1);
        view.setTag("1");
        view.setOnClickListener(this);
        View view1 = findViewById(R.id.tv_2);
        view1.setTag("2");
        view1.setOnClickListener(this);
        View view2 = findViewById(R.id.tv_3);
        view2.setTag("3");
        view2.setOnClickListener(this);
        HookUtils.hookListener(this, this);//要在setOnxxxListener之后调用
    }

    @Override
    public void onClick(View v) {
        Log.d("fxxk", "点击id=" + v.getId() + "v===" + v.getTag().toString());
    }

    @Override
    public void beforeInListener(View v) {
        Log.d("fxxk", "点击前id=" + v.getId() + "v===" + v.getTag().toString());
    }

    @Override
    public void afterInListener(View v) {
        Log.d("fxxk", "点击后id=" + v.getId() + "v===" + v.getTag().toString());
    }
}
