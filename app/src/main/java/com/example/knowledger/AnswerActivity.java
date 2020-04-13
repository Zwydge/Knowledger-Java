package com.example.knowledger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.knowledger.adapter.QuestionAdapter;
import com.example.knowledger.adapter.ReputationAdapter;
import com.example.knowledger.entities.CategoryResponse;
import com.example.knowledger.entities.Question;
import com.example.knowledger.entities.QuestionResponse;
import com.example.knowledger.entities.Reputation;
import com.example.knowledger.entities.ReputationResponse;
import com.example.knowledger.entities.User;
import com.example.knowledger.entities.UserResponse;
import com.example.knowledger.network.ApiService;
import com.example.knowledger.network.RetrofitBuilder;
import com.example.knowledger.tools.ItemClickSupport;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnswerActivity extends AppCompatActivity {

    private static final String TAG = "AnswerActivity";

    ApiService service;
    TokenManager tokenManager;
    Call<QuestionResponse> questCall;

    private RecyclerView recyclerview;
    private RecyclerView.LayoutManager layoutManager;
    private QuestionAdapter questionAdapter;

    List<Question> quest_list = new ArrayList<>();

    @BindView(R.id.quest_list)
    RecyclerView question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if(tokenManager.getToken() == null){
            startActivity(new Intent(AnswerActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        questCall = service.get_quest();
        questCall.enqueue(new Callback<QuestionResponse>() {
            @Override
            public void onResponse(Call<QuestionResponse> call, Response<QuestionResponse> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    for (Question e : response.body().getData()) {
                        quest_list.add(new Question(e.getId(), e.getContent(), e.getUserId(), e.getCatName()));
                    }
                    updateList();
                }else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(AnswerActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<QuestionResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_menu);
        bottomNavigationView.setSelectedItemId(R.id.repondre);

        BottomNavigationItemView btnhome = findViewById(R.id.home);
        btnhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AnswerActivity.this, CategoryActivity.class));
                overridePendingTransition(0,0);
            }
        });

        BottomNavigationItemView btnaccount = findViewById(R.id.compte);
        btnaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AnswerActivity.this, AccountActivity.class));
                overridePendingTransition(0,0);
            }
        });

        BottomNavigationItemView btnquestion = findViewById(R.id.question);
        btnquestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AnswerActivity.this, QuestionActivity.class));
                overridePendingTransition(0,0);
            }
        });
    }

    protected void updateList(){
        recyclerview = findViewById(R.id.quest_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(layoutManager);
        questionAdapter = new QuestionAdapter(quest_list);
        recyclerview.setAdapter(questionAdapter);
        configureOnClickRecyclerView(questionAdapter);
    }

    private void configureOnClickRecyclerView(QuestionAdapter adapter){
        ItemClickSupport.addTo(recyclerview, R.layout.question_list)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerview, int position, View v) {
                        Question quest = adapter.getQuestion(position);
                        Intent intent = new Intent(AnswerActivity.this, CreateAnswer.class);
                        intent.putExtra("id", quest.getId());
                        intent.putExtra("content", quest.getContent());
                        startActivity(intent);
                    }
                });
    }



}
