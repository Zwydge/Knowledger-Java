package com.example.knowledger.entities;

public class Question {

    int id;
    String content;
    int user_id;
    String category;

    public Question(int id, String content, int user_id, String category) {
        this.id = id;
        this.content = content;
        this.user_id = user_id;
        this.category = category;
    }

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUserId() { return user_id; }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public String getCatName() { return category; }

    public void setCatName(String category) {
        this.category = category;
    }

}