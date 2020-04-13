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

import com.example.knowledger.adapter.CategoryAdapter;
import com.example.knowledger.entities.Category;
import com.example.knowledger.entities.CategoryResponse;
import com.example.knowledger.network.ApiService;
import com.example.knowledger.network.RetrofitBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {

    private static final String TAG = "CategoryActivity";

    private RecyclerView recyclerview;
    private RecyclerView.LayoutManager layoutManager;
    private CategoryAdapter categoryAdapter;

    @BindView(R.id.category_list)
    RecyclerView category;

    @OnClick(R.id.question)
    void goToQuestion(){
        startActivity(new Intent(CategoryActivity.this, QuestionActivity.class));
        overridePendingTransition(100,100);
    }
    @OnClick(R.id.repondre)
    void goToAnswer(){
        startActivity(new Intent(CategoryActivity.this, AnswerActivity.class));
        overridePendingTransition(100,100);
    }
    @OnClick(R.id.compte)
    void goToAccount(){
        startActivity(new Intent(CategoryActivity.this, AccountActivity.class));
        overridePendingTransition(100,100);
    }

    List<Category> cat_list = new ArrayList<>();

    ApiService service;
    TokenManager tokenManager;
    Call<CategoryResponse> call;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.drawable.com_facebook_button_like_icon_selected)
                .setContentTitle("Knowledger")
                .setContentText("Quelqu'un a répondu à votre question")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        ButterKnife.bind(this);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if(tokenManager.getToken() == null){
            startActivity(new Intent(CategoryActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        call = service.category();
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    for (Category e : response.body().getData()) {
                        cat_list.add(new Category(e.getId(),e.getName(),e.getMember()));
                    }
                    updateList();
                }else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(CategoryActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_menu);
        bottomNavigationView.setSelectedItemId(R.id.home);

        BottomNavigationItemView btnquestion = findViewById(R.id.question);
        btnquestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryActivity.this, QuestionActivity.class));
                overridePendingTransition(100,100);
            }
        });

        BottomNavigationItemView btnaccount = findViewById(R.id.compte);
        btnaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryActivity.this, AccountActivity.class));
                overridePendingTransition(100,100);
            }
        });

        BottomNavigationItemView btnanswer = findViewById(R.id.repondre);
        btnanswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryActivity.this, AnswerActivity.class));
                overridePendingTransition(100,100);
            }
        });

        PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        Pusher pusher = new Pusher("948f4379c878fe18ab23", options);


        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                System.out.println("State changed from " + change.getPreviousState() +
                        " to " + change.getCurrentState());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                System.out.println("There was a problem connecting! " +
                        "\ncode: " + code +
                        "\nmessage: " + message +
                        "\nException: " + e
                );
            }
        }, ConnectionState.ALL);

        Channel channel = pusher.subscribe("my-channel");

        channel.bind("my-event", event -> {
            System.out.println("Received event with data: " + event.toString());
            notificationManager.notify(100, builder.build());
        });
    }

    protected void updateList(){
        recyclerview = findViewById(R.id.category_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(layoutManager);
        categoryAdapter = new CategoryAdapter(cat_list);
        recyclerview.setAdapter(categoryAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(call != null){
            call.cancel();
            call = null;
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel name";
            String description = "channel desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
