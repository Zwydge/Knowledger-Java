package com.example.knowledger.entities;

public class Reputation {

    private int id;
    private int value;
    private int user_id;
    private String category;

    public Reputation(int id, int value, int user_id, String category) {
        this.id = id;
        this.value = value;
        this.user_id = user_id;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}