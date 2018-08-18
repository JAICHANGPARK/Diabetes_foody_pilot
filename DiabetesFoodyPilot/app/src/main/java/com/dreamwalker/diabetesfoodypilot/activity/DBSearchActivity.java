package com.dreamwalker.diabetesfoodypilot.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import com.dreamwalker.diabetesfoodypilot.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DBSearchActivity extends AppCompatActivity {

    private static final String TAG = "DBSearchActivity";

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbsearch);
        bindViews();

    }

    private void bindViews(){
        ButterKnife.bind(this);
    }

    @OnClick(R.id.fab)
    public void onClickFloatingActionButton(){

    }
}
