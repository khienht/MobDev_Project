package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Button login,conwgoogle;
    private ImageButton changlang;
    private Button signup;
    private TextView reset_pass;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();

        setContentView(R.layout.activity_main);
        login = (Button) findViewById(R.id.signin_btn);
        signup = (Button) findViewById(R.id.signup_btn);
        reset_pass = (TextView) findViewById(R.id.resetpwtext);
        conwgoogle = (Button) findViewById(R.id.signinwgg_btn);
        changlang = (ImageButton) findViewById(R.id.btn_changelang);

        //change language
        changlang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLaguageDialog();
            }
        });

        //reset pass
        String text = reset_pass.getText().toString();
        SpannableString ss = new SpannableString(text);
        ClickableSpan c1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(MainActivity.this, Resetpw.class);
                startActivity(intent);
                //mở trang để reset password
            }
        };
        ss.setSpan(c1, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        reset_pass.setText(ss);
        reset_pass.setMovementMethod(LinkMovementMethod.getInstance());

        //mở trang login
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, MainActivity2.class);
                int data = 0;
                it.putExtra("key", data);
                startActivity(it);
            }
        });
        //mở trang signup
        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, MainActivity2.class);
                int data = 1;
                it.putExtra("key", data);
                startActivity(it);
            }
        });

        //continue with google
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);
        conwgoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });
    }

    private void SignIn() {
        Intent intent =gsc.getSignInIntent();
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateToHomepage();
            } catch (ApiException e){
                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void navigateToHomepage(){
        Intent intent = new Intent(MainActivity.this,HomePage.class);
        startActivity(intent);
    }
    private void showChangeLaguageDialog(){
        final String[] listItems = {"Tiếng Việt", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle("Choose Language...");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0){
                    //tiếng việt
                    setLocale("vi");
                    recreate();
                }
                else{
                    setLocale("en");
                    recreate();
                }
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        //show alert dialog
        mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale=locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        //save data to shared preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }
    //loadlanguage saved in shared preferences
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language =prefs.getString("My_Lang", "");
        setLocale(language);
    }
}
