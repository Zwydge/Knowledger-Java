package com.example.knowledger;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knowledger.adapter.AnswerAdapter;
import com.example.knowledger.entities.Answer;
import com.example.knowledger.entities.AnswerResponse;
import com.example.knowledger.entities.User;
import com.example.knowledger.entities.UserResponse;
import com.example.knowledger.network.ApiService;
import com.example.knowledger.network.RetrofitBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAnswer extends AppCompatActivity {

    private static int VIDEO_REQUEST = 101;
    private Uri videoUri = null;

    private static final String TAG = "CreateAnswer";

    private RecyclerView recyclerview;
    private RecyclerView.LayoutManager layoutManager;
    private AnswerAdapter answerAdapter;

    @BindView(R.id.answer_list)
    RecyclerView recycle_answer;

    User current_user = new User(1,"test","email",1);
    Answer answer = new Answer(1, "",1,1, 0, 0, 0);

    List<Answer> ans_list = new ArrayList<>();

    ApiService service;
    TokenManager tokenManager;
    Call<UserResponse> userCall;
    Call<AnswerResponse> answerCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_answer);

        int id = getIntent().getIntExtra("id",0);
        String quest_content = getIntent().getStringExtra("content");

        answer.setQuestion_id(id);

        TextView quest_c = findViewById(R.id.the_question);
        quest_c.setText(quest_content);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if(tokenManager.getToken() == null){
            startActivity(new Intent(CreateAnswer.this, LoginActivity.class));
            finish();
        }

        EditText answer_content = findViewById(R.id.text_answer);

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        userCall = service.userInformations();
        userCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                Log.w(TAG, "Réponse reçue: " + response );

                if(response.isSuccessful()){
                    for (User e : response.body().getData()) {
                        current_user.setId(e.getId());
                        answer.setUser_id(e.getId());
                        Log.w(TAG, "user connected :"+current_user.getName());
                    }

                }else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(CreateAnswer.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.w(TAG, "Problème USER: " + t.getMessage() );
            }
        });

        answer_content.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                answer.setContent(answer_content.getText().toString());
            }
        });

        Button givebtn = findViewById(R.id.give_btn);

        givebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answer.getContent().equals("")){
                    giveAnswer(videoUri);
                }
                else{
                    Log.w(TAG, "text vide");
                    Toast.makeText(getApplicationContext(),"Veuillez entrer une réponse",Toast.LENGTH_SHORT).show();
                }
            }
        });

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_menu);
        bottomNavigationView.setSelectedItemId(R.id.question);

        BottomNavigationItemView btnhome = findViewById(R.id.home);
        btnhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAnswer.this, CategoryActivity.class));
                overridePendingTransition(0,0);
            }
        });

        BottomNavigationItemView btnaccount = findViewById(R.id.compte);
        btnaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAnswer.this, AccountActivity.class));
                overridePendingTransition(0,0);
            }
        });

        BottomNavigationItemView btnanswer = findViewById(R.id.repondre);
        btnanswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAnswer.this, AnswerActivity.class));
                overridePendingTransition(0,10);
            }
        });

        BottomNavigationItemView btnquest = findViewById(R.id.question);
        btnanswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAnswer.this, QuestionActivity.class));
                overridePendingTransition(0,10);
            }
        });


        answerCall = service.answer_get(answer.getQuestion_id());
        answerCall.enqueue(new Callback<AnswerResponse>() {
            @Override
            public void onResponse(Call<AnswerResponse> call, Response<AnswerResponse> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    for (Answer e : response.body().getData()) {
                        ans_list.add(new Answer(e.getId(), e.getContent(), e.getUser_id(), e.getQuestion_id(), e.getVote(), e.getLike(), e.getDislike()));
                    }
                    updateList();
                }else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(CreateAnswer.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<AnswerResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });

    }
    protected void updateList(){
        recyclerview = findViewById(R.id.answer_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(layoutManager);
        answerAdapter = new AnswerAdapter(ans_list);
        recyclerview.setAdapter(answerAdapter);
    }

    void giveAnswer(Uri videoUri){
        Log.w(TAG, "réponse envoyée ");
        answerCall = service.giveAnswer(answer.getContent(), answer.getUser_id(), answer.getQuestion_id());
        answerCall.enqueue(new Callback<AnswerResponse>() {
            @Override
            public void onResponse(Call<AnswerResponse> call, Response<AnswerResponse> response) {
                Log.w(TAG, "Réponse reçue askquestion: " + response);

                if (response.isSuccessful()) {
                    Log.w(TAG, "SUCCESS");
                    Toast.makeText(getApplicationContext(), "Réponse envoyée", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateAnswer.this, AnswerActivity.class));

                } else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(CreateAnswer.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<AnswerResponse> call, Throwable t) {
                Log.w(TAG, "Problème CREATE ASK: " + t.getMessage());
            }
        });


    }

    public void recordVideo(View view){

        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if(videoIntent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(videoIntent, VIDEO_REQUEST);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_REQUEST && resultCode == RESULT_OK) {
            videoUri = data.getData();
        }
    }
}
