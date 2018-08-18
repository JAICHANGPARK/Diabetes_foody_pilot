package com.dreamwalker.diabetesfoodypilot.model;

import java.util.ArrayList;

public class FoodTotal  {
    private String intakeType;
    ArrayList<FoodCard> foodCardArrayList;

    public FoodTotal() {
    }

    public FoodTotal(String intakeType, ArrayList<FoodCard> foodCardArrayList) {
        this.intakeType = intakeType;
        this.foodCardArrayList = foodCardArrayList;
    }

    public String getIntakeType() {
        return intakeType;
    }

    public void setIntakeType(String intakeType) {
        this.intakeType = intakeType;
    }

    public ArrayList<FoodCard> getFoodCardArrayList() {
        return foodCardArrayList;
    }

    public void setFoodCardArrayList(ArrayList<FoodCard> foodCardArrayList) {
        this.foodCardArrayList = foodCardArrayList;
    }
}
