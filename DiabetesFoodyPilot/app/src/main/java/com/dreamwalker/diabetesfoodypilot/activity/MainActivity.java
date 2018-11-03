package com.dreamwalker.diabetesfoodypilot.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.dreamwalker.diabetesfoodypilot.R;
import com.dreamwalker.diabetesfoodypilot.adapter.CartListAdapter;
import com.dreamwalker.diabetesfoodypilot.adapter.CartListAdapterV2;
import com.dreamwalker.diabetesfoodypilot.adapter.CartListAdapterV3;
import com.dreamwalker.diabetesfoodypilot.adapter.OnItemClickListrner;
import com.dreamwalker.diabetesfoodypilot.adapter.OnSearchItemClickListener;
import com.dreamwalker.diabetesfoodypilot.adapter.RecyclerItemTouchHelperV3;
import com.dreamwalker.diabetesfoodypilot.adapter.SearchAdapter;
import com.dreamwalker.diabetesfoodypilot.adapter.SearchAdapterV2;
import com.dreamwalker.diabetesfoodypilot.database.food.FoodDock;
import com.dreamwalker.diabetesfoodypilot.database.food.FoodItem;
import com.dreamwalker.diabetesfoodypilot.database.food.MixedFoodItem;
import com.dreamwalker.diabetesfoodypilot.model.Food;
import com.dreamwalker.diabetesfoodypilot.model.FoodCard;
import com.dreamwalker.diabetesfoodypilot.model.FoodTotal;
import com.dreamwalker.diabetesfoodypilot.model.MixedFood;
import com.dreamwalker.diabetesfoodypilot.model.Suggestions;
import com.dreamwalker.diabetesfoodypilot.model.TestModel;
import com.dreamwalker.diabetesfoodypilot.remote.Common;
import com.dreamwalker.diabetesfoodypilot.remote.IMenuRequest;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.philliphsu.bottomsheetpickers.time.BottomSheetTimePickerDialog;
import com.philliphsu.bottomsheetpickers.time.numberpad.NumberPadTimePickerDialog;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnItemClickListrner,
        RecyclerItemTouchHelperV3.RecyclerItemTouchHelperListener,
        OnSearchItemClickListener,
        BottomSheetTimePickerDialog.OnTimeSetListener,
        com.philliphsu.bottomsheetpickers.date.DatePickerDialog.OnDateSetListener {

    private static final String TAG = "MainActivity";
    private final String URL_API = "https://api.androidhive.info/json/menu.json";

//
//    @BindView(R.id.bottomSheet)
//    LinearLayout mBottomSheet;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.bottomSheet)
    CoordinatorLayout mBottomSheet;


    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.floating_search_view)
    FloatingSearchView floatingSearchView;

    // TODO: 2018-08-19 상단 이미지 버튼
    @BindView(R.id.add)
    ImageView addItemButton;
    @BindView(R.id.save)
    ImageView saveItemButton;
    @BindView(R.id.calendar)
    ImageView calendarButton;

    @BindView(R.id.bottom_recycler_view)
    RecyclerView bottomRecyclerView;

    private List<TestModel> cartList;
    private CartListAdapter mAdapter;

    IMenuRequest service;

    SearchAdapter searchAdapter;
    SearchAdapterV2 searchAdapterV2;
    ArrayList<FoodItem> searchList = new ArrayList<>();
    ArrayList<MixedFoodItem> searchListV2 = new ArrayList<>();

    BottomSheetBehavior bottomSheetBehavior;
//    BottomSheetBehavior bottomSheetBehaviorDateTime;

    Realm realm;
    RealmConfiguration realmConfiguration;

    CartListAdapterV2 adapterV2;
    CartListAdapterV3 adapterV3;

    ArrayList<Food> foodArrayList = new ArrayList<>();
    ArrayList<Integer> imageList = new ArrayList<>();

    ArrayList<FoodCard> foodCardArrayList = new ArrayList<>();
    ArrayList<FoodCard> backgroundArrayList = new ArrayList<>();
    ArrayList<com.dreamwalker.diabetesfoodypilot.database.food.FoodCard> backgroundDBArrayList = new ArrayList<>();
    ArrayList<MixedFood> mixedFoodArrayList = new ArrayList<>(10);

    RealmResults<MixedFoodItem> results;


    ArrayList<FoodTotal> resultArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 2018-08-15 액티비티 풀스크린 전환 - 박제창
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        bindViews();
        setBottomSheetBehavior();
