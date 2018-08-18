package com.dreamwalker.diabetesfoodypilot.model;

import java.util.ArrayList;

public class Main {
    private String intakeType;
    private ArrayList<MixedFood> foodCardArrayList;

    public Main(String intakeType, ArrayList<MixedFood> foodCardArrayList) {
        this.intakeType = intakeType;
        this.foodCardArrayList = foodCardArrayList;
    }

    public String getIntakeType() {
        return intakeType;
    }

    public void setIntakeType(String intakeType) {
        this.intakeType = intakeType;
    }

    public ArrayList<MixedFood> getFoodCardArrayList() {
        return foodCardArrayList;
    }

    public void setFoodCardArrayList(ArrayList<MixedFood> foodCardArrayList) {
        this.foodCardArrayList = foodCardArrayList;
    }
}
