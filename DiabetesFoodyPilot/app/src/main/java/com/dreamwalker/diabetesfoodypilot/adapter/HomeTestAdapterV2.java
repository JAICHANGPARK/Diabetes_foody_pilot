package com.dreamwalker.diabetesfoodypilot.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dreamwalker.diabetesfoodypilot.R;
import com.dreamwalker.diabetesfoodypilot.database.food.FoodCard;
import com.dreamwalker.diabetesfoodypilot.database.food.FoodTotal;
import com.dreamwalker.diabetesfoodypilot.model.HomeFood;
import com.dreamwalker.diabetesfoodypilot.utils.timeago.ZamanTextView;
import com.dreamwalker.flipcard.FoldingCell;

import java.util.HashSet;
import java.util.List;

import io.realm.RealmList;

public class HomeTestAdapterV2 extends RecyclerView.Adapter<HomeTestAdapterV2.HomeTestViewHolder> {

    private static final String TAG = "HomeTestAdapterV2";

    Context context;
    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;
    List<HomeFood> objects;

    public HomeTestAdapterV2(Context context, List<HomeFood> objects) {
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public HomeTestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell, viewGroup, false);
        return new HomeTestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeTestViewHolder homeTestViewHolder, int position) {
        HomeFood item = objects.get(position);

        RealmList<FoodTotal> foodTotals = item.getFoodTotals();
        String intakeType = foodTotals.get(0).getIntakeType();
        RealmList<FoodCard> foodCards = foodTotals.get(0).getFoodCardArrayList();

        int N = foodCards.size();

        // TODO: 2018-08-20 동적 텍스트뷰 생성 - 박제창
        TextView[] myTextViews = new TextView[N]; // create an empty array;
        TextView[] myExchangeTextViews = new TextView[6]; // create an empty array;
        // TODO: 2018-08-20 java.lang.NullPointerException: Attempt to invoke virtual method
        // TODO: 'void android.widget.TextView.setText(java.lang.CharSequence)' on a null object referenc
        // TODO: 2018-08-20  NullPointerException 방지 - 박제창
        for (int i = 0; i < 6; i++){
            myExchangeTextViews[i]  = new TextView(context);
            myExchangeTextViews[i].setTextSize(16.0f);
//            myExchangeTextViews[i].setTextColor(parent.getContext().getResources().getColor(R.color.black));
        }


        int kCal = 0; // 총 칼로리 변수
        int amount = 0; // 총 중량 변수
        float exchange_value = 0.0F; // 총 교환단위 변수

        float group1 = 0.0F; // 곡류군
        float group2 = 0.0F; // 어육류
        float group3 = 0.0F; // 채소류
        float group4 = 0.0F; // 과일류
        float group5 = 0.0F; // 우유군
        float group6 = 0.0F; // 지방군

        // TODO: 2018-08-20 중간 음식 해드 텍스트뷰 추가
        TextView midHeadTextView = new TextView(context); // mid Top Header
        midHeadTextView.setTextColor(context.getResources().getColor(R.color.black));
        midHeadTextView.setTextSize(18.0f);
        String midHeadText = "섭취 음식 수 : " + N + " 개";
        midHeadTextView.setText(midHeadText);
        homeTestViewHolder.intakeLinearLayout.addView(midHeadTextView);


        for (int k = 0; k < foodCards.size(); k++) {
            kCal += Integer.valueOf(foodCards.get(k).getKcal());
            amount += Integer.valueOf(foodCards.get(k).getFoodAmount());
            exchange_value += Float.valueOf(foodCards.get(k).getTotalExchange());

            // TODO: 2018-08-20 교환단위 연산 - 박제창
            group1 += Float.valueOf(foodCards.get(k).getFoodGroup1());
            group2 += Float.valueOf(foodCards.get(k).getFoodGroup2());
            group3 += Float.valueOf(foodCards.get(k).getFoodGroup3());
            group4 += Float.valueOf(foodCards.get(k).getFoodGroup4());
            group5 += Float.valueOf(foodCards.get(k).getFoodGroup5());
            group6 += Float.valueOf(foodCards.get(k).getFoodGroup6());

            TextView rowTextView = new TextView(context);

            rowTextView.setTextSize(16.0f);
//            rowTextView.setTextColor(parent.getContext().getResources().getColor(R.color.black));

            String setDynamicText = ""+ k + "--> "
                    + foodCards.get(k).getCardClass() + " : "
                    + foodCards.get(k).getFoodClass() + " : "
                    + foodCards.get(k).getFoodName();

            rowTextView.setText(setDynamicText);
            homeTestViewHolder.intakeLinearLayout.addView(rowTextView);
            myTextViews[k] = rowTextView;
        }
        Log.e(TAG, "getView: " + kCal + ", " + amount + ", " + exchange_value);
        Log.e(TAG, "getView: Date " + DateFormat.getDateFormat(context).format(item.getUserSelectDate()));
        Log.e(TAG, "getView: time " + DateFormat.getTimeFormat(context).format(item.getSaveDate()));


        // bind data from selected element to view through view holder
        homeTestViewHolder.price.setText(intakeType);
//        viewHolder.time.setText("Temp");
        homeTestViewHolder.date.setText(DateFormat.getDateFormat(context).format(item.getUserSelectDate()));
        homeTestViewHolder.time.setTimeStamp(item.getUserSelectDate().getTime() / 1000);
        homeTestViewHolder.fromAddress.setText(DateFormat.getTimeFormat(context).format(item.getStartIntakeDate()));
        homeTestViewHolder.toAddress.setText(DateFormat.getTimeFormat(context).format(item.getEndIntakeDate()));
        homeTestViewHolder.requestsCount.setText("" + kCal);
        homeTestViewHolder.pledgePrice.setText("" + amount);
        homeTestViewHolder.weight.setText("" + exchange_value);

        homeTestViewHolder.headerIntakeTextView.setText(intakeType);
        homeTestViewHolder.headerKcalTextView.setText(String.valueOf(kCal));
        homeTestViewHolder.headerAmountTextView.setText(String.valueOf(amount));
        homeTestViewHolder.headerExchangeTextView.setText(String.valueOf(exchange_value));


        // TODO: 2018-08-20 교환단위 합계 텍스트 설정  -- 박제창
        String sGroup1 = "곡류군 : " + group1 + " 단위";
        String sGroup2 = "어육류군 : " + group2 + " 단위";
        String sGroup3 = "채소군 : " + group3 + " 단위";
        String sGroup4 = "과일군 : " + group4 + " 단위";
        String sGroup5 = "우유군 : " + group5 + " 단위";
        String sGroup6 = "지방군 : " + group6 + " 단위";

        myExchangeTextViews[0].setText(sGroup1);
        myExchangeTextViews[1].setText(sGroup2);
        myExchangeTextViews[2].setText(sGroup3);
        myExchangeTextViews[3].setText(sGroup4);
        myExchangeTextViews[4].setText(sGroup5);
        myExchangeTextViews[5].setText(sGroup6);

        TextView bottomHeadTextVeiw = new TextView(context);
        bottomHeadTextVeiw.setTextSize(18.0f);
        bottomHeadTextVeiw.setTextColor(context.getResources().getColor(R.color.black));
        String bottomHeadText = "교환단위 합 : " + exchange_value + " 단위";
        bottomHeadTextVeiw.setText(bottomHeadText);

        homeTestViewHolder.exchangeLinearLayout.addView(bottomHeadTextVeiw);


        for (int i = 0; i < 6; i++){
            homeTestViewHolder.exchangeLinearLayout.addView(myExchangeTextViews[i]);
        }

//        Glide.with(parent.getContext()).load("https://cdn.pixabay.com/photo/2015/09/02/13/10/coffee-918926_960_720.jpg").into(viewHolder.headerImageView);
        // set custom btn handler for list item from that item
        if (item.getRequestBtnClickListener() != null) {
            homeTestViewHolder.contentRequestBtn.setOnClickListener(item.getRequestBtnClickListener());
        } else {
            // (optionally) add "default" handler if no handler found in item
            homeTestViewHolder.contentRequestBtn.setOnClickListener(defaultRequestBtnClickListener);
        }

        homeTestViewHolder.foldingCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeTestViewHolder.foldingCell.toggle(false);
            }
        });

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    class HomeTestViewHolder extends RecyclerView.ViewHolder {

        FoldingCell foldingCell;

        TextView price;
        MaterialButton contentRequestBtn;
        TextView pledgePrice;
        TextView fromAddress;
        TextView toAddress;
        TextView requestsCount;
        TextView date;
        //        TextView time;
        ZamanTextView time;
        TextView weight;

        // TODO: 2018-08-20 cell content view of parts - 박제창
        ImageView headerImageView;
        TextView headerIntakeTextView;
        TextView headerKcalTextView;
        TextView headerAmountTextView;
        TextView headerExchangeTextView;
        LinearLayout intakeLinearLayout;
        LinearLayout exchangeLinearLayout;

        public HomeTestViewHolder(@NonNull View itemView) {
            super(itemView);

            foldingCell = itemView.findViewById(R.id.folding_cell);
            // binding view parts to view holder
            price = itemView.findViewById(R.id.title_price);
            time = itemView.findViewById(R.id.title_time_label);
            date = itemView.findViewById(R.id.title_date_label);
            fromAddress = itemView.findViewById(R.id.title_from_address);
            toAddress = itemView.findViewById(R.id.title_to_address);
            requestsCount = itemView.findViewById(R.id.title_requests_count);
            pledgePrice = itemView.findViewById(R.id.title_pledge);
            weight = itemView.findViewById(R.id.title_weight);

            headerIntakeTextView = itemView.findViewById(R.id.cell_content_top_intake_type);
            headerKcalTextView = itemView.findViewById(R.id.head_kcal_text_view);
            headerAmountTextView = itemView.findViewById(R.id.head_amount_text_view);
            headerExchangeTextView = itemView.findViewById(R.id.head_exchange_text_view);
            intakeLinearLayout = itemView.findViewById(R.id.linear_layout_intake_food);
            exchangeLinearLayout = itemView.findViewById(R.id.linear_layout_exchange);

            contentRequestBtn = itemView.findViewById(R.id.content_request_btn);


        }

    }
}
