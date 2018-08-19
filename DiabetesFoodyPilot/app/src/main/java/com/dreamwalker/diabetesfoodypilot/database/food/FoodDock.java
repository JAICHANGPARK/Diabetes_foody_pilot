package com.dreamwalker.diabetesfoodypilot.database.food;


import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;

public class FoodDock extends RealmObject implements RealmModel {
    public RealmList<FoodTotal> foodTotals;
    private Date saveDate;
    private long timestamp;
    private Date startIntakeDate;
    private Date endIntakeDate;

    public FoodDock() {

    }

    public FoodDock(RealmList<FoodTotal> main, Date saveDate, long timestamp) {
        this.foodTotals = main;
        this.saveDate = saveDate;
        this.timestamp = timestamp;
    }

    public FoodDock(RealmList<FoodTotal> foodTotals, Date saveDate, long timestamp, Date startIntakeDate, Date endIntakeDate) {
        this.foodTotals = foodTotals;
        this.saveDate = saveDate;
        this.timestamp = timestamp;
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


