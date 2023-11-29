package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.LogPrinter;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Model.User;
import com.example.myapplication.RestAPI.APIClient;
import com.example.myapplication.RestAPI.APIInterface;
import com.example.myapplication.RestAPI.APIManager;
import com.example.myapplication.RestAPI.tokenResponse;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingActivity extends AppCompatActivity {
    private WebView webView;
    private ImageView img;
    public String token;
    private static User me = new User();
    MainActivity2 mainActivity2;


    Handler loginHandler = new Handler(message -> {
        Bundle bundle = message.getData();
        boolean isOK = bundle.getBoolean("LOGIN");
        if (isOK) {
            startActivity(new Intent(mainActivity2, HomeActivity.class));
            mainActivity2.finish();
        }

        return false;
    });

    @Override
    //quay trở về trang trước
    public void onBackPressed() {
        super.onBackPressed();
        // Thực hiện các hành động cần thiết khi quay lại trang trước
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        webView = findViewById(R.id.web_login);
        img = findViewById(R.id.back_btn);
        img.setOnClickListener(new View.OnClickListener() {//quay về trang trước
            @Override
            public void onClick(View v) {
//                onBackPressed();
                Intent it = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(it);
            }
        });

        Intent intent = getIntent();
        //1: login, 2: signup
        int received = intent.getIntExtra("fragment", 0);
        if (received==1){//login
            String received_name = intent.getStringExtra("name");
            String received_pass = intent.getStringExtra("pass");
            CallLoginService(received_name, received_pass);
            img.setVisibility(View.VISIBLE);
            APIManager.getUserInfo();
        }
        else{//sign up
            String usr = intent.getStringExtra("signup_usr");
            String email = intent.getStringExtra("signup_email");
            String pwd = intent.getStringExtra("signup_pass");
            String rePwd=intent.getStringExtra("signup_cfmpass");
            img.setVisibility(View.GONE);

            //String url ="https://uiot.ixxc.dev/auth/realms/master/protocol/openid-connect/auth?client_id=openremote&redirect_uri=https%3A%2F%2Fuiot.ixxc.dev%2Fmanager%2F&state=ec4fb068-fcb9-4247-870f-caceaf59bc0f&response_mode=fragment&response_type=code&scope=openid&nonce=e2762ea1-2f4f-4343-9738-ffa814e3274a/";
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.getSettings().setDatabaseEnabled(false);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setGeolocationEnabled(false);
            webView.getSettings().setSaveFormData(false);

            String baseurl ="https://uiot.ixxc.dev";
            //webView.setWebViewClient(new WebViewClient());
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    //super.onPageFinished(view, url);
                    // Tìm và nhấn vào nút sau khi trang đã tải xong
                    if (url.contains(baseurl + "/auth/realms/master/protocol/openid-connect/auth?client_id=openremote")) {
                        // Thực hiện click vào nút SIGN UP khi địa chỉ URL khớp
                        webView.loadUrl("javascript:(function(){" +
                                "document.getElementsByClassName('btn waves-effect waves-light')[1].click();" +
                                "})()");
                        webView.setWebViewClient(new WebViewClient(){
                            @Override
                            public void onPageFinished(WebView view, String url) {
                                String usrScript = "document.getElementById('username').value='" + usr + "';";
                                String emailScript = "document.getElementById('email').value='" + email + "';";
                                String pwdScript = "document.getElementById('password').value='" + pwd + "';";
                                String rePwdScript = "document.getElementById('password-confirm').value='" + rePwd + "';";

                                view.evaluateJavascript(usrScript, null);
                                view.evaluateJavascript(emailScript, null);
                                view.evaluateJavascript(pwdScript, null);
                                view.evaluateJavascript(rePwdScript, null);
                                view.evaluateJavascript("document.getElementById('kc-register-form').submit();", null);

                                CallSignUpService(usr, pwd);
                            }
                        });

                    }
                }
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    // Xử lý chuyển hướng sau khi nhấn nút
                    if (url.startsWith(baseurl)) {
                        view.loadUrl(url);
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            webView.loadUrl(baseurl);
        }

    }
    private void CallLoginService(String name, String pass){
        ProgressBar progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        try{
            final String id = name;
            final String key = pass;
            APIInterface service = APIClient.getClient().create(APIInterface.class);
            Call<ResponseBody> srvLogin = service.getToken("openremote",id,key,"password");
            srvLogin.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        try {
                            String ResponseJson = response.body().string();
                            Gson objGson = new Gson();
                            tokenResponse objResp=objGson.fromJson(ResponseJson, tokenResponse.class);
                            token = objResp.getAccess_token();
                            Toast.makeText(LoadingActivity.this, token, Toast.LENGTH_SHORT).show();
                            Toast.makeText(LoadingActivity.this, "Token success", Toast.LENGTH_SHORT).show();
                            TextView text = findViewById(R.id.text_wait);
                            text.setText("Success");
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoadingActivity.this,User.getMe().username, Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(LoadingActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(LoadingActivity.this, "Invalid, Try again", Toast.LENGTH_SHORT).show();
                        TextView text = findViewById(R.id.text_wait);
                        text.setText("Login Failed \n Please try again!");
                        progressBar.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(LoadingActivity.this, "System error", Toast.LENGTH_SHORT).show();
                    Log.e("NetworkError", "Error: " + t.getMessage());
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(LoadingActivity.this, "System error", Toast.LENGTH_SHORT).show();
        }

    }
    private void CallSignUpService(String name, String pass){
        ProgressBar progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        try{
            final String id = name;
            final String key = pass;
            APIInterface service = APIClient.getClient().create(APIInterface.class);
            Call<ResponseBody> srvLogin = service.getToken("openremote",id,key,"password");
            srvLogin.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        try {
                            String ResponseJson = response.body().string();
                            Gson objGson = new Gson();
                            tokenResponse objResp=objGson.fromJson(ResponseJson, tokenResponse.class);
                            token = objResp.getAccess_token();
                            Toast.makeText(LoadingActivity.this, token, Toast.LENGTH_SHORT).show();
                            Toast.makeText(LoadingActivity.this, "Token success", Toast.LENGTH_SHORT).show();
                            TextView text = findViewById(R.id.text_wait);
                            text.setText(R.string.success);
                            progressBar.setVisibility(View.GONE);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(LoadingActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(LoadingActivity.this, "Invalid, Try again", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(LoadingActivity.this, "System error", Toast.LENGTH_SHORT).show();
                    Log.e("NetworkError", "Error: " + t.getMessage());
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(LoadingActivity.this, "System error", Toast.LENGTH_SHORT).show();
        }
    }
}
