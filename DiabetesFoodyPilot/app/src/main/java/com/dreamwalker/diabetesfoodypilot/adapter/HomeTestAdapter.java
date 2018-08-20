package com.dreamwalker.diabetesfoodypilot.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dreamwalker.diabetesfoodypilot.R;
import com.dreamwalker.diabetesfoodypilot.database.food.FoodCard;
import com.dreamwalker.diabetesfoodypilot.database.food.FoodTotal;
import com.dreamwalker.diabetesfoodypilot.model.HomeFood;
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

            //viewHolder.contentRequestBtn = cell.findViewById(R.id.content_request_btn);
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

        if (null == item){
            return cell;
        }


        RealmList<FoodTotal> foodTotals = item.getFoodTotals();
        String intakeType = foodTotals.get(0).getIntakeType();
        RealmList<FoodCard> foodCards = foodTotals.get(0).getFoodCardArrayList();
        int kCal = 0;
        int amount = 0;
        float exchange_value = 0.0F;
        for (int k = 0; k < foodCards.size(); k++){
            kCal += Integer.valueOf(foodCards.get(k).getKcal());
            amount += Integer.valueOf(foodCards.get(k).getFoodAmount());
            exchange_value += Float.valueOf(foodCards.get(k).getTotalExchange());
        }
        Log.e(TAG, "getView: " + kCal + ", " + amount + ", " + exchange_value);



        // bind data from selected element to view through view holder
        viewHolder.price.setText(intakeType);
        viewHolder.time.setText("Temp");
        viewHolder.date.setText("Test");
        viewHolder.fromAddress.setText(item.getSaveDate().toString());
        viewHolder.toAddress.setText(item.getEndIntakeDate().toString());
        viewHolder.requestsCount.setText(""+kCal);
        viewHolder.pledgePrice.setText(""+amount);
        viewHolder.weight.setText(""+exchange_value);

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
        TextView contentRequestBtn;
        TextView pledgePrice;
        TextView fromAddress;
        TextView toAddress;
        TextView requestsCount;
        TextView date;
        TextView time;
        TextView weight;
    }
}
