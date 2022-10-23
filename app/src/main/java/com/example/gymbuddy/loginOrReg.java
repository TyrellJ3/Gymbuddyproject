package com.example.gymbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class loginOrReg extends AppCompatActivity {
    private Button mLogin, mReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_reg);

        mLogin = (Button) findViewById(R.id.login);
        mReg = (Button) findViewById(R.id.reg);

    mLogin.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        Intent intent = new Intent(loginOrReg.this, Login.class);
                    startActivity(intent);
                    finish();
                    return;
        }
    });

        mReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(loginOrReg.this, RegistrationPage.class);
                startActivity(intent);
                finish();
                return;
            }
        });


    }
}