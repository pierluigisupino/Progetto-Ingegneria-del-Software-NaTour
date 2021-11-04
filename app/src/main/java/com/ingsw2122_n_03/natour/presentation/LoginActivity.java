package com.ingsw2122_n_03.natour.presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.Controller;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public class LoginActivity extends BaseActivity {

    private LinearProgressIndicator progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Controller controller = Controller.getInstance();

        MaterialToolbar materialToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        TextInputEditText usernameEditText = findViewById(R.id.username);
        TextInputEditText passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);

        materialToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DA FARE CHECK INPUT
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                controller.login(LoginActivity.this, username, password);
            }
        });
    }

    @Override
    public void onSuccess(String snackbarMessage) {
        progressBar.setVisibility(View.INVISIBLE);
        //SHOW SNACKBAR
    }

    @Override
    public void onFail(String snackbarMessage) {
        progressBar.setVisibility(View.INVISIBLE);
        //SHOW SNACKBAR
    }
}