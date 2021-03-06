package com.dreamwalker.diabetesfoodypilot.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class HomeTestAdapter extends ArrayAdapter<HomeFood> {
    private static final String TAG = "HomeTestAdapter";
    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;

    public HomeTestAdapter(Context context, List<HomeFood> objects) {
        super(context, 0, objects);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // get item for selected view
        HomeFood item = getItem(position);
        // if cell is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.cell, parent, false);
            // binding view parts to view holder
            viewHolder.price = cell.findViewById(R.id.title_price);
            viewHolder.time = cell.findViewById(R.id.title_time_label);
            viewHolder.date = cell.findViewById(R.id.title_date_label);
            viewHolder.fromAddress = cell.findViewById(R.id.title_from_address);
            viewHolder.toAddress = cell.findViewById(R.id.title_to_address);
            viewHolder.requestsCount = cell.findViewById(R.id.title_requests_count);
            viewHolder.pledgePrice = cell.findViewById(R.id.title_pledge);
            viewHolder.weight = cell.findViewById(R.id.title_weight);

            viewHolder.headerIntakeTextView = cell.findViewById(R.id.cell_content_top_intake_type);
            viewHolder.headerKcalTextView = cell.findViewById(R.id.head_kcal_text_view);
            viewHolder.headerAmountTextView = cell.findViewById(R.id.head_amount_text_view);
            viewHolder.headerExchangeTextView = cell.findViewById(R.id.head_exchange_text_view);
            viewHolder.intakeLinearLayout = cell.findViewById(R.id.linear_layout_intake_food);
            viewHolder.exchangeLinearLayout = cell.findViewById(R.id.linear_layout_exchange);

            viewHolder.contentRequestBtn = cell.findViewById(R.id.content_request_btn);
            cell.setTag(viewHolder);

        } else {
            // for existing cell set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }

        if (null == item) {
            return cell;
        }

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
            myExchangeTextViews[i]  = new TextView(parent.getContext());
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
        TextView midHeadTextView = new TextView(parent.getContext()); // mid Top Header
        midHeadTextView.setTextColor(parent.getContext().getResources().getColor(R.color.black));
        midHeadTextView.setTextSize(18.0f);
        String midHeadText = "섭취 음식 수 : " + N + " 개";
        midHeadTextView.setText(midHeadText);
        viewHolder.intakeLinearLayout.addView(midHeadTextView);

        
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

            TextView rowTextView = new TextView(parent.getContext());

            rowTextView.setTextSize(16.0f);
//            rowTextView.setTextColor(parent.getContext().getResources().getColor(R.color.black));

            String setDynamicText = ""+ k + "--> "
                    + foodCards.get(k).getCardClass() + " : "
                    + foodCards.get(k).getFoodClass() + " : "
                    + foodCards.get(k).getFoodName();

            rowTextView.setText(setDynamicText);
            viewHolder.intakeLinearLayout.addView(rowTextView);
            myTextViews[k] = rowTextView;
        }
        Log.e(TAG, "getView: " + kCal + ", " + amount + ", " + exchange_value);
        Log.e(TAG, "getView: Date " + DateFormat.getDateFormat(parent.getContext()).format(item.getUserSelectDate()));
        Log.e(TAG, "getView: time " + DateFormat.getTimeFormat(parent.getContext()).format(item.getSaveDate()));


        // bind data from selected element to view through view holder
        viewHolder.price.setText(intakeType);
//        viewHolder.time.setText("Temp");
        viewHolder.date.setText(DateFormat.getDateFormat(parent.getContext()).format(item.getUserSelectDate()));
        viewHolder.time.setTimeStamp(item.getUserSelectDate().getTime() / 1000);
        viewHolder.fromAddress.setText(DateFormat.getTimeFormat(parent.getContext()).format(item.getStartIntakeDate()));
        viewHolder.toAddress.setText(DateFormat.getTimeFormat(parent.getContext()).format(item.getEndIntakeDate()));
        viewHolder.requestsCount.setText("" + kCal);
        viewHolder.pledgePrice.setText("" + amount);
        viewHolder.weight.setText("" + exchange_value);

        viewHolder.headerIntakeTextView.setText(intakeType);
        viewHolder.headerKcalTextView.setText(String.valueOf(kCal));
        viewHolder.headerAmountTextView.setText(String.valueOf(amount));
        viewHolder.headerExchangeTextView.setText(String.valueOf(exchange_value));


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

        TextView bottomHeadTextVeiw = new TextView(parent.getContext());
        bottomHeadTextVeiw.setTextSize(18.0f);
        bottomHeadTextVeiw.setTextColor(parent.getContext().getResources().getColor(R.color.black));
        String bottomHeadText = "교환단위 합 : " + exchange_value + " 단위";
        bottomHeadTextVeiw.setText(bottomHeadText);

        viewHolder.exchangeLinearLayout.addView(bottomHeadTextVeiw);


        for (int i = 0; i < 6; i++){
            viewHolder.exchangeLinearLayout.addView(myExchangeTextViews[i]);
        }

//        Glide.with(parent.getContext()).load("https://cdn.pixabay.com/photo/2015/09/02/13/10/coffee-918926_960_720.jpg").into(viewHolder.headerImageView);
        // set custom btn handler for list item from that item
        if (item.getRequestBtnClickListener() != null) {
            viewHolder.contentRequestBtn.setOnClickListener(item.getRequestBtnClickListener());
        } else {
            // (optionally) add "default" handler if no handler found in item
            viewHolder.contentRequestBtn.setOnClickListener(defaultRequestBtnClickListener);
        }

        return cell;
    }

    // simple methods for register cell state changes
    public void registerToggle(int position) {
        if (unfoldedIndexes.contains(position))
            registerFold(position);
        else
            registerUnfold(position);
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
    }


    public View.OnClickListener getDefaultRequestBtnClickListener() {
        return defaultRequestBtnClickListener;
    }

    public void setDefaultRequestBtnClickListener(View.OnClickListener defaultRequestBtnClickListener) {
        this.defaultRequestBtnClickListener = defaultRequestBtnClickListener;
    }

    // View lookup cache
    private static class ViewHolder {
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
    }
}
