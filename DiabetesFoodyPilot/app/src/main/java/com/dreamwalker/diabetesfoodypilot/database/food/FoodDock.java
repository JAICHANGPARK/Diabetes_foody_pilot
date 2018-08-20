package com.dreamwalker.diabetesfoodypilot.database.food;


import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;

public class FoodDock extends RealmObject implements RealmModel {
    private RealmList<FoodTotal> foodTotals;  //FoodTotal List
    private Date saveDate;        // 저장한 시간
    private Date userSelectDate;  // 섭취 닐짜
    private Date startIntakeDate; // 섭취 시작 시간
    private Date endIntakeDate;   // 섭취 종료 시간
    private long timestamp;       // Timestamp 저장한

    public FoodDock() {

    }

    public FoodDock(RealmList<FoodTotal> foodTotals, Date saveDate, long timestamp, Date userSelectDate, Date startIntakeDate, Date endIntakeDate) {
        this.foodTotals = foodTotals;
        this.saveDate = saveDate;
        this.timestamp = timestamp;
        this.userSelectDate = userSelectDate;
        this.startIntakeDate = startIntakeDate;
        this.endIntakeDate = endIntakeDate;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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
}


