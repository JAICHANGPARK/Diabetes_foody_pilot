package com.dreamwalker.diabetesfoodypilot.database.food;

import io.realm.RealmObject;

public class FoodItem extends RealmObject {

    String foodNumber;
    String foodGroup;
    String foodName;
    String foodAmount;
    String foodKcal;
    String foodCarbo;
    String foodProtein;
    String foodFat;
    String foodSugar;
    String foodNatrium;
    String foodCholest;
    String foodFatty;
    String foodTransFatty;

    public FoodItem() {
    }

    public FoodItem(String foodNumber, String foodGroup, String foodName, String foodAmount, String foodKcal, String foodCarbo,
                    String foodProtein, String foodFat, String foodSugar, String foodNatrium, String foodCholest,
                    String foodFatty, String foodTransFatty) {
        this.foodNumber = foodNumber;
        this.foodGroup = foodGroup;
        this.foodName = foodName;
        this.foodAmount = foodAmount;
        this.foodKcal = foodKcal;
        this.foodCarbo = foodCarbo;
        this.foodProtein = foodProtein;
        this.foodFat = foodFat;
        this.foodSugar = foodSugar;
        this.foodNatrium = foodNatrium;
        this.foodCholest = foodCholest;
        this.foodFatty = foodFatty;
        this.foodTransFatty = foodTransFatty;
    }

    public String getFoodNumber() {
        return foodNumber;
    }

    public void setFoodNumber(String foodNumber) {
        this.foodNumber = foodNumber;
    }

    public String getFoodGroup() {
        return foodGroup;
    }

    public void setFoodGroup(String foodGroup) {
        this.foodGroup = foodGroup;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodAmount() {
        return foodAmount;
    }

    public void setFoodAmount(String foodAmount) {
        this.foodAmount = foodAmount;
    }

    public String getFoodKcal() {
        return foodKcal;
    }

    public void setFoodKcal(String foodKcal) {
        this.foodKcal = foodKcal;
    }

    public String getFoodCarbo() {
        return foodCarbo;
    }

    public void setFoodCarbo(String foodCarbo) {
        this.foodCarbo = foodCarbo;
    }

    public String getFoodProtein() {
        return foodProtein;
    }

    public void setFoodProtein(String foodProtein) {
        this.foodProtein = foodProtein;
    }

    public String getFoodFat() {
        return foodFat;
    }

    public void setFoodFat(String foodFat) {
        this.foodFat = foodFat;
    }

    public String getFoodSugar() {
        return foodSugar;
    }

    public void setFoodSugar(String foodSugar) {
        this.foodSugar = foodSugar;
    }

    public String getFoodNatrium() {
        return foodNatrium;
    }

    public void setFoodNatrium(String foodNatrium) {
        this.foodNatrium = foodNatrium;
    }

    public String getFoodCholest() {
        return foodCholest;
    }

    public void setFoodCholest(String foodCholest) {
        this.foodCholest = foodCholest;
    }

    public String getFoodFatty() {
        return foodFatty;
    }

    public void setFoodFatty(String foodFatty) {
        this.foodFatty = foodFatty;
    }

    public String getFoodTransFatty() {
        return foodTransFatty;
    }

    public void setFoodTransFatty(String foodTransFatty) {
        this.foodTransFatty = foodTransFatty;
    }
}
