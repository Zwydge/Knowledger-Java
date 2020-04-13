package com.example.knowledger.entities;

public class Category {

    int id;
    String member;
    String name;

    public Category(int id, String name, String member) {
        this.id = id;
        this.name = name;
        this.member = member;
    }

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public String getMember() { return member; }

    public void setMember(String member) {
        this.member = member;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
