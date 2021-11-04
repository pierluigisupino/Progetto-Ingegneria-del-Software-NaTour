package com.ingsw2122_n_03.natour.presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.Controller;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;


public class RegisterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Controller controller = Controller.getInstance();

        MaterialToolbar materialToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        TextInputEditText usernameEditText = findViewById(R.id.username);
        TextInputEditText emailEditText = findViewById(R.id.email);
        TextInputEditText passwordEditText = findViewById(R.id.password);
        Button registerButton = findViewById(R.id.registerButton);

        materialToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DA FARE CHECK INPUT
                String username = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                controller.signUp(RegisterActivity.this, username, email, password);
            }
        });
    }

    @Override
    public void onSuccess() {
        //SHOW SNACKBAR
    }

    @Override
    public void onFail() {
        //SHOW SNACKBAR
    }
}