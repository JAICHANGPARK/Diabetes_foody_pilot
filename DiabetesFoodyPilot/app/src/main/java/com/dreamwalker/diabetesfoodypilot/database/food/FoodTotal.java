package com.dreamwalker.diabetesfoodypilot.database.food;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;

public class FoodTotal extends RealmObject implements RealmModel {
    private String intakeType;  // 식사 섭취 유형
    private RealmList<FoodCard> foodCardArrayList; // 섭취한 음식 리스트

    public FoodTotal() {
    }

    public FoodTotal(String intakeType, RealmList<FoodCard> foodCardArrayList) {
        this.intakeType = intakeType;
        this.foodCardArrayList = foodCardArrayList;
    }

    public String getIntakeType() {
        return intakeType;
    }

    public void setIntakeType(String intakeType) {
        this.intakeType = intakeType;
    }

    public RealmList<FoodCard> getFoodCardArrayList() {
        return foodCardArrayList;
    }

    public void setFoodCardArrayList(RealmList<FoodCard> foodCardArrayList) {
        this.foodCardArrayList = foodCardArrayList;
    }
}