//        setDateTimeBottomSheetBehavior();

        // TODO: 2018-08-15 Realm 초기화 - 박제창
        initRealm();

        // TODO: 2018-08-15 card animation added - 박제창
        initCardAnimate();

        service = Common.getMenuRequest();

        //recyclerView = findViewById(R.id.recycler_view);
        cartList = new ArrayList<>();
        mAdapter = new CartListAdapter(this, cartList);

//        setInitData();
        // TODO: 2018-08-18 버전별 데이터 세팅 변경 메소드 호출요 - 박제창
        setInitDataVersion(3);
        // TODO: 2018-08-18 버전별 리사이클러 뷰 변경 이요 (메소드 참고하세요 )- 박제창
        initRecyclerView(3);

        initBottomRecyclerView();
        fetchFromRealm();
        setFloatingSearchView();

//        recyclerView.setAdapter(mAdapter);
        //addItemToCart();
//        for (int i = 0 ; i < results.size(); i++){
//            Log.e(TAG, "onCreate: " + results.get(i).getFoodName() );
//        }

    }

    private void initBottomRecyclerView() {

        searchAdapter = new SearchAdapter(this, searchList);
        searchAdapterV2 = new SearchAdapterV2(this, searchListV2);
        // TODO: 2018-08-18 터치 리스너 추가
        searchAdapterV2.setOnSearchItemClickListener(this);
        //bottomRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
        bottomRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));
        bottomRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // bottomRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        // bottomRecyclerView.setHasFixedSize(true);
        //bottomRecyclerView.setNestedScrollingEnabled(true);
        bottomRecyclerView.setAdapter(searchAdapterV2);

    }

    private void initRecyclerView(int versionCode) {
        RecyclerView.LayoutManager mLayoutManager;

        switch (versionCode) {
            case 1:
                break;
            case 2:
//                adapterV2 = new CartListAdapterV2(this, imageList, foodArrayList);
//                mLayoutManager = new LinearLayoutManager(getApplicationContext());
//                recyclerView.setLayoutManager(mLayoutManager);
//                recyclerView.setItemAnimator(new DefaultItemAnimator());
//                recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//                recyclerView.setAdapter(adapterV2);
//                ItemTouchHelper.SimpleCallback itemTouchHelperCallbackV2 = new RecyclerItemTouchHelperV2(0, ItemTouchHelper.LEFT, this);
//                new ItemTouchHelper(itemTouchHelperCallbackV2).attachToRecyclerView(recyclerView);
//                adapterV2.setOnItemClickListrner(this);
                break;
            case 3:
                adapterV3 = new CartListAdapterV3(this, foodCardArrayList);
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
                recyclerView.setAdapter(adapterV3);
                ItemTouchHelper.SimpleCallback itemTouchHelperCallbackV3 = new RecyclerItemTouchHelperV3(0, ItemTouchHelper.LEFT, this);
                new ItemTouchHelper(itemTouchHelperCallbackV3).attachToRecyclerView(recyclerView);
                adapterV3.setOnItemClickListrner(this);
                break;
        }

    }

    private void bindViews() {
        ButterKnife.bind(this);
    }

    private void initCardAnimate() {
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        RelativeLayout rl = findViewById(R.id.card_container);
        rl.startAnimation(slideUp);
    }

    private void initRealm() {
        Realm.init(this);
//        realm = Realm.getDefaultInstance();

        realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        realm = Realm.getInstance(realmConfiguration);
    }

    private void fetchFromRealm() {
        results = realm.where(MixedFoodItem.class).findAll();
        Log.e(TAG, "onCreate: " + results.size());
    }

    private void setInitDataVersion(int versionCode) {
        switch (versionCode) {
            case 1:
                break;
            case 2:
                foodArrayList.add(new Food("", "식품 선택 입력", "", "", "", "", ""));
                imageList.add(R.drawable.rice);

                foodArrayList.add(new Food("", "식품 선택 입력", "", "", "", "", ""));
                imageList.add(R.drawable.soup);

                foodArrayList.add(new Food("", "식품 입력", "", "", "", "", ""));
                imageList.add(R.drawable.side_dish_01);

                foodArrayList.add(new Food("", "식품 입력", "", "", "", "", ""));
                imageList.add(R.drawable.side_dish_02);

                foodArrayList.add(new Food("", "식품 입력", "", "", "", "", ""));
                imageList.add(R.drawable.side_dish_03);

                foodArrayList.add(new Food("", "식품 입력", "", "", "", "", ""));
                imageList.add(R.drawable.side_dish_04);
                break;
            case 3:
                foodCardArrayList.add(new FoodCard("밥", "없음", "식품 터치 검색", "0", "0"));
                foodCardArrayList.add(new FoodCard("국", "없음", "식품 터치 검색", "0", "0"));
                foodCardArrayList.add(new FoodCard("찬", "없음", "식품 터치 검색", "0", "0"));
                foodCardArrayList.add(new FoodCard("찬", "없음", "식품 터치 검색", "0", "0"));
                foodCardArrayList.add(new FoodCard("찬", "없음", "식품 터치 검색", "0", "0"));
                foodCardArrayList.add(new FoodCard("찬", "없음", "식품 터치 검색", "0", "0"));

                mixedFoodArrayList.add(new MixedFood("", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                mixedFoodArrayList.add(new MixedFood("", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                mixedFoodArrayList.add(new MixedFood("", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                mixedFoodArrayList.add(new MixedFood("", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                mixedFoodArrayList.add(new MixedFood("", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                mixedFoodArrayList.add(new MixedFood("", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));


                backgroundArrayList.add(new FoodCard("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                backgroundArrayList.add(new FoodCard("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                backgroundArrayList.add(new FoodCard("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                backgroundArrayList.add(new FoodCard("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                backgroundArrayList.add(new FoodCard("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));

                backgroundDBArrayList.add(new com.dreamwalker.diabetesfoodypilot.database.food.FoodCard("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                backgroundDBArrayList.add(new com.dreamwalker.diabetesfoodypilot.database.food.FoodCard("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                backgroundDBArrayList.add(new com.dreamwalker.diabetesfoodypilot.database.food.FoodCard("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                backgroundDBArrayList.add(new com.dreamwalker.diabetesfoodypilot.database.food.FoodCard("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                backgroundDBArrayList.add(new com.dreamwalker.diabetesfoodypilot.database.food.FoodCard("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                backgroundDBArrayList.add(new com.dreamwalker.diabetesfoodypilot.database.food.FoodCard("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));

        }
    }


    /**
     * 검색 처리
     */
    private void setFloatingSearchView() {

        floatingSearchView.setOnQueryChangeListener((oldQuery, newQuery) -> {

            searchListV2.clear();
            searchAdapterV2.notifyDataSetChanged();

            List<Suggestions> foodName = new ArrayList<>();
            Log.e(TAG, ": oldQuery --> " + oldQuery + " new Query --> " + newQuery);
            RealmResults<MixedFoodItem> result1 = realm.where(MixedFoodItem.class).contains("foodName", newQuery).findAll();
//            RealmResults<FoodItem> result1 = realm.where(FoodItem.class).contains("foodName", newQuery).findAll();
            //floatingSearchView.swapSuggestions();
            //RealmResults<FoodItem> result1 = realm.where(FoodItem.class).like("foodName", newQuery, Case.INSENSITIVE).findAllSortedAsync("foodName", Sort.ASCENDING);
            if (result1.size() != 0) {
//                for (FoodItem item : result1) {
//                    foodName.add(new Suggestions(item.getFoodName()));
//                }
                for (MixedFoodItem item : result1) {
                    foodName.add(new Suggestions(item.getFoodName()));
                }
            }

            floatingSearchView.swapSuggestions(foodName);


            //floatingSearchView.swapSuggestions(foodName);

        });


        floatingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                // Log.e(TAG, "onSuggestionClicked: ");
                searchListV2.clear();
                searchAdapterV2.notifyDataSetChanged();
                Log.e(TAG, "onSuggestionClicked: " + searchSuggestion.getBody());
                RealmResults<MixedFoodItem> result1 = realm.where(MixedFoodItem.class).contains("foodName", searchSuggestion.getBody()).findAll();
                searchListV2.addAll(result1);
                //searchAdapter.notifyDataSetChanged();
                searchAdapterV2.notifyDataSetChanged();
                floatingSearchView.setSearchFocused(false);
            }

            @Override
            public void onSearchAction(String currentQuery) {
                Log.e(TAG, "onSearchAction: " + currentQuery);
                searchListV2.clear();
                List<String> foodName = new ArrayList<>();
                //floatingSearchView.swapSuggestions();
//                RealmResults<FoodItem> result1 = realm.where(FoodItem.class).like("foodName", currentQuery, Case.INSENSITIVE).findAllSortedAsync("foodName", Sort.ASCENDING);
//                RealmResults<FoodItem> result1 = realm.where(FoodItem.class).contains("foodName", currentQuery).findAll();
                RealmResults<MixedFoodItem> result1 = realm.where(MixedFoodItem.class).contains("foodName", currentQuery).findAll();
                if (result1.size() != 0) {
                    for (MixedFoodItem item : result1) {

                        Log.e(TAG, "onSearchAction: " + item.getFoodName());
                        foodName.add(item.getFoodName());
                    }
                }

                searchListV2.addAll(result1);
                //searchAdapter.notifyDataSetChanged();
                searchAdapterV2.notifyDataSetChanged();
            }
        });
    }

    /**
     * BottomSheet 처리 메소드
     */
    private void setBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        //finish();
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        bottomSheet.requestLayout();
                        setStatusBarDim(false);
                        break;
                    default:
                        setStatusBarDim(true);
                        break;
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

//    private void setDateTimeBottomSheetBehavior() {
//        bottomSheetBehaviorDateTime = BottomSheetBehavior.from(mBottomSheet2);
//        bottomSheetBehaviorDateTime.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//
//                switch (newState) {
//                    case BottomSheetBehavior.STATE_HIDDEN:
//                        //finish();
//                        break;
//                    case BottomSheetBehavior.STATE_EXPANDED:
//                        bottomSheet.requestLayout();
//                        setStatusBarDim(false);
//
//                        break;
//                    default:
//                        setStatusBarDim(true);
//                        break;
//                }
//
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
//            }
//        });
//
//        bottomSheetBehaviorDateTime.setState(BottomSheetBehavior.STATE_HIDDEN);
//    }


    @Deprecated
    private void addItemToCart() {

        service.getMenuList(URL_API).enqueue(new Callback<List<TestModel>>() {
            @Override
            public void onResponse(Call<List<TestModel>> call, Response<List<TestModel>> response) {
                cartList.clear();
                cartList.addAll(response.body());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<TestModel>> call, Throwable t) {

            }
        });
    }

    // TODO: 2018-08-15 리사이클러뷰 삭제 시 처리
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof CartListAdapterV3.MyViewHolder) {
            // get the removed item name to display it in snack bar
//            String name = foodArrayList.get(viewHolder.getAdapterPosition()).getFoodName();
            // backup of removed item for undo purpose
//            final Food deletedItem = foodArrayList.get(viewHolder.getAdapterPosition());
            String name = foodCardArrayList.get(viewHolder.getAdapterPosition()).getFoodName();
            final FoodCard deletedItem = foodCardArrayList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            //adapterV2.removeItem(viewHolder.getAdapterPosition());
            adapterV3.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    adapterV3.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
//        if (viewHolder instanceof CartListAdapter.MyViewHolder) {
//            // get the removed item name to display it in snack bar
//            String name = cartList.get(viewHolder.getAdapterPosition()).getName();
//
//            // backup of removed item for undo purpose
//            final TestModel deletedItem = cartList.get(viewHolder.getAdapterPosition());
//            final int deletedIndex = viewHolder.getAdapterPosition();
//
//            // remove the item from recycler view
//            mAdapter.removeItem(viewHolder.getAdapterPosition());
//
//            // showing snack bar with Undo option
//            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), name + " removed from cart!", Snackbar.LENGTH_LONG);
//            snackbar.setAction("UNDO", new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    // undo is selected, restore the deleted item
//                    mAdapter.restoreItem(deletedItem, deletedIndex);
//                }
//            });
//            snackbar.setActionTextColor(Color.YELLOW);
//            snackbar.show();
//        }
    }

    @OnClick(R.id.fab)
    public void fadClicked() {

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();

        int width = dm.widthPixels;

        int height = dm.heightPixels;
        Log.e(TAG, "fadClicked: " + height);


        //bottomSheetBehavior.setPeekHeight(60);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//        floatingActionButton.setVisibility(View.GONE);
    }

    private void setStatusBarDim(boolean dim) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(dim ? Color.TRANSPARENT : ContextCompat.getColor(this, getThemedResId(R.attr.colorPrimaryDark)));
        }
    }

    private int getThemedResId(@AttrRes int attr) {
        TypedArray a = getTheme().obtainStyledAttributes(new int[]{attr});
        int resId = a.getResourceId(0, 0);
        a.recycle();
        return resId;
    }

    @OnClick(R.id.add)
    public void onClickedAddButton() {
//        foodArrayList.add(new Food("","식품 입력","","","","",""));
//        imageList.add(R.drawable.rice);
        final String[] selectedItem = new String[1];
        String[] listItems = new String[]{"밥", "국", "반찬", "기타"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an Items");

        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e(TAG, "onClick: " + i);
                switch (i) {
                    case 0:
                        selectedItem[0] = "밥";
                        break;
                    case 1:
                        selectedItem[0] = "국";
                        break;
                    case 2:
                        selectedItem[0] = "찬";
                        break;
                    case 3:
                        selectedItem[0] = "찬";
                        break;
                }
            }
        });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e(TAG, "반찬 추가 onClick: " + i);


//                adapterV2.addItem(new Food("", "식품 입력", "", "", "", "", ""),
//                        R.drawable.side_dish_04);
//                adapterV2.notifyDataSetChanged();
                adapterV3.addItem(new FoodCard(selectedItem[0], "없음", "식품 터치 검색", "0", "0"));
                adapterV3.notifyDataSetChanged();
                // TODO: 2018-08-18 백그라운드에서 하나 생성
                mixedFoodArrayList.add(new MixedFood("", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                backgroundArrayList.add(new FoodCard("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                backgroundDBArrayList.add(new com.dreamwalker.diabetesfoodypilot.database.food.FoodCard("", "",
                        "", "", "", "", "", "", "", "",
                        "", "", "", "", "", ""));
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    String intakeType; // 아침, 점심, 저녁, 간식 구분 맴버 변수

    @OnClick(R.id.save)
    public void onClickedSaveButton() {

        boolean dataExistCheck = false;
        for (MixedFood mf : mixedFoodArrayList) {
            if (!mf.getFoodName().equals("")) {
                dataExistCheck = true;
            }
        }
        // TODO: 2018-08-18 식품선택이 진행되지 않았을 경우의 사용자 예외 처리 - 박제창
        if (dataExistCheck) {
            showSaveDialog();
        } else {
            Snackbar.make(getWindow().getDecorView().getRootView(), "식품을 추가해주세요", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showSaveDialog() {

        // TODO: 2018-08-16 다이얼 로그를 보여주고 유형 선택하기
        String[] listItems = new String[]{"아침", "점심", "저녁", "간식"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an Items");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e(TAG, "onClick: " + i);
                intakeType = listItems[i];
                Log.e(TAG, "onClick: intake--> " + intakeType);
            }
        });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // TODO: 2018-08-18 1 페이즈 최종 저장 처리 부분 - 박제창

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA);
                ArrayList<FoodCard> tempList = new ArrayList<>();
                RealmList<com.dreamwalker.diabetesfoodypilot.database.food.FoodCard> temp2List = new RealmList<>();
                RealmList<com.dreamwalker.diabetesfoodypilot.database.food.FoodTotal> resultList = new RealmList<>();
                // TODO: 2018-08-19  시간 저장되어있는지 확인
                if (startDate == null || endDate == null) {
                    Toast.makeText(MainActivity.this, "식사 시간을 설정해주세요", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                } else {

//                    for (int k = 0; k < mixedFoodArrayList.size(); k++) {
//                        if (!mixedFoodArrayList.get(k).getFoodName().equals("")) {
//                            tempList.add(mixedFoodArrayList.get(k));
//                        } else {
//                            // TODO: 2018-08-18 Pass
//                            Log.e(TAG, "onClick: " + "pass");
//                        }
//                    }

                    for (int k = 0; k < backgroundArrayList.size(); k++) {
                        if (!backgroundArrayList.get(k).getFoodName().equals("")) {
                            tempList.add(backgroundArrayList.get(k));
                            temp2List.add(backgroundDBArrayList.get(k));
                        } else {
                            // TODO: 2018-08-18 Pass
                            Log.e(TAG, "onClick: " + "pass");
                        }
                    }

                    if (tempList.size() != 0) {
                        resultArrayList.add(new FoodTotal(intakeType, tempList));
                        resultList.add(new com.dreamwalker.diabetesfoodypilot.database.food.FoodTotal(intakeType, temp2List));
                        for (FoodTotal m : resultArrayList) {
                            Log.e(TAG, "최종: " + m.getIntakeType());
                            for (FoodCard mf : m.getFoodCardArrayList()) {
                                Log.e(TAG, "최종 리스트 결과: " + mf.getFoodName());
                            }
                        }

                        Date saveDate = new Date();
                        Timestamp timestamp = new Timestamp(saveDate.getTime());
                        long ts = timestamp.getTime();
                        Log.e(TAG, "onClick: " + saveDate + "|" + ts);
                        RealmList<com.dreamwalker.diabetesfoodypilot.database.food.FoodTotal> realmList = new RealmList<>();

                        realmList.addAll(resultList);

                        //realm = Realm.getDefaultInstance();

                        realm.beginTransaction();
                        FoodDock foodDock = new FoodDock();
                        foodDock.setFoodTotals(realmList);
                        foodDock.setSaveDate(saveDate);
                        foodDock.setTimestamp(ts);
                        foodDock.setUserSelectDate(nowDate);
                        foodDock.setStartIntakeDate(startDate);
                        foodDock.setEndIntakeDate(endDate);
                        realm.insertOrUpdate(foodDock);
//                        FoodDock foodItem = realm.createObject(FoodDock.class);
                        realm.commitTransaction();

                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();


                    } else {
                        Log.e(TAG, "onClick: tempList Size Zero");
                    }
                    dialogInterface.dismiss();
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resultArrayList.clear();
                dialogInterface.dismiss();
            }
        });
        builder.show();

    }

    // TODO: 2018-08-19 날짜 시간 선택 다이얼로그 뷰 위젯 클래스 맴버
    TextView dateTextView;
    TextView startTimeTextView;
    TextView endTimeTextView;
    ImageView dateImagePicker;
    ImageView startTimeImagePicker;
    ImageView endTimeImagePicker;
    MaterialButton materialButton;

    boolean startTimeSelectFlag = false;
    boolean endTimeSelectFlag = false;

    private AlertDialog.Builder showDateTimePickerDialog() {

        Calendar now = Calendar.getInstance();
// As of version 2.3.0, `BottomSheetDatePickerDialog` is deprecated.
        DatePickerDialog date = DatePickerDialog.newInstance(
                MainActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
//        date.setThemeDark(true);

        date.setHeaderColor(getResources().getColor(R.color.blue));
        date.setAccentColor(getResources().getColor(R.color.blue));

        NumberPadTimePickerDialog pad = NumberPadTimePickerDialog.newInstance(
                MainActivity.this,
                false);
//        GridTimePickerDialog grid = GridTimePickerDialog.newInstance(
//                MainActivity.this,
//                now.get(Calendar.HOUR_OF_DAY),
//                now.get(Calendar.MINUTE),
//                DateFormat.is24HourFormat(MainActivity.this));
//        grid.setAccentColor(getResources().getColor(android.R.color.white));
//        grid.setBackgroundColor(getResources().getColor(android.R.color.white));
////        grid.setTimeSeparatorColor(getResources().getColor(android.R.color.white));
//        grid.setHeaderTextColorSelected(getResources().getColor(android.R.color.black));
//        grid.setHeaderTextColorUnselected(getResources().getColor(android.R.color.darker_gray));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh시 mm분", Locale.KOREA);

//        bottomSheetBehaviorDateTime.setState(BottomSheetBehavior.STATE_COLLAPSED);
        // floatingActionButton.setVisibility(View.GONE);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        dialog.setTitle("REGISTER");
//        dialog.setMessage("Please use email to register");

//        Calendar now = Calendar.getInstance();
//        Date date = now.getTime();
        LayoutInflater inflater = LayoutInflater.from(this);
        View registerLayout = inflater.inflate(R.layout.layout_bottom_sheet_datetime, null);

        dateTextView = registerLayout.findViewById(R.id.date_text_view);
        startTimeTextView = registerLayout.findViewById(R.id.start_time_text_view);
        endTimeTextView = registerLayout.findViewById(R.id.end_time_text_view);
        dateImagePicker = registerLayout.findViewById(R.id.date_picker);
        startTimeImagePicker = registerLayout.findViewById(R.id.start_time_picker);
        endTimeImagePicker = registerLayout.findViewById(R.id.end_time_picker);
        materialButton = registerLayout.findViewById(R.id.select_button);

        nowDate = now.getTime();
        dateTextView.setText(dateFormat.format(now.getTime()));

        startIntakeTimestamp = now.getTime().getTime();
        endIntakeTimestamp = now.getTime().getTime();

        if (startIntakeTime != null && endIntakeTime != null) {
            startTimeTextView.setText(startIntakeTime);
            endTimeTextView.setText(endIntakeTime);
        } else {
            startIntakeTime = timeFormat.format(now.getTime());
            endIntakeTime = timeFormat.format(now.getTime());
            startTimeTextView.setText(startIntakeTime);
            endTimeTextView.setText(endIntakeTime);
        }


        dateImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                date.show(getSupportFragmentManager(), "datepicker");
            }
        });

        startTimeImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimeSelectFlag = true;
                pad.show(getSupportFragmentManager(), "timePicker");
//                grid.show(getSupportFragmentManager(), "timePicker");
            }
        });

        endTimeImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTimeSelectFlag = true;
                pad.show(getSupportFragmentManager(), "timePicker2");
            }
        });
        //registerLayout.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        dialog.setView(registerLayout);

//        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//
//
//            }
//        });
//
//        dialog.setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });


        return dialog;

    }

    @OnClick(R.id.calendar)
    public void onClickCalendar() {

        AlertDialog alertDialog = showDateTimePickerDialog().create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        materialButton.setOnClickListener(view -> {
            if (startIntakeTimestamp == endIntakeTimestamp) {
                Toast.makeText(MainActivity.this, "시작시간과 종료시간은 같을 수 없습니다.", Toast.LENGTH_SHORT).show();
            } else if (startIntakeTimestamp > endIntakeTimestamp) {
                Toast.makeText(MainActivity.this, "종료 시간이 시작 시간보다 빠를 수 없습니다.", Toast.LENGTH_SHORT).show();
            } else {
                alertDialog.dismiss();
            }


        });

        alertDialog.show();

    }



    // TODO: 2018-08-18 선택하지 않고 검색만 할 경우를 대비해야한다. - 박제창
    private int cartListPosition = 1000;

    // TODO: 2018-08-18 음식 리스트 터치 리스너
    @Override
    public void onItemClick(View v, int position) {
        cartListPosition = position;
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        Log.e(TAG, "onItemClick: " + position);
        //Log.e(TAG, "onItemClick: " + searchListV2.get(position).getFoodName());
    }



    // TODO: 2018-08-18 검색 후 터치 리스터  
    @Override
    public void onSearchItemClick(View v, int position) {
        if (cartListPosition != 1000) {
            // TODO: 2018-08-18  아이템을 선택하여 클릭했을때 처리하는 부분입니다. - 박제창
            foodCardArrayList.set(cartListPosition, new FoodCard(foodCardArrayList.get(cartListPosition).getCardClass(),
                    searchListV2.get(position).getFoodClass(),
                    searchListV2.get(position).getFoodName(),
                    searchListV2.get(position).getFoodAmount(),
                    searchListV2.get(position).getTotalExchange()));
            adapterV3.notifyDataSetChanged();

            try {
                backgroundArrayList.set(cartListPosition,
                        new FoodCard(foodCardArrayList.get(cartListPosition).getCardClass(),
                                searchListV2.get(position).getFoodClass(),
                                searchListV2.get(position).getFoodName(),
                                searchListV2.get(position).getFoodAmount(),
                                searchListV2.get(position).getFoodGroup1(),
                                searchListV2.get(position).getFoodGroup2(),
                                searchListV2.get(position).getFoodGroup3(),
                                searchListV2.get(position).getFoodGroup4(),
                                searchListV2.get(position).getFoodGroup5(),
                                searchListV2.get(position).getFoodGroup6(),
                                searchListV2.get(position).getTotalExchange(),
                                searchListV2.get(position).getKcal(),
                                searchListV2.get(position).getCarbo(),
                                searchListV2.get(position).getFatt(),
                                searchListV2.get(position).getProt(),
                                searchListV2.get(position).getFiber()));

                backgroundDBArrayList.set(cartListPosition,
                        new com.dreamwalker.diabetesfoodypilot.database.food.FoodCard(
                                foodCardArrayList.get(cartListPosition).getCardClass(),
                                searchListV2.get(position).getFoodClass(),
                                searchListV2.get(position).getFoodName(),
                                searchListV2.get(position).getFoodAmount(),
                                searchListV2.get(position).getFoodGroup1(),
                                searchListV2.get(position).getFoodGroup2(),
                                searchListV2.get(position).getFoodGroup3(),
                                searchListV2.get(position).getFoodGroup4(),
                                searchListV2.get(position).getFoodGroup5(),
                                searchListV2.get(position).getFoodGroup6(),
                                searchListV2.get(position).getTotalExchange(),
                                searchListV2.get(position).getKcal(),
                                searchListV2.get(position).getCarbo(),
                                searchListV2.get(position).getFatt(),
                                searchListV2.get(position).getProt(),
                                searchListV2.get(position).getFiber()));

                mixedFoodArrayList.set(cartListPosition,
                        new MixedFood(searchListV2.get(position).getFoodClass(),
                                searchListV2.get(position).getFoodName(),
                                searchListV2.get(position).getFoodAmount(),
                                searchListV2.get(position).getFoodGroup1(),
                                searchListV2.get(position).getFoodGroup2(),
                                searchListV2.get(position).getFoodGroup3(),
                                searchListV2.get(position).getFoodGroup4(),
                                searchListV2.get(position).getFoodGroup5(),
                                searchListV2.get(position).getFoodGroup6(),
                                searchListV2.get(position).getTotalExchange(),
                                searchListV2.get(position).getKcal(),
                                searchListV2.get(position).getCarbo(),
                                searchListV2.get(position).getFatt(),
                                searchListV2.get(position).getProt(),
                                searchListV2.get(position).getFiber()
                        ));

            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }


            Log.e(TAG, "onSearchItemClick: " + searchListV2.get(position).getFoodName());

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            cartListPosition = 1000;


        } else {
            // TODO: 2018-08-18 그냥 검색버튼을 사용자가 눌렀을 때는 아무 반응 없이 검색만 가능해야 합니다.
            cartListPosition = 1000;
        }
    }


    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        Log.e(TAG, "onDateSet: " + year + monthOfYear + dayOfMonth);

        Calendar cal = new java.util.GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        // TODO: 2018-08-19 사용자가 날짜를 변경하면 변경한 Date 값을 넣어야한다. - 박제창
        nowDate = cal.getTime();
        Log.e(TAG, "onDateSet: " +  DateFormat.getDateFormat(this).format(cal.getTime()));
//        mText.setText("Date set: " + DateFormat.getDateFormat(this).format(cal.getTime()));

    }

    String startIntakeTime; //문자열 변수
    String endIntakeTime; // 문자열 변수
    Date nowDate;
    Date startDate; // Realm 데이터베이스 저장 맴버 변수
    Date endDate; // Realm 데이터베이스 저장 맴버 변수
    long startIntakeTimestamp; //사용자 시간 선택 예외 처리 변수
    long endIntakeTimestamp; // 사용자 시간 선택 예외 처리 변수

    @Override
    public void onTimeSet(ViewGroup viewGroup, int hourOfDay, int minute) {

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss", Locale.KOREA);

        Log.e(TAG, "onTimeSet: " + viewGroup.getTag());
        Log.e(TAG, "onTimeSet:  " + hourOfDay + minute);
        Calendar cal = new java.util.GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        Log.e(TAG, "onTimeSet: time -->" + cal.getTime());
        Log.e(TAG, "onTimeSet: " + DateFormat.getTimeFormat(this).format(cal.getTime()));

        if (startTimeSelectFlag) {
            startTimeTextView.setText(DateFormat.getTimeFormat(this).format(cal.getTime()));
            startIntakeTime = DateFormat.getTimeFormat(this).format(cal.getTime());
            startDate = cal.getTime();
            startIntakeTimestamp = startDate.getTime();
            Log.e(TAG, "startIntakeTimestamp: " + startIntakeTimestamp);
            startTimeSelectFlag = false;

        } else if (endTimeSelectFlag) {
            endTimeTextView.setText(DateFormat.getTimeFormat(this).format(cal.getTime()));
            endIntakeTime = DateFormat.getTimeFormat(this).format(cal.getTime());
            endDate = cal.getTime();
            endIntakeTimestamp = endDate.getTime();
            Log.e(TAG, "endIntakeTimestamp: " + endIntakeTimestamp);
            endTimeSelectFlag = false;
        }

    }
}
