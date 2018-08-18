package com.dreamwalker.diabetesfoodypilot.model;

public class FoodCard {

    private String intakeType;
    private String cardClass;
    private String foodClass;
    private String foodName;
    private String foodAmount;
    private String foodGroup1;
    private String foodGroup2;
    private String foodGroup3;
    private String foodGroup4;
    private String foodGroup5;
    private String foodGroup6;
    private String totalExchange;
    private String kcal;
    private String carbo;
    private String fatt;
    private String prot;
    private String fiber;

    public FoodCard() {
    }

    public FoodCard(String cardClass, String foodName, String foodAmount, String totalExchange) {
        this.cardClass = cardClass;
        this.foodName = foodName;
        this.foodAmount = foodAmount;
        this.totalExchange = totalExchange;
    }

    public FoodCard(String cardClass, String foodClass, String foodName, String foodAmount, String totalExchange) {
        this.cardClass = cardClass;
        this.foodClass = foodClass;
        this.foodName = foodName;
        this.foodAmount = foodAmount;
        this.totalExchange = totalExchange;
    }

    public FoodCard(String cardClass, String foodClass, String foodName, String foodAmount,
                    String foodGroup1, String foodGroup2, String foodGroup3, String foodGroup4, String foodGroup5, String foodGroup6,
                    String totalExchange, String kcal, String carbo, String fatt, String prot, String fiber) {
        this.cardClass = cardClass;
        this.foodClass = foodClass;
        this.foodName = foodName;
        this.foodAmount = foodAmount;
        this.foodGroup1 = foodGroup1;
        this.foodGroup2 = foodGroup2;
        this.foodGroup3 = foodGroup3;
        this.foodGroup4 = foodGroup4;
        this.foodGroup5 = foodGroup5;
        this.foodGroup6 = foodGroup6;
        this.totalExchange = totalExchange;
        this.kcal = kcal;
        this.carbo = carbo;
        this.fatt = fatt;
        this.prot = prot;
        this.fiber = fiber;
    }

    public String getIntakeType() {
        return intakeType;
    }

    public void setIntakeType(String intakeType) {
        this.intakeType = intakeType;
    }

    public String getCardClass() {
        return cardClass;
    }

    public void setCardClass(String cardClass) {
        this.cardClass = cardClass;
    }

    public String getFoodClass() {
        return foodClass;
    }

    public void setFoodClass(String foodClass) {
        this.foodClass = foodClass;
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

    public String getFoodGroup1() {
        return foodGroup1;
    }

    public void setFoodGroup1(String foodGroup1) {
        this.foodGroup1 = foodGroup1;
    }

    public String getFoodGroup2() {
        return foodGroup2;
    }

    public void setFoodGroup2(String foodGroup2) {
        this.foodGroup2 = foodGroup2;
    }

    public String getFoodGroup3() {
        return foodGroup3;
    }

    public void setFoodGroup3(String foodGroup3) {
        this.foodGroup3 = foodGroup3;
    }

    public String getFoodGroup4() {
        return foodGroup4;
    }

    public void setFoodGroup4(String foodGroup4) {
        this.foodGroup4 = foodGroup4;
    }

    public String getFoodGroup5() {
        return foodGroup5;
    }

    public void setFoodGroup5(String foodGroup5) {
        this.foodGroup5 = foodGroup5;
    }

    public String getFoodGroup6() {
        return foodGroup6;
    }

    public void setFoodGroup6(String foodGroup6) {
        this.foodGroup6 = foodGroup6;
    }

    public String getTotalExchange() {
        return totalExchange;
    }

    public void setTotalExchange(String totalExchange) {
        this.totalExchange = totalExchange;
    }

    public String getKcal() {
        return kcal;
    }

    public void setKcal(String kcal) {
        this.kcal = kcal;
    }

    public String getCarbo() {
        return carbo;
    }

    public void setCarbo(String carbo) {
        this.carbo = carbo;
    }

    public String getFatt() {
        return fatt;
    }

    public void setFatt(String fatt) {
        this.fatt = fatt;
    }

    public String getProt() {
        return prot;
    }

    public void setProt(String prot) {
        this.prot = prot;
    }

    public String getFiber() {
        return fiber;
    }

    public void setFiber(String fiber) {
        this.fiber = fiber;
    }
}
