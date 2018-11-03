package com.dreamwalker.diabetesfoodypilot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.dreamwalker.diabetesfoodypilot.R;
import com.dreamwalker.diabetesfoodypilot.activity.accessory.foodtray.TrayScanActivity;
import com.dreamwalker.diabetesfoodypilot.activity.diary.VoiceDiaryActivity;
import com.dreamwalker.spacebottomnav.SpaceItem;
import com.dreamwalker.spacebottomnav.SpaceNavigationView;
import com.dreamwalker.spacebottomnav.SpaceOnClickListener;
import com.dreamwalker.spacebottomnav.SpaceOnLongClickListener;

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.consts.IntentConst;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;

public class ProfileHomeActivity extends AppCompatActivity implements IActivityBaseSetting {

    @BindView(R.id.space)
    SpaceNavigationView spaceNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_home);
        initSetting();
        setSpaceNavigationView(savedInstanceState);

    }

    @Override
    public void bindView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initSetting() {
        bindView();
        setStatusBarColor();
        initToasty();
    }

    private void initToasty() {
        Toasty.Config.getInstance().apply();
    }

    private void setStatusBarColor() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);// clear FLAG_TRANSLUCENT_STATUS flag:
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.default_background));// finally change the color
    }

    private void setSpaceNavigationView(Bundle sis) {
        spaceNavigationView.initWithSaveInstanceState(sis);
        spaceNavigationView.setCentreButtonIcon(R.drawable.ic_add_white_24dp);
        spaceNavigationView.addSpaceItem(new SpaceItem("HOME", R.drawable.ic_home_white_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("SEARCH", R.drawable.ic_search_white_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("CHART", R.drawable.ic_bubble_chart_white_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("PROFILE", R.drawable.ic_person_outline_white_24dp));
        spaceNavigationView.shouldShowFullBadgeText(true);
        spaceNavigationView.setCentreButtonIconColorFilterEnabled(false);
        spaceNavigationView.showIconOnly();

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Log.d("onCentreButtonClick ", "onCentreButtonClick");
                spaceNavigationView.shouldShowFullBadgeText(true);
                startActivity(new Intent(ProfileHomeActivity.this, TrayScanActivity.class));
//                Snackbar.make(getWindow().getDecorView().getRootView(), "길게눌러 ", Snackbar.LENGTH_SHORT).setAction(android.R.string.ok, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                    }
//                }).show();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {

                Log.d("onItemClick ", "" + itemIndex + " " + itemName);
                switch (itemIndex) {
                    case 0:
                        startActivity(new Intent(ProfileHomeActivity.this, HomeActivity.class));
                        finish();
                        break;
                    case 1:
                        Toasty.warning(ProfileHomeActivity.this, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(ProfileHomeActivity.this, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(HomeActivity.this, DBSearchActivity.class));
                        break;
                    case 2:
                        Toasty.warning(ProfileHomeActivity.this, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(HomeActivity.this, DBSearchActivity.class));
                        break;
                    case 3:
//                        Toast.makeText(ProfileHomeActivity.this, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(HomeActivity.this, DBSearchActivity.class));

                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Log.d("onItemReselected ", "" + itemIndex + " " + itemName);
                switch (itemIndex) {
                    case 0:
                        startActivity(new Intent(ProfileHomeActivity.this, HomeActivity.class));
                        finish();
                        break;
                    case 1:
                        Toasty.warning(ProfileHomeActivity.this, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(ProfileHomeActivity.this, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(HomeActivity.this, DBSearchActivity.class));
                        break;
                    case 2:
                        Toasty.warning(ProfileHomeActivity.this, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(ProfileHomeActivity.this, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(HomeActivity.this, DBSearchActivity.class));
                        break;
                    case 3:
//                        Toast.makeText(ProfileHomeActivity.this, "공사중--업데이트 예정이에요", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(HomeActivity.this, DBSearchActivity.class));
                        break;
                }
            }
        });

        spaceNavigationView.setSpaceOnLongClickListener(new SpaceOnLongClickListener() {
            @Override
            public void onCentreButtonLongClick() {
//                Toast.makeText(MainActivity.this, "onCentreButtonLongClick", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(ProfileHomeActivity.this, MainActivity.class);
//                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int itemIndex, String itemName) {
                Toast.makeText(ProfileHomeActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }

    @OnClick(R.id.about_app_button)
    public void onClickedSetting() {
        startActivity(new Intent(ProfileHomeActivity.this, SettingActivity.class));
    }

    @OnClick(R.id.my_device_button)
    public void onClickedMyDeviceButton() {
        if (Paper.book("user").read("device") == null) {
            Toasty.error(this, "등록된 액세서리가 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(ProfileHomeActivity.this, UserDeviceActivity.class));
        }
    }

    @OnClick(R.id.db_realtime_button)
    public void onClickedRealTimeButton() {
        Intent intent = new Intent(ProfileHomeActivity.this, TrayScanActivity.class);
        intent.putExtra(IntentConst.DEVICE_SCAN, IntentConst.REAL_TIME_SCAN_PAGE_VALUE);
        startActivity(intent);

    }

    @OnClick(R.id.db_sync_button)
    public void onClickedSyncButton() {
        Intent intent = new Intent(ProfileHomeActivity.this, TrayScanActivity.class);
        intent.putExtra(IntentConst.DEVICE_SCAN, IntentConst.SYNC_SCAN_PAGE_VALUE);
        startActivity(intent);

    }

    @OnClick(R.id.voice_memo_button)
    public void onClickedVoiceMemoButton(){
        Intent intent = new Intent(ProfileHomeActivity.this, VoiceDiaryActivity.class);
//        intent.putExtra(IntentConst.DEVICE_SCAN, IntentConst.SYNC_SCAN_PAGE_VALUE);
        startActivity(intent);
    }


}
