package com.dreamwalker.diabetesfoodypilot.activity;

import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
import com.dreamwalker.diabetesfoodypilot.database.food.FoodItem;
import com.dreamwalker.diabetesfoodypilot.database.food.MixedFoodItem;
import com.dreamwalker.diabetesfoodypilot.model.Food;
import com.dreamwalker.diabetesfoodypilot.model.FoodCard;
import com.dreamwalker.diabetesfoodypilot.model.MixedFood;
import com.dreamwalker.diabetesfoodypilot.model.Suggestions;
import com.dreamwalker.diabetesfoodypilot.model.TestModel;
import com.dreamwalker.diabetesfoodypilot.remote.Common;
import com.dreamwalker.diabetesfoodypilot.remote.IMenuRequest;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnItemClickListrner,
        RecyclerItemTouchHelperV3.RecyclerItemTouchHelperListener,
        OnSearchItemClickListener {

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

    @BindView(R.id.add)
    ImageView addItemButton;

    @BindView(R.id.save)
    ImageView saveItemButton;

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

    Realm realm;

    CartListAdapterV2 adapterV2;
    CartListAdapterV3 adapterV3;

    ArrayList<Food> foodArrayList = new ArrayList<>();
    ArrayList<Integer> imageList = new ArrayList<>();

    ArrayList<FoodCard> foodCardArrayList = new ArrayList<>();

    ArrayList<MixedFood> mixedFoodArrayList = new ArrayList<>();
    RealmResults<MixedFoodItem> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 2018-08-15 액티비티 풀스크린 전환 - 박제창
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        bindViews();
        setBottomSheetBehavior();

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
        realm = Realm.getDefaultInstance();

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
                foodCardArrayList.add(new FoodCard("밥", "없음", "식품 터치 검색", "없음", "없음"));
                foodCardArrayList.add(new FoodCard("국", "없음", "식품 터치 검색", "없음", "없음"));
                foodCardArrayList.add(new FoodCard("찬", "없음", "식품 터치 검색", "없음", "없음"));
                foodCardArrayList.add(new FoodCard("찬", "없음", "식품 터치 검색", "없음", "없음"));
                foodCardArrayList.add(new FoodCard("찬", "없음", "식품 터치 검색", "없음", "없음"));
                foodCardArrayList.add(new FoodCard("찬", "없음", "식품 터치 검색", "없음", "없음"));
        }
    }

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
                adapterV3.addItem(new FoodCard(selectedItem[0], "없음", "식품 터치 검색", "없음", "없음"));
                adapterV3.notifyDataSetChanged();
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


    @OnClick(R.id.save)
    public void onClickedSaveButton() {

        // TODO: 2018-08-16 다이얼 로그를 보여주고 유형 선택하기
        String[] listItems = new String[]{"아침", "점심", "저녁", "간식"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an Items");
        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e(TAG, "onClick: " + i);

            }
        });
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
            foodCardArrayList.set(cartListPosition, new FoodCard(foodCardArrayList.get(cartListPosition).getCardClass(),
                    searchListV2.get(position).getFoodClass(),
                    searchListV2.get(position).getFoodName(),
                    searchListV2.get(position).getFoodAmount(),
                    searchListV2.get(position).getTotalExchange()));
            adapterV3.notifyDataSetChanged();

            Log.e(TAG, "onSearchItemClick: " + searchListV2.get(position).getFoodName());

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            cartListPosition = 1000;
        } else {
            cartListPosition = 1000;
        }

    }
}
