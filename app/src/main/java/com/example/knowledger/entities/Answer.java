package com.example.knowledger.entities;

public class Answer {

    int id;
    String content;
    int user_id;
    int question_id;
    int vote;
    int like;
    int dislike;

    public Answer(int id, String content, int user_id, int question_id, int vote, int like, int dislike) {
        this.id = id;
        this.content = content;
        this.user_id = user_id;
        this.question_id = question_id;
        this.vote = vote;
        this.like = like;
        this.dislike = dislike;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getDislike() {
        return dislike;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }
}
