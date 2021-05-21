package com.example.mymusic.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mymusic.R;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEdt, passwordEdt;
    private TextView alterTV;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEdt = findViewById(R.id.accountEdt);
        passwordEdt = findViewById(R.id.passwordEdt);
        alterTV = findViewById(R.id.alterAccountTV);

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                alterTV.setVisibility(View.GONE);
                return false;
            }
        };

        usernameEdt.setOnTouchListener(touchListener);
        passwordEdt.setOnTouchListener(touchListener);
    }

    public void onClickLogInBtn(View v) {
        String username = usernameEdt.getText().toString().trim();
        String password = passwordEdt.getText().toString().trim();

        if (username.equals("") && password.equals("")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            alterTV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                InputMethodManager im
                        = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}