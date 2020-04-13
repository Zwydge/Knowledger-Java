package com.example.knowledger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.knowledger.adapter.CategoryAdapter;
import com.example.knowledger.adapter.ReputationAdapter;
import com.example.knowledger.entities.Category;
import com.example.knowledger.entities.CategoryResponse;
import com.example.knowledger.entities.QuestionResponse;
import com.example.knowledger.entities.Reputation;
import com.example.knowledger.entities.ReputationResponse;
import com.example.knowledger.entities.User;
import com.example.knowledger.entities.UserResponse;
import com.example.knowledger.network.ApiService;
import com.example.knowledger.network.RetrofitBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

public class AccountActivity extends AppCompatActivity {

    private static final String TAG = "AccountActivity";

    User current_user = new User(1,"test","email",1);

    ApiService service;
    TokenManager tokenManager;
    Call<CategoryResponse> call;
    Call<UserResponse> userCall;

    private RecyclerView recyclerview;
    private RecyclerView.LayoutManager layoutManager;
    private ReputationAdapter reputationAdapter;

    List<Reputation> rep_list = new ArrayList<>();

    @BindView(R.id.list_rep)
    RecyclerView reputation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);



        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if(tokenManager.getToken() == null){
            startActivity(new Intent(AccountActivity.this, LoginActivity.class));
            finish();
        }

        TextView pseudo = findViewById(R.id.text_pseudo);
        TextView email = findViewById(R.id.text_email);

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

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

                        pseudo.setText(current_user.getName());
                        email.setText(current_user.getEmail());
                    }

                }else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(AccountActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.w(TAG, "Problème USER: " + t.getMessage() );
            }
        });

        Call<ReputationResponse> repCall;

        repCall = service.user_reputation_get(current_user.getId());
        repCall.enqueue(new Callback<ReputationResponse>() {
            @Override
            public void onResponse(Call<ReputationResponse> call, Response<ReputationResponse> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    for (Reputation e : response.body().getData()) {
                        rep_list.add(new Reputation(e.getId(), e.getValue(),e.getUser_id(), e.getCategory()));
                        Log.w(TAG, "REP: " + e.getValue() );
                    }
                    updateList();
                }else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(AccountActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ReputationResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_menu);
        bottomNavigationView.setSelectedItemId(R.id.compte);

        BottomNavigationItemView btnhome = findViewById(R.id.home);
        btnhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this, CategoryActivity.class));
                overridePendingTransition(0,0);
            }
        });

        BottomNavigationItemView btnquestion = findViewById(R.id.question);
        btnquestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this, QuestionActivity.class));
                overridePendingTransition(0,0);
            }
        });

        BottomNavigationItemView btnanswer = findViewById(R.id.repondre);
        btnanswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this, AnswerActivity.class));
                overridePendingTransition(0,0);
            }
        });
    }

    protected void updateList(){
        recyclerview = findViewById(R.id.list_rep);
        layoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(layoutManager);
        reputationAdapter = new ReputationAdapter(rep_list);
        recyclerview.setAdapter(reputationAdapter);
    }
}
