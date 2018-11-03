package com.dreamwalker.diabetesfoodypilot.activity.diary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dreamwalker.diabetesfoodypilot.R;
import com.dreamwalker.diabetesfoodypilot.activity.IActivityBaseSetting;
import com.dreamwalker.spacebottomnav.SpaceNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class VoiceRecordActivityV2 extends AppCompatActivity implements IActivityBaseSetting {

    @BindView(R.id.space)
    SpaceNavigationView spaceNavigationView;

    Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_record_v2);
        initSetting();
//        setSpaceNavigationView(savedInstanceState);

    }

    private void initToasty() {
        Toasty.Config.getInstance().apply();
    }

    private void initRealm(){

        RealmConfiguration realmConfig = new RealmConfiguration.Builder().name("voice_diary.realm").build();
        realm = Realm.getInstance(realmConfig);

    }

    @Override
    public void bindView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initSetting() {
        bindView();
        initToasty();
        initRealm();
    }
}
