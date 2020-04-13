package com.example.knowledger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.knowledger.entities.AccessToken;
import com.example.knowledger.entities.ApiError;
import com.example.knowledger.network.ApiService;
import com.example.knowledger.network.RetrofitBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    @BindView(R.id.text_name)
    TextInputLayout pseudo;
    @BindView(R.id.text_email)
    TextInputLayout temail;
    @BindView(R.id.text_password)
    TextInputLayout tpassword;

    ApiService service;
    Call<AccessToken> call;
    AwesomeValidation validator;
    TokenManager tokenManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_acitivity);

        ButterKnife.bind(this);

        service = RetrofitBuilder.createService(ApiService.class);

        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        setupRules();

        if(tokenManager.getToken().getAccessToken() != null){
            startActivity(new Intent(RegisterActivity.this, CategoryActivity.class));
            finish();
        }
    }

    @OnClick(R.id.go_to_login)
    void goToRegister(){
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

    @OnClick(R.id.btn_register)
    void register(){

        String name = pseudo.getEditText().getText().toString();
        String email = temail.getEditText().getText().toString();
        String password = tpassword.getEditText().getText().toString();

        pseudo.setError(null);
        temail.setError(null);
        tpassword.setError(null);

        validator.clear();

        if(validator.validate()) {
            call = service.register(name, email, password);
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                    Log.w(TAG, "onResponse: " + response);

                    if (response.isSuccessful()) {
                        Log.w(TAG, "onResponse: " + response.body());
                        tokenManager.saveToken(response.body());
                            startActivity(new Intent(RegisterActivity.this, CategoryActivity.class));
                            finish();

                    } else {
                        handleErrors(response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Log.w(TAG, "onFailure: " + t.getMessage());
                }
            });
        }
    }

    private void handleErrors(ResponseBody response){

        ApiError apiError = Utils.converErrors(response);

        for(Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()){
            if(error.getKey().equals("name")){
                pseudo.setError(error.getValue().get(0));
            }
            if(error.getKey().equals("email")){
                temail.setError(error.getValue().get(0));
            }
            if(error.getKey().equals("password")){
                tpassword.setError(error.getValue().get(0));
            }
        }

    }

    public void setupRules(){

        validator.addValidation(this, R.id.text_name, RegexTemplate.NOT_EMPTY, R.string.err_name);
        validator.addValidation(this, R.id.text_email, Patterns.EMAIL_ADDRESS, R.string.err_email);
        validator.addValidation(this, R.id.text_password, "[a-zA-Z0-9]{6,}", R.string.err_password);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(call != null) {
            call.cancel();
            call = null;
        }
    }
}
