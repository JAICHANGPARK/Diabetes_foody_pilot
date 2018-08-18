package com.dreamwalker.diabetesfoodypilot.database.food;


import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;

public class FoodDock extends RealmObject implements RealmModel {
    public RealmList<FoodTotal> foodTotals;
    private Date saveDate;
    private long timestamp;

    public FoodDock() {

    }

    public FoodDock(RealmList<FoodTotal> main, Date saveDate, long timestamp) {
        this.foodTotals = main;
        this.saveDate = saveDate;
        this.timestamp = timestamp;
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
}


