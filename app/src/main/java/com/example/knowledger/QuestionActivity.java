package com.example.knowledger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.knowledger.entities.Category;
import com.example.knowledger.entities.CategoryResponse;
import com.example.knowledger.entities.Question;
import com.example.knowledger.entities.QuestionResponse;
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

public class QuestionActivity extends AppCompatActivity {

    private static final String TAG = "QuestionActivity";

    private Spinner spinner;

    User current_user = new User(1,"test","email",1);
    Question quest_to_ask = new Question(1, "",1,"Cuisine");
    Category current_cat = new Category(-1,"test","12");

    ApiService service;
    TokenManager tokenManager;
    Call<CategoryResponse> call;
    Call<UserResponse> userCall;
    Call<QuestionResponse> questionCall;

    @BindView(R.id.give_btn)
    Button askbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if(tokenManager.getToken() == null){
            startActivity(new Intent(QuestionActivity.this, LoginActivity.class));
            finish();
        }

        EditText question_content = findViewById(R.id.text_answer);

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        call = service.category_get();

        spinner = findViewById(R.id.cat_list);

        List<String> categories = new ArrayList<String>();
        List<Integer> cat_id = new ArrayList<>();
        categories.add("Sélectionnez une catégorie");
        cat_id.add(-1);

        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                Log.w(TAG, "Réponse reçue: " + response );

                if(response.isSuccessful()){
                    for (Category e : response.body().getData()) {
                        categories.add(e.getName());
                        cat_id.add(e.getId());
                    }
                }else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(QuestionActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.w(TAG, "Problème CAT: " + t.getMessage() );
            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categories);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner.setSelection(position);
                String category_selected = spinner.getSelectedItem().toString();
                current_cat.setName(category_selected);
                current_cat.setId(cat_id.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        userCall = service.userInformations();
        userCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                Log.w(TAG, "Réponse reçue: " + response );

                if(response.isSuccessful()){
                    for (User e : response.body().getData()) {
                        current_user.setId(e.getId());
                        current_user.setName(e.getName());
                        current_user.setRole(e.getRole());
                        current_user.setEmail(e.getEmail());
                        Log.w(TAG, "user connected :"+current_user.getName());
                    }

                }else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(QuestionActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.w(TAG, "Problème USER: " + t.getMessage() );
            }
        });

        question_content.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                quest_to_ask.setContent(question_content.getText().toString());
            }
        });

        Button askbtn = findViewById(R.id.give_btn);

        askbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!quest_to_ask.getContent().equals("")){
                    askQuestion(quest_to_ask.getContent());
                }
                else{
                    Log.w(TAG, "text vide");
                    Toast.makeText(getApplicationContext(),"Veuillez entrer une question",Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(QuestionActivity.this, CategoryActivity.class));
                overridePendingTransition(0,0);
            }
        });

        BottomNavigationItemView btnaccount = findViewById(R.id.compte);
        btnaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QuestionActivity.this, AccountActivity.class));
                overridePendingTransition(0,0);
            }
        });

        BottomNavigationItemView btnanswer = findViewById(R.id.repondre);
        btnanswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QuestionActivity.this, AnswerActivity.class));
                overridePendingTransition(0,10);
            }
        });
    }

    void askQuestion(String content){
        if(current_cat.getId() != -1) {
            Log.w(TAG, "question envoyée ");
            questionCall = service.askQuestion(content, current_user.getId(), current_cat.getId());
            questionCall.enqueue(new Callback<QuestionResponse>() {
                @Override
                public void onResponse(Call<QuestionResponse> call, Response<QuestionResponse> response) {
                    Log.w(TAG, "Réponse reçue askquestion: " + response);

                    if (response.isSuccessful()) {
                        Log.w(TAG, "SUCCESS");
                        Toast.makeText(getApplicationContext(), "Question posée avec succès", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(QuestionActivity.this, CategoryActivity.class));

                    } else {
                        tokenManager.deleteToken();
                        startActivity(new Intent(QuestionActivity.this, LoginActivity.class));
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<QuestionResponse> call, Throwable t) {
                    Log.w(TAG, "Problème ASK: " + t.getMessage());
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(), "Veuillez choisir une catégorie", Toast.LENGTH_SHORT).show();
        }

    }

}
