package com.dreamwalker.diabetesfoodypilot.model;

import android.view.View;

import com.dreamwalker.diabetesfoodypilot.database.food.FoodTotal;

import java.util.Date;

import io.realm.RealmList;

public class HomeFood {

    private RealmList<FoodTotal> foodTotals;  //FoodTotal List
    private Date saveDate;        // 저장한 시간
    private Date userSelectDate;  // 섭취 닐짜
    private Date startIntakeDate; // 섭취 시작 시간
    private Date endIntakeDate;   // 섭취 종료 시간
    private long timestamp;       // Timestamp 저장한

    private View.OnClickListener requestBtnClickListener;

    public HomeFood(RealmList<FoodTotal> foodTotals, Date saveDate, Date userSelectDate, Date startIntakeDate, Date endIntakeDate, long timestamp) {
        this.foodTotals = foodTotals;
        this.saveDate = saveDate;
        this.userSelectDate = userSelectDate;
        this.startIntakeDate = startIntakeDate;
        this.endIntakeDate = endIntakeDate;
        this.timestamp = timestamp;
    }

    public HomeFood(RealmList<FoodTotal> foodTotals, Date saveDate, Date userSelectDate,
                    Date startIntakeDate, Date endIntakeDate, long timestamp, View.OnClickListener requestBtnClickListener) {
        this.foodTotals = foodTotals;
        this.saveDate = saveDate;
        this.userSelectDate = userSelectDate;
        this.startIntakeDate = startIntakeDate;
        this.endIntakeDate = endIntakeDate;
        this.timestamp = timestamp;
        this.requestBtnClickListener = requestBtnClickListener;
    }

    public RealmList<FoodTotal> getFoodTotals() {
        return foodTotals;
    }

    public void setFoodTotals(RealmList<FoodTotal> foodTotals) {
        this.foodTotals = foodTotals;
    }

    public Date getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(Date saveDate) {
        this.saveDate = saveDate;
    }

    public Date getUserSelectDate() {
        return userSelectDate;
    }

    public void setUserSelectDate(Date userSelectDate) {
        this.userSelectDate = userSelectDate;
    }

    public Date getStartIntakeDate() {
        return startIntakeDate;
    }

    public void setStartIntakeDate(Date startIntakeDate) {
        this.startIntakeDate = startIntakeDate;
    }

    public Date getEndIntakeDate() {
        return endIntakeDate;
    }

    public void setEndIntakeDate(Date endIntakeDate) {
        this.endIntakeDate = endIntakeDate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public View.OnClickListener getRequestBtnClickListener() {
        return requestBtnClickListener;
    }

    public void setRequestBtnClickListener(View.OnClickListener requestBtnClickListener) {
        this.requestBtnClickListener = requestBtnClickListener;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }

        if (obj == null || getClass() != obj.getClass()){
            return false;
        }

        HomeFood homeFood = (HomeFood) obj;

        if (timestamp != homeFood.timestamp) return false;

        if (foodTotals != null ? !foodTotals.equals(homeFood.foodTotals) : homeFood.foodTotals != null) {
            return false;
        }

        if (saveDate != null ? !saveDate.equals(homeFood.saveDate) : homeFood.saveDate != null) {
            return false;
        }

        if (userSelectDate != null ? !userSelectDate.equals(homeFood.userSelectDate) : homeFood.userSelectDate != null) {
            return false;
        }
        if (startIntakeDate != null ? !startIntakeDate.equals(homeFood.startIntakeDate) : homeFood.startIntakeDate != null) {
            return false;
        }
//        if (endIntakeDate != null ? !endIntakeDate.equals(homeFood.endIntakeDate) : homeFood.endIntakeDate != null) {
//            return false;
//        }

        return !(endIntakeDate != null ? !endIntakeDate.equals(homeFood.endIntakeDate) : homeFood.endIntakeDate != null);
//        return super.equals(obj);
    }

    /**
     *  private Date saveDate;        // 저장한 시간
     private Date userSelectDate;  // 섭취 닐짜
     private Date startIntakeDate; // 섭취 시작 시간
     private Date endIntakeDate;   // 섭취 종료 시간
     private long timestamp;       // Timestamp 저장한
     * @return
     */
    @Override
    public int hashCode() {
        int result = (foodTotals != null) ? foodTotals.hashCode() : 0;
        result = 31 *  result + ( (saveDate != null) ? saveDate.hashCode() : 0 );
        result = 31 *  result + ( (userSelectDate != null) ? userSelectDate.hashCode() : 0 );
        result = 31 *  result + ( (startIntakeDate != null) ? startIntakeDate.hashCode() : 0 );
        result = 31 *  result + ( (endIntakeDate != null) ? endIntakeDate.hashCode() : 0 );
        result = 31 *  result + (int)timestamp;
        return result;
    }
}
