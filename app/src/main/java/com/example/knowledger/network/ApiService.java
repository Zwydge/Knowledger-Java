package com.example.knowledger.network;

import android.net.Uri;

import com.example.knowledger.entities.AccessToken;
import com.example.knowledger.entities.AnswerResponse;
import com.example.knowledger.entities.CategoryResponse;
import com.example.knowledger.entities.QuestionResponse;
import com.example.knowledger.entities.ReputationResponse;
import com.example.knowledger.entities.UserResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("register")
    @FormUrlEncoded
    Call<AccessToken> register(@Field("name") String name, @Field("email") String email, @Field("password") String password);

    @POST("login")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("username") String username, @Field("password") String password);

    @POST("refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);

    @POST("category/allmember")
    Call<CategoryResponse> category();

    @GET("user/get_quest")
    Call<QuestionResponse> get_quest();

    @POST("category/userrep")
    @FormUrlEncoded
    Call<ReputationResponse> user_reputation_get(@Field("user_id") int user_id);

    @POST("answer/get")
    @FormUrlEncoded
    Call<AnswerResponse> answer_get(@Field("question_id") int question_id);

    @POST("category/get")
    Call<CategoryResponse> category_get();

    @POST("question/ask")
    @FormUrlEncoded
    Call<QuestionResponse> askQuestion(@Field("content") String content, @Field("user_id") int user_id, @Field("category_id") int category_id);

    @POST("answer/give")
    @FormUrlEncoded
    Call<AnswerResponse> giveAnswer(@Field("content") String content, @Field("user_id") int user_id, @Field("question_id") int question_id);

    @POST("user/informations")
    Call<UserResponse> userInformations();
}
