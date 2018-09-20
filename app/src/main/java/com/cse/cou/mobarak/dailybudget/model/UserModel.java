package com.cse.cou.mobarak.dailybudget.model;

/**
 * Created by mobarak on 7/28/2018.
 */

public class UserModel {


    String name;
    String email;
    String gender;
    String cost;
    String currency;
    public UserModel(String name, String email, String cost, String gender, String currency) {
        this.name = name;
        this.email = email;
        this.cost = cost;
        this.gender = gender;
        this.currency=currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
