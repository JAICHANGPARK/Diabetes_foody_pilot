package com.dreamwalker.diabetesfoodypilot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.dreamwalker.diabetesfoodypilot.R;
import com.dreamwalker.diabetesfoodypilot.activity.appinfo.DeveloperActivity;
import com.dreamwalker.diabetesfoodypilot.activity.appinfo.FeedbackActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class SettingActivity extends AppCompatActivity implements IActivityBaseSetting{


    @BindView(R.id.developer_button)
    Button developerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initSetting();
    }

    private void initToasty(){
        Toasty.Config.getInstance().apply();
    }

    @Override
    public void bindView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initSetting() {
        bindView();
        initToasty();
    }

    @OnClick(R.id.developer_button)
    public void onClickedDeveloperButton(){
        startActivity(new Intent(SettingActivity.this, DeveloperActivity.class));
    }

    @OnClick(R.id.feedback_button)
    public void onClickedFeedbackButton(){
        startActivity(new Intent(SettingActivity.this, FeedbackActivity.class));
    }

    @OnClick(R.id.db_management_button)
    public void onClickedManagementButton(){
        Toasty.warning(this, "준비중..", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.home)
    public void onClickedHomeButton(){
        finish();
    }





}
